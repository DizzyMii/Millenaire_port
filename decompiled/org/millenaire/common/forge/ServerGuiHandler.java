/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.network.IGuiHandler
 */
package org.millenaire.common.forge;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import org.millenaire.common.block.BlockLockedChest;
import org.millenaire.common.entity.TileEntityFirePit;
import org.millenaire.common.entity.TileEntityLockedChest;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.ui.ContainerPuja;
import org.millenaire.common.ui.ContainerTrade;
import org.millenaire.common.ui.firepit.ContainerFirePit;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;
import org.millenaire.common.world.MillWorldData;

public class ServerGuiHandler
implements IGuiHandler {
    public static final int GUI_MILL_CHEST = 1;
    public static final int GUI_TRADE = 2;
    public static final int GUI_QUEST = 3;
    public static final int GUI_VILLAGECHIEF = 4;
    public static final int GUI_VILLAGEBOOK = 5;
    public static final int GUI_PUJAS = 6;
    public static final int GUI_PANEL = 7;
    public static final int GUI_MERCHANT = 8;
    public static final int GUI_NEGATIONWAND = 9;
    public static final int GUI_NEWBUILDING = 10;
    public static final int GUI_CONTROLLEDPROJECTPANEL = 11;
    public static final int GUI_HIRE = 12;
    public static final int GUI_NEWVILLAGE = 13;
    public static final int GUI_CONTROLLEDMILITARYPANEL = 14;
    public static final int GUI_IMPORTTABLE = 15;
    public static final int GUI_FIRE_PIT = 16;

    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity at;
        MillWorldData mw = Mill.getMillWorld(world);
        if (ID == 1) {
            TileEntity te = world.func_175625_s(new BlockPos(x, y, z));
            if (te != null && te instanceof TileEntityLockedChest) {
                return BlockLockedChest.createContainer(world, x, y, z, player);
            }
        } else if (ID == 2) {
            Building building = mw.getBuilding(new Point(x, y, z));
            if (building != null) {
                return new ContainerTrade(player, building);
            }
            MillLog.error(this, "Server-side trading for unknown building at " + new Point(x, y, z) + " in world: " + world);
        } else if (ID == 8) {
            long id = MillCommonUtilities.unpackLong(x, y);
            if (mw.getVillagerById(id) != null) {
                return new ContainerTrade(player, mw.getVillagerById(id));
            }
            MillLog.error(player, "Failed to find merchant: " + id);
        } else if (ID == 6) {
            Building building = mw.getBuilding(new Point(x, y, z));
            if (building != null && building.pujas != null) {
                return new ContainerPuja(player, building);
            }
        } else if (ID == 16 && (at = world.func_175625_s(new BlockPos(x, y, z))) instanceof TileEntityFirePit) {
            return new ContainerFirePit(player, (TileEntityFirePit)at);
        }
        return null;
    }
}

