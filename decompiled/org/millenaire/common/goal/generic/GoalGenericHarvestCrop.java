/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockDoublePlant
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 */
package org.millenaire.common.goal.generic;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.millenaire.common.annotedparameters.AnnotedParameter;
import org.millenaire.common.annotedparameters.ConfigAnnotations;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.goal.generic.GoalGeneric;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;

public class GoalGenericHarvestCrop
extends GoalGeneric {
    public static final String GOAL_TYPE = "harvesting";
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.BLOCK_ID)
    @ConfigAnnotations.FieldDocumentation(explanation="Type of plant to harvest.")
    public ResourceLocation cropType = null;
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.BONUS_ITEM_ADD)
    @ConfigAnnotations.FieldDocumentation(explanation="Item to be harvested, with chance.")
    public List<AnnotedParameter.BonusItem> harvestItem = new ArrayList<AnnotedParameter.BonusItem>();
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.INVITEM)
    @ConfigAnnotations.FieldDocumentation(explanation="Boons for irrigated villages.")
    public InvItem irrigationBonusCrop = null;
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.BLOCKSTATE)
    @ConfigAnnotations.FieldDocumentation(explanation="Blockstate the crop must have to be harvested. If not set, must have a meta of 7.")
    public IBlockState harvestBlockState = null;

    public static int getCropBlockRipeMeta(ResourceLocation cropType) {
        return 7;
    }

    @Override
    public void applyDefaultSettings() {
        this.duration = 2;
        this.lookAtGoal = true;
        this.tags.add("tag_agriculture");
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) throws MillLog.MillenaireException {
        Point dest = null;
        Building destBuilding = null;
        List<Building> buildings = this.getBuildings(villager);
        for (Building buildingDest : buildings) {
            List<Point> soils;
            if (!this.isDestPossible(villager, buildingDest) || (soils = buildingDest.getResManager().getSoilPoints(this.cropType)) == null) continue;
            for (Point p : soils) {
                if (!this.isValidHarvestSoil(villager.field_70170_p, p) || dest != null && !(p.distanceTo((Entity)villager) < dest.distanceTo((Entity)villager))) continue;
                dest = p.getAbove();
                destBuilding = buildingDest;
            }
        }
        if (dest == null) {
            return null;
        }
        return this.packDest(dest, destBuilding);
    }

    @Override
    public ItemStack[] getHeldItemsTravelling(MillVillager villager) throws Exception {
        return villager.getBestHoeStack();
    }

    @Override
    public ItemStack getIcon() {
        if (this.icon != null) {
            return this.icon.getItemStack();
        }
        if (!this.harvestItem.isEmpty()) {
            return this.harvestItem.get((int)0).item.getItemStack();
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

    private boolean isValidHarvestSoil(World world, Point p) {
        if (this.harvestBlockState != null) {
            return p.getAbove().getBlockActualState(world) == this.harvestBlockState;
        }
        return p.getAbove().getBlock(world) == Block.field_149771_c.func_82594_a((Object)this.cropType) && p.getAbove().getMeta(world) == GoalGenericHarvestCrop.getCropBlockRipeMeta(this.cropType);
    }

    @Override
    public boolean performAction(MillVillager villager) {
        if (this.isValidHarvestSoil(villager.field_70170_p, villager.getGoalDestPoint().getBelow())) {
            if (this.irrigationBonusCrop != null) {
                float irrigation = villager.getTownHall().getVillageIrrigation();
                double rand = Math.random();
                if (rand < (double)(irrigation / 100.0f)) {
                    villager.addToInv(this.irrigationBonusCrop, 1);
                }
            }
            Building dest = villager.getGoalBuildingDest();
            for (AnnotedParameter.BonusItem bonusItem : this.harvestItem) {
                if (bonusItem.tag != null && (dest == null || !dest.containsTags(bonusItem.tag)) || MillCommonUtilities.randomInt(100) > bonusItem.chance) continue;
                villager.addToInv(bonusItem.item, 1);
            }
            villager.setBlockAndMetadata(villager.getGoalDestPoint(), Blocks.field_150350_a, 0);
            if (villager.getBlock(villager.getGoalDestPoint().getAbove()) instanceof BlockDoublePlant) {
                villager.setBlockAndMetadata(villager.getGoalDestPoint().getAbove(), Blocks.field_150350_a, 0);
            }
            villager.func_184609_a(EnumHand.MAIN_HAND);
        }
        if (this.isDestPossibleSpecific(villager, villager.getGoalBuildingDest())) {
            try {
                villager.setGoalInformation(this.getDestination(villager));
            }
            catch (MillLog.MillenaireException e) {
                MillLog.printException(e);
            }
            return false;
        }
        return true;
    }

    @Override
    public int priority(MillVillager villager) throws MillLog.MillenaireException {
        Goal.GoalInformation info = this.getDestination(villager);
        if (info == null || info.getDest() == null) {
            return -1;
        }
        return (int)(1000.0 - villager.getPos().distanceTo(info.getDest()));
    }

    @Override
    public boolean validateGoal() {
        if (this.cropType == null) {
            MillLog.error(this, "The croptype is mandatory in custom harvest goals.");
            return false;
        }
        return true;
    }
}

