package org.dizzymii.sblpoc.behaviour.survival;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.PocNpc;
import org.dizzymii.sblpoc.ai.world.BlockCategory;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Finds mature crops, harvests them, and replants seeds.
 */
public class HarvestCropBehaviour extends ExtendedBehaviour<PocNpc> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT)
            );

    private static final double REACH_DISTANCE_SQ = 4.5 * 4.5;

    private final Deque<BlockPos> cropQueue = new ArrayDeque<>();
    @Nullable private BlockPos currentCrop;
    private boolean navigating = false;
    private int cropsHarvested = 0;
    private static final int MAX_CROPS = 30;

    public HarvestCropBehaviour() {
        noTimeout();
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PocNpc npc) {
        BlockPos farmPos = npc.getSpatialMemory().findNearest(BlockCategory.CROP, npc.blockPosition());
        if (farmPos != null) {
            scanForMatureCrops(level, farmPos);
            return !cropQueue.isEmpty();
        }
        // Fallback: scan nearby
        scanForMatureCrops(level, npc.blockPosition());
        return !cropQueue.isEmpty();
    }

    @Override
    protected void start(ServerLevel level, PocNpc npc, long gameTime) {
        cropsHarvested = 0;
        advanceToNextCrop(level, npc);
    }

    @Override
    protected void tick(ServerLevel level, PocNpc npc, long gameTime) {
        if (currentCrop == null) {
            if (cropQueue.isEmpty()) {
                doStop(level, npc, gameTime);
                return;
            }
            advanceToNextCrop(level, npc);
            return;
        }

        if (navigating) {
            double distSq = npc.blockPosition().distSqr(currentCrop);
            if (distSq <= REACH_DISTANCE_SQ) {
                navigating = false;
            } else {
                return;
            }
        }

        // Harvest the crop
        BlockState state = level.getBlockState(currentCrop);
        if (state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state)) {
            level.destroyBlock(currentCrop, true, npc);
            // Replant
            level.setBlock(currentCrop, crop.defaultBlockState(), 3);
            npc.swing(InteractionHand.MAIN_HAND);
            cropsHarvested++;
            npc.getInventoryModel().markDirty();
        }

        currentCrop = null;
    }

    @Override
    protected boolean shouldKeepRunning(PocNpc npc) {
        return (currentCrop != null || !cropQueue.isEmpty()) && cropsHarvested < MAX_CROPS;
    }

    @Override
    protected void stop(ServerLevel level, PocNpc npc, long gameTime) {
        cropQueue.clear();
        currentCrop = null;
    }

    private void advanceToNextCrop(ServerLevel level, PocNpc npc) {
        currentCrop = cropQueue.poll();
        if (currentCrop == null) return;

        double distSq = npc.blockPosition().distSqr(currentCrop);
        if (distSq > REACH_DISTANCE_SQ) {
            BrainUtils.setMemory(npc, MemoryModuleType.WALK_TARGET,
                    new WalkTarget(Vec3.atCenterOf(currentCrop), 1.0f, 1));
            navigating = true;
        } else {
            navigating = false;
        }
    }

    private void scanForMatureCrops(ServerLevel level, BlockPos center) {
        cropQueue.clear();
        int radius = 12;
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -radius; z <= radius; z++) {
                    mpos.set(center.getX() + x, center.getY() + y, center.getZ() + z);
                    BlockState state = level.getBlockState(mpos);
                    if (state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state)) {
                        cropQueue.add(mpos.immutable());
                    }
                }
            }
        }
    }
}
