package org.dizzymii.millenaire2.village;

import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    // TODO: NBT read/write, full factory method, equipment resolution

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
