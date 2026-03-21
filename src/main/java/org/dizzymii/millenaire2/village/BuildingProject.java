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

    // ========== NBT key constants ==========
    private static final String NBT_KEY = "key";
    private static final String NBT_CUSTOM = "custom";
    private static final String NBT_TIER = "tier";
    private static final String NBT_PRIORITY = "priority";
    private static final String NBT_UPGRADE_LEVEL = "upgradeLevel";
    private static final String NBT_LOC = "loc";

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
        if (key != null) tag.putString(NBT_KEY, key);
        tag.putBoolean(NBT_CUSTOM, isCustomBuilding);
        tag.putString(NBT_TIER, projectTier.name());
        tag.putInt(NBT_PRIORITY, priority);
        tag.putInt(NBT_UPGRADE_LEVEL, upgradeLevel);
        if (location != null) location.save(tag, NBT_LOC);
        return tag;
    }

    public static BuildingProject load(CompoundTag tag) {
        BuildingProject bp = new BuildingProject();
        if (tag.contains(NBT_KEY)) bp.key = tag.getString(NBT_KEY);
        bp.isCustomBuilding = tag.getBoolean(NBT_CUSTOM);
        try {
            bp.projectTier = EnumProjects.valueOf(tag.getString(NBT_TIER));
        } catch (IllegalArgumentException e) {
            bp.projectTier = EnumProjects.EXTRA;
        }
        bp.priority = tag.getInt(NBT_PRIORITY);
        bp.upgradeLevel = tag.getInt(NBT_UPGRADE_LEVEL);
        bp.location = BuildingLocation.read(tag, NBT_LOC);
        return bp;
    }
}
