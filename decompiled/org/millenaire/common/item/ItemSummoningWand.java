/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package org.millenaire.common.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.item.ItemMill;
import org.millenaire.common.ui.GuiActions;
import org.millenaire.common.utilities.Point;

public class ItemSummoningWand
extends ItemMill {
    public ItemSummoningWand(String itemName) {
        super(itemName);
    }

    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos bp, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        Block targetBlock = world.func_180495_p(bp).func_177230_c();
        if (targetBlock == MillBlocks.IMPORT_TABLE) {
            return EnumActionResult.PASS;
        }
        if (world.field_72995_K) {
            return EnumActionResult.PASS;
        }
        if (world.field_73011_w.getDimension() != 0) {
            return EnumActionResult.PASS;
        }
        Point pos = new Point(bp);
        return GuiActions.useSummoningWand((EntityPlayerMP)player, pos);
    }
}

