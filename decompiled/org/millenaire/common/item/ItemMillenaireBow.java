/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBow
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.MillBlocks;

public class ItemMillenaireBow
extends ItemBow {
    public float speedFactor = 1.0f;
    public float damageBonus = 0.0f;
    private final int enchantability;

    public ItemMillenaireBow(String itemName, float speedFactor, float damageBonus, int enchantability) {
        this.speedFactor = speedFactor;
        this.damageBonus = damageBonus;
        this.enchantability = enchantability;
        this.func_77655_b("millenaire." + itemName);
        this.setRegistryName(itemName);
        this.func_77637_a(MillBlocks.tabMillenaire);
    }

    public int func_77619_b() {
        return this.enchantability;
    }

    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation((Item)this, (int)0, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }
}

