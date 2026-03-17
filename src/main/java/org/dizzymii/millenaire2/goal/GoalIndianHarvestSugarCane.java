package org.dizzymii.millenaire2.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.Point;

/**
 * Indian villager harvests tall sugar cane (breaks the top blocks, leaving base).
 */
public class GoalIndianHarvestSugarCane extends Goal {

    { this.tags.add(TAG_AGRICULTURE); }

    @Override
    public GoalInformation getDestination(MillVillager v) {
        BlockPos cane = findTallCane(v);
        if (cane != null) {
            return new GoalInformation(new Point(cane), 2);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        BlockPos pos = v.blockPosition();
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                BlockPos base = pos.offset(dx, 0, dz);
                for (int dy = 0; dy <= 3; dy++) {
                    BlockPos check = base.above(dy);
                    if (v.level().getBlockState(check).is(Blocks.SUGAR_CANE)
                            && v.level().getBlockState(check.above()).is(Blocks.SUGAR_CANE)) {
                        // Break top segment, keep base for regrowth
                        v.level().destroyBlock(check.above(), false);
                        InvItem cane = InvItem.get("minecraft:sugar_cane");
                        if (cane != null) v.addToInv(cane, 1);
                        return true;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 25; }

    private BlockPos findTallCane(MillVillager v) {
        BlockPos center = v.blockPosition();
        for (int dx = -12; dx <= 12; dx += 2) {
            for (int dz = -12; dz <= 12; dz += 2) {
                for (int dy = -2; dy <= 3; dy++) {
                    BlockPos check = center.offset(dx, dy, dz);
                    if (v.level().getBlockState(check).is(Blocks.SUGAR_CANE)
                            && v.level().getBlockState(check.above()).is(Blocks.SUGAR_CANE)) {
                        return check;
                    }
                }
            }
        }
        return null;
    }
}
