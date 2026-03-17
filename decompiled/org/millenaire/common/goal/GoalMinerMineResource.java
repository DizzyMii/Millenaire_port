/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockStone
 *  net.minecraft.block.BlockStone$EnumType
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package org.millenaire.common.goal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;

@DocumentedElement.Documentation(value="Go and mine rocks etc at the villager's house.")
public class GoalMinerMineResource
extends Goal {
    private static final ItemStack[] IS_ULU = new ItemStack[]{new ItemStack((Item)MillItems.ULU, 1)};
    public String buildingTag = null;

    public GoalMinerMineResource() {
        this.icon = InvItem.createInvItem(Items.field_151035_b);
    }

    @Override
    public int actionDuration(MillVillager villager) {
        Block block = villager.getBlock(villager.getGoalDestPoint());
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

    public List<Building> getBuildings(MillVillager villager) {
        ArrayList<Building> buildings = new ArrayList<Building>();
        if (this.buildingTag == null) {
            buildings.add(villager.getHouse());
        } else {
            for (Building b : villager.getTownHall().getBuildingsWithTag(this.buildingTag)) {
                buildings.add(b);
            }
        }
        return buildings;
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
        List<Building> buildings = this.getBuildings(villager);
        ArrayList validSources = new ArrayList();
        ArrayList<Building> validDests = new ArrayList<Building>();
        for (Building possibleDest : buildings) {
            CopyOnWriteArrayList<CopyOnWriteArrayList<Point>> sources = possibleDest.getResManager().sources;
            for (int i = 0; i < sources.size(); ++i) {
                IBlockState sourceBlockState = possibleDest.getResManager().sourceTypes.get(i);
                for (int j = 0; j < ((CopyOnWriteArrayList)sources.get(i)).size(); ++j) {
                    IBlockState actualBlockState = WorldUtilities.getBlockState(villager.field_70170_p, (Point)((CopyOnWriteArrayList)sources.get(i)).get(j));
                    if (actualBlockState != sourceBlockState) continue;
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
        Block targetBlock = villager.getBlock(villager.getGoalDestPoint());
        if (targetBlock == Blocks.field_150354_m || targetBlock == Blocks.field_150435_aG || targetBlock == Blocks.field_150351_n) {
            return villager.getBestShovelStack();
        }
        if (targetBlock == Blocks.field_150431_aC || targetBlock == Blocks.field_150432_aD) {
            return IS_ULU;
        }
        return villager.getBestPickaxeStack();
    }

    @Override
    public AStarConfig getPathingConfig(MillVillager villager) {
        if (!villager.canVillagerClearLeaves()) {
            return JPS_CONFIG_WIDE_NO_LEAVES;
        }
        return JPS_CONFIG_WIDE;
    }

    @Override
    public boolean isPossibleSpecific(MillVillager villager) throws Exception {
        return this.getDestination(villager) != null;
    }

    @Override
    public boolean lookAtGoal() {
        return true;
    }

    @Override
    public boolean performAction(MillVillager villager) throws Exception {
        IBlockState blockState = WorldUtilities.getBlockState(villager.field_70170_p, villager.getGoalDestPoint());
        Block block = blockState.func_177230_c();
        if (block == Blocks.field_150354_m) {
            villager.addToInv((Block)Blocks.field_150354_m, 1);
            WorldUtilities.playSoundBlockBreaking(villager.field_70170_p, villager.getGoalDestPoint(), (Block)Blocks.field_150354_m, 1.0f);
            if (MillConfigValues.LogMiner >= 3 && villager.extraLog) {
                MillLog.debug(this, "Gathered sand at: " + villager.getGoalDestPoint());
            }
        } else if (block == Blocks.field_150348_b) {
            if (blockState.func_177229_b((IProperty)BlockStone.field_176247_a) == BlockStone.EnumType.STONE) {
                villager.addToInv(Blocks.field_150347_e, 1);
            } else {
                villager.addToInv(blockState, 1);
            }
            WorldUtilities.playSoundBlockBreaking(villager.field_70170_p, villager.getGoalDestPoint(), Blocks.field_150348_b, 1.0f);
            if (MillConfigValues.LogMiner >= 3 && villager.extraLog) {
                MillLog.debug(this, "Gather cobblestone at: " + villager.getGoalDestPoint());
            }
        } else if (block == Blocks.field_150322_A) {
            villager.addToInv(Blocks.field_150322_A, 1);
            WorldUtilities.playSoundBlockBreaking(villager.field_70170_p, villager.getGoalDestPoint(), Blocks.field_150322_A, 1.0f);
            if (MillConfigValues.LogMiner >= 3 && villager.extraLog) {
                MillLog.debug(this, "Gather sand stone at: " + villager.getGoalDestPoint());
            }
        } else if (block == Blocks.field_150435_aG) {
            villager.addToInv(Items.field_151119_aD, 1);
            WorldUtilities.playSoundBlockBreaking(villager.field_70170_p, villager.getGoalDestPoint(), Blocks.field_150435_aG, 1.0f);
            if (MillConfigValues.LogMiner >= 3 && villager.extraLog) {
                MillLog.debug(this, "Gather clay at: " + villager.getGoalDestPoint());
            }
        } else if (block == Blocks.field_150351_n) {
            villager.addToInv(Blocks.field_150351_n, 1);
            WorldUtilities.playSoundBlockBreaking(villager.field_70170_p, villager.getGoalDestPoint(), Blocks.field_150351_n, 1.0f);
            if (MillConfigValues.LogMiner >= 3 && villager.extraLog) {
                MillLog.debug(this, "Gather gravel at: " + villager.getGoalDestPoint());
            }
        } else if (block == Blocks.field_150431_aC) {
            villager.addToInv((Block)MillBlocks.SNOW_BRICK, 1);
            WorldUtilities.playSoundBlockBreaking(villager.field_70170_p, villager.getGoalDestPoint(), Blocks.field_150433_aE, 1.0f);
            if (MillConfigValues.LogMiner >= 3) {
                MillLog.debug(this, "Gather snow at: " + villager.getGoalDestPoint());
            }
        } else if (block == Blocks.field_150432_aD) {
            villager.addToInv(MillBlocks.ICE_BRICK, 1);
            WorldUtilities.playSoundBlockBreaking(villager.field_70170_p, villager.getGoalDestPoint(), Blocks.field_150432_aD, 1.0f);
            if (MillConfigValues.LogMiner >= 3) {
                MillLog.debug(this, "Gather ice at: " + villager.getGoalDestPoint());
            }
        }
        return true;
    }

    @Override
    public int priority(MillVillager villager) throws Exception {
        return 30;
    }

    @Override
    public int range(MillVillager villager) {
        return 5;
    }

    @Override
    public boolean stuckAction(MillVillager villager) throws Exception {
        return this.performAction(villager);
    }

    @Override
    public boolean swingArms() {
        return true;
    }
}

