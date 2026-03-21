package org.dizzymii.millenaire2.village;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.dizzymii.millenaire2.culture.Culture;
import org.dizzymii.millenaire2.culture.VillagerType;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.item.InvItem;
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

    public int countInv(String key) {
        return inventory.getOrDefault(key, 0);
    }

    public int countInv(InvItem item) {
        if (item == null) {
            return 0;
        }
        return countInv(item.key);
    }

    public int countInv(Item item) {
        if (item == null) {
            return 0;
        }
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            InvItem inv = InvItem.get(entry.getKey());
            if (inv != null && inv.getItem() == item) {
                return entry.getValue();
            }
        }
        return 0;
    }

    public void addToInv(InvItem item, int count) {
        if (item == null || count == 0) {
            return;
        }
        int updated = inventory.getOrDefault(item.key, 0) + count;
        if (updated <= 0) {
            inventory.remove(item.key);
        } else {
            inventory.put(item.key, updated);
        }
    }

    public int takeFromInv(InvItem item, int count) {
        if (item == null || count <= 0) {
            return 0;
        }
        int taken = Math.min(countInv(item), count);
        if (taken <= 0) {
            return 0;
        }
        addToInv(item, -taken);
        return taken;
    }

    public boolean matches(MillVillager villager) {
        return villager != null && villager.getVillagerId() == this.villagerId;
    }

    public void updateRecord(MillVillager villager) {
        if (villager == null) {
            return;
        }

        this.villagerId = villager.getVillagerId();
        this.firstName = villager.getFirstName();
        this.familyName = villager.getFamilyName();
        this.gender = villager.getGender();
        this.cultureKey = villager.getCultureKey();
        this.type = villager.vtypeKey;
        this.raidingVillage = villager.isRaider;
        this.killed = !villager.isAlive();
        this.housePos = villager.housePoint != null ? new Point(villager.housePoint) : null;
        this.townHallPos = villager.townHallPoint != null ? new Point(villager.townHallPoint) : null;

        this.inventory.clear();
        for (Map.Entry<InvItem, Integer> entry : villager.inventory.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null && entry.getValue() > 0) {
                this.inventory.put(entry.getKey().key, entry.getValue());
            }
        }

        this.flawedRecord = (this.housePos == null && this.townHallPos == null);
    }

    public VillagerRecord generateRaidRecord(Building target) {
        VillagerRecord raidRecord = this.clone();
        raidRecord.villagerId = Math.abs(System.nanoTime() ^ (long) (Math.random() * Long.MAX_VALUE));
        raidRecord.raidingVillage = true;
        raidRecord.awayraiding = false;
        raidRecord.killed = false;
        raidRecord.originalId = this.villagerId;
        raidRecord.originalVillagePos = this.townHallPos != null ? new Point(this.townHallPos) : null;
        raidRecord.raiderSpawn = 0L;

        if (target != null) {
            Point targetPos = target.getPos();
            Point targetTownHall = target.getTownHallPos();
            raidRecord.housePos = targetPos != null ? new Point(targetPos) : null;
            raidRecord.townHallPos = targetTownHall != null
                    ? new Point(targetTownHall)
                    : (targetPos != null ? new Point(targetPos) : null);
        }

        return raidRecord;
    }

    public String getName() {
        String first = firstName == null ? "" : firstName;
        String family = familyName == null ? "" : familyName;
        if (first.isEmpty()) {
            return family;
        }
        if (family.isEmpty()) {
            return first;
        }
        return first + " " + family;
    }

    @Nullable
    public VillagerType getType() {
        if (cultureKey == null || type == null) {
            return null;
        }
        Culture culture = Culture.getCultureByName(cultureKey);
        if (culture == null) {
            return null;
        }
        return culture.getVillagerType(type);
    }

    public String getNativeOccupationName() {
        VillagerType vt = getType();
        if (vt == null) {
            return "";
        }
        return vt.name == null ? "" : vt.name;
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
        vr.rightHanded = tag.getBoolean("rightHanded");
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
            copy.housePos = this.housePos == null ? null : new Point(this.housePos);
            copy.townHallPos = this.townHallPos == null ? null : new Point(this.townHallPos);
            copy.originalVillagePos = this.originalVillagePos == null ? null : new Point(this.originalVillagePos);
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof VillagerRecord other)) return false;
        return this.villagerId == other.villagerId;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(villagerId);
    }

    @Override
    public String toString() {
        String t = type == null ? "" : type;
        return getName() + "/" + t + "/" + villagerId;
    }
}
