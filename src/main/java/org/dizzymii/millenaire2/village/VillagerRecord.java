package org.dizzymii.millenaire2.village;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Persistent record of a villager's data (survives entity unload).
 * Ported from org.millenaire.common.village.VillagerRecord (Forge 1.12.2).
 */
public class VillagerRecord implements Cloneable {

    private static final double RIGHT_HANDED_CHANCE = 0.8;

    // ========== NBT key constants ==========
    private static final String NBT_ID = "id";
    private static final String NBT_GENDER = "gender";
    private static final String NBT_NB = "nb";
    private static final String NBT_SIZE = "size";
    private static final String NBT_SCALE = "scale";
    private static final String NBT_RIGHT_HANDED = "rightHanded";
    private static final String NBT_KILLED = "killed";
    private static final String NBT_RAIDING_VILLAGE = "raidingVillage";
    private static final String NBT_AWAY_RAIDING = "awayraiding";
    private static final String NBT_AWAY_HIRED = "awayhired";
    private static final String NBT_FLAWED_RECORD = "flawedRecord";
    private static final String NBT_RAIDER_SPAWN = "raiderSpawn";
    private static final String NBT_ORIGINAL_ID = "originalId";
    private static final String NBT_CULTURE = "culture";
    private static final String NBT_TYPE = "type";
    private static final String NBT_FIRST_NAME = "firstName";
    private static final String NBT_FAMILY_NAME = "familyName";
    private static final String NBT_TEXTURE = "texture";
    private static final String NBT_FATHERS_NAME = "fathersName";
    private static final String NBT_MOTHERS_NAME = "mothersName";
    private static final String NBT_SPOUSES_NAME = "spousesName";
    private static final String NBT_MAIDEN_NAME = "maidenName";
    private static final String NBT_HOUSE = "house";
    private static final String NBT_TH = "th";
    private static final String NBT_ORIG_VILLAGE = "origVillage";
    private static final String NBT_INVENTORY = "inventory";
    private static final String NBT_QUEST_TAGS = "questTags";

    @Nullable private String cultureKey;
    public String fathersName = "";
    public String mothersName = "";
    public String spousesName = "";
    public String maidenName = "";
    public boolean flawedRecord = false;
    public boolean killed = false;
    public boolean raidingVillage = false;
    public boolean awayraiding = false;
    public boolean awayhired = false;

    @Nullable private Point housePos;
    @Nullable private Point townHallPos;
    @Nullable public Point originalVillagePos;

    private long villagerId;
    public long raiderSpawn = 0L;
    public int nb;
    public int gender;
    public int size;
    public float scale = 1.0f;
    public boolean rightHanded = true;

    public HashMap<String, Integer> inventory = new HashMap<>();
    public List<String> questTags = new ArrayList<>();

    @Nullable public String type;
    @Nullable public String firstName;
    @Nullable public String familyName;
    @Nullable public ResourceLocation texture;

    private long originalId = -1L;

    public VillagerRecord() {}

    // ========== Accessors ==========

    public long getVillagerId() { return villagerId; }
    public void setVillagerId(long id) { this.villagerId = id; }

    @Nullable public String getCultureKey() { return cultureKey; }
    public void setCultureKey(@Nullable String key) { this.cultureKey = key; }

    @Nullable public Point getHousePos() { return housePos; }
    public void setHousePos(@Nullable Point p) { this.housePos = p; }

    @Nullable public Point getTownHallPos() { return townHallPos; }
    public void setTownHallPos(@Nullable Point p) { this.townHallPos = p; }

    public long getOriginalId() { return originalId; }
    public void setOriginalId(long id) { this.originalId = id; }

    public boolean hasQuestTag(String tag) {
        return questTags.contains(tag);
    }

    public void addQuestTag(String tag) {
        if (!questTags.contains(tag)) questTags.add(tag);
    }

    public void removeQuestTag(String tag) {
        questTags.remove(tag);
    }

    // ========== NBT persistence ==========

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putLong(NBT_ID, villagerId);
        tag.putInt(NBT_GENDER, gender);
        tag.putInt(NBT_NB, nb);
        tag.putInt(NBT_SIZE, size);
        tag.putFloat(NBT_SCALE, scale);
        tag.putBoolean(NBT_RIGHT_HANDED, rightHanded);
        tag.putBoolean(NBT_KILLED, killed);
        tag.putBoolean(NBT_RAIDING_VILLAGE, raidingVillage);
        tag.putBoolean(NBT_AWAY_RAIDING, awayraiding);
        tag.putBoolean(NBT_AWAY_HIRED, awayhired);
        tag.putBoolean(NBT_FLAWED_RECORD, flawedRecord);
        tag.putLong(NBT_RAIDER_SPAWN, raiderSpawn);
        tag.putLong(NBT_ORIGINAL_ID, originalId);

        if (cultureKey != null) tag.putString(NBT_CULTURE, cultureKey);
        if (type != null) tag.putString(NBT_TYPE, type);
        if (firstName != null) tag.putString(NBT_FIRST_NAME, firstName);
        if (familyName != null) tag.putString(NBT_FAMILY_NAME, familyName);
        if (texture != null) tag.putString(NBT_TEXTURE, texture.toString());

        tag.putString(NBT_FATHERS_NAME, fathersName);
        tag.putString(NBT_MOTHERS_NAME, mothersName);
        tag.putString(NBT_SPOUSES_NAME, spousesName);
        tag.putString(NBT_MAIDEN_NAME, maidenName);

        if (housePos != null) housePos.writeToNBT(tag, NBT_HOUSE);
        if (townHallPos != null) townHallPos.writeToNBT(tag, NBT_TH);
        if (originalVillagePos != null) originalVillagePos.writeToNBT(tag, NBT_ORIG_VILLAGE);

        // Save inventory
        CompoundTag invTag = new CompoundTag();
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            invTag.putInt(entry.getKey(), entry.getValue());
        }
        tag.put(NBT_INVENTORY, invTag);

        // Save quest tags
        ListTag questList = new ListTag();
        for (String qt : questTags) {
            questList.add(StringTag.valueOf(qt));
        }
        tag.put(NBT_QUEST_TAGS, questList);

        return tag;
    }

    public static VillagerRecord load(CompoundTag tag) {
        VillagerRecord vr = new VillagerRecord();
        vr.villagerId = tag.getLong(NBT_ID);
        vr.gender = tag.getInt(NBT_GENDER);
        vr.nb = tag.getInt(NBT_NB);
        vr.size = tag.getInt(NBT_SIZE);
        vr.scale = tag.getFloat(NBT_SCALE);
        vr.rightHanded = tag.getBoolean(NBT_RIGHT_HANDED);
        vr.killed = tag.getBoolean(NBT_KILLED);
        vr.raidingVillage = tag.getBoolean(NBT_RAIDING_VILLAGE);
        vr.awayraiding = tag.getBoolean(NBT_AWAY_RAIDING);
        vr.awayhired = tag.getBoolean(NBT_AWAY_HIRED);
        vr.flawedRecord = tag.getBoolean(NBT_FLAWED_RECORD);
        vr.raiderSpawn = tag.getLong(NBT_RAIDER_SPAWN);
        vr.originalId = tag.getLong(NBT_ORIGINAL_ID);

        if (tag.contains(NBT_CULTURE)) vr.cultureKey = tag.getString(NBT_CULTURE);
        if (tag.contains(NBT_TYPE)) vr.type = tag.getString(NBT_TYPE);
        if (tag.contains(NBT_FIRST_NAME)) vr.firstName = tag.getString(NBT_FIRST_NAME);
        if (tag.contains(NBT_FAMILY_NAME)) vr.familyName = tag.getString(NBT_FAMILY_NAME);
        if (tag.contains(NBT_TEXTURE)) vr.texture = ResourceLocation.parse(tag.getString(NBT_TEXTURE));

        vr.fathersName = tag.getString(NBT_FATHERS_NAME);
        vr.mothersName = tag.getString(NBT_MOTHERS_NAME);
        vr.spousesName = tag.getString(NBT_SPOUSES_NAME);
        vr.maidenName = tag.getString(NBT_MAIDEN_NAME);

        vr.housePos = Point.readFromNBT(tag, NBT_HOUSE);
        vr.townHallPos = Point.readFromNBT(tag, NBT_TH);
        vr.originalVillagePos = Point.readFromNBT(tag, NBT_ORIG_VILLAGE);

        // Load inventory
        if (tag.contains(NBT_INVENTORY, Tag.TAG_COMPOUND)) {
            CompoundTag invTag = tag.getCompound(NBT_INVENTORY);
            for (String key : invTag.getAllKeys()) {
                vr.inventory.put(key, invTag.getInt(key));
            }
        }

        // Load quest tags
        if (tag.contains(NBT_QUEST_TAGS, Tag.TAG_LIST)) {
            ListTag questList = tag.getList(NBT_QUEST_TAGS, Tag.TAG_STRING);
            for (int i = 0; i < questList.size(); i++) {
                vr.questTags.add(questList.getString(i));
            }
        }

        return vr;
    }

    /**
     * Create a new VillagerRecord with a random ID and given attributes.
     */
    public static VillagerRecord create(String cultureKey, String type, String firstName, String familyName, int gender) {
        VillagerRecord vr = new VillagerRecord();
        vr.villagerId = System.nanoTime() ^ (long)(Math.random() * Long.MAX_VALUE);
        vr.cultureKey = cultureKey;
        vr.type = type;
        vr.firstName = firstName;
        vr.familyName = familyName;
        vr.gender = gender;
        vr.rightHanded = Math.random() < RIGHT_HANDED_CHANCE;
        return vr;
    }

    @Override
    public VillagerRecord clone() {
        try {
            VillagerRecord copy = (VillagerRecord) super.clone();
            copy.inventory = new HashMap<>(this.inventory);
            copy.questTags = new ArrayList<>(this.questTags);
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
}
