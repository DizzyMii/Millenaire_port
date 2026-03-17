/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.NonNullList
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.ItemMill;

public class ItemBannerPattern
extends ItemMill {
    public ItemBannerPattern(String itemName) {
        super(itemName);
        this.func_77627_a(true);
        this.func_77656_e(0);
    }

    public void func_150895_a(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.func_194125_a(tab)) {
            for (int i = 0; i < Mill.BANNER_SHORTNAMES.length; ++i) {
                items.add((Object)new ItemStack((Item)this, 1, i));
            }
        }
    }

    public String func_77667_c(ItemStack stack) {
        int i = stack.func_77960_j();
        return super.func_77658_a() + "." + Mill.BANNER_SHORTNAMES[i];
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        for (int i = 0; i < Mill.BANNER_SHORTNAMES.length; ++i) {
            ModelLoader.setCustomModelResourceLocation((Item)this, (int)i, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "inventory"));
        }
    }
}

