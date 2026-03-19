package org.dizzymii.millenaire2.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherWartBlock;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.VillagerActionRuntime;
import org.dizzymii.millenaire2.entity.action.VillagerActions;
import org.dizzymii.millenaire2.util.Point;

import java.util.List;

/**
 * Villager harvests mature nether wart blocks.
 */
public class GoalHarvestWarts extends Goal {

    { this.tags.add(TAG_AGRICULTURE); }

    @Override
    public GoalInformation getDestination(MillVillager v) {
        BlockPos wart = findMatureWart(v);
        if (wart != null) {
            return new GoalInformation(new Point(wart), 2);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        if (resolvePendingAction(v)) {
            return true;
        }
        BlockPos target = findNearbyMatureWart(v);
        if (target == null) {
            return true;
        }
        return switch (GoalActionSupport.advanceAction(v, "harvest_wart_" + target.asLong(),
                VillagerActions.breakBlock(target, true))) {
            case RUNNING -> false;
            case SUCCESS, FAILED -> true;
        };
    }

    @Override
    public int actionDuration(MillVillager v) { return GoalActionSupport.runtimeBackedDuration(v, 25); }

    private BlockPos findMatureWart(MillVillager v) {
        BlockPos center = v.blockPosition();
        for (int dx = -10; dx <= 10; dx += 2) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -10; dz <= 10; dz += 2) {
                    BlockPos check = center.offset(dx, dy, dz);
                    if (v.level().getBlockState(check).getBlock() == Blocks.NETHER_WART
                            && v.level().getBlockState(check).getValue(NetherWartBlock.AGE) >= 3) {
                        return check;
                    }
                }
            }
        }
        return null;
    }

    private BlockPos findNearbyMatureWart(MillVillager v) {
        BlockPos pos = v.blockPosition();
        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -3; dz <= 3; dz++) {
                    BlockPos check = pos.offset(dx, dy, dz);
                    if (v.level().getBlockState(check).getBlock() == Blocks.NETHER_WART
                            && v.level().getBlockState(check).getValue(NetherWartBlock.AGE) >= 3) {
                        return check;
                    }
                }
            }
        }
        return null;
    }

    private void collectNearbyDrops(MillVillager villager, BlockPos pos) {
        AABB dropsArea = new AABB(pos).inflate(1.5);
        List<ItemEntity> drops = villager.level().getEntitiesOfClass(ItemEntity.class, dropsArea);
        for (ItemEntity drop : drops) {
            ItemStack stack = drop.getItem();
            if (stack.isEmpty()) {
                continue;
            }
            int remaining = villager.inventory.add(stack);
            if (remaining <= 0) {
                drop.discard();
            } else {
                drop.setItem(stack.copyWithCount(remaining));
            }
        }
        villager.syncSelectedItemToHands();
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
        if (actionKey.startsWith("harvest_wart_")) {
            if (result.status() == VillagerActionRuntime.Status.SUCCESS) {
                BlockPos target = parseActionPos(actionKey, "harvest_wart_");
                if (target != null) {
                    collectNearbyDrops(villager, target);
                }
            }
            runtime.reset(villager);
            return true;
        }
        return false;
    }

    private BlockPos parseActionPos(String actionKey, String prefix) {
        try {
            return BlockPos.of(Long.parseLong(actionKey.substring(prefix.length())));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
