/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagString
 *  net.minecraft.tileentity.TileEntityBrewingStand
 */
package org.millenaire.common.goal;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntityBrewingStand;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.Point;

@DocumentedElement.Documentation(value="Brew alchemical potions from nether warts. Currently broken.")
public class GoalBrewPotions
extends Goal {
    public GoalBrewPotions() {
        this.icon = InvItem.createInvItem((Item)Items.field_151068_bn);
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
        int nbWarts = villager.getHouse().countGoods(Items.field_151075_bm);
        int nbBottles = villager.getHouse().countGoods(Items.field_151069_bo);
        int nbPotions = villager.getHouse().countGoods((Item)Items.field_151068_bn, -1);
        for (Point p : villager.getHouse().getResManager().brewingStands) {
            TileEntityBrewingStand brewingStand = p.getBrewingStand(villager.field_70170_p);
            if (brewingStand == null || brewingStand.func_174887_a_(0) != 0) continue;
            if (brewingStand.func_70301_a(3) == ItemStack.field_190927_a && nbWarts > 0 && nbPotions < 64) {
                return this.packDest(p, villager.getHouse());
            }
            if (nbBottles > 2 && (brewingStand.func_70301_a(0) == ItemStack.field_190927_a || brewingStand.func_70301_a(1) == ItemStack.field_190927_a || brewingStand.func_70301_a(2) == ItemStack.field_190927_a) && nbPotions < 64) {
                return this.packDest(p, villager.getHouse());
            }
            for (int i = 0; i < 3; ++i) {
                if (brewingStand.func_70301_a(i) == null || brewingStand.func_70301_a(i).func_77973_b() != Items.field_151068_bn || brewingStand.func_70301_a(i).func_77952_i() != 16) continue;
                return this.packDest(p, villager.getHouse());
            }
        }
        return null;
    }

    @Override
    public boolean isPossibleSpecific(MillVillager villager) throws Exception {
        return this.getDestination(villager) != null;
    }

    @Override
    public boolean performAction(MillVillager villager) throws Exception {
        int nbWarts = villager.getHouse().countGoods(Items.field_151075_bm);
        int nbBottles = villager.getHouse().countGoods(Items.field_151069_bo);
        int nbPotions = villager.getHouse().countGoods((Item)Items.field_151068_bn);
        TileEntityBrewingStand brewingStand = villager.getGoalDestPoint().getBrewingStand(villager.field_70170_p);
        if (brewingStand == null) {
            return true;
        }
        if (brewingStand.func_174887_a_(0) == 0) {
            int i;
            if (brewingStand.func_70301_a(3) == ItemStack.field_190927_a && nbWarts > 0 && nbPotions < 64) {
                brewingStand.func_70299_a(3, new ItemStack(Items.field_151075_bm, 1));
                villager.getHouse().takeGoods(Items.field_151075_bm, 1);
            }
            if (nbBottles > 2 && nbPotions < 64) {
                for (i = 0; i < 3; ++i) {
                    if (brewingStand.func_70301_a(i) != ItemStack.field_190927_a) continue;
                    ItemStack waterPotion = new ItemStack((Item)Items.field_151068_bn, 1, 0);
                    waterPotion.func_77983_a("Potion", (NBTBase)new NBTTagString("minecraft:water"));
                    brewingStand.func_70299_a(i, waterPotion);
                    villager.getHouse().takeGoods(Items.field_151069_bo, 1);
                }
            }
            for (i = 0; i < 3; ++i) {
                if (brewingStand.func_70301_a(i) == ItemStack.field_190927_a || brewingStand.func_70301_a(i).func_77973_b() != Items.field_151068_bn || brewingStand.func_70301_a(i).func_77952_i() != 16) continue;
                brewingStand.func_70299_a(i, ItemStack.field_190927_a);
                villager.getHouse().storeGoods((Item)Items.field_151068_bn, 16, 1);
            }
        }
        return true;
    }

    @Override
    public int priority(MillVillager villager) throws Exception {
        return 100;
    }

    @Override
    public boolean swingArms() {
        return true;
    }
}

