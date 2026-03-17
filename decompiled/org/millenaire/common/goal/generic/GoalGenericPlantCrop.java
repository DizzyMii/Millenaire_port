/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockDoublePlant
 *  net.minecraft.block.BlockDoublePlant$EnumBlockHalf
 *  net.minecraft.block.properties.IProperty
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
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.millenaire.common.annotedparameters.AnnotedParameter;
import org.millenaire.common.annotedparameters.ConfigAnnotations;
import org.millenaire.common.block.BlockGrapeVine;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.goal.generic.GoalGeneric;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;

public class GoalGenericPlantCrop
extends GoalGeneric {
    public static final String GOAL_TYPE = "planting";
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.BLOCK_ID)
    @ConfigAnnotations.FieldDocumentation(explanation="Type of plant to plant.")
    public ResourceLocation cropType = null;
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.BLOCKSTATE_ADD)
    @ConfigAnnotations.FieldDocumentation(explanation="Blockstate to plant. If not set, defaults to cropType. If more than one set, picks one at random.")
    public List<IBlockState> plantBlockState = new ArrayList<IBlockState>();
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.INVITEM)
    @ConfigAnnotations.FieldDocumentation(explanation="Seed item that gets consumed when planting.")
    public InvItem seed = null;
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.BLOCK_ID, defaultValue="minecraft:farmland")
    @ConfigAnnotations.FieldDocumentation(explanation="Block to set below the crop.")
    public ResourceLocation soilType = null;

    public static int getCropBlockMeta(ResourceLocation cropType2) {
        return 0;
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
                if (!this.isValidPlantingLocation(villager.field_70170_p, p) || dest != null && !(p.distanceTo((Entity)villager) < dest.distanceTo((Entity)villager))) continue;
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
    public ItemStack getIcon() {
        if (this.icon != null) {
            return this.icon.getItemStack();
        }
        if (this.seed != null) {
            return this.seed.getItemStack();
        }
        if (this.heldItems != null && this.heldItems.length > 0) {
            return this.heldItems[0];
        }
        return null;
    }

    @Override
    public String getTypeLabel() {
        return GOAL_TYPE;
    }

    @Override
    public boolean isDestPossibleSpecific(MillVillager villager, Building b) {
        return this.seed == null || b.countGoods(this.seed) + villager.countInv(this.seed) != 0;
    }

    @Override
    public boolean isPossibleGenericGoal(MillVillager villager) throws Exception {
        return this.getDestination(villager) != null;
    }

    private boolean isValidPlantingLocation(World world, Point p) {
        Block blockTwoAbove = p.getAbove().getAbove().getBlock(world);
        Block blockAbove = p.getAbove().getBlock(world);
        Block farmBlock = p.getBlock(world);
        if (!(blockAbove != Blocks.field_150350_a && blockAbove != Blocks.field_150433_aE && blockAbove != Blocks.field_150362_t || blockTwoAbove != Blocks.field_150350_a && blockTwoAbove != Blocks.field_150433_aE && blockTwoAbove != Blocks.field_150362_t || farmBlock != Blocks.field_150349_c && farmBlock != Blocks.field_150346_d && farmBlock != Blocks.field_150458_ak)) {
            return true;
        }
        if (BlockItemUtilities.isBlockDecorativePlant(blockAbove)) {
            if (!this.cropType.equals((Object)Mill.CROP_FLOWER)) {
                return true;
            }
            if (blockAbove != Blocks.field_150328_O && blockAbove != Blocks.field_150327_N && blockAbove != Blocks.field_150398_cm) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean performAction(MillVillager villager) {
        int taken;
        Building dest = villager.getGoalBuildingDest();
        if (dest == null) {
            return true;
        }
        if (!this.isValidPlantingLocation(villager.field_70170_p, villager.getGoalDestPoint().getBelow())) {
            return true;
        }
        if (this.seed != null && (taken = villager.takeFromInv(this.seed, 1)) == 0) {
            dest.takeGoods(this.seed, 1);
        }
        Block soil = (Block)Block.field_149771_c.func_82594_a((Object)this.soilType);
        if (villager.getGoalDestPoint().getBelow().getBlock(villager.field_70170_p) != soil) {
            villager.setBlockAndMetadata(villager.getGoalDestPoint().getBelow(), soil, 0);
        }
        if (!this.plantBlockState.isEmpty()) {
            IBlockState cropState = this.plantBlockState.get(MillCommonUtilities.randomInt(this.plantBlockState.size()));
            villager.setBlockstate(villager.getGoalDestPoint(), cropState);
            if (cropState.func_177230_c() instanceof BlockDoublePlant) {
                villager.setBlockstate(villager.getGoalDestPoint().getAbove(), cropState.func_177226_a((IProperty)BlockDoublePlant.field_176492_b, (Comparable)BlockDoublePlant.EnumBlockHalf.UPPER));
            }
        } else {
            Block cropBlock = (Block)Block.field_149771_c.func_82594_a((Object)this.cropType);
            int cropMeta = GoalGenericPlantCrop.getCropBlockMeta(this.cropType);
            villager.setBlockAndMetadata(villager.getGoalDestPoint(), cropBlock, cropMeta);
            if (cropBlock instanceof BlockDoublePlant || cropBlock instanceof BlockGrapeVine) {
                villager.setBlockAndMetadata(villager.getGoalDestPoint().getAbove(), cropBlock, cropMeta | 8);
            }
        }
        villager.func_184609_a(EnumHand.MAIN_HAND);
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
        return (int)(100.0 - villager.getPos().distanceTo(info.getDest()));
    }

    @Override
    public boolean validateGoal() {
        if (this.cropType == null) {
            MillLog.error(this, "The croptype is mandatory in custom planting goals.");
            return false;
        }
        return true;
    }
}

