/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityFurnace
 */
package org.millenaire.common.goal.generic;

import java.util.List;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import org.millenaire.common.annotedparameters.AnnotedParameter;
import org.millenaire.common.annotedparameters.ConfigAnnotations;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.entity.TileEntityFirePit;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.goal.generic.GoalGeneric;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;

public class GoalGenericCooking
extends GoalGeneric {
    public static final String GOAL_TYPE = "cooking";
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.INVITEM)
    @ConfigAnnotations.FieldDocumentation(explanation="The item to be cooked.")
    public InvItem itemToCook = null;
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.INTEGER, defaultValue="16")
    @ConfigAnnotations.FieldDocumentation(explanation="Minimum number of items that can be added to a cooking.")
    public int minimumToCook;

    @Override
    public void applyDefaultSettings() {
        this.lookAtGoal = true;
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
        List<Building> buildings = this.getBuildings(villager);
        for (Building dest : buildings) {
            if (!this.isDestPossible(villager, dest)) continue;
            int countGoods = dest.countGoods(this.itemToCook) + villager.countInv(this.itemToCook);
            for (Point p : dest.getResManager().furnaces) {
                TileEntityFurnace furnace = p.getFurnace(villager.field_70170_p);
                if (furnace == null) continue;
                if (countGoods >= this.minimumToCook && (furnace.func_70301_a(0) == ItemStack.field_190927_a || furnace.func_70301_a(0).func_77973_b() == Items.field_190931_a || furnace.func_70301_a(0).func_77973_b() == this.itemToCook.getItem() && furnace.func_70301_a(0).func_77952_i() == this.itemToCook.meta && furnace.func_70301_a(0).func_190916_E() < 32)) {
                    return this.packDest(p, dest);
                }
                if (furnace.func_70301_a(2) == null || furnace.func_70301_a(2).func_190916_E() < this.minimumToCook) continue;
                return this.packDest(p, dest);
            }
            boolean firepitBurnable = TileEntityFirePit.isFirePitBurnable(this.itemToCook.staticStack);
            if (!firepitBurnable) continue;
            for (Point p : dest.getResManager().firepits) {
                ItemStack stack;
                int slotNb;
                TileEntityFirePit firepit = p.getFirePit(villager.field_70170_p);
                if (firepit == null) continue;
                for (slotNb = 0; slotNb < 3; ++slotNb) {
                    stack = firepit.inputs.getStackInSlot(slotNb);
                    if (countGoods < this.minimumToCook || !stack.func_190926_b() && (stack.func_77973_b() != this.itemToCook.getItem() || stack.func_77952_i() != this.itemToCook.meta || stack.func_190916_E() >= 32)) continue;
                    return this.packDest(p, dest);
                }
                for (slotNb = 0; slotNb < 3; ++slotNb) {
                    stack = firepit.outputs.getStackInSlot(slotNb);
                    if (stack == null || stack.func_190916_E() < this.minimumToCook) continue;
                    return this.packDest(p, dest);
                }
            }
        }
        return null;
    }

    @Override
    public ItemStack getIcon() {
        if (this.icon != null) {
            return this.icon.getItemStack();
        }
        if (this.itemToCook != null) {
            return this.itemToCook.getItemStack();
        }
        return null;
    }

    @Override
    public String getTypeLabel() {
        return GOAL_TYPE;
    }

    @Override
    public boolean isDestPossibleSpecific(MillVillager villager, Building b) {
        return true;
    }

    @Override
    public boolean isPossibleGenericGoal(MillVillager villager) throws Exception {
        return this.getDestination(villager) != null;
    }

    @Override
    public boolean performAction(MillVillager villager) throws Exception {
        TileEntity tileEntity = villager.getGoalDestPoint().getTileEntity(villager.field_70170_p);
        Building dest = villager.getGoalBuildingDest();
        boolean firepitBurnable = TileEntityFirePit.isFirePitBurnable(this.itemToCook.staticStack);
        if (tileEntity != null && dest != null) {
            if (tileEntity instanceof TileEntityFurnace) {
                TileEntityFurnace furnace = (TileEntityFurnace)tileEntity;
                this.performAction_furnace(dest, furnace, villager);
            } else if (firepitBurnable && tileEntity instanceof TileEntityFirePit) {
                TileEntityFirePit firepit = (TileEntityFirePit)tileEntity;
                this.performAction_firepit(dest, firepit, villager);
            }
        }
        return true;
    }

    private void performAction_firepit(Building dest, TileEntityFirePit firepit, MillVillager villager) {
        ItemStack stack;
        int slotNb;
        for (slotNb = 0; slotNb < 3; ++slotNb) {
            int nb;
            stack = firepit.inputs.getStackInSlot(slotNb);
            int countGoods = dest.countGoods(this.itemToCook) + villager.countInv(this.itemToCook);
            if ((!stack.func_190926_b() || countGoods < this.minimumToCook) && (stack.func_190926_b() || stack.func_77973_b() != this.itemToCook.getItem() || stack.func_77952_i() != this.itemToCook.meta || stack.func_190916_E() >= 64 || countGoods <= 0)) continue;
            if (stack.func_190926_b()) {
                nb = Math.min(64, countGoods);
                firepit.inputs.setStackInSlot(slotNb, new ItemStack(this.itemToCook.getItem(), nb, this.itemToCook.meta));
                dest.takeGoods(this.itemToCook, nb);
                continue;
            }
            nb = Math.min(64 - stack.func_190916_E(), countGoods);
            ItemStack newStack = stack.func_77946_l();
            newStack.func_190920_e(stack.func_190916_E() + nb);
            firepit.inputs.setStackInSlot(slotNb, newStack);
            dest.takeGoods(this.itemToCook, nb);
        }
        for (slotNb = 0; slotNb < 3; ++slotNb) {
            stack = firepit.outputs.getStackInSlot(slotNb);
            if (stack.func_190926_b()) continue;
            Item item = stack.func_77973_b();
            int meta = stack.func_77952_i();
            dest.storeGoods(item, meta, stack.func_190916_E());
            firepit.outputs.setStackInSlot(slotNb, ItemStack.field_190927_a);
        }
    }

    private void performAction_furnace(Building dest, TileEntityFurnace furnace, MillVillager villager) {
        int countGoods = dest.countGoods(this.itemToCook) + villager.countInv(this.itemToCook);
        if (furnace.func_70301_a(0).func_190926_b() && countGoods >= this.minimumToCook || !furnace.func_70301_a(0).func_190926_b() && furnace.func_70301_a(0).func_77973_b() == this.itemToCook.getItem() && furnace.func_70301_a(0).func_77952_i() == this.itemToCook.meta && furnace.func_70301_a(0).func_190916_E() < 64 && countGoods > 0) {
            int nb;
            if (furnace.func_70301_a(0).func_190926_b()) {
                nb = Math.min(64, countGoods);
                furnace.func_70299_a(0, new ItemStack(this.itemToCook.getItem(), nb, this.itemToCook.meta));
                dest.takeGoods(this.itemToCook, nb);
            } else {
                nb = Math.min(64 - furnace.func_70301_a(0).func_190916_E(), countGoods);
                ItemStack stack = furnace.func_70301_a(0);
                stack.func_190920_e(furnace.func_70301_a(0).func_190916_E() + nb);
                furnace.func_70299_a(0, stack);
                dest.takeGoods(this.itemToCook, nb);
            }
        }
        if (!furnace.func_70301_a(2).func_190926_b()) {
            Item item = furnace.func_70301_a(2).func_77973_b();
            int meta = furnace.func_70301_a(2).func_77952_i();
            dest.storeGoods(item, meta, furnace.func_70301_a(2).func_190916_E());
            furnace.func_70299_a(2, ItemStack.field_190927_a);
        }
    }

    @Override
    public boolean validateGoal() {
        if (this.itemToCook == null) {
            MillLog.error(this, "The itemtocook id is mandatory in custom cooking goals.");
            return false;
        }
        return true;
    }
}

