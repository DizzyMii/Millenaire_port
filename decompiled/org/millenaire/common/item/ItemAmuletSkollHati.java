/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.world.World
 */
package org.millenaire.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.item.ItemMill;
import org.millenaire.common.utilities.MillLog;

public class ItemAmuletSkollHati
extends ItemMill {
    public ItemAmuletSkollHati(String itemName) {
        super(itemName);
    }

    public ActionResult<ItemStack> func_77659_a(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (MillConfigValues.LogOther >= 3) {
            MillLog.debug((Object)this, "Using skoll amulet.");
        }
        if (worldIn.field_72995_K) {
            return new ActionResult(EnumActionResult.SUCCESS, (Object)playerIn.func_184586_b(handIn));
        }
        long time = worldIn.func_72820_D() + 24000L;
        if (time % 24000L > 11000L && time % 24000L < 23500L) {
            worldIn.func_72877_b(time - time % 24000L - 500L);
        } else {
            worldIn.func_72877_b(time - time % 24000L + 13000L);
        }
        return new ActionResult(EnumActionResult.SUCCESS, (Object)playerIn.func_184586_b(handIn));
    }
}

