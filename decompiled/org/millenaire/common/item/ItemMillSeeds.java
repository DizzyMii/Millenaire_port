/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.CriteriaTriggers
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.EnumPlantType
 *  net.minecraftforge.common.IPlantable
 */
package org.millenaire.common.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import org.millenaire.common.advancements.MillAdvancements;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.ItemMill;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.world.UserProfile;

public class ItemMillSeeds
extends ItemMill
implements IPlantable {
    public final Block crops;
    public final String cropKey;

    public ItemMillSeeds(Block crops, String cropKey) {
        super(cropKey);
        this.crops = crops;
        this.cropKey = cropKey;
    }

    public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
        return this.crops.func_176223_P();
    }

    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return EnumPlantType.Crop;
    }

    public EnumActionResult func_180614_a(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack itemstack = player.func_184586_b(hand);
        IBlockState state = world.func_180495_p(pos);
        if (facing == EnumFacing.UP && player.func_175151_a(pos.func_177972_a(facing), facing, itemstack) && state.func_177230_c().canSustainPlant(state, (IBlockAccess)world, pos, EnumFacing.UP, (IPlantable)this) && world.func_175623_d(pos.func_177984_a())) {
            UserProfile profile = Mill.getMillWorld(world).getProfile(player);
            if (!profile.isTagSet("cropplanting_" + this.cropKey) && !MillConfigValues.DEV) {
                if (!world.field_72995_K) {
                    ServerSender.sendTranslatedSentence(player, 'f', "ui.cropplantingknowledge", new String[0]);
                }
                return EnumActionResult.FAIL;
            }
            world.func_175656_a(pos.func_177984_a(), this.crops.func_176223_P());
            this.crops.func_180633_a(world, pos.func_177984_a(), this.crops.func_176223_P(), (EntityLivingBase)player, itemstack);
            MillAdvancements.MASTER_FARMER.grant(player);
            if (player instanceof EntityPlayerMP) {
                CriteriaTriggers.field_193137_x.func_193173_a((EntityPlayerMP)player, pos.func_177984_a(), itemstack);
            }
            itemstack.func_190918_g(1);
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.FAIL;
    }
}

