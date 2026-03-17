/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.item.ItemMill;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.WorldUtilities;

public class ItemPurse
extends ItemMill {
    private static final String ML_PURSE_DENIER = "ml_Purse_DENIER";
    private static final String ML_PURSE_DENIERARGENT = "ml_Purse_DENIERargent";
    private static final String ML_PURSE_DENIEROR = "ml_Purse_DENIERor";
    private static final String ML_PURSE_RAND = "ml_Purse_rand";

    public ItemPurse(String itemName) {
        super(itemName);
        this.func_77625_d(1);
    }

    @SideOnly(value=Side.CLIENT)
    public String func_77653_i(ItemStack purse) {
        if (purse.func_77978_p() == null) {
            return I18n.func_135052_a((String)(MillItems.PURSE.func_77658_a() + ".name"), (Object[])new Object[0]);
        }
        int DENIERs = purse.func_77978_p().func_74762_e(ML_PURSE_DENIER);
        int DENIERargent = purse.func_77978_p().func_74762_e(ML_PURSE_DENIERARGENT);
        int DENIERor = purse.func_77978_p().func_74762_e(ML_PURSE_DENIEROR);
        String label = "";
        if (DENIERor != 0) {
            label = "\u00a7e" + DENIERor + "o ";
        }
        if (DENIERargent != 0) {
            label = label + "\u00a7f" + DENIERargent + "a ";
        }
        if (DENIERs != 0 || label.length() == 0) {
            label = label + "\u00a76" + DENIERs + "d";
        }
        label = label.trim();
        return "\u00a7f" + I18n.func_135052_a((String)(MillItems.PURSE.func_77658_a() + ".name"), (Object[])new Object[0]) + ": " + label;
    }

    public ActionResult<ItemStack> func_77659_a(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack purse = playerIn.func_184586_b(handIn);
        if (this.totalDeniers(purse) > 0) {
            this.removeDeniersFromPurse(purse, playerIn);
        } else {
            this.storeDeniersInPurse(purse, playerIn);
        }
        return super.func_77659_a(worldIn, playerIn, handIn);
    }

    private void removeDeniersFromPurse(ItemStack purse, EntityPlayer player) {
        if (purse.func_77978_p() != null) {
            int DENIERs = purse.func_77978_p().func_74762_e(ML_PURSE_DENIER);
            int DENIERargent = purse.func_77978_p().func_74762_e(ML_PURSE_DENIERARGENT);
            int DENIERor = purse.func_77978_p().func_74762_e(ML_PURSE_DENIEROR);
            int result = MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, MillItems.DENIER, DENIERs);
            purse.func_77978_p().func_74768_a(ML_PURSE_DENIER, DENIERs - result);
            result = MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, MillItems.DENIER_ARGENT, DENIERargent);
            purse.func_77978_p().func_74768_a(ML_PURSE_DENIERARGENT, DENIERargent - result);
            result = MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, MillItems.DENIER_OR, DENIERor);
            purse.func_77978_p().func_74768_a(ML_PURSE_DENIEROR, DENIERor - result);
            purse.func_77978_p().func_74768_a(ML_PURSE_RAND, player.field_70170_p.field_72995_K ? 0 : 1);
        }
    }

    public void setDeniers(ItemStack purse, EntityPlayer player, int amount) {
        int denier = amount % 64;
        int denier_argent = (amount - denier) / 64 % 64;
        int denier_or = (amount - denier - denier_argent * 64) / 4096;
        this.setDeniers(purse, player, denier, denier_argent, denier_or);
    }

    public void setDeniers(ItemStack purse, EntityPlayer player, int DENIER, int DENIERargent, int DENIERor) {
        if (purse.func_77978_p() == null) {
            purse.func_77982_d(new NBTTagCompound());
        }
        purse.func_77978_p().func_74768_a(ML_PURSE_DENIER, DENIER);
        purse.func_77978_p().func_74768_a(ML_PURSE_DENIERARGENT, DENIERargent);
        purse.func_77978_p().func_74768_a(ML_PURSE_DENIEROR, DENIERor);
        purse.func_77978_p().func_74768_a(ML_PURSE_RAND, player.field_70170_p.field_72995_K ? 0 : 1);
    }

    private void storeDeniersInPurse(ItemStack purse, EntityPlayer player) {
        int deniers = WorldUtilities.getItemsFromChest((IInventory)player.field_71071_by, MillItems.DENIER, 0, Integer.MAX_VALUE);
        int deniersargent = WorldUtilities.getItemsFromChest((IInventory)player.field_71071_by, MillItems.DENIER_ARGENT, 0, Integer.MAX_VALUE);
        int deniersor = WorldUtilities.getItemsFromChest((IInventory)player.field_71071_by, MillItems.DENIER_OR, 0, Integer.MAX_VALUE);
        int total = this.totalDeniers(purse) + deniers + deniersargent * 64 + deniersor * 64 * 64;
        int new_denier = total % 64;
        int new_deniers_argent = (total - new_denier) / 64 % 64;
        int new_deniers_or = (total - new_denier - new_deniers_argent * 64) / 4096;
        this.setDeniers(purse, player, new_denier, new_deniers_argent, new_deniers_or);
    }

    public int totalDeniers(ItemStack purse) {
        if (purse.func_77978_p() == null) {
            return 0;
        }
        int deniers = purse.func_77978_p().func_74762_e(ML_PURSE_DENIER);
        int denier_argent = purse.func_77978_p().func_74762_e(ML_PURSE_DENIERARGENT);
        int denier_or = purse.func_77978_p().func_74762_e(ML_PURSE_DENIEROR);
        return deniers + denier_argent * 64 + denier_or * 64 * 64;
    }
}

