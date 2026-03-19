package org.dizzymii.millenaire2.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.VillagerActionRuntime;
import org.dizzymii.millenaire2.entity.action.VillagerActions;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.Point;

/**
 * Lumberman plants saplings on suitable ground near the village.
 */
public class GoalLumbermanPlantSaplings extends Goal {

    private static final int SEARCH_RADIUS = 16;

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
        InvItem sapling = firstCarriedSapling(v);
        if (sapling == null || v.countInv(sapling) <= 0) {
            return true;
        }
        if (v.getSelectedInventoryItem().getItem() != sapling.getItem()) {
            GoalActionSupport.ActionProgress equipProgress = GoalActionSupport.advanceAction(v, "equip_sapling_" + sapling.key,
                    VillagerActions.equip(sapling.key));
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
        return switch (GoalActionSupport.advanceAction(v, "plant_sapling_" + spot.asLong(),
                VillagerActions.useBlock(spot.below(), Direction.UP, InteractionHand.MAIN_HAND))) {
            case RUNNING -> false;
            case SUCCESS, FAILED -> true;
        };
    }

    @Override
    public int actionDuration(MillVillager v) { return GoalActionSupport.runtimeBackedDuration(v, 20); }

    private BlockPos findPlantSpot(MillVillager v) {
        BlockPos center = v.blockPosition();
        for (int dx = -SEARCH_RADIUS; dx <= SEARCH_RADIUS; dx += 3) {
            for (int dz = -SEARCH_RADIUS; dz <= SEARCH_RADIUS; dz += 3) {
                BlockPos ground = center.offset(dx, -1, dz);
                BlockPos above = center.offset(dx, 0, dz);
                BlockState gs = v.level().getBlockState(ground);
                boolean isDirt = gs.is(Blocks.GRASS_BLOCK) || gs.is(Blocks.DIRT);
                if (isDirt && v.level().getBlockState(above).isAir()) {
                    return above;
                }
            }
        }
        return null;
    }

    private BlockPos findNearbyPlantSpot(MillVillager v) {
        BlockPos pos = v.blockPosition();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos ground = pos.offset(dx, -1, dz);
                BlockPos above = pos.offset(dx, 0, dz);
                BlockState groundState = v.level().getBlockState(ground);
                boolean isDirt = groundState.is(Blocks.GRASS_BLOCK) || groundState.is(Blocks.DIRT);
                if (isDirt && v.level().getBlockState(above).isAir()) {
                    return above;
                }
            }
        }
        return null;
    }

    private InvItem firstCarriedSapling(MillVillager villager) {
        return GoalActionSupport.firstInventoryItemMatching(villager,
                stack -> stack.getItem() instanceof BlockItem blockItem
                        && blockItem.getBlock().defaultBlockState().is(BlockTags.SAPLINGS));
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
        if (actionKey.startsWith("equip_sapling_")) {
            runtime.reset(villager);
            return false;
        }
        if (actionKey.startsWith("plant_sapling_")) {
            runtime.reset(villager);
            return true;
        }
        return false;
    }
}
