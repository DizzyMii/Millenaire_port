package org.dizzymii.millenaire2.village;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.dizzymii.millenaire2.util.Point;

/**
 * NBT serialization/deserialization for Building, extracted to keep Building.java focused.
 * Uses only the public accessor API of Building so no field visibility changes are needed.
 */
class BuildingNbt {

    // ---- NBT key constants (package-private so Building can share them if needed) ----
    static final String NBT_ACTIVE              = "active";
    static final String NBT_TOWNHALL            = "townhall";
    static final String NBT_INN                 = "inn";
    static final String NBT_MARKET              = "market";
    static final String NBT_CHEST_LOCKED        = "chestLocked";
    static final String NBT_HAS_AUTO_SPAWN      = "hasAutoSpawn";
    static final String NBT_UNDER_ATTACK        = "underAttack";
    static final String NBT_CULTURE             = "culture";
    static final String NBT_PLAN_SET_KEY        = "planSetKey";
    static final String NBT_VILLAGE_TYPE_KEY    = "villageTypeKey";
    static final String NBT_BUILDING_LEVEL      = "buildingLevel";
    static final String NBT_NAME                = "name";
    static final String NBT_QUALIFIER           = "qualifier";
    static final String NBT_POS                 = "pos";
    static final String NBT_TH                  = "th";
    static final String NBT_LOC                 = "loc";
    static final String NBT_CONTROLLED_BY       = "controlledBy";
    static final String NBT_CONTROLLED_BY_NAME  = "controlledByName";
    static final String NBT_RAID_TARGET         = "raidTarget";
    static final String NBT_ACTIVE_RAID_START   = "activeRaidStartTick";
    static final String NBT_LAST_RAID_TIME      = "lastRaidGameTime";
    static final String NBT_RAIDS_PERFORMED     = "raidsPerformed";
    static final String NBT_RAIDS_SUFFERED      = "raidsSuffered";
    static final String NBT_VILLAGERS           = "villagers";
    static final String NBT_RELATIONS           = "relations";
    static final String NBT_VR_ID               = "id";
    static final String NBT_VR_GENDER           = "gender";
    static final String NBT_VR_FIRST_NAME       = "firstName";
    static final String NBT_VR_FAMILY_NAME      = "familyName";
    static final String NBT_VR_TYPE             = "type";
    static final String NBT_VR_KILLED           = "killed";
    static final String NBT_VR_AWAY_RAIDING     = "awayraiding";
    static final String NBT_VR_AWAY_HIRED       = "awayhired";
    static final String NBT_VR_SCALE            = "scale";
    static final String NBT_VR_HOUSE            = "house";
    static final String NBT_REL_POINT           = "p";
    static final String NBT_REL_VAL             = "val";

    // ---- Serialization ----

    static CompoundTag save(Building b) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(NBT_ACTIVE,          b.isActive);
        tag.putBoolean(NBT_TOWNHALL,        b.isTownhall);
        tag.putBoolean(NBT_INN,             b.isInn);
        tag.putBoolean(NBT_MARKET,          b.isMarket);
        tag.putBoolean(NBT_CHEST_LOCKED,    b.chestLocked);
        tag.putBoolean(NBT_HAS_AUTO_SPAWN,  b.hasAutoSpawn);
        tag.putBoolean(NBT_UNDER_ATTACK,    b.underAttack);
        if (b.cultureKey != null)     tag.putString(NBT_CULTURE,          b.cultureKey);
        if (b.planSetKey != null)     tag.putString(NBT_PLAN_SET_KEY,     b.planSetKey);
        if (b.villageTypeKey != null) tag.putString(NBT_VILLAGE_TYPE_KEY, b.villageTypeKey);
        tag.putInt(NBT_BUILDING_LEVEL, b.buildingLevel);
        if (b.getName() != null)      tag.putString(NBT_NAME,      b.getName());
        tag.putString(NBT_QUALIFIER, b.getQualifier());

        if (b.getPos() != null)         b.getPos().writeToNBT(tag, NBT_POS);
        if (b.getTownHallPos() != null) b.getTownHallPos().writeToNBT(tag, NBT_TH);
        if (b.location != null)         b.location.save(tag, NBT_LOC);

        if (b.controlledBy != null) {
            tag.putUUID(NBT_CONTROLLED_BY, b.controlledBy);
            if (b.controlledByName != null) tag.putString(NBT_CONTROLLED_BY_NAME, b.controlledByName);
        }

        if (b.raidTarget != null) b.raidTarget.writeToNBT(tag, NBT_RAID_TARGET);
        tag.putLong(NBT_ACTIVE_RAID_START, b.activeRaidStartTick);
        tag.putLong(NBT_LAST_RAID_TIME,    b.lastRaidGameTime);

        ListTag raidsPerformedTag = new ListTag();
        for (String name : b.raidsPerformed) {
            CompoundTag rt = new CompoundTag(); rt.putString(NBT_NAME, name); raidsPerformedTag.add(rt);
        }
        tag.put(NBT_RAIDS_PERFORMED, raidsPerformedTag);

        ListTag raidsSufferedTag = new ListTag();
        for (String name : b.raidsSuffered) {
            CompoundTag rt = new CompoundTag(); rt.putString(NBT_NAME, name); raidsSufferedTag.add(rt);
        }
        tag.put(NBT_RAIDS_SUFFERED, raidsSufferedTag);

        // Villager records
        ListTag vrList = new ListTag();
        for (VillagerRecord vr : b.getVillagerRecords()) {
            CompoundTag vrTag = new CompoundTag();
            vrTag.putLong(NBT_VR_ID,     vr.getVillagerId());
            vrTag.putInt(NBT_VR_GENDER,  vr.gender);
            if (vr.firstName != null)   vrTag.putString(NBT_VR_FIRST_NAME,  vr.firstName);
            if (vr.familyName != null)  vrTag.putString(NBT_VR_FAMILY_NAME, vr.familyName);
            if (vr.type != null)        vrTag.putString(NBT_VR_TYPE,        vr.type);
            if (vr.getCultureKey() != null) vrTag.putString(NBT_CULTURE,    vr.getCultureKey());
            vrTag.putBoolean(NBT_VR_KILLED,       vr.killed);
            vrTag.putBoolean(NBT_VR_AWAY_RAIDING, vr.awayraiding);
            vrTag.putBoolean(NBT_VR_AWAY_HIRED,   vr.awayhired);
            vrTag.putFloat(NBT_VR_SCALE,           vr.scale);
            if (vr.getHousePos() != null)    vr.getHousePos().writeToNBT(vrTag, NBT_VR_HOUSE);
            if (vr.getTownHallPos() != null) vr.getTownHallPos().writeToNBT(vrTag, NBT_TH);
            vrList.add(vrTag);
        }
        tag.put(NBT_VILLAGERS, vrList);

        // Relations
        ListTag relList = new ListTag();
        for (Point p : b.getKnownVillages()) {
            CompoundTag relTag = new CompoundTag();
            p.writeToNBT(relTag, NBT_REL_POINT);
            relTag.putInt(NBT_REL_VAL, b.getRelation(p));
            relList.add(relTag);
        }
        tag.put(NBT_RELATIONS, relList);

        return tag;
    }

    // ---- Deserialization ----

    static Building load(CompoundTag tag) {
        Building b = new Building();
        b.isActive      = tag.getBoolean(NBT_ACTIVE);
        b.isTownhall    = tag.getBoolean(NBT_TOWNHALL);
        b.isInn         = tag.getBoolean(NBT_INN);
        b.isMarket      = tag.getBoolean(NBT_MARKET);
        b.chestLocked   = tag.getBoolean(NBT_CHEST_LOCKED);
        b.hasAutoSpawn  = tag.getBoolean(NBT_HAS_AUTO_SPAWN);
        b.underAttack   = tag.getBoolean(NBT_UNDER_ATTACK);
        if (tag.contains(NBT_CULTURE))          b.cultureKey     = tag.getString(NBT_CULTURE);
        if (tag.contains(NBT_PLAN_SET_KEY))     b.planSetKey     = tag.getString(NBT_PLAN_SET_KEY);
        if (tag.contains(NBT_VILLAGE_TYPE_KEY)) b.villageTypeKey = tag.getString(NBT_VILLAGE_TYPE_KEY);
        b.buildingLevel = tag.getInt(NBT_BUILDING_LEVEL);
        if (tag.contains(NBT_NAME))      b.setName(tag.getString(NBT_NAME));
        b.setQualifier(tag.getString(NBT_QUALIFIER));

        b.setPos(Point.readFromNBT(tag, NBT_POS));
        b.setTownHallPos(Point.readFromNBT(tag, NBT_TH));
        b.location = BuildingLocation.read(tag, NBT_LOC);

        if (tag.hasUUID(NBT_CONTROLLED_BY)) {
            b.controlledBy = tag.getUUID(NBT_CONTROLLED_BY);
            if (tag.contains(NBT_CONTROLLED_BY_NAME)) b.controlledByName = tag.getString(NBT_CONTROLLED_BY_NAME);
        }

        b.raidTarget         = Point.readFromNBT(tag, NBT_RAID_TARGET);
        b.activeRaidStartTick = tag.contains(NBT_ACTIVE_RAID_START) ? tag.getLong(NBT_ACTIVE_RAID_START) : -1L;
        b.lastRaidGameTime   = tag.contains(NBT_LAST_RAID_TIME)  ? tag.getLong(NBT_LAST_RAID_TIME)  : -1L;

        if (tag.contains(NBT_RAIDS_PERFORMED, Tag.TAG_LIST)) {
            ListTag rp = tag.getList(NBT_RAIDS_PERFORMED, Tag.TAG_COMPOUND);
            for (int i = 0; i < rp.size(); i++) b.raidsPerformed.add(rp.getCompound(i).getString(NBT_NAME));
        }
        if (tag.contains(NBT_RAIDS_SUFFERED, Tag.TAG_LIST)) {
            ListTag rs = tag.getList(NBT_RAIDS_SUFFERED, Tag.TAG_COMPOUND);
            for (int i = 0; i < rs.size(); i++) b.raidsSuffered.add(rs.getCompound(i).getString(NBT_NAME));
        }

        // Villager records
        if (tag.contains(NBT_VILLAGERS, Tag.TAG_LIST)) {
            ListTag vrList = tag.getList(NBT_VILLAGERS, Tag.TAG_COMPOUND);
            for (int i = 0; i < vrList.size(); i++) {
                CompoundTag vrTag = vrList.getCompound(i);
                VillagerRecord vr = new VillagerRecord();
                vr.setVillagerId(vrTag.getLong(NBT_VR_ID));
                vr.gender = vrTag.getInt(NBT_VR_GENDER);
                if (vrTag.contains(NBT_VR_FIRST_NAME))  vr.firstName  = vrTag.getString(NBT_VR_FIRST_NAME);
                if (vrTag.contains(NBT_VR_FAMILY_NAME)) vr.familyName = vrTag.getString(NBT_VR_FAMILY_NAME);
                if (vrTag.contains(NBT_VR_TYPE))        vr.type       = vrTag.getString(NBT_VR_TYPE);
                if (vrTag.contains(NBT_CULTURE))        vr.setCultureKey(vrTag.getString(NBT_CULTURE));
                vr.killed      = vrTag.getBoolean(NBT_VR_KILLED);
                vr.awayraiding = vrTag.getBoolean(NBT_VR_AWAY_RAIDING);
                vr.awayhired   = vrTag.getBoolean(NBT_VR_AWAY_HIRED);
                vr.scale       = vrTag.getFloat(NBT_VR_SCALE);
                vr.setHousePos(Point.readFromNBT(vrTag, NBT_VR_HOUSE));
                vr.setTownHallPos(Point.readFromNBT(vrTag, NBT_TH));
                b.addVillagerRecord(vr);
            }
        }

        // Relations
        if (tag.contains(NBT_RELATIONS, Tag.TAG_LIST)) {
            ListTag relList = tag.getList(NBT_RELATIONS, Tag.TAG_COMPOUND);
            for (int i = 0; i < relList.size(); i++) {
                CompoundTag relTag = relList.getCompound(i);
                Point p = Point.readFromNBT(relTag, NBT_REL_POINT);
                if (p != null) b.setRelation(p, relTag.getInt(NBT_REL_VAL));
            }
        }

        return b;
    }
}
