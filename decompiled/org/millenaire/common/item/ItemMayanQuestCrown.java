/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Enchantments
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.MillItems;

public class ItemMayanQuestCrown
extends ItemArmor
implements InvItem.IItemInitialEnchantmens {
    public ItemMayanQuestCrown(String itemName, EntityEquipmentSlot type) {
        super(MillItems.ARMOUR_mayan_quest_crown, -1, type);
        this.func_77656_e(0);
        this.func_77637_a(MillBlocks.tabMillenaire);
        this.func_77655_b("millenaire." + itemName);
        this.setRegistryName(itemName);
    }

    @Override
    public void applyEnchantments(ItemStack stack) {
        if (EnchantmentHelper.func_77506_a((Enchantment)Enchantments.field_185298_f, (ItemStack)stack) == 0) {
            stack.func_77966_a(Enchantments.field_185298_f, 3);
        }
        if (EnchantmentHelper.func_77506_a((Enchantment)Enchantments.field_185299_g, (ItemStack)stack) == 0) {
            stack.func_77966_a(Enchantments.field_185299_g, 1);
        }
        if (EnchantmentHelper.func_77506_a((Enchantment)Enchantments.field_180310_c, (ItemStack)stack) == 0) {
            stack.func_77966_a(Enchantments.field_180310_c, 4);
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation((Item)this, (int)0, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }

    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos bp, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        this.applyEnchantments(player.func_184586_b(hand));
        return super.onItemUseFirst(player, world, bp, side, hitX, hitY, hitZ, hand);
    }

    public void func_77663_a(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5) {
        this.applyEnchantments(par1ItemStack);
        super.func_77663_a(par1ItemStack, par2World, par3Entity, par4, par5);
    }
}

