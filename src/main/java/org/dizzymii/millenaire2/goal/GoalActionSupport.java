package org.dizzymii.millenaire2.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.VillagerActionRuntime;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.village.Building;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public final class GoalActionSupport {

    private GoalActionSupport() {}

    public static int runtimeBackedDuration(MillVillager villager, int baseDuration) {
        VillagerActionRuntime runtime = villager.getActionRuntime();
        return runtime.hasAction() || runtime.getLastResult().status() != VillagerActionRuntime.Status.IDLE ? 1 : baseDuration;
    }

    public static ActionProgress advanceAction(MillVillager villager, String actionKey, VillagerActionRuntime.Action action) {
        VillagerActionRuntime runtime = villager.getActionRuntime();
        if (runtime.hasAction()) {
            return ActionProgress.RUNNING;
        }
        VillagerActionRuntime.Result lastResult = runtime.getLastResult();
        if (lastResult.status() == VillagerActionRuntime.Status.SUCCESS) {
            if (actionKey.equals(runtime.getLastCompletedActionKey())) {
                runtime.reset(villager);
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

    @Nullable
    public static TransferChoice firstAvailableStoredGoods(@Nullable Building building, int maxAmount) {
        if (building == null) {
            return null;
        }
        for (Map.Entry<InvItem, Integer> entry : building.resManager.resources.entrySet()) {
            int amount = Math.min(entry.getValue(), Math.max(0, maxAmount));
            if (amount > 0) {
                return new TransferChoice(entry.getKey(), amount);
            }
        }
        return null;
    }

    @Nullable
    public static InvItem firstInventoryItemMatching(MillVillager villager, Predicate<ItemStack> predicate) {
        for (int slot = 0; slot < villager.getInventoryContainer().getContainerSize(); slot++) {
            ItemStack stack = villager.getInventoryContainer().getItem(slot);
            if (stack.isEmpty() || !predicate.test(stack)) {
                continue;
            }
            InvItem item = InvItem.fromItem(stack.getItem());
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    public static void collectNearbyDrops(MillVillager villager, BlockPos pos, double radius) {
        collectNearbyDrops(villager, new AABB(pos).inflate(radius));
    }

    public static void collectNearbyDrops(MillVillager villager, AABB area) {
        List<ItemEntity> drops = villager.level().getEntitiesOfClass(ItemEntity.class, area);
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

    @Nullable
    public static BlockPos parseActionPos(String actionKey, String prefix) {
        try {
            return BlockPos.of(Long.parseLong(actionKey.substring(prefix.length())));
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    public enum ActionProgress {
        RUNNING,
        SUCCESS,
        FAILED
    }

    public record TransferChoice(InvItem item, int amount) {}
}
