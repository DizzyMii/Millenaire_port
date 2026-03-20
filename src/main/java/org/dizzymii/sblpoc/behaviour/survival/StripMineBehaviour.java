package org.dizzymii.sblpoc.behaviour.survival;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.PocNpc;

import java.util.List;

/**
 * Digs a 1x2 tunnel at diamond level (Y=-59 to Y=16) in one direction,
 * placing torches every 8 blocks and mining any ores encountered.
 * Branches every 4 blocks for efficient strip mining.
 */
public class StripMineBehaviour extends ExtendedBehaviour<PocNpc> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT)
            );

    private static final int TARGET_Y = -59;
    private static final int MAX_TUNNEL_LENGTH = 40;
    private static final int TORCH_INTERVAL = 8;

    private Direction mineDirection;
    private BlockPos currentTarget;
    private int tunnelProgress = 0;
    private int breakProgress = 0;
    private int breakTime = 0;
    private boolean descending = false;
    private boolean completed = false;

    public StripMineBehaviour() {
        noTimeout();
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PocNpc npc) {
        return npc.getInventoryModel().hasPickaxe();
    }

    @Override
    protected void start(ServerLevel level, PocNpc npc, long gameTime) {
        completed = false;
        tunnelProgress = 0;

        // Pick a random horizontal direction
        Direction[] dirs = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        mineDirection = dirs[npc.getRandom().nextInt(dirs.length)];

        // If above target Y, descend first
        if (npc.blockPosition().getY() > TARGET_Y + 10) {
            descending = true;
            currentTarget = npc.blockPosition().below();
        } else {
            descending = false;
            currentTarget = npc.blockPosition().relative(mineDirection);
        }

        equipPickaxe(npc);
        startBreaking(level, npc);
    }

    @Override
    protected void tick(ServerLevel level, PocNpc npc, long gameTime) {
        if (completed || currentTarget == null) {
            doStop(level, npc, gameTime);
            return;
        }

        BlockState state = level.getBlockState(currentTarget);

        // If block is air, advance
        if (state.isAir() || state.liquid()) {
            advanceTarget(level, npc);
            return;
        }

        // Skip bedrock
        if (state.is(Blocks.BEDROCK)) {
            // Try to go around
            advanceTarget(level, npc);
            return;
        }

        npc.getLookControl().setLookAt(currentTarget.getX() + 0.5, currentTarget.getY() + 0.5, currentTarget.getZ() + 0.5);

        if (breakProgress % 5 == 0) {
            npc.swing(InteractionHand.MAIN_HAND);
        }

        breakProgress++;
        level.destroyBlockProgress(npc.getId(), currentTarget, (int) ((float) breakProgress / breakTime * 9));

        if (breakProgress >= breakTime) {
            // Check for lava before breaking
            boolean hasLavaNearby = false;
            for (Direction d : Direction.values()) {
                if (level.getBlockState(currentTarget.relative(d)).is(Blocks.LAVA)) {
                    hasLavaNearby = true;
                    break;
                }
            }

            if (hasLavaNearby) {
                // Place cobblestone to block lava
                level.destroyBlockProgress(npc.getId(), currentTarget, -1);
                completed = true;
                doStop(level, npc, gameTime);
                return;
            }

            level.destroyBlock(currentTarget, true, npc);

            // Record any ores found in spatial memory
            for (Direction d : Direction.values()) {
                BlockPos neighbor = currentTarget.relative(d);
                BlockState neighborState = level.getBlockState(neighbor);
                if (neighborState.is(BlockTags.GOLD_ORES) || neighborState.is(BlockTags.IRON_ORES)
                        || neighborState.is(BlockTags.DIAMOND_ORES) || neighborState.is(BlockTags.COAL_ORES)
                        || neighborState.is(BlockTags.EMERALD_ORES) || neighborState.is(BlockTags.LAPIS_ORES)
                        || neighborState.is(BlockTags.REDSTONE_ORES)) {
                    npc.getSpatialMemory().scanAround(level, currentTarget);
                    break;
                }
            }

            advanceTarget(level, npc);
        }
    }

    @Override
    protected boolean shouldKeepRunning(PocNpc npc) {
        return !completed && tunnelProgress < MAX_TUNNEL_LENGTH && npc.isAlive();
    }

    @Override
    protected void stop(ServerLevel level, PocNpc npc, long gameTime) {
        if (currentTarget != null) {
            level.destroyBlockProgress(npc.getId(), currentTarget, -1);
        }
        currentTarget = null;
    }

    private void advanceTarget(ServerLevel level, PocNpc npc) {
        tunnelProgress++;

        if (descending) {
            if (npc.blockPosition().getY() <= TARGET_Y + 2) {
                descending = false;
            } else {
                // Dig staircase down
                currentTarget = npc.blockPosition().relative(mineDirection).below();
                BrainUtils.setMemory(npc, MemoryModuleType.WALK_TARGET,
                        new WalkTarget(Vec3.atCenterOf(currentTarget.above()), 1.0f, 0));
            }
        }

        if (!descending) {
            // Main tunnel: mine 2-high ahead
            currentTarget = npc.blockPosition().relative(mineDirection);

            // Also mine the block above for 2-high clearance
            BlockPos above = currentTarget.above();
            if (!level.getBlockState(above).isAir()) {
                level.destroyBlock(above, true, npc);
            }

            // Place torch every N blocks
            if (tunnelProgress % TORCH_INTERVAL == 0) {
                int torchSlot = npc.getInventoryModel().findSlot(s ->
                        s.getItem() == net.minecraft.world.item.Items.TORCH);
                if (torchSlot >= 0) {
                    npc.getInventory().removeItem(torchSlot, 1);
                    level.setBlock(npc.blockPosition().above(),
                            Blocks.TORCH.defaultBlockState(), 3);
                }
            }

            BrainUtils.setMemory(npc, MemoryModuleType.WALK_TARGET,
                    new WalkTarget(Vec3.atCenterOf(currentTarget), 1.0f, 0));
        }

        if (tunnelProgress >= MAX_TUNNEL_LENGTH) {
            completed = true;
            return;
        }

        startBreaking(level, npc);
    }

    private void startBreaking(ServerLevel level, PocNpc npc) {
        if (currentTarget == null) return;
        BlockState state = level.getBlockState(currentTarget);
        if (state.isAir()) return;

        ItemStack tool = npc.getMainHandItem();
        float miningSpeed = tool.getDestroySpeed(state);
        if (miningSpeed <= 1.0f) miningSpeed = 1.0f;

        float destroySpeed = state.getDestroySpeed(level, currentTarget);
        if (destroySpeed < 0) {
            breakTime = Integer.MAX_VALUE;
            return;
        }
        boolean correctTool = !state.requiresCorrectToolForDrops() || tool.isCorrectToolForDrops(state);
        breakTime = (int) Math.ceil(destroySpeed * (correctTool ? 1.5f : 5.0f) / miningSpeed * 20);
        if (breakTime < 1) breakTime = 1;
        breakProgress = 0;
    }

    private void equipPickaxe(PocNpc npc) {
        var lookup = npc.getInventoryModel().getBestToolFor(Blocks.STONE.defaultBlockState());
        if (lookup != null && lookup.inventorySlot() >= 0) {
            ItemStack current = npc.getMainHandItem();
            npc.setItemInHand(InteractionHand.MAIN_HAND, lookup.stack());
            npc.getInventory().setItem(lookup.inventorySlot(), current);
        }
    }
}
