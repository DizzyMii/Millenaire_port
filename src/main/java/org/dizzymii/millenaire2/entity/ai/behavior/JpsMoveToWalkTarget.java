package org.dizzymii.millenaire2.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import org.dizzymii.millenaire2.entity.MillVillager;

import java.util.List;

/**
 * A custom replacement for SmartBrainLib's MoveToWalkTarget that routes
 * pathfinding through Millénaire's custom JPS navigation system instead of vanilla navigation.
 */
public class JpsMoveToWalkTarget extends ExtendedBehaviour<MillVillager> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_PRESENT)
            );

    private BlockPos lastTargetPos;

    public JpsMoveToWalkTarget() {
        runFor(entity -> entity.getRandom().nextInt(100) + 150);
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, MillVillager entity) {
        WalkTarget walkTarget = entity.getBrain().getMemory(MemoryModuleType.WALK_TARGET).orElse(null);
        if (walkTarget != null && !hasReachedTarget(entity, walkTarget)) {
            this.lastTargetPos = walkTarget.getTarget().currentBlockPosition();
            return true;
        }

        entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        return false;
    }

    @Override
    protected boolean shouldKeepRunning(MillVillager entity) {
        if (this.lastTargetPos == null) return false;
        
        // Stop if the JPS navigator has finished
        if (!entity.getJpsNavigator().hasPath() && !entity.getJpsNavigator().isPathPending()) {
            return false;
        }

        WalkTarget walkTarget = entity.getBrain().getMemory(MemoryModuleType.WALK_TARGET).orElse(null);
        return walkTarget != null && !hasReachedTarget(entity, walkTarget);
    }

    @Override
    protected void start(ServerLevel level, MillVillager entity, long gameTime) {
        WalkTarget walkTarget = entity.getBrain().getMemory(MemoryModuleType.WALK_TARGET).orElse(null);
        if (walkTarget != null) {
            BlockPos targetPos = walkTarget.getTarget().currentBlockPosition();
            entity.getJpsNavigator().navigateTo(targetPos);
            this.lastTargetPos = targetPos;
        }
    }

    @Override
    protected void tick(ServerLevel level, MillVillager entity, long gameTime) {
        WalkTarget walkTarget = entity.getBrain().getMemory(MemoryModuleType.WALK_TARGET).orElse(null);
        if (walkTarget != null && this.lastTargetPos != null) {
            BlockPos currentPos = walkTarget.getTarget().currentBlockPosition();
            // Recalculate if target moved significantly
            if (currentPos.distSqr(this.lastTargetPos) > 4 && !hasReachedTarget(entity, walkTarget)) {
                this.lastTargetPos = currentPos;
                entity.getJpsNavigator().navigateTo(currentPos);
            }
        }
    }

    @Override
    protected void stop(ServerLevel level, MillVillager entity, long gameTime) {
        entity.getJpsNavigator().clearPath();
        // Fallback safety to stop vanilla nav if it was running
        entity.getNavigation().stop();

        entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }

    protected boolean hasReachedTarget(MillVillager entity, WalkTarget target) {
        return target.getTarget().currentBlockPosition().distManhattan(entity.blockPosition()) <= target.getCloseEnoughDist();
    }
}
