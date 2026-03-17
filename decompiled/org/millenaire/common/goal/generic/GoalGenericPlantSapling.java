/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockPlanks$EnumType
 *  net.minecraft.block.BlockSapling
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumHand
 */
package org.millenaire.common.goal.generic;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.goal.generic.GoalGeneric;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;

public class GoalGenericPlantSapling
extends GoalGeneric {
    public static final String GOAL_TYPE = "plantsapling";

    @Override
    public void applyDefaultSettings() {
        this.duration = 2;
        this.lookAtGoal = true;
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) throws MillLog.MillenaireException {
        List<Building> buildings = this.getBuildings(villager);
        ArrayList<Point> vp = new ArrayList<Point>();
        ArrayList<Point> buildingp = new ArrayList<Point>();
        for (Building grove : buildings) {
            Point p = grove.getResManager().getPlantingLocation();
            if (p == null) continue;
            vp.add(p);
            buildingp.add(grove.getPos());
        }
        if (vp.isEmpty()) {
            return null;
        }
        Point p = (Point)vp.get(0);
        Point buildingP = (Point)buildingp.get(0);
        for (int i = 1; i < vp.size(); ++i) {
            if (!(((Point)vp.get(i)).horizontalDistanceToSquared((Entity)villager) < p.horizontalDistanceToSquared((Entity)villager))) continue;
            p = (Point)vp.get(i);
            buildingP = (Point)buildingp.get(i);
        }
        return this.packDest(p, buildingP);
    }

    @Override
    public ItemStack getIcon() {
        if (this.icon != null) {
            return this.icon.getItemStack();
        }
        return InvItem.createInvItem(Blocks.field_150345_g).getItemStack();
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
    public boolean performAction(MillVillager villager) {
        Block block = WorldUtilities.getBlock(villager.field_70170_p, villager.getGoalDestPoint());
        if (block == Blocks.field_150350_a || block == Blocks.field_150431_aC || BlockItemUtilities.isBlockDecorativePlant(block) && !(block instanceof BlockSapling)) {
            String saplingType = villager.getGoalBuildingDest().getResManager().getPlantingLocationType(villager.getGoalDestPoint());
            IBlockState saplingBS = Blocks.field_150345_g.func_176223_P();
            if ("pinespawn".equals(saplingType)) {
                saplingBS = Blocks.field_150345_g.func_176223_P().func_177226_a((IProperty)BlockSapling.field_176480_a, (Comparable)BlockPlanks.EnumType.SPRUCE);
            } else if ("birchspawn".equals(saplingType)) {
                saplingBS = Blocks.field_150345_g.func_176223_P().func_177226_a((IProperty)BlockSapling.field_176480_a, (Comparable)BlockPlanks.EnumType.BIRCH);
            } else if ("junglespawn".equals(saplingType)) {
                saplingBS = Blocks.field_150345_g.func_176223_P().func_177226_a((IProperty)BlockSapling.field_176480_a, (Comparable)BlockPlanks.EnumType.JUNGLE);
            } else if ("acaciaspawn".equals(saplingType)) {
                saplingBS = Blocks.field_150345_g.func_176223_P().func_177226_a((IProperty)BlockSapling.field_176480_a, (Comparable)BlockPlanks.EnumType.ACACIA);
            } else if ("darkoakspawn".equals(saplingType)) {
                saplingBS = Blocks.field_150345_g.func_176223_P().func_177226_a((IProperty)BlockSapling.field_176480_a, (Comparable)BlockPlanks.EnumType.DARK_OAK);
            } else if ("appletreespawn".equals(saplingType)) {
                saplingBS = MillBlocks.SAPLING_APPLETREE.func_176223_P();
            } else if ("olivetreespawn".equals(saplingType)) {
                saplingBS = MillBlocks.SAPLING_OLIVETREE.func_176223_P();
            } else if ("pistachiotreespawn".equals(saplingType)) {
                saplingBS = MillBlocks.SAPLING_PISTACHIO.func_176223_P();
            } else if ("cherrytreespawn".equals(saplingType)) {
                saplingBS = MillBlocks.SAPLING_CHERRY.func_176223_P();
            } else if ("sakuratreespawn".equals(saplingType)) {
                saplingBS = MillBlocks.SAPLING_SAKURA.func_176223_P();
            }
            villager.takeFromInv(saplingBS, 1);
            villager.setBlockstate(villager.getGoalDestPoint(), saplingBS);
            villager.func_184609_a(EnumHand.MAIN_HAND);
            if (MillConfigValues.LogLumberman >= 3 && villager.extraLog) {
                MillLog.debug(this, "Planted at: " + villager.getGoalDestPoint());
            }
        } else if (MillConfigValues.LogLumberman >= 3 && villager.extraLog) {
            MillLog.debug(this, "Failed to plant at: " + villager.getGoalDestPoint());
        }
        return true;
    }

    @Override
    public boolean validateGoal() {
        return true;
    }
}

