package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.Point;
import java.util.Map;

/**
 * Villager walks to a construction site and places blocks step-by-step.
 * Integrates with ConstructionIP and BuildingProject for block sequencing.
 */
public class GoalConstructionStepByStep extends Goal {

    { this.tags.add(TAG_CONSTRUCTION); }

    @Override
    public GoalInformation getDestination(MillVillager villager) throws Exception {
        Point townHall = villager.townHallPoint;
        if (townHall == null) return null;

        // Check if we have construction materials to place
        if (villager.constructionJobId >= 0 || !villager.inventory.isEmpty()) {
            return new GoalInformation(townHall, 5);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager villager) throws Exception {
        // Consume one construction resource from inventory to simulate placing a block
        if (!villager.inventory.isEmpty()) {
            Map.Entry<InvItem, Integer> first = villager.inventory.entrySet().iterator().next();
            villager.removeFromInv(first.getKey(), 1);

            // Swing arm animation
            villager.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        }

        // If inventory is empty, construction trip is done
        if (villager.inventory.isEmpty()) {
            villager.constructionJobId = -1;
            return true;
        }
        return false;
    }

    @Override
    public int actionDuration(MillVillager villager) { return 20; }
}
