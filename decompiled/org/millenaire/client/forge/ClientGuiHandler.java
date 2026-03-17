/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.client.forge;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.client.gui.GuiFirePit;
import org.millenaire.client.gui.GuiLockedChest;
import org.millenaire.client.gui.GuiPujas;
import org.millenaire.client.gui.GuiTrade;
import org.millenaire.common.entity.TileEntityFirePit;
import org.millenaire.common.entity.TileEntityLockedChest;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.forge.ServerGuiHandler;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;

@SideOnly(value=Side.CLIENT)
public class ClientGuiHandler
extends ServerGuiHandler {
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity at;
        if (ID == 1) {
            TileEntity te = world.func_175625_s(new BlockPos(x, y, z));
            if (te != null && te instanceof TileEntityLockedChest) {
                return GuiLockedChest.createGUI(world, x, y, z, player);
            }
        } else if (ID == 2) {
            Building building = Mill.clientWorld.getBuilding(new Point(x, y, z));
            if (building != null && building.getTownHall() != null) {
                return new GuiTrade(player, building);
            }
        } else if (ID == 8) {
            long id = MillCommonUtilities.unpackLong(x, y);
            if (Mill.clientWorld.getVillagerById(id) != null) {
                return new GuiTrade(player, Mill.clientWorld.getVillagerById(id));
            }
            MillLog.error(player, "Failed to find merchant: " + id);
        } else if (ID == 6) {
            Building building = Mill.clientWorld.getBuilding(new Point(x, y, z));
            if (building != null && building.pujas != null) {
                return new GuiPujas(player, building);
            }
        } else if (ID == 16 && (at = world.func_175625_s(new BlockPos(x, y, z))) instanceof TileEntityFirePit) {
            return new GuiFirePit(player, (TileEntityFirePit)at);
        }
        return null;
    }
}

