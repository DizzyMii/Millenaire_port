/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 */
package org.millenaire.common.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.buildingplan.BuildingImportExport;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.ItemMill;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;
import org.millenaire.common.world.MillWorldData;

public class ItemNegationWand
extends ItemMill {
    public ItemNegationWand(String itemName) {
        super(itemName);
    }

    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos bp, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        Point pos = new Point(bp);
        IBlockState bs = world.func_180495_p(bp);
        if (bs.func_177230_c() == MillBlocks.IMPORT_TABLE) {
            return EnumActionResult.PASS;
        }
        if (world.field_72995_K) {
            if (bs.func_177230_c() == Blocks.field_150472_an && world.field_72995_K) {
                BuildingImportExport.negationWandExportBuilding(player, world, pos);
                return EnumActionResult.SUCCESS;
            }
            return EnumActionResult.FAIL;
        }
        MillWorldData mw = Mill.getMillWorld(world);
        for (int i = 0; i < 2; ++i) {
            MillCommonUtilities.VillageList list = i == 0 ? mw.loneBuildingsList : mw.villagesList;
            for (int j = 0; j < list.names.size(); ++j) {
                Building th;
                Point p = list.pos.get(j);
                int distance = MathHelper.func_76128_c((double)p.horizontalDistanceTo(pos));
                if (distance > 30 || (th = mw.getBuilding(p)) == null || !th.isTownhall) continue;
                if (th.chestLocked && !MillConfigValues.DEV) {
                    ServerSender.sendTranslatedSentence(player, '6', "negationwand.villagelocked", th.villageType.name);
                    return EnumActionResult.SUCCESS;
                }
                ServerSender.displayNegationWandGUI(player, th);
            }
        }
        return EnumActionResult.FAIL;
    }
}

