package org.dizzymii.sblpoc.behaviour.survival;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.PocNpc;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Explores unvisited areas by picking a direction the NPC hasn't been to
 * and walking toward it. The BlockScanSensor handles populating SpatialMemory
 * as the NPC moves.
 */
public class ExploreBehaviour extends ExtendedBehaviour<PocNpc> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT)
            );

    private static final int EXPLORE_DISTANCE = 48;
    private static final int MAX_EXPLORE_TICKS = 600; // 30 seconds max

    @Nullable private BlockPos targetPos;
    private int ticksRunning = 0;
    private boolean arrived = false;

    public ExploreBehaviour() {
        noTimeout();
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PocNpc npc) {
        return true; // Can always explore
    }

    @Override
    protected void start(ServerLevel level, PocNpc npc, long gameTime) {
        arrived = false;
        ticksRunning = 0;

        // Pick a random direction to explore
        double angle = npc.getRandom().nextDouble() * Math.PI * 2;
        int dx = (int) (Math.cos(angle) * EXPLORE_DISTANCE);
        int dz = (int) (Math.sin(angle) * EXPLORE_DISTANCE);

        BlockPos base = npc.blockPosition().offset(dx, 0, dz);
        // Find a valid Y level
        targetPos = findWalkablePos(level, base);

        if (targetPos != null) {
            BrainUtils.setMemory(npc, MemoryModuleType.WALK_TARGET,
                    new WalkTarget(Vec3.atCenterOf(targetPos), 0.8f, 4));
        }
    }

    @Override
    protected void tick(ServerLevel level, PocNpc npc, long gameTime) {
        ticksRunning++;

        if (targetPos == null || ticksRunning > MAX_EXPLORE_TICKS) {
            doStop(level, npc, gameTime);
            return;
        }

        double distSq = npc.blockPosition().distSqr(targetPos);
        if (distSq < 25) { // Within 5 blocks
            arrived = true;
            doStop(level, npc, gameTime);
            return;
        }

        // Re-set walk target if NPC seems stuck (hasn't moved in 5 seconds)
        if (ticksRunning % 100 == 0) {
            BrainUtils.setMemory(npc, MemoryModuleType.WALK_TARGET,
                    new WalkTarget(Vec3.atCenterOf(targetPos), 0.8f, 4));
        }
    }

    @Override
    protected boolean shouldKeepRunning(PocNpc npc) {
        return !arrived && ticksRunning < MAX_EXPLORE_TICKS;
    }

    @Override
    protected void stop(ServerLevel level, PocNpc npc, long gameTime) {
        targetPos = null;
    }

    @Nullable
    private static BlockPos findWalkablePos(ServerLevel level, BlockPos base) {
        // Search vertically to find ground level
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos(base.getX(), 128, base.getZ());
        for (int y = 128; y > -60; y--) {
            mpos.setY(y);
            if (!level.getBlockState(mpos).isAir() && level.getBlockState(mpos.above()).isAir()
                    && level.getBlockState(mpos.above(2)).isAir()) {
                return mpos.above().immutable();
            }
        }
        return null;
    }
}
