/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.EnumDyeColor
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.world.World
 */
package org.millenaire.common.village;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import org.millenaire.common.buildingplan.BuildingCustomPlan;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.buildingplan.IBuildingPlan;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;

public class BuildingLocation
implements Cloneable {
    public String planKey;
    public String shop;
    public int priorityMoveIn = 10;
    public int minx;
    public int maxx;
    public int minz;
    public int maxz;
    public int miny;
    public int maxy;
    public int minxMargin;
    public int maxxMargin;
    public int minyMargin;
    public int maxyMargin;
    public int minzMargin;
    public int maxzMargin;
    public int orientation;
    public int length;
    public int width;
    public int level;
    public int reputation;
    public int price;
    public int version;
    private int variation;
    public boolean isCustomBuilding = false;
    public Point pos;
    public Point chestPos = null;
    public Point sleepingPos = null;
    public Point sellingPos = null;
    public Point craftingPos = null;
    public Point shelterPos = null;
    public Point defendingPos = null;
    public Culture culture;
    public CopyOnWriteArrayList<String> subBuildings;
    public boolean upgradesAllowed = true;
    public boolean bedrocklevel = false;
    public boolean showTownHallSigns;
    public boolean isSubBuildingLocation = false;
    public final Map<EnumDyeColor, EnumDyeColor> paintedBricksColour = new HashMap<EnumDyeColor, EnumDyeColor>();

    private static EnumDyeColor getColourByName(String colourName) {
        for (EnumDyeColor color : EnumDyeColor.values()) {
            if (!color.func_176610_l().equals(colourName)) continue;
            return color;
        }
        return null;
    }

    public static BuildingLocation read(NBTTagCompound nbttagcompound, String label, String debug, Building building) {
        NBTTagCompound nbttagcompound1;
        int i;
        NBTTagList nbttaglist;
        Culture culture;
        if (!nbttagcompound.func_74764_b(label + "_key")) {
            return null;
        }
        BuildingLocation bl = new BuildingLocation();
        bl.pos = Point.read(nbttagcompound, label + "_pos");
        if (nbttagcompound.func_74764_b(label + "_isCustomBuilding")) {
            bl.isCustomBuilding = nbttagcompound.func_74767_n(label + "_isCustomBuilding");
        }
        bl.culture = culture = Culture.getCultureByName(nbttagcompound.func_74779_i(label + "_culture"));
        bl.orientation = nbttagcompound.func_74762_e(label + "_orientation");
        bl.length = nbttagcompound.func_74762_e(label + "_length");
        bl.width = nbttagcompound.func_74762_e(label + "_width");
        bl.minx = nbttagcompound.func_74762_e(label + "_minx");
        bl.miny = nbttagcompound.func_74762_e(label + "_miny");
        bl.minz = nbttagcompound.func_74762_e(label + "_minz");
        bl.maxx = nbttagcompound.func_74762_e(label + "_maxx");
        bl.maxy = nbttagcompound.func_74762_e(label + "_maxy");
        bl.maxz = nbttagcompound.func_74762_e(label + "_maxz");
        bl.level = nbttagcompound.func_74762_e(label + "_level");
        bl.planKey = nbttagcompound.func_74779_i(label + "_key");
        bl.shop = nbttagcompound.func_74779_i(label + "_shop");
        bl.setVariation(nbttagcompound.func_74762_e(label + "_variation"));
        bl.reputation = nbttagcompound.func_74762_e(label + "_reputation");
        bl.priorityMoveIn = nbttagcompound.func_74762_e(label + "_priorityMoveIn");
        bl.price = nbttagcompound.func_74762_e(label + "_price");
        bl.version = nbttagcompound.func_74762_e(label + "_version");
        if (bl.pos == null) {
            MillLog.error(null, "Null point loaded for: " + label + "_pos");
        }
        bl.sleepingPos = Point.read(nbttagcompound, label + "_standingPos");
        bl.sellingPos = Point.read(nbttagcompound, label + "_sellingPos");
        bl.craftingPos = Point.read(nbttagcompound, label + "_craftingPos");
        bl.shelterPos = Point.read(nbttagcompound, label + "_shelterPos");
        bl.defendingPos = Point.read(nbttagcompound, label + "_defendingPos");
        bl.chestPos = Point.read(nbttagcompound, label + "_chestPos");
        if (building != null) {
            ArrayList<String> tags = new ArrayList<String>();
            nbttaglist = nbttagcompound.func_150295_c(label + "_tags", 10);
            for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
                nbttagcompound1 = nbttaglist.func_150305_b(i);
                String value = nbttagcompound1.func_74779_i("value");
                tags.add(value);
                if (MillConfigValues.LogTags < 2) continue;
                MillLog.minor(bl, "Loading tag: " + value);
            }
            building.addTags(tags, "loading from location NBT");
            if (building.getTags().size() > 0 && MillConfigValues.LogTags >= 1) {
                MillLog.major(bl, "Tags loaded from location NBT: " + MillCommonUtilities.flattenStrings(building.getTags()));
            }
        }
        CopyOnWriteArrayList<String> subb = new CopyOnWriteArrayList<String>();
        nbttaglist = nbttagcompound.func_150295_c("subBuildings", 10);
        for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
            nbttagcompound1 = nbttaglist.func_150305_b(i);
            subb.add(nbttagcompound1.func_74779_i("value"));
        }
        nbttaglist = nbttagcompound.func_150295_c(label + "_subBuildings", 10);
        for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
            nbttagcompound1 = nbttaglist.func_150305_b(i);
            subb.add(nbttagcompound1.func_74779_i("value"));
        }
        bl.subBuildings = subb;
        bl.showTownHallSigns = nbttagcompound.func_74767_n(label + "_showTownHallSigns");
        if (nbttagcompound.func_74764_b(label + "_upgradesAllowed")) {
            bl.upgradesAllowed = nbttagcompound.func_74767_n(label + "_upgradesAllowed");
        }
        bl.isSubBuildingLocation = nbttagcompound.func_74767_n(label + "_isSubBuildingLocation");
        nbttaglist = nbttagcompound.func_150295_c(label + "_paintedBricksColour_keys", 10);
        for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
            nbttagcompound1 = nbttaglist.func_150305_b(i);
            EnumDyeColor color = BuildingLocation.getColourByName(nbttagcompound1.func_74779_i("value"));
            bl.paintedBricksColour.put(color, BuildingLocation.getColourByName(nbttagcompound.func_74779_i(label + "_paintedBricksColour_" + color.func_176610_l())));
        }
        if (culture.getBuildingPlanSet(bl.planKey) != null) {
            if (culture.getBuildingPlanSet((String)bl.planKey).plans.size() <= bl.getVariation()) {
                MillLog.error(bl, "Loaded with a building variation of " + bl.getVariation() + " but max for this building is " + (culture.getBuildingPlanSet((String)bl.planKey).plans.size() - 1) + ". Setting to 0.");
                bl.setVariation(0);
                bl.level = culture.getBuildingPlanSet((String)bl.planKey).plans.get(bl.getVariation()).length - 1;
            }
            if (culture.getBuildingPlanSet((String)bl.planKey).plans.get(bl.getVariation()).length <= bl.level) {
                MillLog.error(bl, "Loaded with a building level of " + bl.level + " but max for this building is " + (culture.getBuildingPlanSet((String)bl.planKey).plans.get(bl.getVariation()).length - 1) + ". Setting to max.");
                bl.level = culture.getBuildingPlanSet((String)bl.planKey).plans.get(bl.getVariation()).length - 1;
            }
        }
        if (bl.getPlan() == null && bl.getCustomPlan() == null) {
            MillLog.error(bl, "Unknown building type: " + bl.planKey + " Cancelling load.");
            return null;
        }
        if (bl.isCustomBuilding) {
            bl.initialisePlan();
        } else {
            bl.computeMargins();
        }
        return bl;
    }

    public BuildingLocation() {
    }

    public BuildingLocation(BuildingCustomPlan customBuilding, Point pos, boolean isTownHall) {
        this.pos = pos;
        this.chestPos = pos;
        this.orientation = 0;
        this.planKey = customBuilding.buildingKey;
        this.isCustomBuilding = true;
        this.level = 0;
        this.subBuildings = new CopyOnWriteArrayList();
        this.setVariation(0);
        this.shop = customBuilding.shop;
        this.reputation = 0;
        this.price = 0;
        this.version = 0;
        this.showTownHallSigns = isTownHall;
        this.culture = customBuilding.culture;
        this.priorityMoveIn = customBuilding.priorityMoveIn;
    }

    public BuildingLocation(BuildingPlan plan, Point ppos, int porientation) {
        this.pos = ppos;
        if (this.pos == null) {
            MillLog.error(this, "Attempting to create a location with a null position.");
        }
        this.orientation = porientation;
        this.length = plan.length;
        this.width = plan.width;
        this.planKey = plan.buildingKey;
        this.level = plan.level;
        this.subBuildings = new CopyOnWriteArrayList<String>(plan.subBuildings);
        this.setVariation(plan.variation);
        this.shop = plan.shop;
        this.reputation = plan.reputation;
        this.price = plan.price;
        this.version = plan.version;
        this.showTownHallSigns = plan.showTownHallSigns;
        this.culture = plan.culture;
        this.priorityMoveIn = plan.priorityMoveIn;
        this.initialiseRandomBrickColoursFromPlan(plan);
        if (!this.isCustomBuilding && plan.culture != null) {
            this.initialisePlan();
        }
    }

    public BuildingLocation clone() {
        try {
            BuildingLocation bl = (BuildingLocation)super.clone();
            bl.subBuildings = new CopyOnWriteArrayList<String>(this.subBuildings);
            return bl;
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public void computeMargins() {
        this.minxMargin = this.minx - MillConfigValues.minDistanceBetweenBuildings + 1;
        this.minzMargin = this.minz - MillConfigValues.minDistanceBetweenBuildings + 1;
        this.minyMargin = this.miny - 3;
        this.maxyMargin = this.maxy + 1;
        this.maxxMargin = this.maxx + MillConfigValues.minDistanceBetweenBuildings + 1;
        this.maxzMargin = this.maxz + MillConfigValues.minDistanceBetweenBuildings + 1;
    }

    public boolean containsPlanTag(String tag) {
        BuildingPlan plan = this.getPlan();
        if (plan == null) {
            return false;
        }
        return plan.containsTags(tag);
    }

    public BuildingLocation createLocationForAlternateBuilding(String alternateKey) {
        BuildingPlan plan = this.culture.getBuildingPlanSet(alternateKey).getRandomStartingPlan();
        BuildingLocation bl = this.clone();
        bl.planKey = alternateKey;
        bl.level = -1;
        bl.shop = plan.shop;
        bl.reputation = plan.reputation;
        bl.price = plan.price;
        bl.version = plan.version;
        bl.showTownHallSigns = plan.showTownHallSigns;
        bl.subBuildings = new CopyOnWriteArrayList<String>(plan.subBuildings);
        bl.setVariation(plan.variation);
        bl.priorityMoveIn = plan.priorityMoveIn;
        bl.paintedBricksColour.putAll(this.paintedBricksColour);
        if (!this.isCustomBuilding && plan.culture != null) {
            this.initialisePlan();
        }
        return bl;
    }

    public BuildingLocation createLocationForLevel(int plevel) {
        BuildingPlan plan = this.culture.getBuildingPlanSet((String)this.planKey).plans.get(this.getVariation())[plevel];
        BuildingLocation bl = this.clone();
        bl.level = plevel;
        bl.subBuildings = new CopyOnWriteArrayList<String>(plan.subBuildings);
        return bl;
    }

    public BuildingLocation createLocationForStartingSubBuilding(String subkey) {
        BuildingLocation bl = this.createLocationForSubBuilding(subkey);
        bl.level = 0;
        return bl;
    }

    public BuildingLocation createLocationForSubBuilding(String subkey) {
        BuildingLocation bl = this.createLocationForAlternateBuilding(subkey);
        bl.isSubBuildingLocation = true;
        return bl;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof BuildingLocation)) {
            return false;
        }
        BuildingLocation bl = (BuildingLocation)obj;
        return this.planKey.equals(bl.planKey) && this.level == bl.level && this.pos.equals(bl.pos) && this.orientation == bl.orientation && this.getVariation() == bl.getVariation();
    }

    public Building getBuilding(World world) {
        return Mill.getMillWorld(world).getBuilding(this.chestPos);
    }

    public List<String> getBuildingEffects(World world) {
        BuildingPlan plan;
        ArrayList<String> effects = new ArrayList<String>();
        Building building = this.getBuilding(world);
        if (building != null) {
            if (building.isTownhall) {
                effects.add(LanguageUtilities.string("effect.towncentre"));
            }
            if (building.containsTags("pujas")) {
                effects.add(LanguageUtilities.string("effect.pujalocation"));
            }
            if (building.containsTags("sacrifices")) {
                effects.add(LanguageUtilities.string("effect.sacrificeslocation"));
            }
        }
        if (this.shop != null && this.shop.length() > 0) {
            effects.add(LanguageUtilities.string("effect.shop", this.culture.getCultureString("shop." + this.shop)));
        }
        if ((plan = this.getPlan()) != null && plan.irrigation > 0) {
            effects.add(LanguageUtilities.string("effect.irrigation", "" + plan.irrigation));
        }
        if (building != null && building.getResManager().healingspots.size() > 0) {
            effects.add(LanguageUtilities.string("effect.healing"));
        }
        return effects;
    }

    public Point[] getCorners() {
        Point[] corners = new Point[]{new Point(this.minxMargin, this.pos.getiY(), this.minzMargin), new Point(this.maxxMargin, this.pos.getiY(), this.minzMargin), new Point(this.minxMargin, this.pos.getiY(), this.maxzMargin), new Point(this.maxxMargin, this.pos.getiY(), this.maxzMargin)};
        return corners;
    }

    public BuildingCustomPlan getCustomPlan() {
        if (this.culture == null) {
            MillLog.error(this, "null culture");
            return null;
        }
        if (this.culture.getBuildingCustom(this.planKey) != null) {
            return this.culture.getBuildingCustom(this.planKey);
        }
        return null;
    }

    public List<String> getFemaleResidents() {
        IBuildingPlan plan = this.getIBuildingPlan();
        if (plan != null) {
            return new CopyOnWriteArrayList<String>(plan.getFemaleResident());
        }
        return new ArrayList<String>();
    }

    public String getFullDisplayName() {
        if (this.isCustomBuilding) {
            return this.getCustomPlan().getFullDisplayName();
        }
        return this.getPlan().getNameNativeAndTranslated();
    }

    public String getGameName() {
        if (this.isCustomBuilding) {
            return this.getCustomPlan().getNameTranslated();
        }
        return this.getPlan().getNameTranslated();
    }

    public IBuildingPlan getIBuildingPlan() {
        IBuildingPlan plan = this.getPlan();
        if (plan != null) {
            return plan;
        }
        plan = this.getCustomPlan();
        return plan;
    }

    public List<String> getMaleResidents() {
        IBuildingPlan plan = this.getIBuildingPlan();
        if (plan != null) {
            return new CopyOnWriteArrayList<String>(plan.getMaleResident());
        }
        return new ArrayList<String>();
    }

    public String getNativeName() {
        if (this.isCustomBuilding) {
            return this.getCustomPlan().nativeName;
        }
        return this.getPlan().nativeName;
    }

    public BuildingPlan getPlan() {
        if (this.culture == null) {
            MillLog.printException("null culture", new Exception(""));
            return null;
        }
        if (this.isCustomBuilding) {
            return null;
        }
        if (this.culture.getBuildingPlanSet(this.planKey) != null && this.culture.getBuildingPlanSet((String)this.planKey).plans.size() > this.getVariation()) {
            if (this.level < 0) {
                return this.culture.getBuildingPlanSet((String)this.planKey).plans.get(this.getVariation())[0];
            }
            if (this.culture.getBuildingPlanSet((String)this.planKey).plans.get(this.getVariation()).length > this.level) {
                return this.culture.getBuildingPlanSet((String)this.planKey).plans.get(this.getVariation())[this.level];
            }
            MillLog.error(this, "Cannot find a valid plan for key " + this.planKey + ".");
            return null;
        }
        MillLog.error(this, "Cannot find a plan for key " + this.planKey + ".");
        return null;
    }

    public Point getSellingPos() {
        if (this.sellingPos != null) {
            return this.sellingPos;
        }
        return this.sleepingPos;
    }

    public int getVariation() {
        return this.variation;
    }

    public List<String> getVisitors() {
        IBuildingPlan plan = this.getIBuildingPlan();
        if (plan != null) {
            return new CopyOnWriteArrayList<String>(plan.getVisitors());
        }
        return new ArrayList<String>();
    }

    public int hashCode() {
        return (this.planKey + "_" + this.level + " at " + this.pos + "/" + this.orientation + "/" + this.getVariation()).hashCode();
    }

    public void initialiseBrickColoursFromTheme(Building townHall, VillageType.BrickColourTheme theme) {
        if (!this.getPlan().isWallSegment) {
            for (EnumDyeColor inputColour : EnumDyeColor.values()) {
                this.paintedBricksColour.put(inputColour, theme.getRandomDyeColour(inputColour));
            }
        } else {
            this.paintedBricksColour.putAll(townHall.location.paintedBricksColour);
        }
    }

    private void initialisePlan() {
        Point op1 = BuildingPlan.adjustForOrientation(this.pos.getiX(), this.pos.getiY(), this.pos.getiZ(), this.length / 2, this.width / 2, this.orientation);
        Point op2 = BuildingPlan.adjustForOrientation(this.pos.getiX(), this.pos.getiY(), this.pos.getiZ(), -this.length / 2, -this.width / 2, this.orientation);
        if (op1.getiX() > op2.getiX()) {
            this.minx = op2.getiX();
            this.maxx = op1.getiX();
        } else {
            this.minx = op1.getiX();
            this.maxx = op2.getiX();
        }
        if (op1.getiZ() > op2.getiZ()) {
            this.minz = op2.getiZ();
            this.maxz = op1.getiZ();
        } else {
            this.minz = op1.getiZ();
            this.maxz = op2.getiZ();
        }
        if (this.getPlan() != null) {
            this.miny = this.pos.getiY() + this.getPlan().startLevel;
            this.maxy = this.miny + this.getPlan().nbfloors;
        } else {
            this.miny = this.pos.getiY() - 5;
            this.maxy = this.pos.getiY() + 20;
        }
        this.computeMargins();
    }

    private void initialiseRandomBrickColoursFromPlan(BuildingPlan plan) {
        for (EnumDyeColor color : plan.randomBrickColours.keySet()) {
            int totalWeight = 0;
            for (EnumDyeColor possibleColor : plan.randomBrickColours.get(color).keySet()) {
                totalWeight += plan.randomBrickColours.get(color).get(possibleColor).intValue();
            }
            int pickedValue = MillCommonUtilities.randomInt(totalWeight);
            EnumDyeColor pickedColor = null;
            int currentWeightTotal = 0;
            for (EnumDyeColor possibleColor : plan.randomBrickColours.get(color).keySet()) {
                if (pickedColor != null || pickedValue >= (currentWeightTotal += plan.randomBrickColours.get(color).get(possibleColor).intValue())) continue;
                pickedColor = possibleColor;
            }
            this.paintedBricksColour.put(color, pickedColor);
        }
    }

    public boolean isInside(Point p) {
        return this.minx < p.getiX() && p.getiX() <= this.maxx && this.miny < p.getiY() && p.getiY() <= this.maxy && this.minz < p.getiZ() && p.getiZ() <= this.maxz;
    }

    public boolean isInsidePlanar(Point p) {
        return this.minx < p.getiX() && p.getiX() <= this.maxx && this.minz < p.getiZ() && p.getiZ() <= this.maxz;
    }

    public boolean isInsideWithTolerance(Point p, int tolerance) {
        return this.minx - tolerance < p.getiX() && p.getiX() <= this.maxx + tolerance && this.miny - tolerance < p.getiY() && p.getiY() <= this.maxy + tolerance && this.minz - tolerance < p.getiZ() && p.getiZ() <= this.maxz + tolerance;
    }

    public boolean isInsideZone(Point p) {
        return this.minxMargin <= p.getiX() && p.getiX() <= this.maxxMargin && this.minyMargin <= p.getiY() && p.getiY() <= this.maxyMargin && this.minzMargin <= p.getiZ() && p.getiZ() <= this.maxzMargin;
    }

    public boolean isLocationSamePlace(BuildingLocation l) {
        if (l == null) {
            return false;
        }
        return this.pos.equals(l.pos) && this.orientation == l.orientation && this.getVariation() == l.getVariation();
    }

    public boolean isSameLocation(BuildingLocation l) {
        if (l == null) {
            return false;
        }
        boolean samePlanKey = this.planKey == null && l.planKey == null || this.planKey.equals(l.planKey);
        return this.pos.equals(l.pos) && samePlanKey && this.orientation == l.orientation && this.getVariation() == l.getVariation() && this.isCustomBuilding == l.isCustomBuilding;
    }

    public void setVariation(int var) {
        this.variation = var;
    }

    public String toString() {
        return this.planKey + "_" + (char)(65 + this.variation) + this.level + " at " + this.pos + "/" + this.orientation + "/" + this.getVariation() + "/" + super.hashCode();
    }

    public void writeToNBT(NBTTagCompound nbttagcompound, String label, String debug) {
        NBTTagCompound nbttagcompound1;
        this.pos.write(nbttagcompound, label + "_pos");
        nbttagcompound.func_74757_a(label + "_isCustomBuilding", this.isCustomBuilding);
        nbttagcompound.func_74778_a(label + "_culture", this.culture.key);
        nbttagcompound.func_74768_a(label + "_orientation", this.orientation);
        nbttagcompound.func_74768_a(label + "_minx", this.minx);
        nbttagcompound.func_74768_a(label + "_miny", this.miny);
        nbttagcompound.func_74768_a(label + "_minz", this.minz);
        nbttagcompound.func_74768_a(label + "_maxx", this.maxx);
        nbttagcompound.func_74768_a(label + "_maxy", this.maxy);
        nbttagcompound.func_74768_a(label + "_maxz", this.maxz);
        nbttagcompound.func_74768_a(label + "_length", this.length);
        nbttagcompound.func_74768_a(label + "_width", this.width);
        nbttagcompound.func_74768_a(label + "_level", this.level);
        nbttagcompound.func_74778_a(label + "_key", this.planKey);
        nbttagcompound.func_74768_a(label + "_variation", this.getVariation());
        nbttagcompound.func_74768_a(label + "_reputation", this.reputation);
        nbttagcompound.func_74768_a(label + "_price", this.price);
        nbttagcompound.func_74768_a(label + "_version", this.version);
        nbttagcompound.func_74768_a(label + "_priorityMoveIn", this.priorityMoveIn);
        if (this.shop != null && this.shop.length() > 0) {
            nbttagcompound.func_74778_a(label + "_shop", this.shop);
        }
        NBTTagList nbttaglist = new NBTTagList();
        for (String subb : this.subBuildings) {
            nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.func_74778_a("value", subb);
            nbttaglist.func_74742_a((NBTBase)nbttagcompound1);
        }
        nbttagcompound.func_74782_a(label + "_subBuildings", (NBTBase)nbttaglist);
        if (this.sleepingPos != null) {
            this.sleepingPos.write(nbttagcompound, label + "_standingPos");
        }
        if (this.sellingPos != null) {
            this.sellingPos.write(nbttagcompound, label + "_sellingPos");
        }
        if (this.craftingPos != null) {
            this.craftingPos.write(nbttagcompound, label + "_craftingPos");
        }
        if (this.defendingPos != null) {
            this.defendingPos.write(nbttagcompound, label + "_defendingPos");
        }
        if (this.shelterPos != null) {
            this.shelterPos.write(nbttagcompound, label + "_shelterPos");
        }
        if (this.chestPos != null) {
            this.chestPos.write(nbttagcompound, label + "_chestPos");
        }
        nbttagcompound.func_74757_a(label + "_showTownHallSigns", this.showTownHallSigns);
        nbttagcompound.func_74757_a(label + "_upgradesAllowed", this.upgradesAllowed);
        nbttagcompound.func_74757_a(label + "_isSubBuildingLocation", this.isSubBuildingLocation);
        nbttaglist = new NBTTagList();
        for (EnumDyeColor color : this.paintedBricksColour.keySet()) {
            nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.func_74778_a("value", color.func_176610_l());
            nbttaglist.func_74742_a((NBTBase)nbttagcompound1);
        }
        nbttagcompound.func_74782_a(label + "_paintedBricksColour_keys", (NBTBase)nbttaglist);
        for (EnumDyeColor color : this.paintedBricksColour.keySet()) {
            nbttagcompound.func_74778_a(label + "_paintedBricksColour_" + color.func_176610_l(), this.paintedBricksColour.get(color).func_176610_l());
        }
    }
}

