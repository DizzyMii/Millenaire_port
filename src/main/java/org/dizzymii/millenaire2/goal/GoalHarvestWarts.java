package org.dizzymii.millenaire2.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherWartBlock;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.Point;

/**
 * Villager harvests mature nether wart blocks.
 */
public class GoalHarvestWarts extends Goal {

    { this.tags.add(TAG_AGRICULTURE); }

    @Override
    public GoalInformation getDestination(MillVillager v) {
        BlockPos wart = findMatureWart(v);
        if (wart != null) {
            return new GoalInformation(new Point(wart), 2);
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
                    if (v.level().getBlockState(check).getBlock() == Blocks.NETHER_WART
                            && v.level().getBlockState(check).getValue(NetherWartBlock.AGE) >= 3) {
                        v.level().destroyBlock(check, false);
                        InvItem wart = InvItem.get("minecraft:nether_wart");
                        if (wart != null) v.addToInv(wart, 2 + v.level().random.nextInt(3));
                        return true;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 25; }

    private BlockPos findMatureWart(MillVillager v) {
        BlockPos center = v.blockPosition();
        for (int dx = -10; dx <= 10; dx += 2) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -10; dz <= 10; dz += 2) {
                    BlockPos check = center.offset(dx, dy, dz);
                    if (v.level().getBlockState(check).getBlock() == Blocks.NETHER_WART
                            && v.level().getBlockState(check).getValue(NetherWartBlock.AGE) >= 3) {
                        return check;
                    }
                }
            }
        }
        return null;
    }
}
