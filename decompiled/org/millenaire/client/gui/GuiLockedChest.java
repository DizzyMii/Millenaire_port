/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.inventory.GuiChest
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package org.millenaire.client.gui;

import java.io.IOException;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.millenaire.common.block.BlockLockedChest;
import org.millenaire.common.entity.TileEntityLockedChest;

public class GuiLockedChest
extends GuiChest {
    boolean locked = true;

    public static GuiLockedChest createGUI(World world, int i, int j, int k, EntityPlayer entityplayer) {
        TileEntityLockedChest lockedchest = (TileEntityLockedChest)world.func_175625_s(new BlockPos(i, j, k));
        if (lockedchest == null || world.field_72995_K && !lockedchest.loaded) {
            return null;
        }
        IInventory chest = BlockLockedChest.getInventory(lockedchest, world, i, j, k);
        return new GuiLockedChest(entityplayer, chest, lockedchest);
    }

    private GuiLockedChest(EntityPlayer player, IInventory iinventory1, TileEntityLockedChest lockedchest) {
        super((IInventory)player.field_71071_by, iinventory1);
        this.locked = lockedchest.isLockedFor(player);
    }

    protected void func_73869_a(char par1, int par2) throws IOException {
        if (!this.locked) {
            super.func_73869_a(par1, par2);
        } else if (par2 == 1 || par2 == this.field_146297_k.field_71474_y.field_151445_Q.func_151463_i()) {
            this.field_146297_k.field_71439_g.func_71053_j();
        }
    }

    protected void func_73864_a(int i, int j, int k) throws IOException {
        if (!this.locked) {
            super.func_73864_a(i, j, k);
        }
    }
}

