package org.dizzymii.millenaire2.goal;

import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.phys.AABB;
import org.dizzymii.millenaire2.entity.MillVillager;
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
        AABB area = v.getBoundingBox().inflate(3);
        List<Sheep> nearby = v.level().getEntitiesOfClass(Sheep.class, area);
        for (Sheep s : nearby) {
            if (!s.isSheared() && s.readyForShearing()) {
                s.shear(net.minecraft.sounds.SoundSource.NEUTRAL);
                InvItem wool = InvItem.get("minecraft:white_wool");
                if (wool != null) v.addToInv(wool, 1);
                break;
            }
        }
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 30; }

    private Sheep findShearable(MillVillager v) {
        AABB area = v.getBoundingBox().inflate(16);
        List<Sheep> sheep = v.level().getEntitiesOfClass(Sheep.class, area);
        for (Sheep s : sheep) {
            if (!s.isSheared() && s.readyForShearing()) return s;
        }
        return null;
    }
}
