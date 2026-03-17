package org.dizzymii.millenaire2.village;

import javax.annotation.Nullable;

/**
 * Represents a building construction project in a village.
 * Ported from org.millenaire.common.village.BuildingProject (Forge 1.12.2).
 */
public class BuildingProject {

    public enum EnumProjects {
        CENTRE, START, CORE, SECONDARY, EXTRA, PLAYER
    }

    @Nullable public BuildingLocation location = null;
    @Nullable public String key;
    public boolean isCustomBuilding = false;
    public EnumProjects projectTier = EnumProjects.EXTRA;

    // TODO: References to BuildingPlanSet, BuildingPlan, BuildingCustomPlan
    //       will be added once the building plan system is ported.

    public BuildingProject() {}

    public BuildingProject(String key, BuildingLocation location) {
        this.key = key;
        this.location = location;
    }
}
