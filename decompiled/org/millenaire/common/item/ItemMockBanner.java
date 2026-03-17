/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.advancements.CriteriaTriggers
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBanner
 *  net.minecraft.block.BlockBanner$BlockBannerHanging
 *  net.minecraft.block.BlockStandingSign
 *  net.minecraft.block.BlockWallSign
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.item.EnumDyeColor
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.JsonToNBT
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTException
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagInt
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.item;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBanner;
import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.entity.TileEntityMockBanner;

public class ItemMockBanner
extends ItemBlock {
    public static int BANNER_VILLAGE = 0;
    public static int BANNER_CULTURE = 1;
    public static final EnumDyeColor[] BANNER_COLOURS = new EnumDyeColor[]{EnumDyeColor.RED, EnumDyeColor.YELLOW};
    public static final String[] BANNER_DESIGNS = new String[]{"{Patterns:[{Pattern:dls,Color:15},{Pattern:ls,Color:15}]}", "{Patterns:[{Pattern:ls,Color:0},{Pattern:ts,Color:0},{Pattern:bs,Color:0}]}"};
    private final int bannerDesign;
    private final BlockBanner.BlockBannerHanging wallBanner;
    private final EnumDyeColor color;

    public static ItemStack makeBanner(Item banner, EnumDyeColor color, @Nullable NBTTagCompound patterns) {
        ItemStack itemstack = new ItemStack(banner, 1, color.func_176767_b());
        if (patterns != null && !patterns.func_82582_d()) {
            itemstack.func_190925_c("BlockEntityTag").func_74782_a("Patterns", (NBTBase)patterns.func_74737_b().func_150295_c("Patterns", 10));
        }
        return itemstack;
    }

    public ItemMockBanner(BlockBanner standingBanner, BlockBanner.BlockBannerHanging wallBanner, EnumDyeColor color, int design) {
        super((Block)standingBanner);
        this.wallBanner = wallBanner;
        this.color = color;
        this.bannerDesign = design;
        this.field_77777_bU = 16;
        this.func_77637_a(MillBlocks.tabMillenaireContentCreator);
        this.func_77656_e(0);
    }

    public CreativeTabs func_77640_w() {
        return MillBlocks.tabMillenaireContentCreator;
    }

    public int getMetadata(ItemStack stack) {
        return 0;
    }

    public void func_150895_a(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.func_194125_a(tab)) {
            try {
                items.add((Object)ItemMockBanner.makeBanner((Item)this, this.color, JsonToNBT.func_180713_a((String)BANNER_DESIGNS[this.bannerDesign])));
            }
            catch (NBTException e) {
                items.add((Object)ItemMockBanner.makeBanner((Item)this, this.color, null));
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation((Item)this, (int)0, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }

    public EnumActionResult func_180614_a(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState iblockstate = worldIn.func_180495_p(pos);
        boolean flag = iblockstate.func_177230_c().func_176200_f((IBlockAccess)worldIn, pos);
        if (!(facing == EnumFacing.DOWN || !iblockstate.func_185904_a().func_76220_a() && !flag || flag && facing != EnumFacing.UP)) {
            ItemStack itemstack;
            if (player.func_175151_a(pos = pos.func_177972_a(facing), facing, itemstack = player.func_184586_b(hand)) && this.field_150939_a.func_176196_c(worldIn, pos)) {
                BlockPos blockPos = pos = flag ? pos.func_177977_b() : pos;
                if (facing == EnumFacing.UP) {
                    int i = MathHelper.func_76128_c((double)((double)((player.field_70177_z + 180.0f) * 16.0f / 360.0f) + 0.5)) & 0xF;
                    worldIn.func_180501_a(pos, this.field_150939_a.func_176223_P().func_177226_a((IProperty)BlockStandingSign.field_176413_a, (Comparable)Integer.valueOf(i)), 3);
                } else {
                    worldIn.func_180501_a(pos, this.wallBanner.func_176223_P().func_177226_a((IProperty)BlockWallSign.field_176412_a, (Comparable)facing), 3);
                }
                TileEntity bannerEntity = worldIn.func_175625_s(pos);
                if (bannerEntity instanceof TileEntityMockBanner) {
                    ItemStack bannerStack = itemstack.func_77946_l();
                    bannerStack.func_190925_c("BlockEntityTag").func_74782_a("Base", (NBTBase)new NBTTagInt(this.color.func_176767_b()));
                    ((TileEntityMockBanner)bannerEntity).func_175112_a(bannerStack, true);
                }
                if (player instanceof EntityPlayerMP) {
                    CriteriaTriggers.field_193137_x.func_193173_a((EntityPlayerMP)player, pos, itemstack);
                }
                itemstack.func_190918_g(1);
                return EnumActionResult.SUCCESS;
            }
            return EnumActionResult.FAIL;
        }
        return EnumActionResult.FAIL;
    }
}

