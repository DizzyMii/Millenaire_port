package org.dizzymii.millenaire2.village;

import net.minecraft.nbt.CompoundTag;
import org.dizzymii.millenaire2.buildingplan.BuildingCustomPlan;
import org.dizzymii.millenaire2.culture.BuildingPlanSet;

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
    public int priority = 0;
    public int upgradeLevel = 0;

    @Nullable public BuildingPlanSet planSet;
    @Nullable public BuildingCustomPlan customPlan;

    public BuildingProject() {}

    public BuildingProject(String key, BuildingLocation location) {
        this.key = key;
        this.location = location;
    }

    public BuildingProject(String key, BuildingLocation location, EnumProjects tier) {
        this.key = key;
        this.location = location;
        this.projectTier = tier;
    }

    public static BuildingProject forCustomBuilding(String key, BuildingLocation location, BuildingCustomPlan plan) {
        BuildingProject bp = new BuildingProject(key, location);
        bp.isCustomBuilding = true;
        bp.customPlan = plan;
        bp.projectTier = EnumProjects.PLAYER;
        return bp;
    }

    public static BuildingProject forPlanSet(String key, BuildingLocation location, BuildingPlanSet planSet, int level) {
        BuildingProject bp = new BuildingProject(key, location);
        bp.planSet = planSet;
        bp.upgradeLevel = level;
        return bp;
    }

    /**
     * Get the display name of this project for panels/GUIs.
     */
    public String getDisplayName() {
        if (customPlan != null) return customPlan.getNameTranslated();
        if (planSet != null && planSet.name != null) return planSet.name;
        return key != null ? key : "Unknown";
    }

    // ========== NBT persistence ==========

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        if (key != null) tag.putString("key", key);
        tag.putBoolean("custom", isCustomBuilding);
        tag.putString("tier", projectTier.name());
        tag.putInt("priority", priority);
        tag.putInt("upgradeLevel", upgradeLevel);
        if (location != null) location.save(tag, "loc");
        return tag;
    }

    public static BuildingProject load(CompoundTag tag) {
        BuildingProject bp = new BuildingProject();
        if (tag.contains("key")) bp.key = tag.getString("key");
        bp.isCustomBuilding = tag.getBoolean("custom");
        try {
            bp.projectTier = EnumProjects.valueOf(tag.getString("tier"));
        } catch (IllegalArgumentException e) {
            bp.projectTier = EnumProjects.EXTRA;
        }
        bp.priority = tag.getInt("priority");
        bp.upgradeLevel = tag.getInt("upgradeLevel");
        bp.location = BuildingLocation.read(tag, "loc");
        return bp;
    }
}
