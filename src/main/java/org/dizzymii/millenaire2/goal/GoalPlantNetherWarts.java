package org.dizzymii.millenaire2.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Villager plants nether wart on soul sand blocks.
 */
public class GoalPlantNetherWarts extends Goal {

    { this.tags.add(TAG_AGRICULTURE); }

    @Override
    public GoalInformation getDestination(MillVillager v) {
        BlockPos spot = findPlantSpot(v);
        if (spot != null) {
            return new GoalInformation(new Point(spot), 2);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        BlockPos pos = v.blockPosition();
        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -3; dz <= 3; dz++) {
                    BlockPos check = pos.offset(dx, dy, dz);
                    if (v.level().getBlockState(check).isAir()
                            && v.level().getBlockState(check.below()).is(Blocks.SOUL_SAND)) {
                        v.level().setBlockAndUpdate(check, Blocks.NETHER_WART.defaultBlockState());
                        return true;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 20; }

    private BlockPos findPlantSpot(MillVillager v) {
        BlockPos center = v.blockPosition();
        for (int dx = -10; dx <= 10; dx += 2) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -10; dz <= 10; dz += 2) {
                    BlockPos check = center.offset(dx, dy, dz);
                    if (v.level().getBlockState(check).isAir()
                            && v.level().getBlockState(check.below()).is(Blocks.SOUL_SAND)) {
                        return check;
                    }
                }
            }
        }
        return null;
    }
}
