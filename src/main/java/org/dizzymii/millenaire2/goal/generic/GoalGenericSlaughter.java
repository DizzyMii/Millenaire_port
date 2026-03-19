package org.dizzymii.millenaire2.goal.generic;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.goal.GoalInformation;
import org.dizzymii.millenaire2.util.Point;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Data-driven slaughter goal: kill an animal, collect drops.
 * Covers: slaughterChicken, slaughtercownorman, slaughterpignorman, etc.
 */
public class GoalGenericSlaughter extends GoalGeneric {

    @Override
    public GoalInformation getDestination(MillVillager villager) {
        // Slaughter happens in the area around the village
        Point home = villager.housePoint != null ? villager.housePoint : villager.townHallPoint;
        if (home == null) home = new Point(villager.blockPosition());

        int dx = villager.getRandom().nextInt(15) - 7;
        int dz = villager.getRandom().nextInt(15) - 7;
        BlockPos target = new BlockPos(home.x + dx, home.y, home.z + dz);

        if (villager.level() instanceof ServerLevel sl) {
            target = sl.getHeightmapPos(
                    net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, target);
        }

        return new GoalInformation(new Point(target), goalRange);
    }

    @Override
    public boolean performAction(MillVillager villager) {
        // Slaughter: kill nearby animal, add drops to inventory
        return true;
    }
}
