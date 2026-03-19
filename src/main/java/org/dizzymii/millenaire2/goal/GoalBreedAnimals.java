package org.dizzymii.millenaire2.goal;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.VillagerActionRuntime;
import org.dizzymii.millenaire2.entity.action.VillagerActions;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Villager finds breedable animals and feeds them to trigger breeding.
 */
public class GoalBreedAnimals extends Goal {

    { this.tags.add(TAG_AGRICULTURE); }

    @Override
    public GoalInformation getDestination(MillVillager v) {
        Animal animal = findBreedable(v);
        if (animal != null) {
            return new GoalInformation(new Point(animal.blockPosition()), 3);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        resolvePendingAction(v);
        Animal target = findBreedable(v, 5.0, null);
        if (target == null) {
            return true;
        }

        GoalActionSupport.ActionProgress progress = interactWithBreedable(v, target);
        if (progress == GoalActionSupport.ActionProgress.RUNNING) {
            return false;
        }
        if (progress == GoalActionSupport.ActionProgress.SUCCESS) {
            Animal second = findBreedable(v, 5.0, target);
            if (second == null) {
                return true;
            }
            return interactWithBreedable(v, second) != GoalActionSupport.ActionProgress.RUNNING;
        }
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return GoalActionSupport.runtimeBackedDuration(v, 30); }

    private Animal findBreedable(MillVillager v) {
        return findBreedable(v, 16.0, null);
    }

    private Animal findBreedable(MillVillager v, double radius, @Nullable Animal exclude) {
        AABB area = v.getBoundingBox().inflate(radius);
        List<Animal> animals = v.level().getEntitiesOfClass(Animal.class, area);
        for (Animal a : animals) {
            if (a != exclude && a.getAge() == 0 && !a.isInLove()) return a;
        }
        return null;
    }

    private GoalActionSupport.ActionProgress interactWithBreedable(MillVillager villager, Animal animal) {
        int foodSlot = findBreedingFoodSlot(villager, animal);
        if (foodSlot < 0) {
            return GoalActionSupport.ActionProgress.FAILED;
        }
        if (villager.getSelectedInventorySlot() != foodSlot) {
            villager.setSelectedInventorySlot(foodSlot);
            villager.syncSelectedItemToHands();
        }
        return GoalActionSupport.advanceAction(villager, "breed_animal_" + animal.getId(),
                VillagerActions.interactEntity(animal.getId(), InteractionHand.MAIN_HAND));
    }

    private int findBreedingFoodSlot(MillVillager villager, Animal animal) {
        for (int slot = 0; slot < villager.getInventoryContainer().getContainerSize(); slot++) {
            ItemStack stack = villager.getInventoryContainer().getItem(slot);
            if (!stack.isEmpty() && animal.isFood(stack)) {
                return slot;
            }
        }
        return -1;
    }

    private void resolvePendingAction(MillVillager villager) {
        VillagerActionRuntime runtime = villager.getActionRuntime();
        if (runtime.hasAction()) {
            return;
        }
        String actionKey = runtime.getLastCompletedActionKey();
        VillagerActionRuntime.Result result = runtime.getLastResult();
        if (actionKey != null && actionKey.startsWith("breed_animal_")
                && result.status() != VillagerActionRuntime.Status.IDLE) {
            runtime.reset(villager);
        }
    }
}
