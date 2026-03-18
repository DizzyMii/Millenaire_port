package org.dizzymii.millenaire2.village;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates all marriage and family relationship data for a villager.
 * Stored as part of the villager entity's NBT and mirrored in VillagerRecord.
 *
 * Ported from the relationship fields spread across the original Millénaire codebase.
 */
public class FamilyData {

    private long spouseId = -1L;
    private String spouseName = "";
    private long fatherId = -1L;
    private String fatherName = "";
    private long motherId = -1L;
    private String motherName = "";
    private String maidenName = "";
    private final List<Long> childrenIds = new ArrayList<>();
    private boolean isChild = false;
    private int childSize = 0;
    private boolean pregnant = false;
    private long pregnancyStart = 0L;

    /** Ticks before a child is born after pregnancy begins (~3 Minecraft days). */
    public static final long PREGNANCY_DURATION = 72000L;
    /** Number of ticks a child takes to grow to adult size (~5 Minecraft days). */
    public static final long CHILD_GROWTH_DURATION = 120000L;
    /** Maximum number of children per couple. */
    public static final int MAX_CHILDREN = 4;

    // ========== Marriage ==========

    public boolean isMarried() {
        return spouseId >= 0;
    }

    public long getSpouseId() { return spouseId; }
    public void setSpouseId(long id) { this.spouseId = id; }

    public String getSpouseName() { return spouseName; }
    public void setSpouseName(String name) { this.spouseName = name; }

    /**
     * Establishes a marriage bond. Call on both villagers with each other's data.
     */
    public void marry(long partnerId, String partnerName) {
        this.spouseId = partnerId;
        this.spouseName = partnerName;
    }

    public void divorce() {
        this.spouseId = -1L;
        this.spouseName = "";
    }

    // ========== Parents ==========

    public long getFatherId() { return fatherId; }
    public void setFatherId(long id) { this.fatherId = id; }
    public String getFatherName() { return fatherName; }
    public void setFatherName(String name) { this.fatherName = name; }

    public long getMotherId() { return motherId; }
    public void setMotherId(long id) { this.motherId = id; }
    public String getMotherName() { return motherName; }
    public void setMotherName(String name) { this.motherName = name; }

    public String getMaidenName() { return maidenName; }
    public void setMaidenName(String name) { this.maidenName = name; }

    public boolean hasParents() {
        return fatherId >= 0 || motherId >= 0;
    }

    // ========== Children ==========

    public List<Long> getChildrenIds() { return childrenIds; }

    public void addChild(long childId) {
        if (!childrenIds.contains(childId)) {
            childrenIds.add(childId);
        }
    }

    public void removeChild(long childId) {
        childrenIds.remove(childId);
    }

    public int getChildCount() { return childrenIds.size(); }

    public boolean canHaveMoreChildren() {
        return childrenIds.size() < MAX_CHILDREN;
    }

    // ========== Child growth ==========

    public boolean isChild() { return isChild; }
    public void setChild(boolean child) { this.isChild = child; }

    public int getChildSize() { return childSize; }
    public void setChildSize(int size) { this.childSize = size; }

    // ========== Pregnancy ==========

    public boolean isPregnant() { return pregnant; }
    public void setPregnant(boolean pregnant) { this.pregnant = pregnant; }

    public long getPregnancyStart() { return pregnancyStart; }
    public void setPregnancyStart(long tick) { this.pregnancyStart = tick; }

    /**
     * Checks if pregnancy has reached term.
     * @param currentTick the current game tick
     * @return true if the baby is ready to be born
     */
    public boolean isReadyToBirth(long currentTick) {
        return pregnant && (currentTick - pregnancyStart) >= PREGNANCY_DURATION;
    }

    /**
     * Checks if this couple is eligible for pregnancy.
     */
    public boolean canBecomePregnant() {
        return isMarried() && !pregnant && !isChild && canHaveMoreChildren();
    }

    // ========== NBT ==========

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("spouseId", spouseId);
        tag.putString("spouseName", spouseName);
        tag.putLong("fatherId", fatherId);
        tag.putString("fatherName", fatherName);
        tag.putLong("motherId", motherId);
        tag.putString("motherName", motherName);
        tag.putString("maidenName", maidenName);
        tag.putBoolean("isChild", isChild);
        tag.putInt("childSize", childSize);
        tag.putBoolean("pregnant", pregnant);
        tag.putLong("pregnancyStart", pregnancyStart);

        ListTag childList = new ListTag();
        for (long childId : childrenIds) {
            CompoundTag childTag = new CompoundTag();
            childTag.putLong("id", childId);
            childList.add(childTag);
        }
        tag.put("children", childList);

        return tag;
    }

    public void load(CompoundTag tag) {
        spouseId = tag.getLong("spouseId");
        spouseName = tag.getString("spouseName");
        fatherId = tag.getLong("fatherId");
        fatherName = tag.getString("fatherName");
        motherId = tag.getLong("motherId");
        motherName = tag.getString("motherName");
        maidenName = tag.getString("maidenName");
        isChild = tag.getBoolean("isChild");
        childSize = tag.getInt("childSize");
        pregnant = tag.getBoolean("pregnant");
        pregnancyStart = tag.getLong("pregnancyStart");

        childrenIds.clear();
        ListTag childList = tag.getList("children", Tag.TAG_COMPOUND);
        for (int i = 0; i < childList.size(); i++) {
            childrenIds.add(childList.getCompound(i).getLong("id"));
        }
    }

    /**
     * Copies family data into a VillagerRecord for persistence when the entity is unloaded.
     */
    public void writeToRecord(VillagerRecord record) {
        record.spousesName = spouseName;
        record.fathersName = fatherName;
        record.mothersName = motherName;
        record.maidenName = maidenName;
    }

    /**
     * Loads family name data from a VillagerRecord.
     */
    public void readFromRecord(VillagerRecord record) {
        this.spouseName = record.spousesName;
        this.fatherName = record.fathersName;
        this.motherName = record.mothersName;
        this.maidenName = record.maidenName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("FamilyData{");
        if (isMarried()) sb.append("spouse=").append(spouseName).append("(").append(spouseId).append("), ");
        if (hasParents()) sb.append("parents=").append(fatherName).append("/").append(motherName).append(", ");
        if (!childrenIds.isEmpty()) sb.append("children=").append(childrenIds.size()).append(", ");
        if (isChild) sb.append("child(size=").append(childSize).append("), ");
        if (pregnant) sb.append("pregnant, ");
        sb.append("}");
        return sb.toString();
    }
}
