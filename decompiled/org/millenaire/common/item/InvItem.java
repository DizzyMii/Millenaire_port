/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package org.millenaire.common.item;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;

public final class InvItem
implements Comparable<InvItem> {
    private static Map<Integer, InvItem> CACHE = new HashMap<Integer, InvItem>();
    public static final int ANYENCHANTED = 1;
    public static final int ENCHANTEDSWORD = 2;
    public static final List<InvItem> freeGoods = new ArrayList<InvItem>();
    public static final HashMap<String, InvItem> INVITEMS_BY_NAME = new HashMap();
    public final Item item;
    public final Block block;
    public final ItemStack staticStack;
    public final ItemStack[] staticStackArray;
    public final int meta;
    public final int special;
    private String key = null;

    private static int computeHash(Item item, int meta, int special) {
        if (item == null) {
            return (meta << 8) + (special << 12);
        }
        return item.hashCode() + (meta << 8) + (special << 12);
    }

    public static InvItem createInvItem(Block block) {
        return InvItem.createInvItem(block, 0);
    }

    public static InvItem createInvItem(Block block, int meta) {
        Item item = Item.func_150898_a((Block)block);
        int hash = InvItem.computeHash(item, meta, 0);
        if (CACHE.containsKey(hash)) {
            if (InvItem.CACHE.get((Object)Integer.valueOf((int)hash)).item == item) {
                return CACHE.get(hash);
            }
            MillLog.error(null, "Collision between InvItem hash? " + CACHE.get(hash) + " has same hash as " + item + ":" + meta + ": " + hash);
        }
        InvItem ii = new InvItem(block, meta);
        CACHE.put(hash, ii);
        return ii;
    }

    public static InvItem createInvItem(IBlockState bs) {
        return InvItem.createInvItem(bs.func_177230_c(), bs.func_177230_c().func_176201_c(bs));
    }

    public static InvItem createInvItem(int special) {
        int hash = InvItem.computeHash(null, 0, special);
        if (CACHE.containsKey(hash)) {
            if (InvItem.CACHE.get((Object)Integer.valueOf((int)hash)).special == special) {
                return CACHE.get(hash);
            }
            MillLog.error(null, "Collision between InvItem hash? " + CACHE.get(hash) + " has same hash as special: " + special + ": " + hash);
        }
        InvItem ii = new InvItem(special);
        CACHE.put(hash, ii);
        return ii;
    }

    public static InvItem createInvItem(Item item) {
        return InvItem.createInvItem(item, 0);
    }

    public static InvItem createInvItem(Item item, int meta) {
        int hash = InvItem.computeHash(item, meta, 0);
        if (CACHE.containsKey(hash)) {
            if (InvItem.CACHE.get((Object)Integer.valueOf((int)hash)).item == item) {
                return CACHE.get(hash);
            }
            MillLog.error(null, "Collision between InvItem hash? " + CACHE.get(hash) + " has same hash as " + item + ":" + meta + ": " + hash);
        }
        InvItem ii = new InvItem(item, meta);
        CACHE.put(hash, ii);
        return ii;
    }

    public static InvItem createInvItem(ItemStack is) {
        return InvItem.createInvItem(is.func_77973_b(), is.func_77960_j());
    }

    private static void loadInvItemList(File file) {
        try {
            String line;
            BufferedReader reader = MillCommonUtilities.getReader(file);
            while ((line = reader.readLine()) != null) {
                try {
                    String[] temp;
                    if (line.trim().length() <= 0 || line.startsWith("//") || (temp = line.trim().split(";")).length <= 2) continue;
                    Item item = Item.func_111206_d((String)temp[1]);
                    if (item != null) {
                        INVITEMS_BY_NAME.put(temp[0], InvItem.createInvItem(item, Integer.parseInt(temp[2])));
                        continue;
                    }
                    Block block = Block.func_149684_b((String)temp[1]);
                    if (block == null) {
                        MillLog.error(null, "Could not load good: " + temp[1]);
                        continue;
                    }
                    if (Item.func_150898_a((Block)block) == null) {
                        MillLog.error(null, "Tried to create good from block with no item: " + line);
                        continue;
                    }
                    INVITEMS_BY_NAME.put(temp[0], InvItem.createInvItem(block, Integer.parseInt(temp[2])));
                }
                catch (Exception e) {
                    MillLog.printException("Exception while reading line: " + line, e);
                }
            }
        }
        catch (IOException e) {
            MillLog.printException(e);
            return;
        }
    }

    public static void loadItemList() {
        for (File loadDir : Mill.loadingDirs) {
            File mainList = new File(loadDir, "itemlist.txt");
            if (!mainList.exists()) continue;
            InvItem.loadInvItemList(mainList);
        }
        INVITEMS_BY_NAME.put("anyenchanted", InvItem.createInvItem(1));
        INVITEMS_BY_NAME.put("enchantedsword", InvItem.createInvItem(2));
        for (String key : INVITEMS_BY_NAME.keySet()) {
            INVITEMS_BY_NAME.get(key).setKey(key);
        }
    }

    private InvItem(Block block, int meta) {
        this.block = block;
        this.item = Item.func_150898_a((Block)block);
        this.meta = meta;
        this.staticStack = new ItemStack(this.item, 1, meta);
        this.staticStackArray = new ItemStack[]{this.staticStack};
        this.special = 0;
        this.checkValidity();
    }

    private InvItem(int special) {
        this.special = special;
        this.staticStack = null;
        this.staticStackArray = new ItemStack[]{this.staticStack};
        this.item = null;
        this.block = null;
        this.meta = 0;
        this.checkValidity();
    }

    private InvItem(Item item, int meta) {
        this.item = item;
        this.block = Block.func_149634_a((Item)item) != Blocks.field_150350_a ? Block.func_149634_a((Item)item) : null;
        this.meta = meta;
        this.staticStack = new ItemStack(item, 1, meta);
        this.staticStackArray = new ItemStack[]{this.staticStack};
        this.special = 0;
        this.checkValidity();
    }

    private InvItem(ItemStack is) {
        this.item = is.func_77973_b();
        this.block = Block.func_149634_a((Item)this.item) != Blocks.field_150350_a ? Block.func_149634_a((Item)this.item) : null;
        this.meta = is.func_77952_i() > 0 ? is.func_77952_i() : 0;
        this.staticStack = new ItemStack(this.item, 1, this.meta);
        this.staticStackArray = new ItemStack[]{this.staticStack};
        this.special = 0;
        this.checkValidity();
    }

    private InvItem(String s) {
        this.special = 0;
        if (s.split("/").length > 2) {
            int id = Integer.parseInt(s.split("/")[0]);
            if (Item.func_150899_d((int)id) == null) {
                MillLog.printException("Tried creating InvItem with null id from string: " + s, new Exception());
                this.item = null;
            } else {
                this.item = Item.func_150899_d((int)id);
            }
            this.block = Block.func_149729_e((int)id) == null ? null : Block.func_149729_e((int)id);
            this.meta = Integer.parseInt(s.split("/")[1]);
            this.staticStack = new ItemStack(this.item, 1, this.meta);
        } else {
            this.staticStack = null;
            this.item = null;
            this.block = null;
            this.meta = 0;
        }
        this.staticStackArray = new ItemStack[]{this.staticStack};
        this.checkValidity();
    }

    private void checkValidity() {
        if (this.block == Blocks.field_150350_a) {
            MillLog.error(this, "Attempted to create an InvItem for air blocks.");
        }
        if (this.item == null && this.block == null && this.special == 0) {
            MillLog.error(this, "Attempted to create an empty InvItem.");
        }
    }

    @Override
    public int compareTo(InvItem ii) {
        if (this.special > 0 || ii.special > 0) {
            return this.special - ii.special;
        }
        if (this.item == null || ii.item == null) {
            return this.special - ii.special;
        }
        return this.item.func_77658_a().compareTo(ii.item.func_77658_a()) + this.meta - ii.meta;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof InvItem)) {
            return false;
        }
        InvItem other = (InvItem)obj;
        return other.item == this.item && other.meta == this.meta && other.special == this.special;
    }

    public Block getBlock() {
        return this.block;
    }

    public Item getItem() {
        return this.item;
    }

    public ItemStack getItemStack() {
        if (this.staticStack == null) {
            return null;
        }
        return this.staticStack;
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        if (this.special == 1) {
            return LanguageUtilities.string("ui.anyenchanted");
        }
        if (this.special == 2) {
            return LanguageUtilities.string("ui.enchantedsword");
        }
        if (this.meta == -1 && this.block == Blocks.field_150364_r) {
            return LanguageUtilities.string("ui.woodforplanks");
        }
        if (this.meta == 0 && this.block == Blocks.field_150364_r) {
            return LanguageUtilities.string("ui.woodoak");
        }
        if (this.meta == 1 && this.block == Blocks.field_150364_r) {
            return LanguageUtilities.string("ui.woodpine");
        }
        if (this.meta == 2 && this.block == Blocks.field_150364_r) {
            return LanguageUtilities.string("ui.woodbirch");
        }
        if (this.meta == 3 && this.block == Blocks.field_150364_r) {
            return LanguageUtilities.string("ui.woodjungle");
        }
        if (this.meta == 0 && this.block == Blocks.field_150363_s) {
            return LanguageUtilities.string("ui.woodacacia");
        }
        if (this.meta == 1 && this.block == Blocks.field_150363_s) {
            return LanguageUtilities.string("ui.wooddarkoak");
        }
        if (this.meta == -1) {
            return new ItemStack(this.item, 0).func_82833_r();
        }
        if (this.item != null) {
            return new ItemStack(this.item, 1, this.meta).func_82833_r();
        }
        MillLog.printException(new MillLog.MillenaireException("Trying to get the name of an invalid InvItem."));
        return "id:" + this.item + ";meta:" + this.meta;
    }

    public String getTranslationKey() {
        return "_item:" + Item.func_150891_b((Item)this.item) + ":" + this.meta;
    }

    public int hashCode() {
        return InvItem.computeHash(this.item, this.meta, this.special);
    }

    public boolean matches(InvItem ii) {
        return ii.item == this.item && (ii.meta == this.meta || ii.meta == -1 || this.meta == -1);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String toString() {
        return this.getName() + "/" + this.meta;
    }

    static {
        freeGoods.add(InvItem.createInvItem(Blocks.field_150346_d, 0));
        freeGoods.add(InvItem.createInvItem(MillBlocks.EARTH_DECORATION, 0));
        freeGoods.add(InvItem.createInvItem((Block)Blocks.field_150355_j, 0));
        freeGoods.add(InvItem.createInvItem(Blocks.field_150345_g, 0));
        freeGoods.add(InvItem.createInvItem((Block)Blocks.field_150327_N, 0));
        freeGoods.add(InvItem.createInvItem((Block)Blocks.field_150328_O, 0));
        freeGoods.add(InvItem.createInvItem((Block)Blocks.field_150329_H, 0));
        freeGoods.add(InvItem.createInvItem(Blocks.field_150435_aG, 0));
        freeGoods.add(InvItem.createInvItem(Blocks.field_150382_bo, 0));
        freeGoods.add(InvItem.createInvItem((Block)Blocks.field_150362_t, -1));
        freeGoods.add(InvItem.createInvItem(Blocks.field_150345_g, -1));
        freeGoods.add(InvItem.createInvItem(Blocks.field_150414_aQ, 0));
        freeGoods.add(InvItem.createInvItem(MillBlocks.PATHDIRT, -1));
        freeGoods.add(InvItem.createInvItem(MillBlocks.PATHDIRT_SLAB, -1));
        freeGoods.add(InvItem.createInvItem(MillBlocks.PATHGRAVEL, -1));
        freeGoods.add(InvItem.createInvItem(MillBlocks.PATHGRAVEL_SLAB, -1));
        freeGoods.add(InvItem.createInvItem(MillBlocks.PATHSLABS, -1));
        freeGoods.add(InvItem.createInvItem(MillBlocks.PATHSLABS_SLAB, -1));
        freeGoods.add(InvItem.createInvItem(MillBlocks.PATHSANDSTONE, -1));
        freeGoods.add(InvItem.createInvItem(MillBlocks.PATHSANDSTONE_SLAB, -1));
        freeGoods.add(InvItem.createInvItem(MillBlocks.PATHGRAVELSLABS, -1));
        freeGoods.add(InvItem.createInvItem(MillBlocks.PATHGRAVELSLABS_SLAB, -1));
        freeGoods.add(InvItem.createInvItem(MillBlocks.PATHOCHRESLABS, -1));
        freeGoods.add(InvItem.createInvItem(MillBlocks.PATHOCHRESLABS_SLAB, -1));
    }

    public static interface IItemInitialEnchantmens {
        public void applyEnchantments(ItemStack var1);
    }
}

