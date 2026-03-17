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
package org.millenaire.common.goal.generic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.millenaire.common.annotedparameters.AnnotedParameter;
import org.millenaire.common.annotedparameters.ConfigAnnotations;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.goal.generic.GoalGeneric;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;

public class GoalGenericMining
extends GoalGeneric {
    private static final ItemStack[] IS_ULU = new ItemStack[]{new ItemStack((Item)MillItems.ULU, 1)};
    public static final String GOAL_TYPE = "mining";
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.BLOCKSTATE)
    @ConfigAnnotations.FieldDocumentation(explanation="Blockstate of the source, like stone (not necessarily the block being harvest - stone gives cobblestone for example).")
    public IBlockState sourceBlockState = null;
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName="loot")
    @ConfigAnnotations.FieldDocumentation(explanation="Blocks or items  gained when mining.")
    public Map<InvItem, Integer> loots = new HashMap<InvItem, Integer>();

    @Override
    public int actionDuration(MillVillager villager) {
        Block block = this.sourceBlockState.func_177230_c();
        if (block == Blocks.field_150348_b || block == Blocks.field_150322_A) {
            int toolEfficiency = (int)villager.getBestPickaxe().func_150893_a(new ItemStack((Item)villager.getBestPickaxe(), 1), Blocks.field_150322_A.func_176223_P());
            return 140 - 4 * toolEfficiency;
        }
        if (block == Blocks.field_150354_m || block == Blocks.field_150435_aG || block == Blocks.field_150351_n) {
            int toolEfficiency = (int)villager.getBestShovel().func_150893_a(new ItemStack((Item)villager.getBestShovel(), 1), Blocks.field_150354_m.func_176223_P());
            return 140 - 4 * toolEfficiency;
        }
        return 70;
    }

    @Override
    public void applyDefaultSettings() {
        this.lookAtGoal = true;
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
        List<Building> buildings = this.getBuildings(villager);
        ArrayList validSources = new ArrayList();
        ArrayList<Building> validDests = new ArrayList<Building>();
        for (Building possibleDest : buildings) {
            CopyOnWriteArrayList<CopyOnWriteArrayList<Point>> sources = possibleDest.getResManager().sources;
            for (int i = 0; i < sources.size(); ++i) {
                if (this.sourceBlockState != possibleDest.getResManager().sourceTypes.get(i)) continue;
                for (int j = 0; j < ((CopyOnWriteArrayList)sources.get(i)).size(); ++j) {
                    IBlockState actualBlockState = WorldUtilities.getBlockState(villager.field_70170_p, (Point)((CopyOnWriteArrayList)sources.get(i)).get(j));
                    if (actualBlockState != this.sourceBlockState) continue;
                    validSources.add(((CopyOnWriteArrayList)sources.get(i)).get(j));
                    validDests.add(possibleDest);
                }
            }
        }
        if (validSources.isEmpty()) {
            return null;
        }
        int randomTarget = MillCommonUtilities.randomInt(validSources.size());
        return this.packDest((Point)validSources.get(randomTarget), (Building)validDests.get(randomTarget));
    }

    @Override
    public ItemStack[] getHeldItemsTravelling(MillVillager villager) throws Exception {
        Block targetBlock = this.sourceBlockState.func_177230_c();
        if (targetBlock == Blocks.field_150354_m || targetBlock == Blocks.field_150435_aG || targetBlock == Blocks.field_150351_n) {
            return villager.getBestShovelStack();
        }
        if (targetBlock == Blocks.field_150431_aC || targetBlock == Blocks.field_150432_aD) {
            return IS_ULU;
        }
        return villager.getBestPickaxeStack();
    }

    @Override
    public ItemStack getIcon() {
        if (this.icon != null) {
            return this.icon.getItemStack();
        }
        if (this.sourceBlockState != null) {
            return new ItemStack(this.sourceBlockState.func_177230_c(), 1, this.sourceBlockState.func_177230_c().func_176201_c(this.sourceBlockState));
        }
        return null;
    }

    @Override
    public AStarConfig getPathingConfig(MillVillager villager) {
        if (!villager.canVillagerClearLeaves()) {
            return JPS_CONFIG_WIDE_NO_LEAVES;
        }
        return JPS_CONFIG_WIDE;
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
        return true;
    }

    @Override
    public boolean performAction(MillVillager villager) throws Exception {
        for (InvItem key : this.loots.keySet()) {
            villager.addToInv(key, (int)this.loots.get(key));
            if (MillConfigValues.LogMiner < 3 || !villager.extraLog) continue;
            MillLog.debug(this, "Gathered " + key + " at: " + villager.getGoalDestPoint());
        }
        WorldUtilities.playSoundBlockBreaking(villager.field_70170_p, villager.getGoalDestPoint(), this.sourceBlockState.func_177230_c(), 1.0f);
        return true;
    }

    @Override
    public boolean stuckAction(MillVillager villager) throws Exception {
        return this.performAction(villager);
    }

    @Override
    public boolean swingArms() {
        return true;
    }

    @Override
    public boolean validateGoal() {
        return true;
    }
}

