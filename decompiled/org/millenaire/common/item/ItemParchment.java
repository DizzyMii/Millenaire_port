/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.world.World
 *  net.minecraft.world.chunk.Chunk
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.item;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.client.book.TextBook;
import org.millenaire.client.gui.DisplayActions;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.ItemMill;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;

public class ItemParchment
extends ItemMill {
    private static final String NBT_VILLAGE_POS = "village_pos_";
    public static final int NORMAN_VILLAGERS = 1;
    public static final int NORMAN_BUILDINGS = 2;
    public static final int NORMAN_ITEMS = 3;
    public static final int VILLAGE_BOOK = 4;
    public static final int INDIAN_VILLAGERS = 5;
    public static final int INDIAN_BUILDINGS = 6;
    public static final int INDIAN_ITEMS = 7;
    public static final int MAYAN_VILLAGERS = 9;
    public static final int MAYAN_BUILDINGS = 10;
    public static final int MAYAN_ITEMS = 11;
    public static final int JAPANESE_VILLAGERS = 16;
    public static final int JAPANESE_BUILDINGS = 17;
    public static final int JAPANESE_ITEMS = 18;
    public static final int SADHU = 15;
    private final int[] textsId;

    public static ItemStack createParchmentForVillage(Building townHall) {
        ItemStack parchment = new ItemStack((Item)MillItems.PARCHMENT_VILLAGE_SCROLL);
        NBTTagCompound compound = new NBTTagCompound();
        townHall.getPos().write(compound, NBT_VILLAGE_POS);
        parchment.func_77982_d(compound);
        return parchment;
    }

    public ItemParchment(String itemName, int t, boolean obsolete) {
        this(itemName, new int[]{t}, obsolete);
    }

    public ItemParchment(String itemName, int[] tIds, boolean obsolete) {
        super(itemName);
        this.textsId = tIds;
        this.field_77777_bU = 1;
        if (obsolete) {
            this.func_77637_a(null);
        }
    }

    private void displayVillageBook(EntityPlayer player, ItemStack is) {
        if (player.field_70170_p.field_72995_K) {
            return;
        }
        Point p = Point.read(is.func_77978_p(), NBT_VILLAGE_POS);
        Building townHall = Mill.getMillWorld(player.field_70170_p).getBuilding(p);
        if (townHall == null) {
            ServerSender.sendTranslatedSentence(player, '6', "panels.invalidid", new String[0]);
            return;
        }
        Chunk chunk = player.field_70170_p.func_72964_e(p.getChunkX(), p.getChunkZ());
        if (!chunk.func_177410_o()) {
            ServerSender.sendTranslatedSentence(player, '6', "panels.toofar", new String[0]);
            return;
        }
        if (!townHall.isActive) {
            ServerSender.sendTranslatedSentence(player, '6', "panels.toofar", new String[0]);
            return;
        }
        ServerSender.displayVillageBookGUI(player, p);
    }

    @SideOnly(value=Side.CLIENT)
    public String func_77653_i(ItemStack stack) {
        Building townHall;
        Point p;
        if (this.textsId[0] == 4 && stack.func_77978_p() != null && (p = Point.read(stack.func_77978_p(), NBT_VILLAGE_POS)) != null && (townHall = Mill.getMillWorld((World)Minecraft.func_71410_x().field_71441_e).getBuilding(p)) != null) {
            return super.func_77653_i(stack) + ": " + townHall.getVillageQualifiedName();
        }
        return super.func_77653_i(stack);
    }

    public ActionResult<ItemStack> func_77659_a(World world, EntityPlayer entityplayer, EnumHand handIn) {
        ItemStack itemstack = entityplayer.func_184586_b(handIn);
        if (this.textsId[0] == 4) {
            if (!world.field_72995_K && this.textsId[0] == 4) {
                this.displayVillageBook(entityplayer, itemstack);
                return new ActionResult(EnumActionResult.SUCCESS, (Object)entityplayer.func_184586_b(handIn));
            }
            return new ActionResult(EnumActionResult.SUCCESS, (Object)entityplayer.func_184586_b(handIn));
        }
        if (world.field_72995_K) {
            if (this.textsId.length == 1) {
                List<List<String>> parchment = LanguageUtilities.getParchment(this.textsId[0]);
                if (parchment != null) {
                    TextBook book = TextBook.convertStringsToBook(parchment);
                    DisplayActions.displayParchmentPanelGUI(entityplayer, book, null, 0, true);
                } else {
                    Mill.proxy.localTranslatedSentence(entityplayer, '6', "panels.notextfound", "" + this.textsId[0]);
                }
            } else {
                ArrayList<List<String>> combinedText = new ArrayList<List<String>>();
                for (int i = 0; i < this.textsId.length; ++i) {
                    List<List<String>> parchment = LanguageUtilities.getParchment(this.textsId[i]);
                    if (parchment == null) continue;
                    for (List<String> page : parchment) {
                        combinedText.add(page);
                    }
                }
                TextBook book = TextBook.convertStringsToBook(combinedText);
                DisplayActions.displayParchmentPanelGUI(entityplayer, book, null, 0, true);
            }
        }
        return new ActionResult(EnumActionResult.SUCCESS, (Object)entityplayer.func_184586_b(handIn));
    }
}

