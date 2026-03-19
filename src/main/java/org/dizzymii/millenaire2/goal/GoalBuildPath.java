package org.dizzymii.millenaire2.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.VillagerActionRuntime;
import org.dizzymii.millenaire2.entity.action.VillagerActions;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.Point;

/**
 * Villager places path blocks between village buildings.
 */
public class GoalBuildPath extends Goal {

    { this.tags.add(TAG_CONSTRUCTION); }

    @Override
    public GoalInformation getDestination(MillVillager v) {
        Point th = v.townHallPoint;
        if (th != null) {
            return new GoalInformation(th, 5);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        if (resolvePendingAction(v)) {
            return true;
        }
        InvItem gravel = InvItem.get("gravel");
        if (gravel == null || v.countInv(gravel) <= 0) {
            return true;
        }
        if (v.getSelectedInventoryItem().getItem() != gravel.getItem()) {
            GoalActionSupport.ActionProgress equipProgress = GoalActionSupport.advanceAction(v, "equip_path_gravel",
                    VillagerActions.equip(gravel.key));
            if (equipProgress == GoalActionSupport.ActionProgress.RUNNING) {
                return false;
            }
            if (equipProgress == GoalActionSupport.ActionProgress.FAILED) {
                return true;
            }
        }
        BlockPos pos = v.blockPosition().below();
        BlockState current = v.level().getBlockState(pos);
        if (!current.canBeReplaced()) {
            return true;
        }
        return switch (GoalActionSupport.advanceAction(v, "build_path_place_" + pos.asLong(),
                VillagerActions.placeBlock(pos, Blocks.GRAVEL.defaultBlockState(), true))) {
            case RUNNING -> false;
            case SUCCESS, FAILED -> true;
        };
    }

    @Override
    public int actionDuration(MillVillager v) { return GoalActionSupport.runtimeBackedDuration(v, 20); }

    private boolean resolvePendingAction(MillVillager villager) {
        VillagerActionRuntime runtime = villager.getActionRuntime();
        if (runtime.hasAction()) {
            return false;
        }
        String actionKey = runtime.getLastCompletedActionKey();
        VillagerActionRuntime.Result result = runtime.getLastResult();
        if (actionKey == null || result.status() == VillagerActionRuntime.Status.IDLE) {
            return false;
        }
        if ("equip_path_gravel".equals(actionKey)) {
            runtime.reset(villager);
            return false;
        }
        if (actionKey.startsWith("build_path_place_")) {
            runtime.reset(villager);
            return true;
        }
        return false;
    }
}
