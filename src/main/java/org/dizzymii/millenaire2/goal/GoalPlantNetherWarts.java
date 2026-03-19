package org.dizzymii.millenaire2.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Blocks;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.VillagerActionRuntime;
import org.dizzymii.millenaire2.entity.action.VillagerActions;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.Point;

/**
 * Villager plants nether wart on soul sand blocks.
 */
public class GoalPlantNetherWarts extends Goal {

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
        InvItem wart = InvItem.get("netherwart");
        if (wart == null || v.countInv(wart) <= 0) {
            return true;
        }
        if (v.getSelectedInventoryItem().getItem() != wart.getItem()) {
            GoalActionSupport.ActionProgress equipProgress = GoalActionSupport.advanceAction(v, "equip_netherwart",
                    VillagerActions.equip(wart.key));
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
        BlockPos soil = spot.below();
        return switch (GoalActionSupport.advanceAction(v, "plant_netherwart_" + spot.asLong(),
                VillagerActions.useBlock(soil, Direction.UP, InteractionHand.MAIN_HAND))) {
            case RUNNING -> false;
            case SUCCESS, FAILED -> true;
        };
    }

    @Override
    public int actionDuration(MillVillager v) { return GoalActionSupport.runtimeBackedDuration(v, 20); }

    private BlockPos findPlantSpot(MillVillager v) {
        BlockPos center = v.blockPosition();
        for (int dx = -10; dx <= 10; dx += 2) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -10; dz <= 10; dz += 2) {
                    BlockPos check = center.offset(dx, dy, dz);
                    if (v.level().getBlockState(check).isAir()
                            && v.level().getBlockState(check.below()).is(Blocks.SOUL_SAND)) {
                        return check;
                    }
                }
            }
        }
        return null;
    }

    private BlockPos findNearbyPlantSpot(MillVillager v) {
        BlockPos pos = v.blockPosition();
        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -3; dz <= 3; dz++) {
                    BlockPos check = pos.offset(dx, dy, dz);
                    if (v.level().getBlockState(check).isAir()
                            && v.level().getBlockState(check.below()).is(Blocks.SOUL_SAND)) {
                        return check;
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
        if ("equip_netherwart".equals(actionKey)) {
            runtime.reset(villager);
            return false;
        }
        if (actionKey.startsWith("plant_netherwart_")) {
            runtime.reset(villager);
            return true;
        }
        return false;
    }
}
