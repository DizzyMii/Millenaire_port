package org.dizzymii.millenaire2.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.Point;

/**
 * Villager finds nearby water and catches fish, adding them to inventory.
 */
public class GoalFish extends Goal {

    private static final int SEARCH_RADIUS = 16;

    { this.tags.add(TAG_AGRICULTURE); }

    @Override
    public GoalInformation getDestination(MillVillager v) {
        BlockPos water = findWater(v);
        if (water != null) {
            return new GoalInformation(new Point(water), 3);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        // Simulate catching a fish
        InvItem fish = InvItem.get("minecraft:cod");
        if (fish != null) {
            v.addToInv(fish, 1);
        }
        return true; // One fish per action cycle
    }

    @Override
    public int actionDuration(MillVillager v) { return 60; }

    private BlockPos findWater(MillVillager v) {
        BlockPos center = v.blockPosition();
        for (int dx = -SEARCH_RADIUS; dx <= SEARCH_RADIUS; dx += 2) {
            for (int dz = -SEARCH_RADIUS; dz <= SEARCH_RADIUS; dz += 2) {
                BlockPos check = center.offset(dx, 0, dz);
                if (v.level().getBlockState(check).is(Blocks.WATER)) {
                    return check;
                }
            }
        }
        return null;
    }
}
