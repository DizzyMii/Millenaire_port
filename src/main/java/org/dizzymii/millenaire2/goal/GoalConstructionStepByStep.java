package org.dizzymii.millenaire2.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.VillagerActionRuntime;
import org.dizzymii.millenaire2.entity.action.VillagerActions;
import org.dizzymii.millenaire2.buildingplan.BuildingBlock;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.village.ConstructionIP;

/**
 * Villager walks to a construction site and places blocks step-by-step.
 * Delegates actual block placement to the Building's ConstructionIP.
 */
public class GoalConstructionStepByStep extends Goal {

    { this.tags.add(TAG_CONSTRUCTION); }

    @Override
    public GoalInformation getDestination(MillVillager villager) throws Exception {
        // Find a building under construction that this villager can work on
        Building building = findConstructionSite(villager);
        if (building == null) return null;

        Point buildPos = building.getPos();
        if (buildPos == null) return null;

        return new GoalInformation(buildPos, building, 5);
    }

    @Override
    public boolean performAction(MillVillager villager) throws Exception {
        GoalInformation info = villager.getGoalInformation();
        Building building = info != null ? info.targetBuilding : null;
        if (building == null) return true; // No building, done

        ConstructionIP cip = building.currentConstruction;
        if (cip == null || cip.isComplete()) return true; // Nothing to build

        BuildingBlock nextBlock = cip.peekNextBlock();
        BlockPos origin = cip.location != null && cip.location.pos != null ? cip.location.pos.toBlockPos() : null;
        if (nextBlock == null || origin == null || nextBlock.blockState == null) {
            return finishConstructionStep(building, cip);
        }

        BlockPos targetPos = nextBlock.getBlockPos(origin, cip.orientation);
        BlockState targetState = BuildingBlock.rotateBlockState(nextBlock.blockState, cip.orientation);
        if (villager.level().getBlockState(targetPos).equals(targetState)) {
            return finishConstructionStep(building, cip);
        }

        return switch (advanceAction(villager, "construction_" + targetPos.asLong() + "_" + cip.nbBlocksDone,
                VillagerActions.placeBlock(targetPos, targetState, false))) {
            case RUNNING -> false;
            case SUCCESS -> finishConstructionStep(building, cip);
            case FAILED -> false;
        };
    }

    @Override
    public int actionDuration(MillVillager villager) {
        VillagerActionRuntime runtime = villager.getActionRuntime();
        return runtime.hasAction() || runtime.getLastResult().status() != VillagerActionRuntime.Status.IDLE ? 1 : 15;
    }

    private boolean finishConstructionStep(Building building, ConstructionIP cip) {
        if (cip.markNextBlockPlaced() && building.mw != null) {
            building.mw.setDirty();
        }
        if (cip.isComplete()) {
            building.currentConstruction = null;
            if (building.mw != null) building.mw.setDirty();
            return true;
        }
        return false;
    }

    private ActionProgress advanceAction(MillVillager villager, String actionKey, VillagerActionRuntime.Action action) {
        VillagerActionRuntime runtime = villager.getActionRuntime();
        if (runtime.hasAction()) {
            return ActionProgress.RUNNING;
        }
        VillagerActionRuntime.Result lastResult = runtime.getLastResult();
        if (lastResult.status() == VillagerActionRuntime.Status.SUCCESS) {
            if (actionKey.equals(runtime.getLastCompletedActionKey())) {
                runtime.reset(villager);
                villager.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
                return ActionProgress.SUCCESS;
            }
            runtime.reset(villager);
        }
        if (lastResult.status() == VillagerActionRuntime.Status.FAILED) {
            if (actionKey.equals(runtime.getLastCompletedActionKey())) {
                runtime.reset(villager);
                return ActionProgress.FAILED;
            }
            runtime.reset(villager);
        }
        runtime.start(actionKey, action, villager);
        return ActionProgress.RUNNING;
    }

    private enum ActionProgress {
        RUNNING,
        SUCCESS,
        FAILED
    }

    /**
     * Find a building under construction that this villager should work on.
     * Prioritises the villager's home building, then the town hall.
     */
    private Building findConstructionSite(MillVillager villager) {
        Building home = villager.getHomeBuilding();
        if (home != null && home.isUnderConstruction()) return home;

        Building th = villager.getTownHallBuilding();
        if (th != null && th.isUnderConstruction()) return th;

        return null;
    }
}
