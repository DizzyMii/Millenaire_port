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
        tag.putLong("id", villagerId);
        tag.putInt("gender", gender);
        tag.putInt("nb", nb);
        tag.putInt("size", size);
        tag.putFloat("scale", scale);
        tag.putBoolean("rightHanded", rightHanded);
        tag.putBoolean("killed", killed);
        tag.putBoolean("raidingVillage", raidingVillage);
        tag.putBoolean("awayraiding", awayraiding);
        tag.putBoolean("awayhired", awayhired);
        tag.putBoolean("flawedRecord", flawedRecord);
        tag.putLong("raiderSpawn", raiderSpawn);
        tag.putLong("originalId", originalId);

        if (cultureKey != null) tag.putString("culture", cultureKey);
        if (type != null) tag.putString("type", type);
        if (firstName != null) tag.putString("firstName", firstName);
        if (familyName != null) tag.putString("familyName", familyName);
        if (texture != null) tag.putString("texture", texture.toString());

        tag.putString("fathersName", fathersName);
        tag.putString("mothersName", mothersName);
        tag.putString("spousesName", spousesName);
        tag.putString("maidenName", maidenName);

        if (housePos != null) housePos.writeToNBT(tag, "house");
        if (townHallPos != null) townHallPos.writeToNBT(tag, "th");
        if (originalVillagePos != null) originalVillagePos.writeToNBT(tag, "origVillage");

        // Save inventory
        CompoundTag invTag = new CompoundTag();
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            invTag.putInt(entry.getKey(), entry.getValue());
        }
        tag.put("inventory", invTag);

        // Save quest tags
        ListTag questList = new ListTag();
        for (String qt : questTags) {
            questList.add(StringTag.valueOf(qt));
        }
        tag.put("questTags", questList);

        return tag;
    }

    public static VillagerRecord load(CompoundTag tag) {
        VillagerRecord vr = new VillagerRecord();
        vr.villagerId = tag.getLong("id");
        vr.gender = tag.getInt("gender");
        vr.nb = tag.getInt("nb");
        vr.size = tag.getInt("size");
        vr.scale = tag.getFloat("scale");
        vr.rightHanded = tag.contains("rightHanded") ? tag.getBoolean("rightHanded") : true;
        vr.killed = tag.getBoolean("killed");
        vr.raidingVillage = tag.getBoolean("raidingVillage");
        vr.awayraiding = tag.getBoolean("awayraiding");
        vr.awayhired = tag.getBoolean("awayhired");
        vr.flawedRecord = tag.getBoolean("flawedRecord");
        vr.raiderSpawn = tag.getLong("raiderSpawn");
        vr.originalId = tag.getLong("originalId");

        if (tag.contains("culture")) vr.cultureKey = tag.getString("culture");
        if (tag.contains("type")) vr.type = tag.getString("type");
        if (tag.contains("firstName")) vr.firstName = tag.getString("firstName");
        if (tag.contains("familyName")) vr.familyName = tag.getString("familyName");
        if (tag.contains("texture")) vr.texture = ResourceLocation.parse(tag.getString("texture"));

        vr.fathersName = tag.getString("fathersName");
        vr.mothersName = tag.getString("mothersName");
        vr.spousesName = tag.getString("spousesName");
        vr.maidenName = tag.getString("maidenName");

        vr.housePos = Point.readFromNBT(tag, "house");
        vr.townHallPos = Point.readFromNBT(tag, "th");
        vr.originalVillagePos = Point.readFromNBT(tag, "origVillage");

        // Load inventory
        if (tag.contains("inventory", Tag.TAG_COMPOUND)) {
            CompoundTag invTag = tag.getCompound("inventory");
            for (String key : invTag.getAllKeys()) {
                vr.inventory.put(key, invTag.getInt(key));
            }
        }

        // Load quest tags
        if (tag.contains("questTags", Tag.TAG_LIST)) {
            ListTag questList = tag.getList("questTags", Tag.TAG_STRING);
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
