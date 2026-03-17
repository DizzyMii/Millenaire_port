/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockPane
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyBool
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.EnumDyeColor
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.block;

import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.advancements.MillAdvancements;
import org.millenaire.common.block.IPaintedBlock;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.item.ItemPaintBucket;
import org.millenaire.common.utilities.Point;

public class BlockPaintedBricks
extends Block
implements IPaintedBlock {
    public static final PropertyBool TOP_FRIEZE = PropertyBool.func_177716_a((String)"top_frieze");
    public static final PropertyBool BOTTOM_FRIEZE = PropertyBool.func_177716_a((String)"bottom_frieze");
    private final String blockName;
    private final String baseBlockName;
    private final EnumDyeColor colour;

    public static IBlockState getBlockStateWithColour(IBlockState input, EnumDyeColor colour) {
        IPaintedBlock paintedBlock = (IPaintedBlock)input.func_177230_c();
        Block newBlock = MillBlocks.PAINTED_BRICK_MAP.get(paintedBlock.getBlockType()).get(colour);
        return newBlock.func_176203_a(input.func_177230_c().func_176201_c(input));
    }

    public static String getColorName(EnumDyeColor colour) {
        String colourName = colour.func_176762_d();
        if (colourName.equalsIgnoreCase("lightBlue")) {
            colourName = "light_blue";
        }
        return colourName;
    }

    public static EnumDyeColor getColourFromBlockState(IBlockState bs) {
        if (bs.func_177230_c() instanceof IPaintedBlock) {
            return ((IPaintedBlock)bs.func_177230_c()).getDyeColour();
        }
        return null;
    }

    public BlockPaintedBricks(String baseBlockName, EnumDyeColor colour) {
        super(Material.field_151576_e);
        this.baseBlockName = baseBlockName;
        String colourName = BlockPaintedBricks.getColorName(colour);
        this.blockName = baseBlockName + "_" + colourName;
        this.colour = colour;
        this.func_149663_c("millenaire." + this.blockName);
        this.setRegistryName(this.blockName);
        this.func_149647_a(MillBlocks.tabMillenaire);
        this.setHarvestLevel("pickaxe", 0);
        this.func_149711_c(1.5f);
        this.func_149752_b(10.0f);
    }

    protected BlockStateContainer func_180661_e() {
        return new BlockStateContainer((Block)this, new IProperty[]{TOP_FRIEZE, BOTTOM_FRIEZE});
    }

    public int friezePriority(IBlockAccess worldIn, BlockPos pos, IBlockState ourState, IBlockState otherState, EnumFacing side) {
        if (BlockPaintedBricks.getColourFromBlockState(otherState) == this.colour) {
            if (otherState.isSideSolid(worldIn, pos, side)) {
                return 0;
            }
            return 3;
        }
        if (otherState.func_185890_d(worldIn, pos) == field_185506_k) {
            return 5;
        }
        if (otherState.func_177230_c() instanceof BlockPane) {
            return 2;
        }
        if (otherState.func_177230_c() instanceof IPaintedBlock) {
            return 1;
        }
        return 10;
    }

    public IBlockState func_176221_a(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        int bottomPriority;
        int topPriority = this.friezePriority(worldIn, pos.func_177984_a(), state, worldIn.func_180495_p(pos.func_177984_a()), EnumFacing.DOWN);
        if (topPriority > (bottomPriority = this.friezePriority(worldIn, pos.func_177977_b(), state, worldIn.func_180495_p(pos.func_177977_b()), EnumFacing.UP))) {
            return state.func_177226_a((IProperty)TOP_FRIEZE, (Comparable)Boolean.valueOf(true)).func_177226_a((IProperty)BOTTOM_FRIEZE, (Comparable)Boolean.valueOf(false));
        }
        if (bottomPriority > 0) {
            return state.func_177226_a((IProperty)BOTTOM_FRIEZE, (Comparable)Boolean.valueOf(true)).func_177226_a((IProperty)TOP_FRIEZE, (Comparable)Boolean.valueOf(false));
        }
        return state.func_177226_a((IProperty)BOTTOM_FRIEZE, (Comparable)Boolean.valueOf(false)).func_177226_a((IProperty)TOP_FRIEZE, (Comparable)Boolean.valueOf(false));
    }

    public BlockRenderLayer func_180664_k() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public String getBlockType() {
        return this.baseBlockName;
    }

    @Override
    public EnumDyeColor getDyeColour() {
        return this.colour;
    }

    public int func_176201_c(IBlockState state) {
        return 0;
    }

    public IBlockState func_180642_a(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.func_176203_a(meta);
    }

    public IBlockState func_176203_a(int meta) {
        return this.func_176223_P();
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation((Item)Item.func_150898_a((Block)this), (int)0, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "up=true,down=true"));
    }

    public boolean func_180639_a(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (player.field_71071_by.func_70448_g() != ItemStack.field_190927_a && player.field_71071_by.func_70448_g().func_77973_b() instanceof ItemPaintBucket) {
            ItemStack bucket = player.field_71071_by.func_70448_g();
            EnumDyeColor targetColor = ((ItemPaintBucket)player.field_71071_by.func_70448_g().func_77973_b()).getColour();
            if (targetColor != null && this.colour != targetColor) {
                ArrayList<Point> pointsToTest = new ArrayList<Point>();
                pointsToTest.add(new Point(pos));
                int blockColoured = 0;
                while (!pointsToTest.isEmpty()) {
                    Point p = (Point)pointsToTest.get(pointsToTest.size() - 1);
                    IBlockState bs = p.getBlockActualState(worldIn);
                    if (BlockPaintedBricks.getColourFromBlockState(bs) == this.colour) {
                        p.setBlockState(worldIn, BlockPaintedBricks.getBlockStateWithColour(bs, targetColor));
                        ++blockColoured;
                        pointsToTest.add(p.getAbove());
                        pointsToTest.add(p.getBelow());
                        pointsToTest.add(p.getNorth());
                        pointsToTest.add(p.getEast());
                        pointsToTest.add(p.getSouth());
                        pointsToTest.add(p.getWest());
                    }
                    pointsToTest.remove(p);
                }
                if (blockColoured < bucket.func_77958_k() - bucket.func_77952_i()) {
                    bucket.func_77972_a(blockColoured, (EntityLivingBase)player);
                } else {
                    player.field_71071_by.func_70304_b(player.field_71071_by.field_70461_c);
                    player.field_71071_by.func_191971_c(player.field_71071_by.field_70461_c, new ItemStack(Items.field_151133_ar));
                }
                MillAdvancements.RAINBOW.grant(player);
                return true;
            }
        }
        return false;
    }
}

