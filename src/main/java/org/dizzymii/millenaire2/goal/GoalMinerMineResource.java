package org.dizzymii.millenaire2.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.Point;

/**
 * Miner villager finds and mines ore/stone blocks underground.
 */
public class GoalMinerMineResource extends Goal {

    private static final int SEARCH_RADIUS = 16;

    @Override
    public GoalInformation getDestination(MillVillager v) {
        BlockPos ore = findOre(v);
        if (ore != null) {
            return new GoalInformation(new Point(ore), 2);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        BlockPos pos = v.blockPosition();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    BlockPos check = pos.offset(dx, dy, dz);
                    if (v.level().getBlockState(check).is(BlockTags.STONE_ORE_REPLACEABLES)) {
                        v.level().destroyBlock(check, false);
                        InvItem cobble = InvItem.get("minecraft:cobblestone");
                        if (cobble != null) v.addToInv(cobble, 1);
                        return true;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 40; }

    private BlockPos findOre(MillVillager v) {
        BlockPos center = v.blockPosition();
        for (int dx = -SEARCH_RADIUS; dx <= SEARCH_RADIUS; dx += 3) {
            for (int dy = -8; dy <= 0; dy += 2) {
                for (int dz = -SEARCH_RADIUS; dz <= SEARCH_RADIUS; dz += 3) {
                    BlockPos check = center.offset(dx, dy, dz);
                    if (v.level().getBlockState(check).is(BlockTags.STONE_ORE_REPLACEABLES)) {
                        return check;
                    }
                }
            }
        }
        return null;
    }
}
