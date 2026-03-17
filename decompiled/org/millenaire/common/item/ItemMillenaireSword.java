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
 *  net.minecraft.item.Item
 *  net.minecraft.item.Item$ToolMaterial
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemSword
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.advancements.MillAdvancements;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.item.InvItem;

public class ItemMillenaireSword
extends ItemSword
implements InvItem.IItemInitialEnchantmens {
    private final boolean knockback;
    private final int enchantability;

    public ItemMillenaireSword(String itemName, Item.ToolMaterial material, int enchantability, boolean knockback) {
        super(material);
        this.knockback = knockback;
        this.enchantability = enchantability;
        this.func_77655_b("millenaire." + itemName);
        this.setRegistryName(itemName);
        this.func_77637_a(MillBlocks.tabMillenaire);
    }

    @Override
    public void applyEnchantments(ItemStack stack) {
        if (this.knockback && EnchantmentHelper.func_77506_a((Enchantment)Enchantments.field_180313_o, (ItemStack)stack) == 0) {
            stack.func_77966_a(Enchantments.field_180313_o, 2);
        }
    }

    public int func_77619_b() {
        if (this.enchantability >= 0) {
            return this.enchantability;
        }
        return super.func_77619_b();
    }

    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation((Item)this, (int)0, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }

    public void func_77622_d(ItemStack stack, World par2World, EntityPlayer par3EntityPlayer) {
        this.applyEnchantments(stack);
    }

    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos bp, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        this.applyEnchantments(player.func_184586_b(hand));
        return super.onItemUseFirst(player, world, bp, side, hitX, hitY, hitZ, hand);
    }

    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (entity instanceof EntityPlayer) {
            MillAdvancements.MP_WEAPON.grant(player);
        }
        this.applyEnchantments(stack);
        return super.onLeftClickEntity(stack, player, entity);
    }
}

