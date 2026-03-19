package org.dizzymii.millenaire2.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.VillagerActionRuntime;
import org.dizzymii.millenaire2.entity.action.VillagerActions;
import org.dizzymii.millenaire2.util.Point;

/**
 * Indian villager harvests tall sugar cane (breaks the top blocks, leaving base).
 */
public class GoalIndianHarvestSugarCane extends Goal {

    { this.tags.add(TAG_AGRICULTURE); }

    @Override
    public GoalInformation getDestination(MillVillager v) {
        BlockPos cane = findTallCane(v);
        if (cane != null) {
            return new GoalInformation(new Point(cane), 2);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        if (resolvePendingAction(v)) {
            return true;
        }
        BlockPos target = findNearbyTopSegment(v);
        if (target == null) {
            return true;
        }
        return switch (GoalActionSupport.advanceAction(v, "harvest_sugarcane_" + target.asLong(),
                VillagerActions.breakBlock(target, true))) {
            case RUNNING -> false;
            case SUCCESS, FAILED -> true;
        };
    }

    @Override
    public int actionDuration(MillVillager v) { return GoalActionSupport.runtimeBackedDuration(v, 25); }

    private BlockPos findTallCane(MillVillager v) {
        BlockPos center = v.blockPosition();
        for (int dx = -12; dx <= 12; dx += 2) {
            for (int dz = -12; dz <= 12; dz += 2) {
                for (int dy = -2; dy <= 3; dy++) {
                    BlockPos check = center.offset(dx, dy, dz);
                    if (v.level().getBlockState(check).is(Blocks.SUGAR_CANE)
                            && v.level().getBlockState(check.above()).is(Blocks.SUGAR_CANE)) {
                        return check;
                    }
                }
            }
        }
        return null;
    }

    private BlockPos findNearbyTopSegment(MillVillager v) {
        BlockPos pos = v.blockPosition();
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                BlockPos base = pos.offset(dx, 0, dz);
                for (int dy = 0; dy <= 3; dy++) {
                    BlockPos check = base.above(dy);
                    if (v.level().getBlockState(check).is(Blocks.SUGAR_CANE)
                            && v.level().getBlockState(check.above()).is(Blocks.SUGAR_CANE)) {
                        return check.above();
                    }
                }
            }
        }
        return null;
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
        if (actionKey.startsWith("harvest_sugarcane_")) {
            if (result.status() == VillagerActionRuntime.Status.SUCCESS) {
                BlockPos target = GoalActionSupport.parseActionPos(actionKey, "harvest_sugarcane_");
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
