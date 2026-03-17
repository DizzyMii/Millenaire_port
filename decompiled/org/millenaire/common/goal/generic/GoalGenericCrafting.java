/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package org.millenaire.common.goal.generic;

import java.util.HashMap;
import java.util.List;
import net.minecraft.item.ItemStack;
import org.millenaire.common.annotedparameters.AnnotedParameter;
import org.millenaire.common.annotedparameters.ConfigAnnotations;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.goal.generic.GoalGeneric;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;

public class GoalGenericCrafting
extends GoalGeneric {
    public static final String GOAL_TYPE = "crafting";
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD)
    @ConfigAnnotations.FieldDocumentation(explanation="Each action will require and use all the inputs.")
    public HashMap<InvItem, Integer> input = new HashMap();
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD)
    @ConfigAnnotations.FieldDocumentation(explanation="Each action will produce all the outputs.")
    public HashMap<InvItem, Integer> output = new HashMap();

    @Override
    public void applyDefaultSettings() {
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
        List<Building> buildings = this.getBuildings(villager);
        for (Building dest : buildings) {
            if (!this.isDestPossible(villager, dest)) continue;
            return this.packDest(dest.getResManager().getCraftingPos(), dest);
        }
        return null;
    }

    @Override
    public ItemStack getIcon() {
        if (this.icon != null) {
            return this.icon.getItemStack();
        }
        if (!this.output.isEmpty()) {
            return this.output.keySet().iterator().next().getItemStack();
        }
        return null;
    }

    @Override
    public String getTypeLabel() {
        return GOAL_TYPE;
    }

    @Override
    public boolean isDestPossibleSpecific(MillVillager villager, Building b) {
        for (InvItem item : this.input.keySet()) {
            if (villager.countInv(item) + b.countGoods(item) >= this.input.get(item)) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean isPossibleGenericGoal(MillVillager villager) throws Exception {
        return true;
    }

    @Override
    public boolean performAction(MillVillager villager) throws Exception {
        Building dest = villager.getGoalBuildingDest();
        if (dest == null) {
            return true;
        }
        for (InvItem item : this.input.keySet()) {
            if (villager.countInv(item) + dest.countGoods(item) >= this.input.get(item)) continue;
            return true;
        }
        for (InvItem item : this.input.keySet()) {
            int nbTaken = villager.takeFromInv(item, (int)this.input.get(item));
            if (nbTaken >= this.input.get(item)) continue;
            dest.takeGoods(item, this.input.get(item) - nbTaken);
        }
        for (InvItem item : this.output.keySet()) {
            dest.storeGoods(item, (int)this.output.get(item));
        }
        if (this.sound != null) {
            WorldUtilities.playSoundByMillName(villager.field_70170_p, villager.getPos(), this.sound, 1.0f);
        }
        return true;
    }

    @Override
    public boolean swingArms() {
        return true;
    }

    @Override
    public boolean validateGoal() {
        if (this.output.isEmpty()) {
            MillLog.error(this, "Generic crafting goals require at least one output.");
            return false;
        }
        return true;
    }
}

