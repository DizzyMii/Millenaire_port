/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.attributes.AttributeModifier
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.world.World
 *  net.minecraft.world.storage.ISaveHandler
 *  net.minecraft.world.storage.SaveHandler
 *  net.minecraftforge.fml.common.Loader
 *  net.minecraftforge.fml.relauncher.ReflectionHelper
 */
package org.millenaire.common.utilities;

import com.google.common.collect.Multimap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.millenaire.common.advancements.GenericAdvancement;
import org.millenaire.common.advancements.MillAdvancements;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillagerType;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.world.UserProfile;

public class MillCommonUtilities {
    private static final String MILLENAIRE_ORG_ROOT = "http://millenaire.org";
    public static Random random = new Random();
    private static File baseDir = null;
    private static File customDir = null;

    public static boolean chanceOn(int i) {
        return MillCommonUtilities.getRandom().nextInt(i) == 0;
    }

    public static void changeMoney(IInventory chest, int toChange, EntityPlayer player) {
        boolean hasPurse = false;
        for (int i = 0; i < chest.func_70302_i_() && !hasPurse; ++i) {
            ItemStack stack = chest.func_70301_a(i);
            if (stack == null || stack.func_77973_b() != MillItems.PURSE) continue;
            hasPurse = true;
        }
        if (hasPurse) {
            int current_denier = WorldUtilities.getItemsFromChest(chest, MillItems.DENIER, 0, Integer.MAX_VALUE);
            int current_DENIER_ARGENT = WorldUtilities.getItemsFromChest(chest, MillItems.DENIER_ARGENT, 0, Integer.MAX_VALUE);
            int current_DENIER_OR = WorldUtilities.getItemsFromChest(chest, MillItems.DENIER_OR, 0, Integer.MAX_VALUE);
            int finalChange = current_DENIER_OR * 64 * 64 + current_DENIER_ARGENT * 64 + current_denier + toChange;
            for (int i = 0; i < chest.func_70302_i_() && finalChange != 0; ++i) {
                ItemStack stack = chest.func_70301_a(i);
                if (stack == null || stack.func_77973_b() != MillItems.PURSE) continue;
                int content = MillItems.PURSE.totalDeniers(stack) + finalChange;
                if (content >= 0) {
                    MillItems.PURSE.setDeniers(stack, player, content);
                    finalChange = 0;
                    continue;
                }
                MillItems.PURSE.setDeniers(stack, player, 0);
                finalChange = content;
            }
        } else {
            int total = toChange + MillCommonUtilities.countMoney(chest);
            int denier = total % 64;
            int DENIER_ARGENT = (total - denier) / 64 % 64;
            int DENIER_OR = (total - denier - DENIER_ARGENT * 64) / 4096;
            if (player != null && DENIER_OR > 0) {
                MillAdvancements.CRESUS.grant(player);
            }
            int current_denier = MillCommonUtilities.countChestItems(chest, MillItems.DENIER, 0);
            int current_DENIER_ARGENT = MillCommonUtilities.countChestItems(chest, MillItems.DENIER_ARGENT, 0);
            int current_DENIER_OR = MillCommonUtilities.countChestItems(chest, MillItems.DENIER_OR, 0);
            if (MillConfigValues.LogWifeAI >= 1) {
                MillLog.major(null, "Putting: " + denier + "/" + DENIER_ARGENT + "/" + DENIER_OR + " replacing " + current_denier + "/" + current_DENIER_ARGENT + "/" + current_DENIER_OR);
            }
            if (denier < current_denier) {
                WorldUtilities.getItemsFromChest(chest, MillItems.DENIER, 0, current_denier - denier);
            } else if (denier > current_denier) {
                MillCommonUtilities.putItemsInChest(chest, MillItems.DENIER, 0, denier - current_denier);
            }
            if (DENIER_ARGENT < current_DENIER_ARGENT) {
                WorldUtilities.getItemsFromChest(chest, MillItems.DENIER_ARGENT, 0, current_DENIER_ARGENT - DENIER_ARGENT);
            } else if (DENIER_ARGENT > current_DENIER_ARGENT) {
                MillCommonUtilities.putItemsInChest(chest, MillItems.DENIER_ARGENT, 0, DENIER_ARGENT - current_DENIER_ARGENT);
            }
            if (DENIER_OR < current_DENIER_OR) {
                WorldUtilities.getItemsFromChest(chest, MillItems.DENIER_OR, 0, current_DENIER_OR - DENIER_OR);
            } else if (DENIER_OR > current_DENIER_OR) {
                MillCommonUtilities.putItemsInChest(chest, MillItems.DENIER_OR, 0, DENIER_OR - current_DENIER_OR);
            }
        }
    }

    public static int countChestItems(IInventory chest, Block block, int meta) {
        return MillCommonUtilities.countChestItems(chest, Item.func_150898_a((Block)block), meta);
    }

    public static int countChestItems(IInventory chest, IBlockState blockState) {
        return MillCommonUtilities.countChestItems(chest, blockState.func_177230_c(), blockState.func_177230_c().func_176201_c(blockState));
    }

    public static int countChestItems(IInventory chest, Item item, int meta) {
        if (chest == null) {
            return 0;
        }
        int maxSlot = chest.func_70302_i_();
        if (chest instanceof InventoryPlayer) {
            maxSlot -= 5;
        }
        int nb = 0;
        for (int i = 0; i < maxSlot; ++i) {
            ItemStack stack = chest.func_70301_a(i);
            if (stack != null && stack.func_77973_b() == item && (meta == -1 || stack.func_77952_i() < 0 || stack.func_77952_i() == meta)) {
                nb += stack.func_190916_E();
            }
            if (item != Item.func_150898_a((Block)Blocks.field_150364_r) || meta != -1 || stack == null || stack.func_77973_b() != Item.func_150898_a((Block)Blocks.field_150363_s)) continue;
            nb += stack.func_190916_E();
        }
        return nb;
    }

    public static int countFurnaceItems(IInventory furnace, Item item, int meta) {
        if (furnace == null) {
            return 0;
        }
        int nb = 0;
        ItemStack stack = furnace.func_70301_a(2);
        if (stack != null && stack.func_77973_b() == item && (meta == -1 || stack.func_77952_i() < 0 || stack.func_77952_i() == meta)) {
            nb += stack.func_190916_E();
        }
        if (item == Item.func_150898_a((Block)Blocks.field_150364_r) && meta == -1 && stack != null && stack.func_77973_b() == Item.func_150898_a((Block)Blocks.field_150363_s)) {
            nb += stack.func_190916_E();
        }
        return nb;
    }

    public static int countMoney(IInventory chest) {
        int deniers = 0;
        for (int i = 0; i < chest.func_70302_i_(); ++i) {
            ItemStack stack = chest.func_70301_a(i);
            if (stack == null) continue;
            if (stack.func_77973_b() == MillItems.PURSE) {
                deniers += MillItems.PURSE.totalDeniers(stack);
                continue;
            }
            if (stack.func_77973_b() == MillItems.DENIER) {
                deniers += stack.func_190916_E();
                continue;
            }
            if (stack.func_77973_b() == MillItems.DENIER_ARGENT) {
                deniers += stack.func_190916_E() * 64;
                continue;
            }
            if (stack.func_77973_b() != MillItems.DENIER_OR) continue;
            deniers += stack.func_190916_E() * 64 * 64;
        }
        return deniers;
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; ++i) {
                boolean success = MillCommonUtilities.deleteDir(new File(dir, children[i]));
                if (success) continue;
                return false;
            }
        }
        return dir.delete();
    }

    public static String flattenStrings(Collection<String> strings) {
        return strings.stream().collect(Collectors.joining(", "));
    }

    public static void generateHearts(Entity ent) {
        for (int var3 = 0; var3 < 7; ++var3) {
            double var4 = random.nextGaussian() * 0.02;
            double var6 = random.nextGaussian() * 0.02;
            double var8 = random.nextGaussian() * 0.02;
            ent.field_70170_p.func_175688_a(EnumParticleTypes.HEART, ent.field_70165_t + (double)(random.nextFloat() * ent.field_70130_N * 2.0f) - (double)ent.field_70130_N, ent.field_70163_u + 0.5 + (double)(random.nextFloat() * ent.field_70131_O), ent.field_70161_v + (double)(random.nextFloat() * ent.field_70130_N * 2.0f) - (double)ent.field_70130_N, var4, var6, var8, new int[0]);
        }
    }

    public static BufferedWriter getAppendWriter(File file) throws UnsupportedEncodingException, FileNotFoundException {
        return new BufferedWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(file, true), "UTF8"));
    }

    public static File getBuildingsDir(World world) {
        File buildingsDir;
        File saveDir = MillCommonUtilities.getWorldSaveDir(world);
        File millenaireDir = new File(saveDir, "millenaire");
        if (!millenaireDir.exists()) {
            millenaireDir.mkdir();
        }
        if (!(buildingsDir = new File(millenaireDir, "buildings")).exists()) {
            buildingsDir.mkdir();
        }
        return buildingsDir;
    }

    public static String getCardinalDirectionStringFromAngle(int angle) {
        if ((angle %= 360) < 0) {
            angle += 360;
        }
        if (angle < 22 || angle > 338) {
            return "south";
        }
        if (angle < 68) {
            return "south-west";
        }
        if (angle < 112) {
            return "west";
        }
        if (angle < 158) {
            return "north-west";
        }
        if (angle < 202) {
            return "north";
        }
        if (angle < 248) {
            return "north-east";
        }
        if (angle < 292) {
            return "east";
        }
        return "south-east";
    }

    public static Method getDrawItemStackInventoryMethod(GuiContainer gui) {
        return ReflectionHelper.findMethod(GuiContainer.class, (String)"drawItemStack", (String)"func_146982_a", (Class[])new Class[]{ItemStack.class, Integer.TYPE, Integer.TYPE, String.class});
    }

    public static Method getDrawSlotInventoryMethod(GuiContainer gui) {
        return ReflectionHelper.findMethod(GuiContainer.class, (String)"drawSlot", (String)"func_146977_a", (Class[])new Class[]{Slot.class});
    }

    public static File getExportDir() {
        File exportDir = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "exports");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        return exportDir;
    }

    public static List<String> getFileLines(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)new FileInputStream(file), "UTF8"));
        ArrayList<String> lines = new ArrayList<String>();
        String line = reader.readLine();
        while (line != null) {
            lines.add(line);
            line = reader.readLine();
        }
        reader.close();
        return lines;
    }

    public static int getInvItemHashTotal(HashMap<InvItem, Integer> map) {
        int total = 0;
        for (InvItem key : map.keySet()) {
            total += map.get(key).intValue();
        }
        return total;
    }

    public static Item getItemById(int id) {
        return Item.func_150899_d((int)id);
    }

    public static double getItemWeaponDamage(Item item) {
        Multimap multimap = item.func_111205_h(EntityEquipmentSlot.MAINHAND);
        if (multimap.containsKey((Object)SharedMonsterAttributes.field_111264_e.func_111108_a()) && multimap.get((Object)SharedMonsterAttributes.field_111264_e.func_111108_a()).toArray().length > 0 && multimap.get((Object)SharedMonsterAttributes.field_111264_e.func_111108_a()).toArray()[0] instanceof AttributeModifier) {
            AttributeModifier weaponModifier = (AttributeModifier)multimap.get((Object)SharedMonsterAttributes.field_111264_e.func_111108_a()).toArray()[0];
            return weaponModifier.func_111164_d();
        }
        return 0.0;
    }

    public static int[] getJumpDestination(World world, int x, int y, int z) {
        if (!WorldUtilities.isBlockFullCube(world, x, y, z) && !WorldUtilities.isBlockFullCube(world, x, y + 1, z)) {
            return new int[]{x, y, z};
        }
        if (!WorldUtilities.isBlockFullCube(world, x + 1, y, z) && !WorldUtilities.isBlockFullCube(world, x + 1, y + 1, z)) {
            return new int[]{x + 1, y, z};
        }
        if (!WorldUtilities.isBlockFullCube(world, x - 1, y, z) && !WorldUtilities.isBlockFullCube(world, x - 1, y + 1, z)) {
            return new int[]{x - 1, y, z};
        }
        if (!WorldUtilities.isBlockFullCube(world, x, y, z + 1) && !WorldUtilities.isBlockFullCube(world, x, y + 1, z + 1)) {
            return new int[]{x, y, z + 1};
        }
        if (!WorldUtilities.isBlockFullCube(world, x, y, z - 1) && !WorldUtilities.isBlockFullCube(world, x, y + 1, z - 1)) {
            return new int[]{x, y, z - 1};
        }
        return null;
    }

    public static File getMillenaireContentDir() {
        if (baseDir == null) {
            baseDir = new File(MillCommonUtilities.getModsDir(), "millenaire");
        }
        return baseDir;
    }

    public static File getMillenaireCustomContentDir() {
        if (customDir == null) {
            customDir = new File(MillCommonUtilities.getModsDir(), "millenaire-custom");
        }
        return customDir;
    }

    public static File getMillenaireHelpDir() {
        return new File(MillCommonUtilities.getMillenaireContentDir(), "help");
    }

    public static File getModsDir() {
        return new File(Loader.instance().getConfigDir().getParentFile(), "mods");
    }

    public static int getPriceColourMC(int price) {
        if (price >= 4096) {
            return 14;
        }
        if (price >= 64) {
            return 15;
        }
        return 6;
    }

    public static Random getRandom() {
        if (random == null) {
            random = new Random();
        }
        return random;
    }

    public static BufferedReader getReader(File file) throws UnsupportedEncodingException, FileNotFoundException {
        return new BufferedReader(new InputStreamReader((InputStream)new FileInputStream(file), "UTF8"));
    }

    public static String getShortPrice(int price) {
        String res = "";
        if (price >= 4096) {
            res = (int)Math.floor(price / 4096) + "o ";
            price %= 4096;
        }
        if (price >= 64) {
            res = res + (int)Math.floor(price / 64) + "a ";
            price %= 64;
        }
        if (price > 0) {
            res = res + price + "d";
        }
        return res.trim();
    }

    public static WeightedChoice getWeightedChoice(List<? extends WeightedChoice> choices, EntityPlayer player) {
        int weightTotal = 0;
        ArrayList<Integer> weights = new ArrayList<Integer>();
        for (WeightedChoice weightedChoice : choices) {
            weightTotal += weightedChoice.getChoiceWeight(player);
            weights.add(weightedChoice.getChoiceWeight(player));
        }
        if (weightTotal < 1) {
            return null;
        }
        int random = MillCommonUtilities.randomInt(weightTotal);
        boolean bl = false;
        for (int i = 0; i < choices.size(); ++i) {
            if (random >= (var5_8 += ((Integer)weights.get(i)).intValue())) continue;
            return choices.get(i);
        }
        return null;
    }

    public static File getWorldSaveDir(World world) {
        ISaveHandler isavehandler = world.func_72860_G();
        if (isavehandler instanceof SaveHandler) {
            return ((SaveHandler)isavehandler).func_75765_b();
        }
        return null;
    }

    public static BufferedWriter getWriter(File file) throws UnsupportedEncodingException, FileNotFoundException {
        return new BufferedWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(file), "UTF8"));
    }

    public static void initRandom(int seed) {
        random = new Random(seed);
    }

    /*
     * WARNING - void declaration
     */
    public static void logInstance(World world) {
        void var8_23;
        void var7_14;
        int n;
        if (!MillConfigValues.sendStatistics) {
            return;
        }
        String os = System.getProperty("os.name");
        String mode = Mill.proxy.isTrueServer() ? "s" : (Mill.isDistantClient() ? "c" : "l");
        int totalexp = 0;
        if (Mill.proxy.isTrueServer()) {
            if (!Mill.serverWorlds.isEmpty()) {
                for (UserProfile userProfile : Mill.serverWorlds.get((int)0).profiles.values()) {
                    for (Culture culture : Culture.ListCultures) {
                        totalexp += Math.abs(userProfile.getCultureReputation(culture.key));
                    }
                }
            }
        } else {
            UserProfile p = Mill.proxy.getClientProfile();
            if (p != null) {
                for (Culture c : Culture.ListCultures) {
                    totalexp += Math.abs(p.getCultureReputation(c.key));
                }
            }
        }
        String lang = "";
        if (MillConfigValues.mainLanguage != null) {
            lang = MillConfigValues.mainLanguage.language;
        }
        boolean bl = true;
        if (Mill.proxy.isTrueServer() && !Mill.serverWorlds.isEmpty()) {
            n = Mill.serverWorlds.get((int)0).profiles.size();
        }
        String advancementsSurvivalDone = null;
        for (GenericAdvancement genericAdvancement : MillAdvancements.MILL_ADVANCEMENTS) {
            advancementsSurvivalDone = advancementsSurvivalDone == null ? "" : advancementsSurvivalDone + ",";
            advancementsSurvivalDone = advancementsSurvivalDone + genericAdvancement.getKey() + ":" + MillConfigValues.advancementsSurvival.contains(genericAdvancement.getKey());
        }
        Object var7_13 = null;
        for (GenericAdvancement advancement : MillAdvancements.MILL_ADVANCEMENTS) {
            void var7_17;
            if (var7_14 == null) {
                String string = "";
            } else {
                String string = (String)var7_14 + ",";
            }
            String string = (String)var7_17 + advancement.getKey() + ":" + MillConfigValues.advancementsCreative.contains(advancement.getKey());
        }
        String string = "http://millenaire.org/php/mlnuse.php?uid=" + MillConfigValues.randomUid + "&mlnversion=" + "8.1.2" + "&mode=" + mode + "&lang=" + lang + "&backuplang=" + MillConfigValues.fallback_language + "&nbplayers=" + n + "&os=" + os + "&totalexp=" + totalexp + "&advancementssurvival=" + advancementsSurvivalDone + "&advancementscreative=" + (String)var7_14 + "&validation=" + MillAdvancements.computeKey();
        if (Mill.proxy.getClientProfile() != null && MillConfigValues.sendAdvancementLogin) {
            String string2 = string + "&login=" + Mill.proxy.getClientProfile().playerName;
        }
        String string3 = var8_23.replaceAll(" ", "%20");
        MillConfigValues.logPerformed = true;
        new LogThread(string3).start();
    }

    public static int[] packLong(long nb) {
        return new int[]{(int)(nb >> 32), (int)nb};
    }

    public static String parseItemString(Culture culture, String inputString) {
        if (inputString.split("/").length != 2) {
            return "";
        }
        String result = "";
        String goodKey = inputString.split("/")[0];
        TradeGood good = culture.getTradeGood(goodKey);
        if (good != null) {
            result = good.getName() + ": " + inputString.split("/")[1];
        }
        return result;
    }

    public static boolean probability(double probability) {
        return MillCommonUtilities.getRandom().nextDouble() < probability;
    }

    public static int putItemsInChest(IInventory chest, Block block, int toPut) {
        return MillCommonUtilities.putItemsInChest(chest, Item.func_150898_a((Block)block), 0, toPut);
    }

    public static int putItemsInChest(IInventory chest, Block block, int meta, int toPut) {
        return MillCommonUtilities.putItemsInChest(chest, Item.func_150898_a((Block)block), meta, toPut);
    }

    public static int putItemsInChest(IInventory chest, Item item, int toPut) {
        return MillCommonUtilities.putItemsInChest(chest, item, 0, toPut);
    }

    public static int putItemsInChest(IInventory chest, Item item, int meta, int toPut) {
        ItemStack stack;
        int i;
        if (chest == null) {
            return 0;
        }
        int nb = 0;
        int maxSlot = chest.func_70302_i_();
        if (chest instanceof InventoryPlayer) {
            maxSlot -= 5;
        }
        for (i = 0; i < maxSlot && nb < toPut; ++i) {
            stack = chest.func_70301_a(i);
            if (stack == ItemStack.field_190927_a || stack.func_77973_b() != item || stack.func_77952_i() != meta) continue;
            if (stack.func_77976_d() - stack.func_190916_E() >= toPut - nb) {
                stack.func_190920_e(stack.func_190916_E() + toPut - nb);
                nb = toPut;
            } else {
                nb += stack.func_77976_d() - stack.func_190916_E();
                stack.func_190920_e(stack.func_77976_d());
            }
            chest.func_70299_a(i, stack);
        }
        for (i = 0; i < maxSlot && nb < toPut; ++i) {
            stack = chest.func_70301_a(i);
            if (stack != ItemStack.field_190927_a) continue;
            stack = new ItemStack(item, 1, meta);
            if (stack.func_77973_b() instanceof InvItem.IItemInitialEnchantmens) {
                ((InvItem.IItemInitialEnchantmens)stack.func_77973_b()).applyEnchantments(stack);
            }
            if (toPut - nb <= stack.func_77976_d()) {
                stack.func_190920_e(toPut - nb);
                nb = toPut;
            } else {
                stack.func_190920_e(stack.func_77976_d());
                nb += stack.func_190916_E();
            }
            chest.func_70299_a(i, stack);
        }
        return nb;
    }

    public static int randomInt(int max) {
        return MillCommonUtilities.getRandom().nextInt(max);
    }

    public static long randomLong() {
        return MillCommonUtilities.getRandom().nextLong();
    }

    public static int readInteger(String line) throws Exception {
        int res = 1;
        for (String s : line.trim().split("\\*")) {
            res *= Integer.parseInt(s);
        }
        return res;
    }

    public static void readInventory(NBTTagList nbttaglist, Map<InvItem, Integer> inventory) {
        for (int i = 0; i < nbttaglist.func_74745_c(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.func_150305_b(i);
            String itemName = nbttagcompound1.func_74779_i("item");
            String itemMod = nbttagcompound1.func_74779_i("itemmod");
            int itemMeta = nbttagcompound1.func_74762_e("meta");
            inventory.put(InvItem.createInvItem(Item.func_111206_d((String)(itemMod + ":" + itemName)), itemMeta), nbttagcompound1.func_74762_e("amount"));
        }
    }

    public static boolean testResourcePresence(String domain, String path) {
        return VillagerType.class.getResourceAsStream("/assets/" + domain + "/" + path) != null;
    }

    public static long unpackLong(int nb1, int nb2) {
        return (long)nb1 << 32 | (long)nb2 & 0xFFFFFFFFL;
    }

    public static NBTTagList writeInventory(Map<InvItem, Integer> inventory) {
        NBTTagList nbttaglist = new NBTTagList();
        for (InvItem key : inventory.keySet()) {
            if (key.getItem() != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.func_74778_a("item", key.getItem().getRegistryName().func_110623_a());
                nbttagcompound1.func_74778_a("itemmod", key.getItem().getRegistryName().func_110624_b());
                nbttagcompound1.func_74768_a("meta", key.meta);
                nbttagcompound1.func_74768_a("amount", inventory.get(key).intValue());
                nbttaglist.func_74742_a((NBTBase)nbttagcompound1);
                continue;
            }
            MillLog.error(null, "Key with null item when saving inventory: " + key);
        }
        return nbttaglist;
    }

    public static interface WeightedChoice {
        public int getChoiceWeight(EntityPlayer var1);
    }

    public static class VillageList {
        public List<Point> pos = new ArrayList<Point>();
        public List<String> names = new ArrayList<String>();
        public List<String> types = new ArrayList<String>();
        public List<String> cultures = new ArrayList<String>();
        public List<String> generatedFor = new ArrayList<String>();
        public List<List<Long>> buildingsTime = new ArrayList<List<Long>>();
        public List<List<Long>> villagersTime = new ArrayList<List<Long>>();
        public Map<Point, Integer> rankByPos = new HashMap<Point, Integer>();

        public void addVillage(Point p, String name, String type, String culture, String generatedFor) {
            this.pos.add(p);
            this.names.add(name);
            this.types.add(type);
            this.cultures.add(culture);
            this.generatedFor.add(generatedFor);
            this.buildingsTime.add(new ArrayList());
            this.villagersTime.add(new ArrayList());
            this.rankByPos.put(p, this.pos.size() - 1);
        }

        public void removeVillage(Point p) {
            int i;
            int id = -1;
            for (i = 0; i < this.pos.size() && id == -1; ++i) {
                if (!p.sameBlock(this.pos.get(i))) continue;
                id = i;
            }
            if (id != -1) {
                this.pos.remove(id);
                this.names.remove(id);
                this.types.remove(id);
                this.cultures.remove(id);
                this.generatedFor.remove(id);
            }
            this.rankByPos.clear();
            for (i = 0; i < this.pos.size(); ++i) {
                this.rankByPos.put(this.pos.get(i), i);
            }
        }
    }

    public static class VillageInfo
    implements Comparable<VillageInfo> {
        public String textKey;
        public String[] values;
        public int distance;

        @Override
        public int compareTo(VillageInfo arg0) {
            return arg0.distance - this.distance;
        }

        public boolean equals(Object o) {
            if (o == null || !(o instanceof VillageInfo)) {
                return false;
            }
            return this.distance == ((VillageInfo)o).distance;
        }

        public int hashCode() {
            return super.hashCode();
        }
    }

    public static class VersionCheckThread
    extends Thread {
        @Override
        public void run() {
            try {
                InputStream stream;
                BufferedReader reader;
                String currentVersion;
                if ("8.1.2".contains("@VERSION@")) {
                    return;
                }
                Thread.sleep(60000L);
                boolean devVersion = false;
                if ("8.1.2".contains("alpha") || "8.1.2".contains("beta") || "8.1.2".contains("rc")) {
                    devVersion = true;
                }
                String url = "http://millenaire.org/lastversion/1.12.2";
                if (devVersion) {
                    url = url + "-dev";
                }
                if ((currentVersion = (reader = new BufferedReader(new InputStreamReader(stream = new URL(url = url + ".txt").openStream()))).readLine()) != null && !(currentVersion = currentVersion.trim()).equals("8.1.2")) {
                    String releaseNotesEN = reader.readLine().trim();
                    String releaseNotesFR = reader.readLine().trim();
                    if (MillConfigValues.mainLanguage.language.startsWith("fr")) {
                        String releaseNote = releaseNotesFR;
                    } else if (MillConfigValues.mainLanguage.language.startsWith("en")) {
                        String releaseNote = releaseNotesEN;
                    } else if (MillConfigValues.fallback_language.equals("fr")) {
                        String releaseNote = releaseNotesFR;
                    } else {
                        String releaseNote = releaseNotesEN;
                    }
                    String string = devVersion ? "startup.outdatedversiondev" : "startup.outdatedversion";
                }
            }
            catch (Exception e) {
                MillLog.printException("Error when checking version:", e);
            }
        }
    }

    public static class PrefixExtFileFilter
    implements FilenameFilter {
        String ext = null;
        String prefix = null;

        public PrefixExtFileFilter(String pref, String ext) {
            this.ext = ext;
            this.prefix = pref;
        }

        @Override
        public boolean accept(File file, String name) {
            if (!name.toLowerCase().endsWith("." + this.ext)) {
                return false;
            }
            if (!name.toLowerCase().startsWith(this.prefix)) {
                return false;
            }
            return !name.startsWith(".");
        }
    }

    private static class LogThread
    extends Thread {
        String url;

        public LogThread(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            block2: {
                try {
                    InputStream stream = new URL(this.url).openStream();
                    stream.close();
                }
                catch (Exception e) {
                    if (!MillConfigValues.DEV) break block2;
                    MillLog.error(null, "Exception when calling statistic service:" + e.getMessage().substring(0, e.getMessage().indexOf("?")));
                }
            }
        }
    }

    public static class ExtFileFilter
    implements FilenameFilter {
        String ext = null;

        public ExtFileFilter(String ext) {
            this.ext = ext;
        }

        @Override
        public boolean accept(File file, String name) {
            if (!name.toLowerCase().endsWith("." + this.ext)) {
                return false;
            }
            return !name.startsWith(".");
        }
    }

    public static class BonusThread
    extends Thread {
        String login;

        public BonusThread(String login) {
            this.login = login;
        }

        @Override
        public void run() {
            try {
                InputStream stream = new URL("http://millenaire.org/php/bonuscheck.php?login=" + this.login).openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String result = reader.readLine();
                if (result.trim().equals("thik hai")) {
                    MillConfigValues.bonusEnabled = true;
                    MillConfigValues.bonusCode = MillConfigValues.calculateLoginMD5(this.login);
                    MillConfigValues.writeConfigFile();
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }
}

