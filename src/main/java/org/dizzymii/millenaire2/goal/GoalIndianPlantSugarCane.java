package org.dizzymii.millenaire2.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Blocks;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.VillagerActionRuntime;
import org.dizzymii.millenaire2.entity.action.VillagerActions;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.Point;

/**
 * Indian villager plants sugar cane on dirt/sand blocks adjacent to water.
 */
public class GoalIndianPlantSugarCane extends Goal {

    { this.tags.add(TAG_AGRICULTURE); }

    @Override
    public GoalInformation getDestination(MillVillager v) {
        BlockPos spot = findPlantSpot(v);
        if (spot != null) {
            return new GoalInformation(new Point(spot), 2);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        if (resolvePendingAction(v)) {
            return true;
        }
        InvItem cane = InvItem.get("sugarcane");
        if (cane == null || v.countInv(cane) <= 0) {
            return true;
        }
        if (v.getSelectedInventoryItem().getItem() != cane.getItem()) {
            GoalActionSupport.ActionProgress equipProgress = GoalActionSupport.advanceAction(v, "equip_sugarcane",
                    VillagerActions.equip(cane.key));
            if (equipProgress == GoalActionSupport.ActionProgress.RUNNING) {
                return false;
            }
            if (equipProgress == GoalActionSupport.ActionProgress.FAILED) {
                return true;
            }
        }
        BlockPos spot = findNearbyPlantSpot(v);
        if (spot == null) {
            return true;
        }
        return switch (GoalActionSupport.advanceAction(v, "plant_sugarcane_" + spot.asLong(),
                VillagerActions.useBlock(spot.below(), Direction.UP, InteractionHand.MAIN_HAND))) {
            case RUNNING -> false;
            case SUCCESS, FAILED -> true;
        };
    }

    @Override
    public int actionDuration(MillVillager v) { return GoalActionSupport.runtimeBackedDuration(v, 20); }

    private boolean isValidPlantSpot(MillVillager v, BlockPos pos) {
        if (!v.level().getBlockState(pos).isAir()) return false;
        BlockPos below = pos.below();
        if (!v.level().getBlockState(below).is(BlockTags.DIRT) && !v.level().getBlockState(below).is(Blocks.SAND)) {
            return false;
        }
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (v.level().getBlockState(below.relative(dir)).is(Blocks.WATER)) {
                return true;
            }
        }
        return false;
    }

    private BlockPos findPlantSpot(MillVillager v) {
        BlockPos center = v.blockPosition();
        for (int dx = -10; dx <= 10; dx += 2) {
            for (int dz = -10; dz <= 10; dz += 2) {
                BlockPos check = center.offset(dx, 0, dz);
                if (isValidPlantSpot(v, check)) {
                    return check;
                }
            }
        }
        return null;
    }

    private BlockPos findNearbyPlantSpot(MillVillager v) {
        BlockPos pos = v.blockPosition();
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                BlockPos check = pos.offset(dx, 0, dz);
                if (isValidPlantSpot(v, check)) {
                    return check;
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
        if ("equip_sugarcane".equals(actionKey)) {
            runtime.reset(villager);
            return false;
        }
        if (actionKey.startsWith("plant_sugarcane_")) {
            runtime.reset(villager);
            return true;
        }
        return false;
    }
}
