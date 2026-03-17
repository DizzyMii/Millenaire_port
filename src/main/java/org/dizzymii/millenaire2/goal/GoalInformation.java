package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;

import javax.annotation.Nullable;

/**
 * Return type for Goal.getDestination() — encapsulates the target point and building.
 * Ported from org.millenaire.common.goal.Goal.GoalInformation (Forge 1.12.2).
 */
public class GoalInformation {

    @Nullable public final Point targetPoint;
    @Nullable public final Building targetBuilding;
    public final int range;

    public GoalInformation(@Nullable Point targetPoint, @Nullable Building targetBuilding, int range) {
        this.targetPoint = targetPoint;
        this.targetBuilding = targetBuilding;
        this.range = range;
    }

    public GoalInformation(@Nullable Point targetPoint, int range) {
        this(targetPoint, null, range);
    }

    public boolean hasTarget() {
        return targetPoint != null;
    }
}
