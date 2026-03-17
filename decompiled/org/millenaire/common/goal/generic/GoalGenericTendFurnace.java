/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityFurnace
 */
package org.millenaire.common.goal.generic;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
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
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;

public class GoalGenericTendFurnace
extends GoalGeneric {
    public static final String GOAL_TYPE = "tendfurnace";
    private static ItemStack[][] PLANKS = new ItemStack[][]{{new ItemStack(Blocks.field_150344_f, 1, 0)}, {new ItemStack(Blocks.field_150344_f, 1, 1)}, {new ItemStack(Blocks.field_150344_f, 1, 2)}, {new ItemStack(Blocks.field_150344_f, 1, 3)}, {new ItemStack(Blocks.field_150344_f, 1, 4)}, {new ItemStack(Blocks.field_150344_f, 1, 5)}};
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.INTEGER, defaultValue="4")
    @ConfigAnnotations.FieldDocumentation(explanation="Minimum number of wood to put back in one go.")
    public int minimumFuel;

    @Override
    public void applyDefaultSettings() {
        this.lookAtGoal = true;
        this.icon = InvItem.createInvItem(Blocks.field_150460_al);
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
        List<Building> buildings = this.getBuildings(villager);
        for (Building dest : buildings) {
            if (!this.isDestPossible(villager, dest)) continue;
            int anyWoodAvailable = dest.countGoods(Blocks.field_150364_r, -1) + villager.countInv(Blocks.field_150364_r, -1) + villager.getHouse().countGoods(Blocks.field_150364_r, -1);
            for (Point p : dest.getResManager().furnaces) {
                int woodMeta;
                int woodAvailable;
                TileEntityFurnace furnace = p.getFurnace(villager.field_70170_p);
                if (furnace == null) continue;
                if (furnace.func_70301_a(1) == ItemStack.field_190927_a && anyWoodAvailable > 4) {
                    return this.packDest(p, dest);
                }
                if (furnace.func_70301_a(1).func_190916_E() >= 32 || furnace.func_70301_a(1).func_77973_b() != Item.func_150898_a((Block)Blocks.field_150344_f) || (woodAvailable = this.getWoodCountByMeta(villager, dest, woodMeta = furnace.func_70301_a(1).func_77960_j())) < this.minimumFuel) continue;
                return this.packDest(p, dest);
            }
            for (Point p : dest.getResManager().firepits) {
                int woodMeta;
                int woodAvailable;
                TileEntityFirePit firepit = p.getFirePit(villager.field_70170_p);
                if (firepit == null) continue;
                ItemStack stack = firepit.fuel.getStackInSlot(0);
                if (stack.func_190926_b() && anyWoodAvailable > 4) {
                    return this.packDest(p, dest);
                }
                if (stack.func_190916_E() >= 32 || stack.func_77973_b() != Item.func_150898_a((Block)Blocks.field_150344_f) || (woodAvailable = this.getWoodCountByMeta(villager, dest, woodMeta = stack.func_77960_j())) <= 4) continue;
                return this.packDest(p, dest);
            }
        }
        return null;
    }

    @Override
    public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
        Building dest = villager.getGoalBuildingDest();
        TileEntity tileEntity = villager.getGoalDestPoint().getTileEntity(villager.field_70170_p);
        if (dest != null && tileEntity != null) {
            if (tileEntity instanceof TileEntityFurnace) {
                TileEntityFurnace furnace = (TileEntityFurnace)tileEntity;
                if (furnace.func_70301_a(1) == ItemStack.field_190927_a) {
                    int mostWoodAvailable = 0;
                    int mostWoodAvailableMeta = -1;
                    for (int woodMeta = 0; woodMeta < 6; ++woodMeta) {
                        int woodAvailable = this.getWoodCountByMeta(villager, dest, woodMeta);
                        if (woodAvailable <= mostWoodAvailable) continue;
                        mostWoodAvailable = woodAvailable;
                        mostWoodAvailableMeta = woodMeta;
                    }
                    if (mostWoodAvailableMeta > -1) {
                        return PLANKS[mostWoodAvailableMeta];
                    }
                } else if (furnace.func_70301_a(1).func_190916_E() < 64 && furnace.func_70301_a(1).func_77973_b() == Item.func_150898_a((Block)Blocks.field_150344_f)) {
                    int woodMeta = furnace.func_70301_a(1).func_77960_j();
                    return PLANKS[woodMeta];
                }
            } else if (tileEntity instanceof TileEntityFirePit) {
                TileEntityFirePit firepit = (TileEntityFirePit)tileEntity;
                if (firepit.fuel.getStackInSlot(0) == ItemStack.field_190927_a) {
                    int mostWoodAvailable = 0;
                    int mostWoodAvailableMeta = 0;
                    for (int woodMeta = 0; woodMeta < 6; ++woodMeta) {
                        int woodAvailable = this.getWoodCountByMeta(villager, dest, woodMeta);
                        if (woodAvailable <= mostWoodAvailable) continue;
                        mostWoodAvailable = woodAvailable;
                        mostWoodAvailableMeta = woodMeta;
                    }
                    return PLANKS[mostWoodAvailableMeta];
                }
                if (firepit.fuel.getStackInSlot(0).func_190916_E() < 64 && firepit.fuel.getStackInSlot(0).func_77973_b() == Item.func_150898_a((Block)Blocks.field_150344_f)) {
                    int woodMeta = firepit.fuel.getStackInSlot(0).func_77960_j();
                    return PLANKS[woodMeta];
                }
            }
        }
        return null;
    }

    @Override
    public String getTypeLabel() {
        return GOAL_TYPE;
    }

    private int getWoodCountByMeta(MillVillager villager, Building dest, int woodMeta) {
        IBlockState logsToTake = BlockItemUtilities.getLogBlockstateFromPlankMeta(woodMeta);
        return dest.countGoods(logsToTake) + villager.countInv(logsToTake) + villager.getHouse().countGoods(logsToTake);
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
        Building dest = villager.getGoalBuildingDest();
        TileEntity tileEntity = villager.getGoalDestPoint().getTileEntity(villager.field_70170_p);
        if (dest != null && tileEntity != null) {
            if (tileEntity instanceof TileEntityFurnace) {
                this.performAction_furnace(villager, (TileEntityFurnace)tileEntity, dest);
            } else if (tileEntity instanceof TileEntityFirePit) {
                this.performAction_firepit(villager, (TileEntityFirePit)tileEntity, dest);
            }
        }
        return true;
    }

    private void performAction_firepit(MillVillager villager, TileEntityFirePit firepit, Building dest) {
        if (firepit.fuel.getStackInSlot(0).func_190926_b()) {
            int mostWoodAvailable = 0;
            int mostWoodAvailableMeta = -1;
            for (int woodMeta = 0; woodMeta < 6; ++woodMeta) {
                int woodAvailable = this.getWoodCountByMeta(villager, dest, woodMeta);
                if (woodAvailable <= mostWoodAvailable) continue;
                mostWoodAvailable = woodAvailable;
                mostWoodAvailableMeta = woodMeta;
            }
            int nbplanks = Math.min(64, mostWoodAvailable * 4);
            firepit.fuel.setStackInSlot(0, new ItemStack(Blocks.field_150344_f, nbplanks, mostWoodAvailableMeta));
            IBlockState logsToTake = BlockItemUtilities.getLogBlockstateFromPlankMeta(mostWoodAvailableMeta);
            int nbTaken = dest.takeGoods(logsToTake, nbplanks / 4);
            if (nbTaken < nbplanks / 4) {
                nbTaken += villager.takeFromInv(logsToTake, nbplanks / 4 - nbTaken);
            }
            if (nbTaken < nbplanks / 4) {
                nbTaken += villager.getHouse().takeGoods(logsToTake, nbplanks / 4 - nbTaken);
            }
        } else if (firepit.fuel.getStackInSlot(0).func_190916_E() < 64 && firepit.fuel.getStackInSlot(0).func_77973_b() == Item.func_150898_a((Block)Blocks.field_150344_f)) {
            int woodMeta = firepit.fuel.getStackInSlot(0).func_77960_j();
            IBlockState logsToTake = BlockItemUtilities.getLogBlockstateFromPlankMeta(woodMeta);
            int woodAvailable = this.getWoodCountByMeta(villager, dest, woodMeta);
            int nbplanks = Math.min(64 - firepit.fuel.getStackInSlot(0).func_190916_E(), woodAvailable * 4);
            firepit.fuel.setStackInSlot(1, new ItemStack(Blocks.field_150344_f, firepit.fuel.getStackInSlot(0).func_190916_E() + nbplanks, woodMeta));
            int nbTaken = dest.takeGoods(logsToTake, nbplanks / 4);
            if (nbTaken < nbplanks / 4) {
                nbTaken += villager.takeFromInv(logsToTake, nbplanks / 4 - nbTaken);
            }
            if (nbTaken < nbplanks / 4) {
                nbTaken += villager.getHouse().takeGoods(logsToTake, nbplanks / 4 - nbTaken);
            }
        }
    }

    private void performAction_furnace(MillVillager villager, TileEntityFurnace furnace, Building dest) {
        if (furnace.func_70301_a(1) == ItemStack.field_190927_a) {
            int mostWoodAvailable = 0;
            int mostWoodAvailableMeta = -1;
            for (int woodMeta = 0; woodMeta < 6; ++woodMeta) {
                int woodAvailable = this.getWoodCountByMeta(villager, dest, woodMeta);
                if (woodAvailable <= mostWoodAvailable) continue;
                mostWoodAvailable = woodAvailable;
                mostWoodAvailableMeta = woodMeta;
            }
            int nbplanks = Math.min(64, mostWoodAvailable * 4);
            furnace.func_70299_a(1, new ItemStack(Blocks.field_150344_f, nbplanks, mostWoodAvailableMeta));
            IBlockState logsToTake = BlockItemUtilities.getLogBlockstateFromPlankMeta(mostWoodAvailableMeta);
            int nbTaken = dest.takeGoods(logsToTake, nbplanks / 4);
            if (nbTaken < nbplanks / 4) {
                nbTaken += villager.takeFromInv(logsToTake, nbplanks / 4 - nbTaken);
            }
            if (nbTaken < nbplanks / 4) {
                nbTaken += villager.getHouse().takeGoods(logsToTake, nbplanks / 4 - nbTaken);
            }
        } else if (furnace.func_70301_a(1).func_190916_E() < 64 && furnace.func_70301_a(1).func_77973_b() == Item.func_150898_a((Block)Blocks.field_150344_f)) {
            int woodMeta = furnace.func_70301_a(1).func_77960_j();
            IBlockState logsToTake = BlockItemUtilities.getLogBlockstateFromPlankMeta(woodMeta);
            int woodAvailable = this.getWoodCountByMeta(villager, dest, woodMeta);
            int nbplanks = Math.min(64 - furnace.func_70301_a(1).func_190916_E(), woodAvailable * 4);
            furnace.func_70299_a(1, new ItemStack(Blocks.field_150344_f, furnace.func_70301_a(1).func_190916_E() + nbplanks, woodMeta));
            int nbTaken = dest.takeGoods(logsToTake, nbplanks / 4);
            if (nbTaken < nbplanks / 4) {
                nbTaken += villager.takeFromInv(logsToTake, nbplanks / 4 - nbTaken);
            }
            if (nbTaken < nbplanks / 4) {
                nbTaken += villager.getHouse().takeGoods(logsToTake, nbplanks / 4 - nbTaken);
            }
        }
    }

    @Override
    public boolean validateGoal() {
        return true;
    }
}

