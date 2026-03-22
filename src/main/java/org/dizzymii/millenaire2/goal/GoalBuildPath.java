package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Villager places path blocks between village buildings.
 */
public class GoalBuildPath extends Goal {

    { this.tags.add(TAG_CONSTRUCTION); }

    @Override
    public GoalInformation getDestination(MillVillager v) {
        Point th = v.getTownHallPoint();
        if (th != null) {
            return new GoalInformation(th, 5);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        // Place a path block at the villager's position if they have gravel in inventory
        org.dizzymii.millenaire2.item.InvItem gravel = org.dizzymii.millenaire2.item.InvItem.get("minecraft:gravel");
        if (gravel != null && v.countInv(gravel) > 0) {
            net.minecraft.core.BlockPos pos = v.blockPosition().below();
            if (v.level() instanceof net.minecraft.server.level.ServerLevel sl) {
                net.minecraft.world.level.block.state.BlockState current = sl.getBlockState(pos);
                if (current.canBeReplaced()) {
                    sl.setBlock(pos, net.minecraft.world.level.block.Blocks.GRAVEL.defaultBlockState(), 3);
                    v.removeFromInv(gravel, 1);
                    v.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
                }
            }
        }
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 20; }
}
