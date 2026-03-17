/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.WorldServer
 */
package org.millenaire.common.village;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillagerType;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.entity.VillagerConfig;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;
import org.millenaire.common.world.MillWorldData;

public class VillagerRecord
implements Cloneable {
    private static final double RIGHT_HANDED_CHANCE = 0.8;
    private Culture culture;
    public String fathersName = "";
    public String mothersName = "";
    public String spousesName = "";
    public String maidenName = "";
    public boolean flawedRecord = false;
    public boolean killed = false;
    public boolean raidingVillage = false;
    public boolean awayraiding = false;
    public boolean awayhired = false;
    private Point housePos;
    private Point townHallPos;
    public Point originalVillagePos;
    private long villagerId;
    public long raiderSpawn = 0L;
    public int nb;
    public int gender;
    public int size;
    public float scale = 1.0f;
    public boolean rightHanded = true;
    public HashMap<InvItem, Integer> inventory = new HashMap();
    public List<String> questTags = new ArrayList<String>();
    public String type;
    public String firstName;
    public String familyName;
    public ResourceLocation texture;
    private Building house;
    private Building townHall;
    private Building originalVillage;
    public MillWorldData mw;
    private long originalId = -1L;

    public static VillagerRecord createVillagerRecord(Culture c, String type, MillWorldData worldData, Point housePos, Point thPos, String firstName, String familyName, long villagerId, boolean mockVillager) {
        VillagerType vtype;
        if (!(mockVillager || !worldData.world.field_72995_K && worldData.world instanceof WorldServer)) {
            MillLog.printException("Tried creating a villager record in client world: " + worldData.world, new Exception());
            return null;
        }
        VillagerRecord villagerRecord = new VillagerRecord(worldData);
        if (type == null || type.length() == 0) {
            MillLog.error(null, "Tried creating villager of null type: " + type);
        }
        if (c.getVillagerType(type.toLowerCase()) == null) {
            for (Culture c2 : Culture.ListCultures) {
                if (c2.getVillagerType(type) == null) continue;
                MillLog.error(null, "Could not find villager type " + type + " in culture " + c.key + " but could in " + c2.key + " so switching.");
                c = c2;
            }
        }
        villagerRecord.setCulture(c);
        if (c.getVillagerType(type.toLowerCase()) != null) {
            vtype = c.getVillagerType(type.toLowerCase());
            villagerRecord.type = vtype.key;
            if (!mockVillager) {
                villagerRecord.setHousePos(housePos);
                villagerRecord.setTownHallPos(thPos);
            }
            if (familyName != null) {
                villagerRecord.familyName = familyName;
            } else {
                Set<Object> namesTaken = thPos != null ? worldData.getBuilding(thPos).getAllFamilyNames() : new HashSet();
                villagerRecord.familyName = vtype.getRandomFamilyName(namesTaken);
            }
            villagerRecord.firstName = firstName != null ? firstName : vtype.getRandomFirstName();
            if (villagerId == -1L) {
                villagerRecord.setVillagerId(Math.abs(MillCommonUtilities.randomLong()));
            } else {
                villagerRecord.setVillagerId(villagerId);
            }
        } else {
            MillLog.error(null, "Unknown villager type: " + type + " for culture " + c);
            return null;
        }
        villagerRecord.gender = vtype.gender;
        villagerRecord.texture = vtype.getNewTexture();
        VillagerRecord.initialisePersonalizedData(villagerRecord, vtype);
        villagerRecord.rightHanded = MillCommonUtilities.random.nextDouble() < 0.8;
        if (MillConfigValues.LogVillagerSpawn >= 1) {
            MillLog.major(villagerRecord, "Created new villager record.");
        }
        if (!mockVillager) {
            worldData.registerVillagerRecord(villagerRecord, true);
        }
        return villagerRecord;
    }

    private static void initialisePersonalizedData(VillagerRecord villagerRecord, VillagerType vtype) {
        if (vtype.isChild) {
            villagerRecord.size = 0;
            villagerRecord.scale = villagerRecord.getType().baseScale;
        } else {
            villagerRecord.scale = villagerRecord.getType().baseScale * ((80.0f + (float)MillCommonUtilities.randomInt(10)) / 100.0f);
        }
    }

    public static VillagerRecord read(MillWorldData mw, NBTTagCompound nbttagcompound, String label) {
        NBTTagCompound nbttagcompound1;
        int i;
        if (!nbttagcompound.func_74764_b(label + "_id") && !nbttagcompound.func_74764_b(label + "_lid")) {
            return null;
        }
        VillagerRecord vr = new VillagerRecord(mw, Culture.getCultureByName(nbttagcompound.func_74779_i(label + "_culture")));
        if (nbttagcompound.func_74764_b(label + "_lid")) {
            vr.setVillagerId(Math.abs(nbttagcompound.func_74763_f(label + "_lid")));
        }
        vr.nb = nbttagcompound.func_74762_e(label + "_nb");
        vr.gender = nbttagcompound.func_74762_e(label + "_gender");
        vr.type = nbttagcompound.func_74779_i(label + "_type").toLowerCase();
        vr.raiderSpawn = nbttagcompound.func_74763_f(label + "_raiderSpawn");
        vr.firstName = nbttagcompound.func_74779_i(label + "_firstName");
        vr.familyName = nbttagcompound.func_74779_i(label + "_familyName");
        String texture = nbttagcompound.func_74779_i(label + "_texture");
        vr.texture = texture.contains(":") ? new ResourceLocation(texture) : new ResourceLocation("millenaire", texture);
        vr.setHousePos(Point.read(nbttagcompound, label + "_housePos"));
        vr.setTownHallPos(Point.read(nbttagcompound, label + "_townHallPos"));
        vr.originalId = nbttagcompound.func_74763_f(label + "_originalId");
        vr.originalVillagePos = Point.read(nbttagcompound, label + "_originalVillagePos");
        vr.size = nbttagcompound.func_74762_e(label + "_size");
        vr.scale = nbttagcompound.func_74760_g(label + "_scale");
        if (nbttagcompound.func_74764_b(label + "_rightHanded")) {
            vr.rightHanded = nbttagcompound.func_74767_n(label + "_rightHanded");
        }
        vr.fathersName = nbttagcompound.func_74779_i(label + "_fathersName");
        vr.mothersName = nbttagcompound.func_74779_i(label + "_mothersName");
        vr.maidenName = nbttagcompound.func_74779_i(label + "_maidenName");
        vr.spousesName = nbttagcompound.func_74779_i(label + "_spousesName");
        vr.killed = nbttagcompound.func_74767_n(label + "_killed");
        vr.raidingVillage = nbttagcompound.func_74767_n(label + "_raidingVillage");
        vr.awayraiding = nbttagcompound.func_74767_n(label + "_awayraiding");
        vr.awayhired = nbttagcompound.func_74767_n(label + "_awayhired");
        NBTTagList nbttaglist = nbttagcompound.func_150295_c(label + "questTags", 10);
        for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
            nbttagcompound1 = nbttaglist.func_150305_b(i);
            vr.questTags.add(nbttagcompound1.func_74779_i("tag"));
        }
        nbttaglist = nbttagcompound.func_150295_c(label + "_inventory", 10);
        for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
            nbttagcompound1 = nbttaglist.func_150305_b(i);
            vr.inventory.put(InvItem.createInvItem(Item.func_150899_d((int)nbttagcompound1.func_74762_e("item")), nbttagcompound1.func_74762_e("meta")), nbttagcompound1.func_74762_e("amount"));
        }
        nbttaglist = nbttagcompound.func_150295_c(label + "_inventoryNew", 10);
        MillCommonUtilities.readInventory(nbttaglist, vr.inventory);
        if (vr.getType() == null) {
            MillLog.error(vr, "Could not find type " + vr.type + " for VR. Skipping.");
            return null;
        }
        if (vr.scale == 0.0f || vr.scale == 1.0f) {
            VillagerRecord.initialisePersonalizedData(vr, vr.getType());
        }
        return vr;
    }

    public VillagerRecord(MillWorldData mw) {
        this.mw = mw;
    }

    private VillagerRecord(MillWorldData mw, Culture c) {
        this.setCulture(c);
        this.mw = mw;
    }

    public VillagerRecord(MillWorldData mw, MillVillager v) {
        this.mw = mw;
        this.setCulture(v.getCulture());
        this.setVillagerId(v.getVillagerId());
        if (v.vtype != null) {
            this.type = v.vtype.key;
        }
        this.firstName = v.firstName;
        this.familyName = v.familyName;
        this.gender = v.gender;
        this.nb = 1;
        this.texture = v.getTexture();
        this.setHousePos(v.housePoint);
        this.setTownHallPos(v.townHallPoint);
        this.raidingVillage = v.isRaider;
        for (InvItem iv : v.getInventoryKeys()) {
            this.inventory.put(iv, v.countInv(iv));
        }
        if (this.getHousePos() == null) {
            MillLog.error(this, "Creation constructor: House position in record is null.");
            this.flawedRecord = true;
        }
    }

    public VillagerRecord clone() {
        try {
            return (VillagerRecord)super.clone();
        }
        catch (CloneNotSupportedException e) {
            MillLog.printException(e);
            return null;
        }
    }

    public int countInv(InvItem invItem) {
        if (this.inventory.containsKey(invItem)) {
            return this.inventory.get(invItem);
        }
        return 0;
    }

    public int countInv(Item item) {
        return this.countInv(item, 0);
    }

    public int countInv(Item item, int meta) {
        InvItem key = InvItem.createInvItem(item, meta);
        return this.countInv(key);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof VillagerRecord)) {
            return false;
        }
        VillagerRecord other = (VillagerRecord)obj;
        return other.getVillagerId() == this.getVillagerId();
    }

    public VillagerRecord generateRaidRecord(Building target) {
        VillagerRecord raidRecord = this.clone();
        raidRecord.setVillagerId(Math.abs(MillCommonUtilities.randomLong()));
        raidRecord.setHousePos(target.getPos());
        raidRecord.setTownHallPos(target.getTownHall().getPos());
        raidRecord.townHall = target.getTownHall();
        raidRecord.house = target;
        raidRecord.raidingVillage = true;
        raidRecord.awayraiding = false;
        raidRecord.originalVillagePos = this.getTownHall().getPos();
        raidRecord.originalId = this.getVillagerId();
        raidRecord.raiderSpawn = this.getTownHall().world.func_72820_D();
        return raidRecord;
    }

    public InvItem getArmourPiece(EntityEquipmentSlot slotIn) {
        if (slotIn == EntityEquipmentSlot.HEAD) {
            for (InvItem item : this.getConfig().armoursHelmetSorted) {
                if (this.countInv(item) <= 0) continue;
                return item;
            }
            return null;
        }
        if (slotIn == EntityEquipmentSlot.CHEST) {
            for (InvItem item : this.getConfig().armoursChestplateSorted) {
                if (this.countInv(item) <= 0) continue;
                return item;
            }
            return null;
        }
        if (slotIn == EntityEquipmentSlot.LEGS) {
            for (InvItem item : this.getConfig().armoursLeggingsSorted) {
                if (this.countInv(item) <= 0) continue;
                return item;
            }
            return null;
        }
        if (slotIn == EntityEquipmentSlot.FEET) {
            for (InvItem item : this.getConfig().armoursBootsSorted) {
                if (this.countInv(item) <= 0) continue;
                return item;
            }
            return null;
        }
        return null;
    }

    public Item getBestMeleeWeapon() {
        double max = 1.0;
        Item best = null;
        for (InvItem item : this.inventory.keySet()) {
            if (this.inventory.get(item) <= 0) continue;
            if (item.getItem() == null) {
                MillLog.error(this, "Attempting to check null melee weapon with id: " + this.inventory.get(item));
                continue;
            }
            if (!(MillCommonUtilities.getItemWeaponDamage(item.getItem()) > max)) continue;
            max = MillCommonUtilities.getItemWeaponDamage(item.getItem());
            best = item.getItem();
        }
        if (this.getType() != null && this.getType().startingWeapon != null && MillCommonUtilities.getItemWeaponDamage(this.getType().startingWeapon.getItem()) > max) {
            max = MillCommonUtilities.getItemWeaponDamage(this.getType().startingWeapon.getItem());
            best = this.getType().startingWeapon.getItem();
        }
        return best;
    }

    public VillagerConfig getConfig() {
        return this.getType().villagerConfig;
    }

    public Culture getCulture() {
        return this.culture;
    }

    public String getGameOccupation() {
        String game;
        if (this.getCulture() == null || this.getCulture().getVillagerType(this.type) == null) {
            return "";
        }
        String s = this.getCulture().getVillagerType((String)this.type).name;
        if (this.getCulture().canReadVillagerNames() && !(game = this.getCulture().getCultureString("villager." + this.getNameKey())).equals("")) {
            s = s + " (" + game + ")";
        }
        return s;
    }

    public Building getHouse() {
        if (this.house != null) {
            return this.house;
        }
        if (MillConfigValues.LogVillager >= 3) {
            MillLog.debug(this, "Seeking uncached house");
        }
        this.house = this.mw.getBuilding(this.getHousePos());
        return this.house;
    }

    public Point getHousePos() {
        return this.housePos;
    }

    public int getMaxHealth() {
        if (this.getType() == null) {
            return 20;
        }
        if (this.getType().isChild) {
            return 10 + this.size / 2;
        }
        return this.getType().health;
    }

    public int getMilitaryStrength() {
        int strength = this.getMaxHealth() / 2;
        int attack = this.getType().baseAttackStrength;
        Item bestMelee = this.getBestMeleeWeapon();
        if (bestMelee != null) {
            attack = (int)((double)attack + MillCommonUtilities.getItemWeaponDamage(bestMelee));
        }
        strength += attack * 2;
        if (this.getType().isArcher && this.countInv((Item)Items.field_151031_f) > 0 || this.countInv((Item)MillItems.YUMI_BOW) > 0) {
            strength += 10;
        }
        return strength += this.getTotalArmorValue() * 2;
    }

    public String getName() {
        return this.firstName + " " + this.familyName;
    }

    public String getNameKey() {
        if (this.getType().isChild && this.size == 20) {
            return this.getType().altkey;
        }
        return this.getType().key;
    }

    public String getNativeOccupationName() {
        if (this.getType().isChild && this.size == 20) {
            return this.getType().altname;
        }
        return this.getType().name;
    }

    public long getOriginalId() {
        return this.originalId;
    }

    public Building getOriginalVillage() {
        if (this.originalVillage != null) {
            return this.originalVillage;
        }
        if (MillConfigValues.LogVillager >= 3) {
            MillLog.debug(this, "Seeking uncached originalVillage");
        }
        this.originalVillage = this.mw.getBuilding(this.originalVillagePos);
        return this.originalVillage;
    }

    public int getTotalArmorValue() {
        int total = 0;
        for (EntityEquipmentSlot slot : new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET}) {
            InvItem armour = this.getArmourPiece(slot);
            if (armour == null || !(armour.getItem() instanceof ItemArmor)) continue;
            total += ((ItemArmor)armour.getItem()).field_77879_b;
        }
        return total;
    }

    public Building getTownHall() {
        if (this.townHall != null) {
            return this.townHall;
        }
        if (MillConfigValues.LogVillager >= 3) {
            MillLog.debug(this, "Seeking uncached townHall");
        }
        this.townHall = this.mw.getBuilding(this.getTownHallPos());
        return this.townHall;
    }

    public Point getTownHallPos() {
        return this.townHallPos;
    }

    public VillagerType getType() {
        if (this.getCulture().getVillagerType(this.type) == null) {
            for (Culture c : Culture.ListCultures) {
                if (c.getVillagerType(this.type) == null) continue;
                MillLog.error(this, "Could not find villager type " + this.type + " in culture " + this.getCulture().key + " but could in " + c.key + " so switching.");
                this.setCulture(c);
            }
        }
        return this.getCulture().getVillagerType(this.type);
    }

    public long getVillagerId() {
        return this.villagerId;
    }

    public int hashCode() {
        return Long.valueOf(this.getVillagerId()).hashCode();
    }

    public boolean isTextureValid(String texture) {
        if (this.getType() != null) {
            return this.getType().isTextureValid(texture);
        }
        return true;
    }

    public boolean matches(MillVillager v) {
        return this.getVillagerId() == v.getVillagerId();
    }

    public void setCulture(Culture culture) {
        this.culture = culture;
    }

    public void setHousePos(Point housePos) {
        this.housePos = housePos;
        this.house = null;
    }

    public void setTownHallPos(Point townHallPos) {
        this.townHallPos = townHallPos;
        this.townHall = null;
    }

    public void setVillagerId(long id) {
        this.villagerId = id;
    }

    public String toString() {
        return this.firstName + " " + this.familyName + "/" + this.type + "/" + this.getVillagerId();
    }

    public void updateRecord(MillVillager v) {
        if (v.vtype != null) {
            this.type = v.vtype.key;
        }
        this.firstName = v.firstName;
        this.familyName = v.familyName;
        this.gender = v.gender;
        this.nb = 1;
        this.texture = v.getTexture();
        this.setHousePos(v.housePoint);
        this.setTownHallPos(v.townHallPoint);
        this.raidingVillage = v.isRaider;
        this.killed = v.isReallyDead();
        if (this.getHousePos() == null) {
            MillLog.error(this, "updateRecord(): House position in record is null.");
            this.flawedRecord = true;
        }
        this.inventory.clear();
        for (InvItem iv : v.getInventoryKeys()) {
            this.inventory.put(iv, v.countInv(iv));
        }
    }

    public void write(NBTTagCompound nbttagcompound, String label) {
        nbttagcompound.func_74772_a(label + "_lid", this.getVillagerId());
        nbttagcompound.func_74768_a(label + "_nb", this.nb);
        nbttagcompound.func_74778_a(label + "_type", this.type);
        nbttagcompound.func_74778_a(label + "_firstName", this.firstName);
        nbttagcompound.func_74778_a(label + "_familyName", this.familyName);
        if (this.fathersName != null && this.fathersName.length() > 0) {
            nbttagcompound.func_74778_a(label + "_fathersName", this.fathersName);
        }
        if (this.mothersName != null && this.mothersName.length() > 0) {
            nbttagcompound.func_74778_a(label + "_mothersName", this.mothersName);
        }
        if (this.maidenName != null && this.maidenName.length() > 0) {
            nbttagcompound.func_74778_a(label + "_maidenName", this.maidenName);
        }
        if (this.spousesName != null && this.spousesName.length() > 0) {
            nbttagcompound.func_74778_a(label + "_spousesName", this.spousesName);
        }
        nbttagcompound.func_74768_a(label + "_gender", this.gender);
        nbttagcompound.func_74778_a(label + "_texture", this.texture.toString());
        nbttagcompound.func_74757_a(label + "_killed", this.killed);
        nbttagcompound.func_74757_a(label + "_raidingVillage", this.raidingVillage);
        nbttagcompound.func_74757_a(label + "_awayraiding", this.awayraiding);
        nbttagcompound.func_74757_a(label + "_awayhired", this.awayhired);
        nbttagcompound.func_74772_a(label + "_raiderSpawn", this.raiderSpawn);
        if (this.getHousePos() != null) {
            this.getHousePos().write(nbttagcompound, label + "_housePos");
        }
        if (this.getTownHallPos() != null) {
            this.getTownHallPos().write(nbttagcompound, label + "_townHallPos");
        }
        nbttagcompound.func_74772_a(label + "_originalId", this.originalId);
        if (this.originalVillagePos != null) {
            this.originalVillagePos.write(nbttagcompound, label + "_originalVillagePos");
        }
        nbttagcompound.func_74768_a(label + "_size", this.size);
        nbttagcompound.func_74776_a(label + "_scale", this.scale);
        nbttagcompound.func_74757_a(label + "_rightHanded", this.rightHanded);
        NBTTagList nbttaglist = new NBTTagList();
        for (String tag : this.questTags) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.func_74778_a("tag", tag);
            nbttaglist.func_74742_a((NBTBase)nbttagcompound1);
        }
        nbttagcompound.func_74782_a(label + "questTags", (NBTBase)nbttaglist);
        nbttaglist = MillCommonUtilities.writeInventory(this.inventory);
        nbttagcompound.func_74782_a(label + "_inventoryNew", (NBTBase)nbttaglist);
        nbttagcompound.func_74778_a(label + "_culture", this.getCulture().key);
    }
}

