package org.dizzymii.millenaire2.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.Point;

/**
 * Mayan villager harvests mature cocoa beans from jungle trees.
 */
public class GoalHarvestCacao extends Goal {

    { this.tags.add(TAG_AGRICULTURE); }

    @Override
    public GoalInformation getDestination(MillVillager v) {
        BlockPos cacao = findMatureCacao(v);
        if (cacao != null) {
            return new GoalInformation(new Point(cacao), 2);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        BlockPos pos = v.blockPosition();
        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -2; dy <= 3; dy++) {
                for (int dz = -3; dz <= 3; dz++) {
                    BlockPos check = pos.offset(dx, dy, dz);
                    if (v.level().getBlockState(check).getBlock() == Blocks.COCOA
                            && v.level().getBlockState(check).getValue(CocoaBlock.AGE) >= 2) {
                        v.level().destroyBlock(check, false);
                        InvItem beans = InvItem.get("minecraft:cocoa_beans");
                        if (beans != null) v.addToInv(beans, 1 + v.level().random.nextInt(2));
                        return true;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 30; }

    private BlockPos findMatureCacao(MillVillager v) {
        BlockPos center = v.blockPosition();
        for (int dx = -12; dx <= 12; dx += 2) {
            for (int dy = -2; dy <= 6; dy++) {
                for (int dz = -12; dz <= 12; dz += 2) {
                    BlockPos check = center.offset(dx, dy, dz);
                    if (v.level().getBlockState(check).getBlock() == Blocks.COCOA
                            && v.level().getBlockState(check).getValue(CocoaBlock.AGE) >= 2) {
                        return check;
                    }
                }
            }
        }
        return null;
    }
}
