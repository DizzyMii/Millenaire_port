package org.dizzymii.millenaire2.village;

import net.minecraft.nbt.CompoundTag;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Location/layout data for a building instance.
 * Ported from org.millenaire.common.village.BuildingLocation (Forge 1.12.2).
 */
public class BuildingLocation implements Cloneable {

    public String planKey;
    public String shop;
    public int priorityMoveIn = 10;
    public int minx, maxx, miny, maxy, minz, maxz;
    public int minxMargin, maxxMargin, minyMargin, maxyMargin, minzMargin, maxzMargin;
    public int orientation;
    public int length;
    public int width;
    public int level;
    public int reputation;
    public int price;
    public int version;
    private int variation;
    public boolean isCustomBuilding = false;

    @Nullable public Point pos;
    @Nullable public Point chestPos;
    @Nullable public Point sleepingPos;
    @Nullable public Point sellingPos;
    @Nullable public Point craftingPos;
    @Nullable public Point shelterPos;
    @Nullable public Point defendingPos;

    @Nullable public String cultureKey;
    public CopyOnWriteArrayList<String> subBuildings = new CopyOnWriteArrayList<>();
    public boolean upgradesAllowed = true;
    public boolean bedrocklevel = false;
    public boolean showTownHallSigns = false;
    public boolean isSubBuildingLocation = false;

    public int getVariation() { return variation; }
    public void setVariation(int v) { this.variation = v; }

    // ========== NBT persistence ==========

    public void save(CompoundTag tag, String label) {
        if (planKey != null) tag.putString(label + "_key", planKey);
        if (pos != null) pos.write(tag, label + "_pos");
        tag.putBoolean(label + "_isCustomBuilding", isCustomBuilding);
        if (cultureKey != null) tag.putString(label + "_culture", cultureKey);
        tag.putInt(label + "_orientation", orientation);
        tag.putInt(label + "_length", length);
        tag.putInt(label + "_width", width);
        tag.putInt(label + "_level", level);
        tag.putInt(label + "_variation", variation);
        tag.putInt(label + "_reputation", reputation);
        tag.putInt(label + "_price", price);
        tag.putInt(label + "_version", version);
        // TODO: save remaining fields (margins, special positions, sub-buildings)
    }

    @Nullable
    public static BuildingLocation read(CompoundTag tag, String label) {
        if (!tag.contains(label + "_key")) return null;
        BuildingLocation bl = new BuildingLocation();
        bl.planKey = tag.getString(label + "_key");
        bl.pos = Point.read(tag, label + "_pos");
        bl.isCustomBuilding = tag.getBoolean(label + "_isCustomBuilding");
        bl.cultureKey = tag.contains(label + "_culture") ? tag.getString(label + "_culture") : null;
        bl.orientation = tag.getInt(label + "_orientation");
        bl.length = tag.getInt(label + "_length");
        bl.width = tag.getInt(label + "_width");
        bl.level = tag.getInt(label + "_level");
        bl.variation = tag.getInt(label + "_variation");
        bl.reputation = tag.getInt(label + "_reputation");
        bl.price = tag.getInt(label + "_price");
        bl.version = tag.getInt(label + "_version");
        // TODO: read remaining fields
        return bl;
    }

    @Override
    public BuildingLocation clone() {
        try {
            BuildingLocation copy = (BuildingLocation) super.clone();
            copy.subBuildings = new CopyOnWriteArrayList<>(this.subBuildings);
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
}
