package org.dizzymii.millenaire2.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.PickaxeItem;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.VillagerActionRuntime;
import org.dizzymii.millenaire2.entity.action.VillagerActions;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.Point;

/**
 * Miner villager finds and mines ore/stone blocks underground.
 */
public class GoalMinerMineResource extends Goal {

    private static final int SEARCH_RADIUS = 16;

    @Override
    public GoalInformation getDestination(MillVillager v) {
        BlockPos ore = findOre(v);
        if (ore != null) {
            return new GoalInformation(new Point(ore), 2);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        if (resolvePendingAction(v)) {
            return true;
        }
        InvItem pickaxe = firstCarriedPickaxe(v);
        if (pickaxe == null || v.countInv(pickaxe) <= 0) {
            return true;
        }
        if (v.getSelectedInventoryItem().getItem() != pickaxe.getItem()) {
            GoalActionSupport.ActionProgress equipProgress = GoalActionSupport.advanceAction(v, "equip_pickaxe_" + pickaxe.key,
                    VillagerActions.equip(pickaxe.key));
            if (equipProgress == GoalActionSupport.ActionProgress.RUNNING) {
                return false;
            }
            if (equipProgress == GoalActionSupport.ActionProgress.FAILED) {
                return true;
            }
        }
        BlockPos target = findNearbyOre(v);
        if (target == null) {
            return true;
        }
        return switch (GoalActionSupport.advanceAction(v, "mine_resource_" + target.asLong(),
                VillagerActions.breakBlockAsPlayer(target))) {
            case RUNNING -> false;
            case SUCCESS, FAILED -> true;
        };
    }

    @Override
    public int actionDuration(MillVillager v) { return GoalActionSupport.runtimeBackedDuration(v, 40); }

    private BlockPos findOre(MillVillager v) {
        BlockPos center = v.blockPosition();
        for (int dx = -SEARCH_RADIUS; dx <= SEARCH_RADIUS; dx += 3) {
            for (int dy = -8; dy <= 0; dy += 2) {
                for (int dz = -SEARCH_RADIUS; dz <= SEARCH_RADIUS; dz += 3) {
                    BlockPos check = center.offset(dx, dy, dz);
                    if (v.level().getBlockState(check).is(BlockTags.STONE_ORE_REPLACEABLES)) {
                        return check;
                    }
                }
            }
        }
        return null;
    }

    private BlockPos findNearbyOre(MillVillager v) {
        BlockPos pos = v.blockPosition();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    BlockPos check = pos.offset(dx, dy, dz);
                    if (v.level().getBlockState(check).is(BlockTags.STONE_ORE_REPLACEABLES)) {
                        return check;
                    }
                }
            }
        }
        return null;
    }

    private InvItem firstCarriedPickaxe(MillVillager villager) {
        return GoalActionSupport.firstInventoryItemMatching(villager, stack -> stack.getItem() instanceof PickaxeItem);
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
        if (actionKey.startsWith("equip_pickaxe_")) {
            runtime.reset(villager);
            return false;
        }
        if (actionKey.startsWith("mine_resource_")) {
            if (result.status() == VillagerActionRuntime.Status.SUCCESS) {
                BlockPos target = GoalActionSupport.parseActionPos(actionKey, "mine_resource_");
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
