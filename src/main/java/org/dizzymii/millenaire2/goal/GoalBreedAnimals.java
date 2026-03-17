package org.dizzymii.millenaire2.goal;

import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.phys.AABB;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

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
        AABB area = v.getBoundingBox().inflate(5);
        List<Animal> animals = v.level().getEntitiesOfClass(Animal.class, area);
        int bred = 0;
        for (Animal a : animals) {
            if (a.getAge() == 0 && !a.isInLove() && bred < 2) {
                a.setInLove(null);
                bred++;
            }
        }
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 30; }

    private Animal findBreedable(MillVillager v) {
        AABB area = v.getBoundingBox().inflate(16);
        List<Animal> animals = v.level().getEntitiesOfClass(Animal.class, area);
        for (Animal a : animals) {
            if (a.getAge() == 0 && !a.isInLove()) return a;
        }
        return null;
    }
}
