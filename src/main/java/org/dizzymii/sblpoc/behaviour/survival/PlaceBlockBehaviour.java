package org.dizzymii.sblpoc.behaviour.survival;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.PocNpc;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Places a block from the NPC's inventory at a target position.
 * Navigates to within reach, then places the block.
 */
public class PlaceBlockBehaviour extends ExtendedBehaviour<PocNpc> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT)
            );

    private static final double REACH_DISTANCE_SQ = 4.5 * 4.5;

    @Nullable private BlockPos targetPos;
    @Nullable private Item blockToPlace;
    private boolean navigating = false;
    private boolean placed = false;

    public PlaceBlockBehaviour() {
        noTimeout();
    }

    public PlaceBlockBehaviour target(BlockPos pos, Item block) {
        this.targetPos = pos;
        this.blockToPlace = block;
        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PocNpc npc) {
        if (targetPos == null || blockToPlace == null) return false;
        if (!(blockToPlace instanceof BlockItem)) return false;
        return npc.getInventoryModel().hasItem(blockToPlace);
    }

    @Override
    protected void start(ServerLevel level, PocNpc npc, long gameTime) {
        placed = false;
        navigating = false;

        double distSq = npc.blockPosition().distSqr(targetPos);
        if (distSq > REACH_DISTANCE_SQ) {
            BrainUtils.setMemory(npc, MemoryModuleType.WALK_TARGET,
                    new WalkTarget(Vec3.atCenterOf(targetPos), 1.0f, 2));
            navigating = true;
        }
    }

    @Override
    protected void tick(ServerLevel level, PocNpc npc, long gameTime) {
        if (placed || targetPos == null || blockToPlace == null) {
            doStop(level, npc, gameTime);
            return;
        }

        if (navigating) {
            double distSq = npc.blockPosition().distSqr(targetPos);
            if (distSq <= REACH_DISTANCE_SQ) {
                navigating = false;
            } else {
                return;
            }
        }

        // Place the block
        BlockState existing = level.getBlockState(targetPos);
        if (!existing.canBeReplaced()) {
            doStop(level, npc, gameTime);
            return;
        }

        // Find and consume the block from inventory
        int slot = npc.getInventoryModel().findSlot(stack -> stack.getItem() == blockToPlace);
        if (slot < 0) {
            doStop(level, npc, gameTime);
            return;
        }

        BlockItem blockItem = (BlockItem) blockToPlace;
        Block block = blockItem.getBlock();
        level.setBlock(targetPos, block.defaultBlockState(), 3);
        npc.getInventory().removeItem(slot, 1);
        npc.swing(InteractionHand.MAIN_HAND);
        placed = true;

        doStop(level, npc, gameTime);
    }

    @Override
    protected boolean shouldKeepRunning(PocNpc npc) {
        return !placed && targetPos != null;
    }

    @Override
    protected void stop(ServerLevel level, PocNpc npc, long gameTime) {
        targetPos = null;
        blockToPlace = null;
    }
}
