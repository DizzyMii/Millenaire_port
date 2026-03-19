package org.dizzymii.millenaire2.village;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;
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
    @Nullable public Point leisurePos;

    @Nullable public String cultureKey;
    public CopyOnWriteArrayList<String> tags = new CopyOnWriteArrayList<>();
    public CopyOnWriteArrayList<String> villageTags = new CopyOnWriteArrayList<>();
    public CopyOnWriteArrayList<String> clearTags = new CopyOnWriteArrayList<>();
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
        tag.putInt(label + "_priorityMoveIn", priorityMoveIn);
        if (shop != null) tag.putString(label + "_shop", shop);
        tag.putBoolean(label + "_upgradesAllowed", upgradesAllowed);
        tag.putBoolean(label + "_bedrocklevel", bedrocklevel);
        tag.putBoolean(label + "_showTownHallSigns", showTownHallSigns);
        tag.putBoolean(label + "_isSubBuildingLocation", isSubBuildingLocation);

        // Margins
        tag.putInt(label + "_minxM", minxMargin);
        tag.putInt(label + "_maxxM", maxxMargin);
        tag.putInt(label + "_minyM", minyMargin);
        tag.putInt(label + "_maxyM", maxyMargin);
        tag.putInt(label + "_minzM", minzMargin);
        tag.putInt(label + "_maxzM", maxzMargin);

        // Bounds
        tag.putInt(label + "_minx", minx);
        tag.putInt(label + "_maxx", maxx);
        tag.putInt(label + "_miny", miny);
        tag.putInt(label + "_maxy", maxy);
        tag.putInt(label + "_minz", minz);
        tag.putInt(label + "_maxz", maxz);

        // Special positions
        if (chestPos != null) chestPos.write(tag, label + "_chest");
        if (sleepingPos != null) sleepingPos.write(tag, label + "_sleep");
        if (sellingPos != null) sellingPos.write(tag, label + "_sell");
        if (craftingPos != null) craftingPos.write(tag, label + "_craft");
        if (shelterPos != null) shelterPos.write(tag, label + "_shelter");
        if (defendingPos != null) defendingPos.write(tag, label + "_defend");
        if (leisurePos != null) leisurePos.write(tag, label + "_leisure");

        // Sub-buildings
        ListTag subList = new ListTag();
        for (String sub : subBuildings) {
            subList.add(StringTag.valueOf(sub));
        }
        tag.put(label + "_subs", subList);

        ListTag tagList = new ListTag();
        for (String value : tags) {
            tagList.add(StringTag.valueOf(value));
        }
        tag.put(label + "_tags", tagList);

        ListTag villageTagList = new ListTag();
        for (String value : villageTags) {
            villageTagList.add(StringTag.valueOf(value));
        }
        tag.put(label + "_villagetags", villageTagList);

        ListTag clearTagList = new ListTag();
        for (String value : clearTags) {
            clearTagList.add(StringTag.valueOf(value));
        }
        tag.put(label + "_cleartags", clearTagList);
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
        bl.priorityMoveIn = tag.getInt(label + "_priorityMoveIn");
        if (tag.contains(label + "_shop")) bl.shop = tag.getString(label + "_shop");
        bl.upgradesAllowed = tag.getBoolean(label + "_upgradesAllowed");
        bl.bedrocklevel = tag.getBoolean(label + "_bedrocklevel");
        bl.showTownHallSigns = tag.getBoolean(label + "_showTownHallSigns");
        bl.isSubBuildingLocation = tag.getBoolean(label + "_isSubBuildingLocation");

        // Margins
        bl.minxMargin = tag.getInt(label + "_minxM");
        bl.maxxMargin = tag.getInt(label + "_maxxM");
        bl.minyMargin = tag.getInt(label + "_minyM");
        bl.maxyMargin = tag.getInt(label + "_maxyM");
        bl.minzMargin = tag.getInt(label + "_minzM");
        bl.maxzMargin = tag.getInt(label + "_maxzM");

        // Bounds
        bl.minx = tag.getInt(label + "_minx");
        bl.maxx = tag.getInt(label + "_maxx");
        bl.miny = tag.getInt(label + "_miny");
        bl.maxy = tag.getInt(label + "_maxy");
        bl.minz = tag.getInt(label + "_minz");
        bl.maxz = tag.getInt(label + "_maxz");

        // Special positions
        bl.chestPos = Point.read(tag, label + "_chest");
        bl.sleepingPos = Point.read(tag, label + "_sleep");
        bl.sellingPos = Point.read(tag, label + "_sell");
        bl.craftingPos = Point.read(tag, label + "_craft");
        bl.shelterPos = Point.read(tag, label + "_shelter");
        bl.defendingPos = Point.read(tag, label + "_defend");
        bl.leisurePos = Point.read(tag, label + "_leisure");

        // Sub-buildings
        if (tag.contains(label + "_subs", Tag.TAG_LIST)) {
            ListTag subList = tag.getList(label + "_subs", Tag.TAG_STRING);
            for (int i = 0; i < subList.size(); i++) {
                bl.subBuildings.add(subList.getString(i));
            }
        }
        if (tag.contains(label + "_tags", Tag.TAG_LIST)) {
            ListTag tagList = tag.getList(label + "_tags", Tag.TAG_STRING);
            for (int i = 0; i < tagList.size(); i++) {
                bl.tags.add(tagList.getString(i));
            }
        }
        if (tag.contains(label + "_villagetags", Tag.TAG_LIST)) {
            ListTag tagList = tag.getList(label + "_villagetags", Tag.TAG_STRING);
            for (int i = 0; i < tagList.size(); i++) {
                bl.villageTags.add(tagList.getString(i));
            }
        }
        if (tag.contains(label + "_cleartags", Tag.TAG_LIST)) {
            ListTag tagList = tag.getList(label + "_cleartags", Tag.TAG_STRING);
            for (int i = 0; i < tagList.size(); i++) {
                bl.clearTags.add(tagList.getString(i));
            }
        }
        return bl;
    }

    @Override
    public BuildingLocation clone() {
        try {
            BuildingLocation copy = (BuildingLocation) super.clone();
            copy.tags = new CopyOnWriteArrayList<>(this.tags);
            copy.villageTags = new CopyOnWriteArrayList<>(this.villageTags);
            copy.clearTags = new CopyOnWriteArrayList<>(this.clearTags);
            copy.subBuildings = new CopyOnWriteArrayList<>(this.subBuildings);
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
}
