/*
 * Decompiled with CFR 0.152.
 */
package org.millenaire.common.annotedparameters;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.millenaire.common.annotedparameters.ValueIO;
import org.millenaire.common.buildingplan.BuildingCustomPlan;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.WallType;
import org.millenaire.common.utilities.MillLog;

public abstract class CultureValueIO
extends ValueIO {
    @Override
    public void readValue(Object targetClass, Field field, String value) throws Exception {
        MillLog.error(this, "Using readValue on a CultureValueIO object.");
    }

    @Override
    public boolean useCulture() {
        return true;
    }

    public static class WallIO
    extends CultureValueIO {
        public WallIO() {
            this.description = "A wall type from the current culture. One allowed.";
        }

        @Override
        public void readValueCulture(Culture culture, Object targetClass, Field field, String value) throws Exception {
            if (!culture.wallTypes.containsKey(value)) {
                throw new MillLog.MillenaireException("Unknown wall type: " + value);
            }
            field.set(targetClass, culture.wallTypes.get(value));
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            WallType wall = (WallType)rawValue;
            return WallIO.createListFromValue(wall.key);
        }
    }

    public static class VillagerAddIO
    extends CultureValueIO {
        public VillagerAddIO() {
            this.description = "A villager type from the current culture. Multiple lines allowed.";
        }

        @Override
        public void readValueCulture(Culture culture, Object targetClass, Field field, String value) throws Exception {
            if (culture != null && culture.villagerTypes.get(value.toLowerCase()) == null) {
                throw new MillLog.MillenaireException("Unknown villager type: " + value);
            }
            ((List)field.get(targetClass)).add(value.toLowerCase());
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            List values = (List)rawValue;
            return values;
        }
    }

    public static class ShopIO
    extends CultureValueIO {
        public ShopIO() {
            this.description = "A shop from the current culture.";
        }

        @Override
        public void readValueCulture(Culture culture, Object targetClass, Field field, String value) throws Exception {
            value = value.toLowerCase();
            if (!(culture == null || culture.shopBuys.containsKey(value) || culture.shopSells.containsKey(value) || culture.shopBuysOptional.containsKey(value))) {
                throw new MillLog.MillenaireException("Unknown shop: " + value);
            }
            field.set(targetClass, value);
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            String value = (String)rawValue;
            return ShopIO.createListFromValue(value);
        }
    }

    public static class BuildingSetIO
    extends CultureValueIO {
        public BuildingSetIO() {
            this.description = "A building from the current culture. One allowed.";
        }

        @Override
        public void readValueCulture(Culture culture, Object targetClass, Field field, String value) throws Exception {
            if (culture.getBuildingPlanSet(value) == null) {
                throw new MillLog.MillenaireException("Unknown building: " + value);
            }
            field.set(targetClass, culture.getBuildingPlanSet(value));
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            BuildingPlanSet plan = (BuildingPlanSet)rawValue;
            return BuildingSetIO.createListFromValue(plan.key);
        }
    }

    public static class BuildingSetAddIO
    extends CultureValueIO {
        public BuildingSetAddIO() {
            this.description = "A building from the current culture. Multiple lines allowed.";
        }

        @Override
        public void readValueCulture(Culture culture, Object targetClass, Field field, String value) throws Exception {
            if (culture.getBuildingPlanSet(value) == null) {
                throw new MillLog.MillenaireException("Unknown building: " + value);
            }
            ((List)field.get(targetClass)).add(culture.getBuildingPlanSet(value));
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            List plans = (List)rawValue;
            ArrayList<String> results = new ArrayList<String>();
            for (BuildingPlanSet plan : plans) {
                results.add(plan.key);
            }
            return results;
        }
    }

    public static class BuildingCustomIO
    extends CultureValueIO {
        public BuildingCustomIO() {
            this.description = "A custom building from the current culture. One allowed.";
        }

        @Override
        public void readValueCulture(Culture culture, Object targetClass, Field field, String value) throws Exception {
            if (culture.getBuildingCustom(value) == null) {
                throw new MillLog.MillenaireException("Unknown custom building: " + value);
            }
            field.set(targetClass, culture.getBuildingCustom(value));
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            BuildingCustomPlan plan = (BuildingCustomPlan)rawValue;
            return BuildingCustomIO.createListFromValue(plan.buildingKey);
        }
    }

    public static class BuildingCustomAddIO
    extends CultureValueIO {
        public BuildingCustomAddIO() {
            this.description = "A custom building from the current culture. Multiple lines allowed.";
        }

        @Override
        public void readValueCulture(Culture culture, Object targetClass, Field field, String value) throws Exception {
            if (culture.getBuildingCustom(value) == null) {
                throw new MillLog.MillenaireException("Unknown custom building: " + value);
            }
            ((List)field.get(targetClass)).add(culture.getBuildingCustom(value));
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            List customPlans = (List)rawValue;
            ArrayList<String> results = new ArrayList<String>();
            for (BuildingCustomPlan customPlan : customPlans) {
                results.add(customPlan.buildingKey);
            }
            return results;
        }
    }
}

