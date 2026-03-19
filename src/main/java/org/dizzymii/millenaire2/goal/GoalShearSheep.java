package org.dizzymii.millenaire2.goal;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.VillagerActionRuntime;
import org.dizzymii.millenaire2.entity.action.VillagerActions;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.Point;

import java.util.List;

/**
 * Villager finds nearby sheep and shears them for wool.
 */
public class GoalShearSheep extends Goal {

    { this.tags.add(TAG_AGRICULTURE); }

    @Override
    public GoalInformation getDestination(MillVillager v) {
        Sheep sheep = findShearable(v);
        if (sheep != null) {
            return new GoalInformation(new Point(sheep.blockPosition()), 2);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        if (resolvePendingAction(v)) {
            return true;
        }
        Sheep sheep = findShearable(v, 3.0);
        if (sheep == null) {
            return true;
        }
        InvItem shears = InvItem.get("shears");
        if (shears == null || v.countInv(shears) == 0) {
            return true;
        }

        if (v.getSelectedInventoryItem().getItem() != shears.getItem()) {
            GoalActionSupport.ActionProgress equipProgress = GoalActionSupport.advanceAction(v, "equip_shears",
                    VillagerActions.equip(shears.key));
            if (equipProgress == GoalActionSupport.ActionProgress.RUNNING) {
                return false;
            }
            if (equipProgress == GoalActionSupport.ActionProgress.FAILED) {
                return true;
            }
        }

        GoalActionSupport.ActionProgress shearProgress = GoalActionSupport.advanceAction(v, "shear_sheep_" + sheep.getId(),
                VillagerActions.interactEntity(sheep.getId(), InteractionHand.MAIN_HAND));
        if (shearProgress == GoalActionSupport.ActionProgress.RUNNING) {
            return false;
        }
        if (shearProgress == GoalActionSupport.ActionProgress.SUCCESS) {
            collectNearbyDrops(v, sheep);
        }
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return GoalActionSupport.runtimeBackedDuration(v, 30); }

    private Sheep findShearable(MillVillager v) {
        return findShearable(v, 16.0);
    }

    private Sheep findShearable(MillVillager v, double radius) {
        AABB area = v.getBoundingBox().inflate(radius);
        List<Sheep> sheep = v.level().getEntitiesOfClass(Sheep.class, area);
        for (Sheep s : sheep) {
            if (!s.isSheared() && s.readyForShearing()) return s;
        }
        return null;
    }

    private void collectNearbyDrops(MillVillager villager, Sheep sheep) {
        if (sheep == null) {
            villager.syncSelectedItemToHands();
            return;
        }
        AABB dropsArea = sheep.getBoundingBox().inflate(1.5);
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
        if ("equip_shears".equals(actionKey)) {
            runtime.reset(villager);
            return false;
        }
        if (actionKey.startsWith("shear_sheep_")) {
            if (result.status() == VillagerActionRuntime.Status.SUCCESS) {
                Sheep sheep = findSheepByActionKey(villager, actionKey);
                collectNearbyDrops(villager, sheep);
            }
            runtime.reset(villager);
            return true;
        }
        return false;
    }

    private Sheep findSheepByActionKey(MillVillager villager, String actionKey) {
        try {
            int entityId = Integer.parseInt(actionKey.substring("shear_sheep_".length()));
            net.minecraft.world.entity.Entity entity = villager.level().getEntity(entityId);
            return entity instanceof Sheep sheep ? sheep : null;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
