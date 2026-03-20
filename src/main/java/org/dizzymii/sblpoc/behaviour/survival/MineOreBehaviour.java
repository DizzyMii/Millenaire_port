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
import org.dizzymii.sblpoc.ai.world.BlockCategory;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Navigates to a known ore vein from SpatialMemory and mines it.
 * Follows the vein by mining connected ore blocks, avoids lava.
 */
public class MineOreBehaviour extends ExtendedBehaviour<PocNpc> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT)
            );

    private static final double REACH_DISTANCE_SQ = 4.5 * 4.5;
    private static final int MAX_ORE_BLOCKS = 20;

    @Nullable private BlockCategory targetOreCategory;
    private final Deque<BlockPos> oreQueue = new ArrayDeque<>();
    private final Set<BlockPos> visited = new HashSet<>();
    @Nullable private BlockPos currentOre;
    private int breakProgress = 0;
    private int breakTime = 0;
    private boolean navigating = false;
    private int oresMined = 0;

    public MineOreBehaviour() {
        noTimeout();
    }

    public MineOreBehaviour oreType(BlockCategory category) {
        this.targetOreCategory = category;
        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PocNpc npc) {
        if (targetOreCategory == null) {
            // Try to find any ore
            for (BlockCategory cat : new BlockCategory[]{
                    BlockCategory.DIAMOND_ORE, BlockCategory.IRON_ORE,
                    BlockCategory.GOLD_ORE, BlockCategory.COAL_ORE}) {
                BlockPos pos = npc.getSpatialMemory().findNearest(cat, npc.blockPosition());
                if (pos != null) {
                    targetOreCategory = cat;
                    oreQueue.add(pos);
                    return true;
                }
            }
            return false;
        }

        BlockPos pos = npc.getSpatialMemory().findNearest(targetOreCategory, npc.blockPosition());
        if (pos != null) {
            oreQueue.add(pos);
            return true;
        }
        return false;
    }

    @Override
    protected void start(ServerLevel level, PocNpc npc, long gameTime) {
        oresMined = 0;
        visited.clear();
        equipPickaxe(npc);
        advanceToNextOre(level, npc);
    }

    @Override
    protected void tick(ServerLevel level, PocNpc npc, long gameTime) {
        if (currentOre == null) {
            if (oreQueue.isEmpty()) {
                doStop(level, npc, gameTime);
                return;
            }
            advanceToNextOre(level, npc);
            return;
        }

        BlockState state = level.getBlockState(currentOre);
        if (state.isAir()) {
            currentOre = null;
            return;
        }

        if (navigating) {
            double distSq = npc.blockPosition().distSqr(currentOre);
            if (distSq <= REACH_DISTANCE_SQ) {
                navigating = false;
                startMining(level, npc);
            }
            return;
        }

        // Check for lava adjacency
        for (Direction d : Direction.values()) {
            if (level.getBlockState(currentOre.relative(d)).is(Blocks.LAVA)) {
                // Skip this ore to avoid lava
                currentOre = null;
                return;
            }
        }

        npc.getLookControl().setLookAt(currentOre.getX() + 0.5, currentOre.getY() + 0.5, currentOre.getZ() + 0.5);

        if (breakProgress % 5 == 0) {
            npc.swing(InteractionHand.MAIN_HAND);
        }

        breakProgress++;
        level.destroyBlockProgress(npc.getId(), currentOre, (int) ((float) breakProgress / breakTime * 9));

        if (breakProgress >= breakTime) {
            level.destroyBlock(currentOre, true, npc);
            oresMined++;

            // Discover connected ores
            discoverConnectedOres(level, currentOre);

            currentOre = null;
            breakProgress = 0;
        }
    }

    @Override
    protected boolean shouldKeepRunning(PocNpc npc) {
        return (currentOre != null || !oreQueue.isEmpty()) && oresMined < MAX_ORE_BLOCKS && npc.isAlive();
    }

    @Override
    protected void stop(ServerLevel level, PocNpc npc, long gameTime) {
        if (currentOre != null) {
            level.destroyBlockProgress(npc.getId(), currentOre, -1);
        }
        oreQueue.clear();
        visited.clear();
        currentOre = null;
        targetOreCategory = null;
    }

    private void advanceToNextOre(ServerLevel level, PocNpc npc) {
        currentOre = oreQueue.poll();
        if (currentOre == null) return;

        visited.add(currentOre);

        double distSq = npc.blockPosition().distSqr(currentOre);
        if (distSq > REACH_DISTANCE_SQ) {
            BrainUtils.setMemory(npc, MemoryModuleType.WALK_TARGET,
                    new WalkTarget(Vec3.atCenterOf(currentOre), 1.0f, 1));
            navigating = true;
        } else {
            navigating = false;
            startMining(level, npc);
        }
    }

    private void startMining(ServerLevel level, PocNpc npc) {
        if (currentOre == null) return;
        BlockState state = level.getBlockState(currentOre);

        ItemStack tool = npc.getMainHandItem();
        float miningSpeed = tool.getDestroySpeed(state);
        if (miningSpeed <= 1.0f) miningSpeed = 1.0f;

        float destroySpeed = state.getDestroySpeed(level, currentOre);
        boolean correctTool = !state.requiresCorrectToolForDrops() || tool.isCorrectToolForDrops(state);
        breakTime = (int) Math.ceil(destroySpeed * (correctTool ? 1.5f : 5.0f) / miningSpeed * 20);
        if (breakTime < 1) breakTime = 1;
        breakProgress = 0;
    }

    private void discoverConnectedOres(ServerLevel level, BlockPos center) {
        for (Direction d : Direction.values()) {
            BlockPos neighbor = center.relative(d);
            if (visited.contains(neighbor)) continue;

            BlockState state = level.getBlockState(neighbor);
            if (isOre(state)) {
                oreQueue.add(neighbor);
            }
        }
    }

    private boolean isOre(BlockState state) {
        return state.is(BlockTags.GOLD_ORES) || state.is(BlockTags.IRON_ORES)
                || state.is(BlockTags.DIAMOND_ORES) || state.is(BlockTags.COAL_ORES)
                || state.is(BlockTags.EMERALD_ORES) || state.is(BlockTags.LAPIS_ORES)
                || state.is(BlockTags.REDSTONE_ORES);
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
