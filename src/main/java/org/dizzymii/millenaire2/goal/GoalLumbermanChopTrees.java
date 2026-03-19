package org.dizzymii.millenaire2.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.VillagerActionRuntime;
import org.dizzymii.millenaire2.entity.action.VillagerActions;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.Point;

/**
 * Lumberman searches for nearby trees, walks to them, and chops logs.
 */
public class GoalLumbermanChopTrees extends Goal {

    private static final int SEARCH_RADIUS = 24;

    { this.tags.add(TAG_AGRICULTURE); }

    @Override
    public GoalInformation getDestination(MillVillager v) {
        BlockPos tree = findNearestLog(v);
        if (tree != null) {
            return new GoalInformation(new Point(tree), 2);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        if (resolvePendingAction(v)) {
            return false;
        }
        InvItem axe = firstCarriedAxe(v);
        if (axe == null || v.countInv(axe) <= 0) {
            return true;
        }
        if (v.getSelectedInventoryItem().getItem() != axe.getItem()) {
            GoalActionSupport.ActionProgress equipProgress = GoalActionSupport.advanceAction(v, "equip_axe_" + axe.key,
                    VillagerActions.equip(axe.key));
            if (equipProgress == GoalActionSupport.ActionProgress.RUNNING) {
                return false;
            }
            if (equipProgress == GoalActionSupport.ActionProgress.FAILED) {
                return true;
            }
        }
        BlockPos target = findNearbyLog(v);
        if (target == null) {
            return true;
        }
        return switch (GoalActionSupport.advanceAction(v, "chop_tree_" + target.asLong(),
                VillagerActions.breakBlockAsPlayer(target))) {
            case RUNNING -> false;
            case SUCCESS, FAILED -> false;
        };
    }

    @Override
    public int actionDuration(MillVillager v) { return GoalActionSupport.runtimeBackedDuration(v, 15); }

    private BlockPos findNearestLog(MillVillager v) {
        BlockPos center = v.blockPosition();
        BlockPos nearest = null;
        double nearestDist = Double.MAX_VALUE;

        for (int dx = -SEARCH_RADIUS; dx <= SEARCH_RADIUS; dx += 2) {
            for (int dz = -SEARCH_RADIUS; dz <= SEARCH_RADIUS; dz += 2) {
                for (int dy = -2; dy <= 8; dy++) {
                    BlockPos check = center.offset(dx, dy, dz);
                    if (v.level().getBlockState(check).is(BlockTags.LOGS)) {
                        double dist = center.distSqr(check);
                        if (dist < nearestDist) {
                            nearestDist = dist;
                            nearest = check;
                        }
                        break; // Found log at this column, skip higher
                    }
                }
            }
        }
        return nearest;
    }

    private BlockPos findNearbyLog(MillVillager v) {
        BlockPos pos = v.blockPosition();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -1; dy <= 3; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    BlockPos check = pos.offset(dx, dy, dz);
                    if (v.level().getBlockState(check).is(BlockTags.LOGS)) {
                        return check;
                    }
                }
            }
        }
        return null;
    }

    private InvItem firstCarriedAxe(MillVillager villager) {
        return GoalActionSupport.firstInventoryItemMatching(villager, stack -> stack.getItem() instanceof AxeItem);
    }

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
        if (actionKey.startsWith("equip_axe_")) {
            runtime.reset(villager);
            return false;
        }
        if (actionKey.startsWith("chop_tree_")) {
            if (result.status() == VillagerActionRuntime.Status.SUCCESS) {
                BlockPos target = GoalActionSupport.parseActionPos(actionKey, "chop_tree_");
                if (target != null) {
                    GoalActionSupport.collectNearbyDrops(villager, target, 1.5);
                }
            }
            runtime.reset(villager);
            return true;
        }
        return false;
    }
}
