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
 * Mayan villager plants cocoa beans on jungle log blocks.
 */
public class GoalPlantCacao extends Goal {

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
        InvItem cocoaBeans = InvItem.get("dye_brown");
        if (cocoaBeans == null || v.countInv(cocoaBeans) <= 0) {
            return true;
        }
        if (v.getSelectedInventoryItem().getItem() != cocoaBeans.getItem()) {
            GoalActionSupport.ActionProgress equipProgress = GoalActionSupport.advanceAction(v, "equip_cacao_beans",
                    VillagerActions.equip(cocoaBeans.key));
            if (equipProgress == GoalActionSupport.ActionProgress.RUNNING) {
                return false;
            }
            if (equipProgress == GoalActionSupport.ActionProgress.FAILED) {
                return true;
            }
        }
        PlantTarget target = findNearbyPlantTarget(v);
        if (target == null) {
            return true;
        }
        return switch (GoalActionSupport.advanceAction(v, "plant_cacao_" + target.spot.asLong(),
                VillagerActions.useBlock(target.supportPos, target.face, InteractionHand.MAIN_HAND))) {
            case RUNNING -> false;
            case SUCCESS, FAILED -> true;
        };
    }

    @Override
    public int actionDuration(MillVillager v) { return GoalActionSupport.runtimeBackedDuration(v, 20); }

    private boolean hasAdjacentJungleLog(MillVillager v, BlockPos pos) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (v.level().getBlockState(pos.relative(dir)).is(Blocks.JUNGLE_LOG)) {
                return true;
            }
        }
        return false;
    }

    private BlockPos findPlantSpot(MillVillager v) {
        BlockPos center = v.blockPosition();
        for (int dx = -10; dx <= 10; dx += 2) {
            for (int dy = 0; dy <= 5; dy++) {
                for (int dz = -10; dz <= 10; dz += 2) {
                    BlockPos check = center.offset(dx, dy, dz);
                    if (v.level().getBlockState(check).isAir() && hasAdjacentJungleLog(v, check)) {
                        return check;
                    }
                }
            }
        }
        return null;
    }

    private PlantTarget findNearbyPlantTarget(MillVillager v) {
        BlockPos pos = v.blockPosition();
        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -1; dy <= 3; dy++) {
                for (int dz = -3; dz <= 3; dz++) {
                    PlantTarget target = toPlantTarget(v, pos.offset(dx, dy, dz));
                    if (target != null) {
                        return target;
                    }
                }
            }
        }
        return null;
    }

    private PlantTarget toPlantTarget(MillVillager v, BlockPos spot) {
        if (!v.level().getBlockState(spot).isAir()) {
            return null;
        }
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos support = spot.relative(dir);
            if (v.level().getBlockState(support).is(Blocks.JUNGLE_LOG)) {
                return new PlantTarget(spot, support, dir.getOpposite());
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
        if ("equip_cacao_beans".equals(actionKey)) {
            runtime.reset(villager);
            return false;
        }
        if (actionKey.startsWith("plant_cacao_")) {
            runtime.reset(villager);
            return true;
        }
        return false;
    }

    private static final class PlantTarget {
        private final BlockPos spot;
        private final BlockPos supportPos;
        private final Direction face;

        private PlantTarget(BlockPos spot, BlockPos supportPos, Direction face) {
            this.spot = spot;
            this.supportPos = supportPos;
            this.face = face;
        }
    }
}
