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
import org.dizzymii.millenaire2.entity.ai.MillMemoryTypes;
import org.dizzymii.millenaire2.goal.Goal;
import org.dizzymii.millenaire2.goal.GoalInformation;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.village.Building;

import java.util.List;

/**
 * Behaviour: Walk to a building under construction and place blocks.
 * Wraps the logic from GoalConstructionStepByStep and GoalGetResourcesForBuild.
 */
public class MillConstructionBehaviour extends ExtendedBehaviour<MillVillager> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MillMemoryTypes.TOWNHALL_POS.get(), MemoryStatus.VALUE_PRESENT),
                    Pair.of(MillMemoryTypes.VILLAGE_UNDER_ATTACK.get(), MemoryStatus.VALUE_ABSENT)
            );

    private boolean hasStarted = false;

    public MillConstructionBehaviour() {
        runFor(entity -> entity.getRandom().nextIntBetweenInclusive(100, 300));
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, MillVillager villager) {
        if (villager.vtype == null) return false;
        // Only villagers with the construction tag should build
        if (villager.vtype.goals == null || villager.vtype.goals.isEmpty()) return false;
        boolean hasConstructionGoal = villager.vtype.goals.stream()
                .anyMatch(g -> g.equals("construction") || g.equals("getresourcesforbuild"));
        if (!hasConstructionGoal) return false;

        // Check if there's active construction in the village
        Building th = villager.getTownHallBuilding();
        return th != null && th.currentConstruction != null;
    }

    @Override
    protected void start(ServerLevel level, MillVillager villager, long gameTime) {
        hasStarted = false;
        // Delegate to original goal system for destination finding
        Goal constructionGoal = Goal.construction;
        if (constructionGoal == null) return;

        try {
            GoalInformation info = constructionGoal.getDestination(villager);
            if (info != null && info.hasTarget()) {
                BlockPos target = info.targetPoint.toBlockPos();
                villager.getBrain().setMemory(MillMemoryTypes.CONSTRUCTION_TARGET.get(), target);
                villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET,
                        new WalkTarget(target, 0.7f, 2));
                villager.getBrain().setMemory(MillMemoryTypes.ACTIVE_GOAL_KEY.get(), "construction");
                hasStarted = true;
            }
        } catch (Exception e) {
            MillLog.error(villager, "Error starting construction behaviour", e);
        }
    }

    @Override
    protected boolean shouldKeepRunning(MillVillager villager) {
        if (!hasStarted) return false;
        Building th = villager.getTownHallBuilding();
        return th != null && th.currentConstruction != null;
    }

    @Override
    protected void tick(ServerLevel level, MillVillager villager, long gameTime) {
        // Check if we're close enough to the construction target
        BlockPos target = villager.getBrain().getMemory(MillMemoryTypes.CONSTRUCTION_TARGET.get()).orElse(null);
        if (target == null) return;

        double dist = villager.distanceToSqr(target.getX() + 0.5, target.getY(), target.getZ() + 0.5);
        if (dist > 9.0) return; // Still walking

        // Perform one construction step via the original goal
        Goal constructionGoal = Goal.construction;
        if (constructionGoal == null) return;

        try {
            boolean finished = constructionGoal.performAction(villager);
            if (finished) {
                // Construction step done, find next block
                GoalInformation info = constructionGoal.getDestination(villager);
                if (info != null && info.hasTarget()) {
                    BlockPos nextTarget = info.targetPoint.toBlockPos();
                    villager.getBrain().setMemory(MillMemoryTypes.CONSTRUCTION_TARGET.get(), nextTarget);
                    villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET,
                            new WalkTarget(nextTarget, 0.7f, 2));
                } else {
                    villager.getBrain().eraseMemory(MillMemoryTypes.CONSTRUCTION_TARGET.get());
                    doStop(level, villager, gameTime);
                }
            }
        } catch (Exception e) {
            MillLog.error(villager, "Error in construction tick", e);
            doStop(level, villager, gameTime);
        }
    }

    @Override
    protected void stop(ServerLevel level, MillVillager villager, long gameTime) {
        villager.getBrain().eraseMemory(MillMemoryTypes.CONSTRUCTION_TARGET.get());
        villager.getBrain().eraseMemory(MillMemoryTypes.ACTIVE_GOAL_KEY.get());
    }
}
