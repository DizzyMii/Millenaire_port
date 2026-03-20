package org.dizzymii.sblpoc.behaviour.survival;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.PocNpc;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Navigates to a target block, equips the correct tool, and breaks it.
 * Simulates block-breaking by ticking a break progress counter
 * matching the block's destroy time for the held tool.
 */
public class MineBlockBehaviour extends ExtendedBehaviour<PocNpc> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT)
            );

    private static final double REACH_DISTANCE_SQ = 4.5 * 4.5;

    @Nullable private BlockPos targetPos;
    private int breakProgress = 0;
    private int breakTime = 0;
    private boolean navigating = false;

    public MineBlockBehaviour() {
        noTimeout();
    }

    public MineBlockBehaviour target(BlockPos pos) {
        this.targetPos = pos;
        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PocNpc npc) {
        return targetPos != null && !level.getBlockState(targetPos).isAir();
    }

    @Override
    protected void start(ServerLevel level, PocNpc npc, long gameTime) {
        breakProgress = 0;
        navigating = false;

        double distSq = npc.blockPosition().distSqr(targetPos);
        if (distSq > REACH_DISTANCE_SQ) {
            // Navigate to block
            BrainUtils.setMemory(npc, MemoryModuleType.WALK_TARGET,
                    new WalkTarget(Vec3.atCenterOf(targetPos), 1.0f, 1));
            navigating = true;
        } else {
            startMining(level, npc);
        }
    }

    @Override
    protected void tick(ServerLevel level, PocNpc npc, long gameTime) {
        if (targetPos == null) {
            doStop(level, npc, gameTime);
            return;
        }

        BlockState state = level.getBlockState(targetPos);
        if (state.isAir()) {
            doStop(level, npc, gameTime);
            return;
        }

        if (navigating) {
            double distSq = npc.blockPosition().distSqr(targetPos);
            if (distSq <= REACH_DISTANCE_SQ) {
                navigating = false;
                startMining(level, npc);
            }
            return;
        }

        // Look at block
        npc.getLookControl().setLookAt(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5);

        // Swing arm for visual feedback
        if (breakProgress % 5 == 0) {
            npc.swing(InteractionHand.MAIN_HAND);
        }

        breakProgress++;
        level.destroyBlockProgress(npc.getId(), targetPos, (int) ((float) breakProgress / breakTime * 9));

        if (breakProgress >= breakTime) {
            // Break the block
            level.destroyBlock(targetPos, true, npc);
            doStop(level, npc, gameTime);
        }
    }

    @Override
    protected boolean shouldKeepRunning(PocNpc npc) {
        return targetPos != null && npc.isAlive();
    }

    @Override
    protected void stop(ServerLevel level, PocNpc npc, long gameTime) {
        if (targetPos != null) {
            level.destroyBlockProgress(npc.getId(), targetPos, -1);
        }
        targetPos = null;
        breakProgress = 0;
    }

    private void startMining(ServerLevel level, PocNpc npc) {
        // Equip best tool for this block
        equipBestTool(npc, level.getBlockState(targetPos));

        // Calculate break time
        BlockState state = level.getBlockState(targetPos);
        float destroySpeed = state.getDestroySpeed(level, targetPos);
        if (destroySpeed < 0) {
            // Unbreakable (bedrock, etc.)
            doStop(level, npc, level.getGameTime());
            return;
        }

        ItemStack tool = npc.getMainHandItem();
        float miningSpeed = tool.getDestroySpeed(state);
        if (miningSpeed <= 1.0f) miningSpeed = 1.0f;

        // Simplified break time calculation matching vanilla formula
        boolean correctTool = !state.requiresCorrectToolForDrops() || tool.isCorrectToolForDrops(state);
        float speedFactor = miningSpeed;
        if (!correctTool) speedFactor = 1.0f;

        breakTime = (int) Math.ceil((destroySpeed * (correctTool ? 1.5f : 5.0f)) / speedFactor * 20);
        if (breakTime < 1) breakTime = 1;
        breakProgress = 0;
    }

    private void equipBestTool(PocNpc npc, BlockState state) {
        var lookup = npc.getInventoryModel().getBestToolFor(state);
        if (lookup != null && lookup.inventorySlot() >= 0) {
            // Swap inventory tool to main hand
            ItemStack current = npc.getMainHandItem();
            npc.setItemInHand(InteractionHand.MAIN_HAND, lookup.stack());
            npc.getInventory().setItem(lookup.inventorySlot(), current);
        }
    }
}
