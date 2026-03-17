package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Villager removes old or obsolete path blocks that are no longer needed.
 */
public class GoalClearOldPath extends Goal {

    { this.tags.add(TAG_CONSTRUCTION); }

    @Override
    public GoalInformation getDestination(MillVillager v) {
        Point th = v.townHallPoint;
        if (th != null) {
            return new GoalInformation(th, 5);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        // Remove gravel path blocks near the villager's position
        if (v.level() instanceof net.minecraft.server.level.ServerLevel sl) {
            net.minecraft.core.BlockPos pos = v.blockPosition().below();
            if (sl.getBlockState(pos).is(net.minecraft.world.level.block.Blocks.GRAVEL)) {
                sl.destroyBlock(pos, true);
                v.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
            }
        }
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 20; }
}
