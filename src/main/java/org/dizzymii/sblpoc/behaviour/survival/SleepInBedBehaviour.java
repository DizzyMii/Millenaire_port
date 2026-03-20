package org.dizzymii.sblpoc.behaviour.survival;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.PocNpc;
import org.dizzymii.sblpoc.ai.world.BlockCategory;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Navigates to a known bed and sleeps through the night.
 * Simulates sleeping by making the NPC stand still at the bed until daytime.
 */
public class SleepInBedBehaviour extends ExtendedBehaviour<PocNpc> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT)
            );

    private static final double REACH_DISTANCE_SQ = 3.0 * 3.0;

    @Nullable private BlockPos bedPos;
    private boolean navigating = false;
    private boolean sleeping = false;

    public SleepInBedBehaviour() {
        noTimeout();
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PocNpc npc) {
        if (level.isDay()) return false;

        bedPos = npc.getSpatialMemory().findNearest(BlockCategory.BED, npc.blockPosition());
        if (bedPos == null) {
            bedPos = findNearbyBed(level, npc.blockPosition(), 16);
        }
        return bedPos != null;
    }

    @Override
    protected void start(ServerLevel level, PocNpc npc, long gameTime) {
        sleeping = false;
        navigating = false;

        if (bedPos == null) return;

        double distSq = npc.blockPosition().distSqr(bedPos);
        if (distSq > REACH_DISTANCE_SQ) {
            BrainUtils.setMemory(npc, MemoryModuleType.WALK_TARGET,
                    new WalkTarget(Vec3.atCenterOf(bedPos), 1.0f, 1));
            navigating = true;
        } else {
            startSleeping(npc);
        }
    }

    @Override
    protected void tick(ServerLevel level, PocNpc npc, long gameTime) {
        if (bedPos == null) {
            doStop(level, npc, gameTime);
            return;
        }

        if (navigating) {
            double distSq = npc.blockPosition().distSqr(bedPos);
            if (distSq <= REACH_DISTANCE_SQ) {
                navigating = false;
                startSleeping(npc);
            }
            return;
        }

        if (sleeping) {
            // Stop sleeping when day arrives
            if (level.isDay()) {
                sleeping = false;
                npc.setPose(net.minecraft.world.entity.Pose.STANDING);
                doStop(level, npc, gameTime);
            }
        }
    }

    @Override
    protected boolean shouldKeepRunning(PocNpc npc) {
        return (navigating || sleeping) && npc.isAlive();
    }

    @Override
    protected void stop(ServerLevel level, PocNpc npc, long gameTime) {
        npc.setPose(net.minecraft.world.entity.Pose.STANDING);
        bedPos = null;
        sleeping = false;
    }

    private void startSleeping(PocNpc npc) {
        sleeping = true;
        npc.setPose(net.minecraft.world.entity.Pose.SLEEPING);
        npc.getNavigation().stop();
    }

    @Nullable
    private static BlockPos findNearbyBed(ServerLevel level, BlockPos center, int radius) {
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
        double bestDist = Double.MAX_VALUE;
        BlockPos best = null;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -4; y <= 4; y++) {
                for (int z = -radius; z <= radius; z++) {
                    mpos.set(center.getX() + x, center.getY() + y, center.getZ() + z);
                    if (level.getBlockState(mpos).is(BlockTags.BEDS)) {
                        double dist = center.distSqr(mpos);
                        if (dist < bestDist) {
                            bestDist = dist;
                            best = mpos.immutable();
                        }
                    }
                }
            }
        }
        return best;
    }
}
