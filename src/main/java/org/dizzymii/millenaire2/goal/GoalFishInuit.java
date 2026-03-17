package org.dizzymii.millenaire2.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.Point;

/**
 * Inuit ice-fishing: villager finds ice blocks and fishes through them.
 */
public class GoalFishInuit extends Goal {

    @Override
    public GoalInformation getDestination(MillVillager v) {
        BlockPos ice = findIce(v);
        if (ice != null) {
            return new GoalInformation(new Point(ice), 2);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        long elapsed = v.level().getGameTime() - v.goalStarted;
        if (elapsed > 200) {
            InvItem fish = InvItem.get("minecraft:cod");
            if (fish != null) v.addToInv(fish, 1 + v.level().random.nextInt(2));
            return true;
        }
        return false;
    }

    @Override
    public int actionDuration(MillVillager v) { return 40; }

    private BlockPos findIce(MillVillager v) {
        BlockPos center = v.blockPosition();
        for (int dx = -12; dx <= 12; dx += 2) {
            for (int dz = -12; dz <= 12; dz += 2) {
                BlockPos check = center.offset(dx, 0, dz);
                for (int dy = -3; dy <= 3; dy++) {
                    BlockPos p = check.above(dy);
                    if (v.level().getBlockState(p).is(Blocks.ICE) || v.level().getBlockState(p).is(Blocks.PACKED_ICE)) {
                        return p;
                    }
                }
            }
        }
        return null;
    }
}
