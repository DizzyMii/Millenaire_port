/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.EntityList
 *  net.minecraft.item.EnumDyeColor
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 */
package org.millenaire.common.annotedparameters;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.millenaire.common.annotedparameters.AnnotedParameter;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.entity.VillagerConfig;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.BlockStateUtilities;
import org.millenaire.common.utilities.MillLog;

public abstract class ValueIO {
    public String description;

    protected static List<String> createListFromValue(String value) {
        ArrayList<String> list = new ArrayList<String>();
        list.add(value);
        return list;
    }

    public abstract void readValue(Object var1, Field var2, String var3) throws Exception;

    public void readValueCulture(Culture culture, Object targetClass, Field field, String value) throws Exception {
        MillLog.error(this, "Trying to use readValueCulture but it is not implemented.");
    }

    public boolean skipWritingValue(Object value) {
        return false;
    }

    public boolean useCulture() {
        return false;
    }

    public abstract List<String> writeValue(Object var1) throws Exception;

    public static class VillagerConfigIO
    extends ValueIO {
        public VillagerConfigIO() {
            this.description = "A villager config (from millenaire/villagerconfig).";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            if (!VillagerConfig.villagerConfigs.containsKey(value.toLowerCase())) {
                throw new MillLog.MillenaireException("Unknown villager config: " + value);
            }
            field.set(targetClass, VillagerConfig.villagerConfigs.get(value.toLowerCase()));
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            VillagerConfig config = (VillagerConfig)rawValue;
            return VillagerConfigIO.createListFromValue(config.key);
        }
    }

    public static class TranslatedStringAddIO
    extends ValueIO {
        public TranslatedStringAddIO() {
            this.description = "translated string, with the format: 'fr,ferme'";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            String[] temp2 = value.toLowerCase().split(",");
            if (temp2.length != 2) {
                throw new MillLog.MillenaireException("Translated strings must take the form of language,string. Ex: fr,ferme.");
            }
            ((Map)field.get(targetClass)).put(temp2[0].toLowerCase().trim(), temp2[1]);
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            Map map = (Map)rawValue;
            ArrayList<String> results = new ArrayList<String>();
            ArrayList keys = new ArrayList(map.keySet());
            Collections.sort(keys);
            for (String key : keys) {
                results.add(key + "," + (String)map.get(key));
            }
            return results;
        }
    }

    public static class ToolCategoriesIO
    extends ValueIO {
        public ToolCategoriesIO() {
            this.description = "A tool category to require, from: meleeweapons, rangedweapons, armour, pickaxes, axes, shovels and hoes.";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            List categories = (List)field.get(targetClass);
            if (value.equalsIgnoreCase("meleeweapons")) {
                MillLog.warning(targetClass, "Usage of 'meleeweapons' tool class is discouraged (it makes villagers gather any tools for use as weapons). Use 'toolssword' instead.");
                categories.add(VillagerConfig.CATEGORY_WEAPONSHANDTOHAND);
            } else if (value.equalsIgnoreCase("toolssword")) {
                categories.add(VillagerConfig.CATEGORY_TOOLSSWORD);
            } else if (value.equalsIgnoreCase("rangedweapons")) {
                categories.add(VillagerConfig.CATEGORY_WEAPONSRANGED);
            } else if (value.equalsIgnoreCase("armour")) {
                categories.add(VillagerConfig.CATEGORY_ARMOURSBOOTS);
                categories.add(VillagerConfig.CATEGORY_ARMOURSCHESTPLATE);
                categories.add(VillagerConfig.CATEGORY_ARMOURSHELMET);
                categories.add(VillagerConfig.CATEGORY_ARMOURSLEGGINGS);
            } else if (value.equalsIgnoreCase("pickaxes")) {
                categories.add(VillagerConfig.CATEGORY_TOOLSPICKAXE);
            } else if (value.equalsIgnoreCase("axes")) {
                categories.add(VillagerConfig.CATEGORY_TOOLSAXE);
            } else if (value.equalsIgnoreCase("shovels")) {
                categories.add(VillagerConfig.CATEGORY_TOOLSSHOVEL);
            } else if (value.equalsIgnoreCase("hoes")) {
                categories.add(VillagerConfig.CATEGORY_TOOLSHOE);
            } else {
                MillLog.error(null, "Unknown tool class found: " + value);
            }
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            List categories = (List)rawValue;
            ArrayList<String> results = new ArrayList<String>();
            for (String category : categories) {
                if (category.equalsIgnoreCase(VillagerConfig.CATEGORY_WEAPONSHANDTOHAND)) {
                    results.add("meleeweapons");
                    continue;
                }
                if (category.equalsIgnoreCase(VillagerConfig.CATEGORY_WEAPONSRANGED)) {
                    results.add("rangedweapons");
                    continue;
                }
                if (category.equalsIgnoreCase(VillagerConfig.CATEGORY_ARMOURSCHESTPLATE)) {
                    results.add("armour");
                    continue;
                }
                if (category.equalsIgnoreCase(VillagerConfig.CATEGORY_TOOLSPICKAXE)) {
                    results.add("pickaxes");
                    continue;
                }
                if (category.equalsIgnoreCase(VillagerConfig.CATEGORY_TOOLSAXE)) {
                    results.add("axes");
                    continue;
                }
                if (category.equalsIgnoreCase(VillagerConfig.CATEGORY_TOOLSSHOVEL)) {
                    results.add("shovels");
                    continue;
                }
                if (!category.equalsIgnoreCase(VillagerConfig.CATEGORY_TOOLSHOE)) continue;
                results.add("hoes");
            }
            return results;
        }
    }

    public static class StringNumberAddIO
    extends ValueIO {
        public StringNumberAddIO() {
            this.description = "string and integer: 'test,12'";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            String[] temp2 = value.toLowerCase().split(",");
            if (temp2.length != 2) {
                throw new MillLog.MillenaireException("Invalid parameter. Must take the form: 'value,number'.");
            }
            ((Map)field.get(targetClass)).put(temp2[0], Integer.parseInt(temp2[1]));
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            Map map = (Map)rawValue;
            ArrayList<String> results = new ArrayList<String>();
            ArrayList keys = new ArrayList(map.keySet());
            Collections.sort(keys);
            for (String key : keys) {
                results.add(key + "," + map.get(key));
            }
            return results;
        }
    }

    public static class StringListIO
    extends ValueIO {
        public StringListIO() {
            this.description = "list of strings (value1, value2...)";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            String[] segments = value.toLowerCase().split(",");
            ArrayList<String> result = new ArrayList<String>();
            for (String s : segments) {
                if ((s = s.trim()).length() <= 0) continue;
                result.add(s);
            }
            field.set(targetClass, result);
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            List values = (List)rawValue;
            String result = "";
            for (String value : values) {
                if (result.length() > 0) {
                    result = result + ",";
                }
                result = result + value;
            }
            return StringListIO.createListFromValue(result);
        }
    }

    public static class StringIO
    extends ValueIO {
        public StringIO() {
            this.description = "string (case-insensitive)";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            field.set(targetClass, value.toLowerCase());
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            String value = (String)rawValue;
            return StringIO.createListFromValue(value);
        }
    }

    public static class StringInvItemAddIO
    extends ValueIO {
        public StringInvItemAddIO() {
            this.description = "String and item: 'villager,wheat'";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            String[] temp2 = value.toLowerCase().split(",");
            if (temp2.length != 2) {
                throw new MillLog.MillenaireException("Invalid setting. They must take the form of parameter=string,item.");
            }
            if (!InvItem.INVITEMS_BY_NAME.containsKey(temp2[1])) {
                throw new MillLog.MillenaireException("Unknown item: " + temp2[1]);
            }
            ((Map)field.get(targetClass)).put(temp2[0], InvItem.INVITEMS_BY_NAME.get(temp2[1]));
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            Map map = (Map)rawValue;
            ArrayList<String> results = new ArrayList<String>();
            ArrayList keys = new ArrayList(map.keySet());
            Collections.sort(keys);
            for (String string : keys) {
                results.add(string + "," + ((InvItem)map.get(string)).getKey());
            }
            return results;
        }
    }

    public static class StringDisplayIO
    extends ValueIO {
        public StringDisplayIO() {
            this.description = "string";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            field.set(targetClass, value);
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            String value = (String)rawValue;
            return StringDisplayIO.createListFromValue(value);
        }
    }

    public static class StringCaseSensitiveAddIO
    extends ValueIO {
        public StringCaseSensitiveAddIO() {
            this.description = "string (multiple parameters possible)";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            ((List)field.get(targetClass)).add(value);
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            List strings = (List)rawValue;
            return strings;
        }
    }

    public static class StringAddIO
    extends ValueIO {
        public StringAddIO() {
            this.description = "string (case-insensitive, multiple parameters possible)";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            ((List)field.get(targetClass)).add(value.toLowerCase());
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            List strings = (List)rawValue;
            return strings;
        }
    }

    public static class StartingItemAddIO
    extends ValueIO {
        public StartingItemAddIO() {
            this.description = "item, chance, fixed number, maximum bonus number ('leather,0.5,8,8' for between 8 and 16 leather 50% of the time)";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            String[] params = value.toLowerCase().split(",");
            if (params.length != 4) {
                MillLog.error(null, "Error when reading starting good: expected four fields, found " + params.length + ": " + value);
            } else {
                String s = params[0];
                if (!InvItem.INVITEMS_BY_NAME.containsKey(s)) {
                    MillLog.error(null, "Error when reading starting good: unknown good: " + s);
                } else {
                    BuildingPlan.StartingGood sg = new BuildingPlan.StartingGood(InvItem.INVITEMS_BY_NAME.get(s), Double.parseDouble(params[1]), Integer.parseInt(params[2]), Integer.parseInt(params[3]));
                    ((List)field.get(targetClass)).add(sg);
                }
            }
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            List startingGoods = (List)rawValue;
            ArrayList<String> results = new ArrayList<String>();
            for (BuildingPlan.StartingGood startingGood : startingGoods) {
                results.add(startingGood.item.getKey() + "," + startingGood.probability + "," + startingGood.fixedNumber + "," + startingGood.randomNumber);
            }
            return results;
        }
    }

    public static class ResourceLocationIO
    extends ValueIO {
        public ResourceLocationIO() {
            this.description = "Minecraft resource path";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            field.set(targetClass, new ResourceLocation(value));
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            ResourceLocation resource = (ResourceLocation)rawValue;
            String value = resource.toString();
            return ResourceLocationIO.createListFromValue(value);
        }
    }

    public static class RandomBrickColourAddIO
    extends ValueIO {
        public RandomBrickColourAddIO() {
            this.description = "Example: 'white;white:50,yellow:40,orange:30' means that white coloured bricks can turn into white, yellow or orange bricks, with weights of 50, 40 and 30 respectively.";
        }

        private String getAllColourNames() {
            String colours = "";
            for (EnumDyeColor color : EnumDyeColor.values()) {
                colours = colours + color.func_176610_l() + " ";
            }
            return colours;
        }

        private EnumDyeColor getColourByName(String colourName) {
            for (EnumDyeColor color : EnumDyeColor.values()) {
                if (!color.func_176610_l().equals(colourName)) continue;
                return color;
            }
            return null;
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            String mainColourName = value.split(";")[0];
            EnumDyeColor mainColour = this.getColourByName(mainColourName);
            if (mainColour == null) {
                throw new MillLog.MillenaireException("Unknown colour: " + mainColourName + ". It should be among: " + this.getAllColourNames());
            }
            String possibleValues = value.split(";")[1];
            HashMap<EnumDyeColor, Integer> values = new HashMap<EnumDyeColor, Integer>();
            for (String weightedColour : possibleValues.split(",")) {
                String colourName = weightedColour.split(":")[0];
                EnumDyeColor colour = this.getColourByName(colourName);
                if (colour == null) {
                    throw new MillLog.MillenaireException("Unknown colour: " + colourName + ". It should be among: " + this.getAllColourNames());
                }
                int weight = Integer.parseInt(weightedColour.split(":")[1]);
                values.put(colour, weight);
            }
            Map coloursMap = (Map)field.get(targetClass);
            coloursMap.put(mainColour, values);
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            Map coloursMap = (Map)rawValue;
            ArrayList<String> results = new ArrayList<String>();
            for (EnumDyeColor mainColour : coloursMap.keySet()) {
                String line = mainColour.func_176610_l() + ":";
                String values = "";
                for (EnumDyeColor colour : ((Map)coloursMap.get(mainColour)).keySet()) {
                    if (values.length() > 0) {
                        values = values + ",";
                    }
                    values = values + colour.func_176610_l() + ":" + ((Map)coloursMap.get(mainColour)).get(colour);
                }
                line = line + values;
                results.add(line);
            }
            return results;
        }
    }

    public static class PosTypeIO
    extends ValueIO {
        public PosTypeIO() {
            this.description = "Type of position point (sleeping, leasure, selling...)";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            AnnotedParameter.PosType posType = AnnotedParameter.PosType.getByType(value.toLowerCase());
            if (posType == null) {
                throw new MillLog.MillenaireException("Unknown position type: " + value + ". It should be among: " + AnnotedParameter.PosType.getAllCodes() + ".");
            }
            field.set(targetClass, (Object)posType);
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            AnnotedParameter.PosType value = (AnnotedParameter.PosType)((Object)rawValue);
            return PosTypeIO.createListFromValue(value.code);
        }
    }

    public static class MillisecondsIO
    extends ValueIO {
        public MillisecondsIO() {
            this.description = "milliseconds";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            field.set(targetClass, Integer.parseInt(value) * 20 / 1000);
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            int value = (Integer)rawValue;
            return MillisecondsIO.createListFromValue("" + value * 1000 / 20);
        }
    }

    public static class ItemStackArrayIO
    extends ValueIO {
        public ItemStackArrayIO() {
            this.description = "list of items: 'chickenmeat,chickenmeatcooked'";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            String[] temp2 = value.toLowerCase().split(",");
            ItemStack[] itemsList = new ItemStack[temp2.length];
            for (int i = 0; i < temp2.length; ++i) {
                if (InvItem.INVITEMS_BY_NAME.containsKey(temp2[i])) {
                    itemsList[i] = InvItem.INVITEMS_BY_NAME.get(temp2[i]).getItemStack();
                    if (itemsList[i].func_77973_b() != null) continue;
                    throw new MillLog.MillenaireException("Item list with null item: " + temp2[i]);
                }
                throw new MillLog.MillenaireException("Unknown item: " + temp2[i]);
            }
            field.set(targetClass, itemsList);
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            ItemStack[] stacks = (ItemStack[])rawValue;
            String result = "";
            for (ItemStack stack : stacks) {
                if (result.length() > 0) {
                    result = result + ",";
                }
                result = result + InvItem.createInvItem(stack).getKey();
            }
            return ItemStackArrayIO.createListFromValue(result);
        }
    }

    public static class InvItemPriceAddIO
    extends ValueIO {
        public InvItemPriceAddIO() {
            this.description = "item and price in the form of gold/silver/bronze deniers: 'bone,1/0/0'";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            int price;
            String[] temp2 = value.toLowerCase().split(",");
            if (temp2.length != 2) {
                throw new MillLog.MillenaireException("Invalid item quantity setting. They must take the form of parameter=goodname,goodquatity.");
            }
            if (InvItem.INVITEMS_BY_NAME.containsKey(temp2[0])) {
                price = 0;
                String[] pricestr = temp2[1].split("/");
                if (pricestr.length == 1) {
                    price = Integer.parseInt(pricestr[0]);
                } else if (pricestr.length == 2) {
                    price = Integer.parseInt(pricestr[0]) * 64 + Integer.parseInt(pricestr[1]);
                } else if (pricestr.length == 3) {
                    price = Integer.parseInt(pricestr[0]) * 64 * 64 + Integer.parseInt(pricestr[1]) * 64 + Integer.parseInt(pricestr[2]);
                } else {
                    MillLog.error(this, "Could not parse the price: " + value);
                }
            } else {
                throw new MillLog.MillenaireException("Unknown item: " + temp2[0]);
            }
            ((Map)field.get(targetClass)).put(InvItem.INVITEMS_BY_NAME.get(temp2[0]), price);
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            Map invItems = (Map)rawValue;
            ArrayList<String> results = new ArrayList<String>();
            ArrayList keys = new ArrayList(invItems.keySet());
            Collections.sort(keys);
            for (InvItem invItem : keys) {
                int price = (Integer)invItems.get(invItem);
                int priceGold = price / 4096;
                int priceSilver = (price - priceGold * 64 * 64) / 64;
                int priceBronze = price - priceGold * 64 * 64 - priceSilver * 64;
                results.add(invItem.getKey() + "," + priceGold + "/" + priceSilver + "/" + priceBronze);
            }
            return results;
        }
    }

    public static class InvItemPairIO
    extends ValueIO {
        public InvItemPairIO() {
            this.description = "pair of items: 'stone,sand'";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            String[] temp2 = value.toLowerCase().split(",");
            if (temp2.length != 2) {
                throw new MillLog.MillenaireException("Item pairs must take the form of parameter=firstgood,secondgood.");
            }
            if (!InvItem.INVITEMS_BY_NAME.containsKey(temp2[0]) && !InvItem.INVITEMS_BY_NAME.containsKey(temp2[1])) {
                throw new MillLog.MillenaireException("Unknown item : " + temp2[0] + " or " + temp2[1]);
            }
            field.set(targetClass, new InvItem[]{InvItem.INVITEMS_BY_NAME.get(temp2[0]), InvItem.INVITEMS_BY_NAME.get(temp2[1])});
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            InvItem[] pair = (InvItem[])rawValue;
            return InvItemPairIO.createListFromValue(pair[0].getKey() + "," + pair[1].getKey());
        }
    }

    public static class InvItemNumberAddIO
    extends ValueIO {
        public InvItemNumberAddIO() {
            this.description = "item and number: 'bone,8'";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            String[] temp2 = value.toLowerCase().split(",");
            if (temp2.length != 2) {
                throw new MillLog.MillenaireException("Invalid item quantity setting. They must take the form of parameter=goodname,goodquatity.");
            }
            if (!InvItem.INVITEMS_BY_NAME.containsKey(temp2[0])) {
                throw new MillLog.MillenaireException("Unknown item: " + temp2[0]);
            }
            ((Map)field.get(targetClass)).put(InvItem.INVITEMS_BY_NAME.get(temp2[0]), Integer.parseInt(temp2[1]));
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            Map invItems = (Map)rawValue;
            ArrayList<String> results = new ArrayList<String>();
            ArrayList keys = new ArrayList(invItems.keySet());
            Collections.sort(keys);
            for (InvItem invItem : keys) {
                results.add(invItem.getKey() + "," + invItems.get(invItem));
            }
            return results;
        }
    }

    public static class InvItemIO
    extends ValueIO {
        public InvItemIO() {
            this.description = "item (from itemlist.txt)";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            if (!InvItem.INVITEMS_BY_NAME.containsKey(value.toLowerCase())) {
                throw new MillLog.MillenaireException("Unknown item: " + value);
            }
            field.set(targetClass, InvItem.INVITEMS_BY_NAME.get(value.toLowerCase()));
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            InvItem value = (InvItem)rawValue;
            return InvItemIO.createListFromValue(value.getKey());
        }
    }

    public static class InvItemAddIO
    extends ValueIO {
        public InvItemAddIO() {
            this.description = "an item: ('chickenmeat')";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            if (!InvItem.INVITEMS_BY_NAME.containsKey(value.toLowerCase())) {
                throw new MillLog.MillenaireException("Unknown item: " + value);
            }
            ((List)field.get(targetClass)).add(InvItem.INVITEMS_BY_NAME.get(value.toLowerCase()));
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            List invItems = (List)rawValue;
            ArrayList<String> results = new ArrayList<String>();
            for (InvItem invItem : invItems) {
                results.add(invItem.getKey());
            }
            return results;
        }
    }

    public static class IntegerIO
    extends ValueIO {
        public IntegerIO() {
            this.description = "integer value";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            field.set(targetClass, Integer.parseInt(value));
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            int value = (Integer)rawValue;
            return IntegerIO.createListFromValue("" + value);
        }
    }

    public static class IntegerArrayIO
    extends ValueIO {
        public IntegerArrayIO() {
            this.description = "list of integers: '1,2,3'";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            String[] segments = value.toLowerCase().split(",");
            if (segments[0].length() > 0) {
                int[] array = new int[segments.length];
                for (int i = 0; i < segments.length; ++i) {
                    array[i] = Integer.parseInt(segments[i]);
                }
                field.set(targetClass, array);
            }
        }

        @Override
        public boolean skipWritingValue(Object value) {
            int[] ints = (int[])value;
            return ints.length == 1 && ints[0] == 0;
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            int[] ints = (int[])rawValue;
            String result = "";
            for (int i : ints) {
                if (result.length() > 0) {
                    result = result + ",";
                }
                result = result + i;
            }
            return IntegerArrayIO.createListFromValue(result);
        }
    }

    public static class GoalAddIO
    extends ValueIO {
        public GoalAddIO() {
            this.description = "Id of a goal ('construction', 'gopray'...)";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            if (Goal.goals.containsKey(value.toLowerCase())) {
                ((List)field.get(targetClass)).add(Goal.goals.get(value.toLowerCase()));
            } else {
                MillLog.error(null, "Unknown goal: " + value);
            }
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            List goals = (List)rawValue;
            ArrayList<String> results = new ArrayList<String>();
            for (Goal goal : goals) {
                results.add(goal.key);
            }
            return results;
        }
    }

    public static class GenderIO
    extends ValueIO {
        public GenderIO() {
            this.description = "A gender, either 'male' or 'female'";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            if (value.toLowerCase().equals("male")) {
                field.set(targetClass, 1);
            } else if (value.toLowerCase().equals("female")) {
                field.set(targetClass, 2);
            } else {
                MillLog.error(null, "Unknown gender found: " + value);
            }
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            Integer value = (Integer)rawValue;
            if (value == 1) {
                return GenderIO.createListFromValue("male");
            }
            return GenderIO.createListFromValue("female");
        }
    }

    public static class FloatIO
    extends ValueIO {
        public FloatIO() {
            this.description = "floating point value";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            field.set(targetClass, Float.valueOf(Float.parseFloat(value)));
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            Float value = (Float)rawValue;
            return FloatIO.createListFromValue("" + value);
        }
    }

    public static class EntityIO
    extends ValueIO {
        public EntityIO() {
            this.description = "Minecraft ID of an entity ('cow')";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            ResourceLocation rl = new ResourceLocation(value.toLowerCase());
            if (!EntityList.func_180124_b().contains(rl)) {
                throw new MillLog.MillenaireException("Unknown entity: " + value);
            }
            field.set(targetClass, rl);
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            ResourceLocation value = (ResourceLocation)rawValue;
            return EntityIO.createListFromValue(value.toString());
        }
    }

    public static class DirectionIO
    extends ValueIO {
        public DirectionIO() {
            this.description = "A direction, such as 'east' or 'north'";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            if (value.equalsIgnoreCase("east")) {
                field.set(targetClass, 3);
            } else if (value.equalsIgnoreCase("west")) {
                field.set(targetClass, 1);
            } else if (value.equalsIgnoreCase("north")) {
                field.set(targetClass, 0);
            } else if (value.equalsIgnoreCase("south")) {
                field.set(targetClass, 2);
            } else {
                MillLog.error(null, "Unknown direction found: " + value);
            }
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            Integer value = (Integer)rawValue;
            if (value == 3) {
                return DirectionIO.createListFromValue("east");
            }
            if (value == 1) {
                return DirectionIO.createListFromValue("west");
            }
            if (value == 0) {
                return DirectionIO.createListFromValue("north");
            }
            if (value == 2) {
                return DirectionIO.createListFromValue("south");
            }
            return new ArrayList<String>();
        }
    }

    public static class ClothAddIO
    extends ValueIO {
        public ClothAddIO() {
            this.description = "A cloth texture that can be worn when an item is present. The optional second parameter is the layer it will be placed on. ('free,textures/entity/byzanz/male/clothes/byz.miner.1.A.png' or 'free,0,textures/entity/norman/female/clothes/nor_housewife_0.png')";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            Map map = (Map)field.get(targetClass);
            if ((value = value.toLowerCase()).split(",").length < 2) {
                MillLog.error(null, "Two or three values are required for all clothes tag: either (cloth name, then texture file) or (cloth name, layer, then texture file).");
            } else {
                String textpath;
                int layer = 0;
                String clothname = value.split(",")[0];
                if (value.split(",").length == 2) {
                    textpath = value.split(",")[1];
                } else {
                    layer = Integer.parseInt(value.split(",")[1]);
                    textpath = value.split(",")[2];
                }
                if (!map.containsKey(clothname + "_" + layer)) {
                    map.put(clothname + "_" + layer, new ArrayList());
                }
                ((List)map.get(clothname + "_" + layer)).add(textpath);
            }
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            Map clothes = (Map)rawValue;
            ArrayList<String> results = new ArrayList<String>();
            ArrayList keys = new ArrayList(clothes.keySet());
            Collections.sort(keys);
            for (String key : keys) {
                String cloth = key.split("_")[0];
                String layer = key.split("_")[1];
                for (String texture : (List)clothes.get(key)) {
                    results.add(cloth + "," + layer + "," + texture);
                }
            }
            return results;
        }
    }

    public static class BrickColourThemeAddIO
    extends ValueIO {
        public BrickColourThemeAddIO() {
            this.description = "Example: 'rajput:30;brown:50,red:40,orange:30;yellow:30'";
        }

        private String getAllColourNames() {
            String colours = "";
            for (EnumDyeColor color : EnumDyeColor.values()) {
                colours = colours + color.func_176610_l() + " ";
            }
            return colours;
        }

        private EnumDyeColor getColourByName(String colourName) {
            for (EnumDyeColor color : EnumDyeColor.values()) {
                if (!color.func_176610_l().equals(colourName)) continue;
                return color;
            }
            return null;
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            String themeDefinition = value.split(";")[0];
            String themeKey = themeDefinition.split(":")[0];
            int themeWeight = Integer.parseInt(themeDefinition.split(":")[1]);
            HashMap<EnumDyeColor, Map<EnumDyeColor, Integer>> themesMapping = new HashMap<EnumDyeColor, Map<EnumDyeColor, Integer>>();
            HashMap<EnumDyeColor, Integer> otherMapping = null;
            for (int i = 1; i < value.split(";").length; ++i) {
                String themeData = value.split(";")[i];
                String key = themeData.split(":")[0];
                String possibleValues = themeData.substring(key.length() + 1, themeData.length());
                HashMap<EnumDyeColor, Integer> values = new HashMap<EnumDyeColor, Integer>();
                for (String weightedColour : possibleValues.split(",")) {
                    String colourName = weightedColour.split(":")[0];
                    EnumDyeColor colour = this.getColourByName(colourName);
                    if (colour == null) {
                        throw new MillLog.MillenaireException("Unknown colour: " + colourName + ". It should be among: " + this.getAllColourNames());
                    }
                    int weight = Integer.parseInt(weightedColour.split(":")[1]);
                    values.put(colour, weight);
                }
                if (key.equals("other")) {
                    otherMapping = values;
                    continue;
                }
                EnumDyeColor inputColour = this.getColourByName(key);
                if (inputColour == null) {
                    throw new MillLog.MillenaireException("Unknown colour: " + key + ". It should be among: " + this.getAllColourNames());
                }
                themesMapping.put(inputColour, values);
            }
            for (EnumDyeColor inputColour : EnumDyeColor.values()) {
                if (themesMapping.containsKey(inputColour)) continue;
                themesMapping.put(inputColour, otherMapping);
            }
            List themeList = (List)field.get(targetClass);
            themeList.add(new VillageType.BrickColourTheme(themeKey, themeWeight, themesMapping));
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            List themeList = (List)rawValue;
            ArrayList<String> results = new ArrayList<String>();
            for (VillageType.BrickColourTheme theme : themeList) {
                String line = theme.key + ":" + theme.weight;
                for (EnumDyeColor inputColour : theme.colours.keySet()) {
                    line = line + ";" + inputColour.func_176610_l() + ":";
                    String values = "";
                    for (EnumDyeColor outputColour : theme.colours.get(inputColour).keySet()) {
                        if (values.length() > 0) {
                            values = values + ",";
                        }
                        values = values + outputColour.func_176610_l() + ":" + theme.colours.get(inputColour).get(outputColour);
                    }
                    line = line + values;
                }
                results.add(line);
            }
            return results;
        }
    }

    public static class BooleanIO
    extends ValueIO {
        public BooleanIO() {
            this.description = "boolean";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            field.set(targetClass, Boolean.parseBoolean(value));
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            Boolean value = (Boolean)rawValue;
            return BooleanIO.createListFromValue(value != false ? "true" : "false");
        }
    }

    public static class BonusItemAddIO
    extends ValueIO {
        public BonusItemAddIO() {
            this.description = "item, chance and (optional) required tag ('leather,50' or 'boudin,50,oven')";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            String[] temp2 = value.toLowerCase().split(",");
            AnnotedParameter.BonusItem bonusItem = null;
            if (temp2.length != 3 && temp2.length != 2) {
                throw new MillLog.MillenaireException("bonusitem must take the form of bonusitem=goodname,chanceon100 or bonusitem=goodname,chanceon100,requiredtag (ex: leather,50 or tripes,10,oven).");
            }
            if (InvItem.INVITEMS_BY_NAME.containsKey(temp2[0])) {
                bonusItem = temp2.length == 3 ? new AnnotedParameter.BonusItem(InvItem.INVITEMS_BY_NAME.get(temp2[0]), Integer.parseInt(temp2[1]), temp2[2].trim()) : new AnnotedParameter.BonusItem(InvItem.INVITEMS_BY_NAME.get(temp2[0]), Integer.parseInt(temp2[1]));
            } else {
                throw new MillLog.MillenaireException("Unknown bonusitem item :" + temp2[0]);
            }
            ((List)field.get(targetClass)).add(bonusItem);
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            List bonusItems = (List)rawValue;
            ArrayList<String> results = new ArrayList<String>();
            for (AnnotedParameter.BonusItem bonusItem : bonusItems) {
                if (bonusItem.tag == null) {
                    results.add(bonusItem.item.getKey() + "," + bonusItem.chance);
                    continue;
                }
                results.add(bonusItem.item.getKey() + "," + bonusItem.chance + "," + bonusItem.tag);
            }
            return results;
        }
    }

    public static class BlockStateIO
    extends ValueIO {
        public BlockStateIO() {
            this.description = "a Minecraft blockstate ('red_flower;type=blue_orchid')";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            String[] params = value.split(";");
            Block block = Block.func_149684_b((String)params[0]);
            if (block == null) {
                throw new MillLog.MillenaireException("Unknown block: " + value);
            }
            if (params.length > 1) {
                field.set(targetClass, BlockStateUtilities.getBlockStateWithValues(block.func_176223_P(), params[1]));
            } else {
                field.set(targetClass, block.func_176223_P());
            }
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            IBlockState value = (IBlockState)rawValue;
            return BlockStateIO.createListFromValue(BlockStateUtilities.getStringFromBlockState(value));
        }
    }

    public static class BlockStateAddIO
    extends ValueIO {
        public BlockStateAddIO() {
            this.description = "a Minecraft blockstate ('red_flower;type=blue_orchid') (multiple lines possible)";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            String[] params = value.split(";");
            Block block = Block.func_149684_b((String)params[0]);
            if (block == null) {
                throw new MillLog.MillenaireException("Unknown block: " + value);
            }
            IBlockState bs = params.length > 1 ? BlockStateUtilities.getBlockStateWithValues(block.func_176223_P(), params[1]) : block.func_176223_P();
            ((List)field.get(targetClass)).add(bs);
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            List blockStates = (List)rawValue;
            ArrayList<String> results = new ArrayList<String>();
            for (IBlockState bs : blockStates) {
                results.add(BlockStateUtilities.getStringFromBlockState(bs));
            }
            return results;
        }
    }

    public static class BlockIdIO
    extends ValueIO {
        public BlockIdIO() {
            this.description = "Minecraft ID of a block ('wheat')";
        }

        @Override
        public void readValue(Object targetClass, Field field, String value) throws Exception {
            ResourceLocation rl = new ResourceLocation(value.toLowerCase());
            if (Block.field_149771_c.func_82594_a((Object)rl) == null) {
                throw new MillLog.MillenaireException("Unknown block: " + value);
            }
            field.set(targetClass, rl);
        }

        @Override
        public List<String> writeValue(Object rawValue) throws Exception {
            ResourceLocation value = (ResourceLocation)rawValue;
            if (value == null) {
                return null;
            }
            return BlockIdIO.createListFromValue(value.toString());
        }
    }
}

