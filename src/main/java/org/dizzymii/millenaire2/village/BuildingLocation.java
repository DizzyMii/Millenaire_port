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

    @Nullable public String cultureKey;
    public CopyOnWriteArrayList<String> subBuildings = new CopyOnWriteArrayList<>();
    public boolean upgradesAllowed = true;
    public boolean bedrocklevel = false;
    public boolean showTownHallSigns = false;
    public boolean isSubBuildingLocation = false;

    public int getVariation() { return variation; }
    public void setVariation(int v) { this.variation = v; }

    // ========== NBT suffix constants ==========
    private static final String S_KEY = "_key";
    private static final String S_POS = "_pos";
    private static final String S_IS_CUSTOM = "_isCustomBuilding";
    private static final String S_CULTURE = "_culture";
    private static final String S_ORIENTATION = "_orientation";
    private static final String S_LENGTH = "_length";
    private static final String S_WIDTH = "_width";
    private static final String S_LEVEL = "_level";
    private static final String S_VARIATION = "_variation";
    private static final String S_REPUTATION = "_reputation";
    private static final String S_PRICE = "_price";
    private static final String S_VERSION = "_version";
    private static final String S_PRIORITY_MOVE_IN = "_priorityMoveIn";
    private static final String S_SHOP = "_shop";
    private static final String S_UPGRADES_ALLOWED = "_upgradesAllowed";
    private static final String S_BEDROCK_LEVEL = "_bedrocklevel";
    private static final String S_SHOW_TH_SIGNS = "_showTownHallSigns";
    private static final String S_IS_SUB_LOC = "_isSubBuildingLocation";
    private static final String S_MINX_M = "_minxM";
    private static final String S_MAXX_M = "_maxxM";
    private static final String S_MINY_M = "_minyM";
    private static final String S_MAXY_M = "_maxyM";
    private static final String S_MINZ_M = "_minzM";
    private static final String S_MAXZ_M = "_maxzM";
    private static final String S_MINX = "_minx";
    private static final String S_MAXX = "_maxx";
    private static final String S_MINY = "_miny";
    private static final String S_MAXY = "_maxy";
    private static final String S_MINZ = "_minz";
    private static final String S_MAXZ = "_maxz";
    private static final String S_CHEST = "_chest";
    private static final String S_SLEEP = "_sleep";
    private static final String S_SELL = "_sell";
    private static final String S_CRAFT = "_craft";
    private static final String S_SHELTER = "_shelter";
    private static final String S_DEFEND = "_defend";
    private static final String S_SUBS = "_subs";

    // ========== NBT persistence ==========

    public void save(CompoundTag tag, String label) {
        if (planKey != null) tag.putString(label + S_KEY, planKey);
        if (pos != null) pos.write(tag, label + S_POS);
        tag.putBoolean(label + S_IS_CUSTOM, isCustomBuilding);
        if (cultureKey != null) tag.putString(label + S_CULTURE, cultureKey);
        tag.putInt(label + S_ORIENTATION, orientation);
        tag.putInt(label + S_LENGTH, length);
        tag.putInt(label + S_WIDTH, width);
        tag.putInt(label + S_LEVEL, level);
        tag.putInt(label + S_VARIATION, variation);
        tag.putInt(label + S_REPUTATION, reputation);
        tag.putInt(label + S_PRICE, price);
        tag.putInt(label + S_VERSION, version);
        tag.putInt(label + S_PRIORITY_MOVE_IN, priorityMoveIn);
        if (shop != null) tag.putString(label + S_SHOP, shop);
        tag.putBoolean(label + S_UPGRADES_ALLOWED, upgradesAllowed);
        tag.putBoolean(label + S_BEDROCK_LEVEL, bedrocklevel);
        tag.putBoolean(label + S_SHOW_TH_SIGNS, showTownHallSigns);
        tag.putBoolean(label + S_IS_SUB_LOC, isSubBuildingLocation);

        // Margins
        tag.putInt(label + S_MINX_M, minxMargin);
        tag.putInt(label + S_MAXX_M, maxxMargin);
        tag.putInt(label + S_MINY_M, minyMargin);
        tag.putInt(label + S_MAXY_M, maxyMargin);
        tag.putInt(label + S_MINZ_M, minzMargin);
        tag.putInt(label + S_MAXZ_M, maxzMargin);

        // Bounds
        tag.putInt(label + S_MINX, minx);
        tag.putInt(label + S_MAXX, maxx);
        tag.putInt(label + S_MINY, miny);
        tag.putInt(label + S_MAXY, maxy);
        tag.putInt(label + S_MINZ, minz);
        tag.putInt(label + S_MAXZ, maxz);

        // Special positions
        if (chestPos != null) chestPos.write(tag, label + S_CHEST);
        if (sleepingPos != null) sleepingPos.write(tag, label + S_SLEEP);
        if (sellingPos != null) sellingPos.write(tag, label + S_SELL);
        if (craftingPos != null) craftingPos.write(tag, label + S_CRAFT);
        if (shelterPos != null) shelterPos.write(tag, label + S_SHELTER);
        if (defendingPos != null) defendingPos.write(tag, label + S_DEFEND);

        // Sub-buildings
        ListTag subList = new ListTag();
        for (String sub : subBuildings) {
            subList.add(StringTag.valueOf(sub));
        }
        tag.put(label + S_SUBS, subList);
    }

    @Nullable
    public static BuildingLocation read(CompoundTag tag, String label) {
        if (!tag.contains(label + S_KEY)) return null;
        BuildingLocation bl = new BuildingLocation();
        bl.planKey = tag.getString(label + S_KEY);
        bl.pos = Point.read(tag, label + S_POS);
        bl.isCustomBuilding = tag.getBoolean(label + S_IS_CUSTOM);
        bl.cultureKey = tag.contains(label + S_CULTURE) ? tag.getString(label + S_CULTURE) : null;
        bl.orientation = tag.getInt(label + S_ORIENTATION);
        bl.length = tag.getInt(label + S_LENGTH);
        bl.width = tag.getInt(label + S_WIDTH);
        bl.level = tag.getInt(label + S_LEVEL);
        bl.variation = tag.getInt(label + S_VARIATION);
        bl.reputation = tag.getInt(label + S_REPUTATION);
        bl.price = tag.getInt(label + S_PRICE);
        bl.version = tag.getInt(label + S_VERSION);
        bl.priorityMoveIn = tag.getInt(label + S_PRIORITY_MOVE_IN);
        if (tag.contains(label + S_SHOP)) bl.shop = tag.getString(label + S_SHOP);
        bl.upgradesAllowed = tag.getBoolean(label + S_UPGRADES_ALLOWED);
        bl.bedrocklevel = tag.getBoolean(label + S_BEDROCK_LEVEL);
        bl.showTownHallSigns = tag.getBoolean(label + S_SHOW_TH_SIGNS);
        bl.isSubBuildingLocation = tag.getBoolean(label + S_IS_SUB_LOC);

        // Margins
        bl.minxMargin = tag.getInt(label + S_MINX_M);
        bl.maxxMargin = tag.getInt(label + S_MAXX_M);
        bl.minyMargin = tag.getInt(label + S_MINY_M);
        bl.maxyMargin = tag.getInt(label + S_MAXY_M);
        bl.minzMargin = tag.getInt(label + S_MINZ_M);
        bl.maxzMargin = tag.getInt(label + S_MAXZ_M);

        // Bounds
        bl.minx = tag.getInt(label + S_MINX);
        bl.maxx = tag.getInt(label + S_MAXX);
        bl.miny = tag.getInt(label + S_MINY);
        bl.maxy = tag.getInt(label + S_MAXY);
        bl.minz = tag.getInt(label + S_MINZ);
        bl.maxz = tag.getInt(label + S_MAXZ);

        // Special positions
        bl.chestPos = Point.read(tag, label + S_CHEST);
        bl.sleepingPos = Point.read(tag, label + S_SLEEP);
        bl.sellingPos = Point.read(tag, label + S_SELL);
        bl.craftingPos = Point.read(tag, label + S_CRAFT);
        bl.shelterPos = Point.read(tag, label + S_SHELTER);
        bl.defendingPos = Point.read(tag, label + S_DEFEND);

        // Sub-buildings
        if (tag.contains(label + S_SUBS, Tag.TAG_LIST)) {
            ListTag subList = tag.getList(label + S_SUBS, Tag.TAG_STRING);
            for (int i = 0; i < subList.size(); i++) {
                bl.subBuildings.add(subList.getString(i));
            }
        }
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
