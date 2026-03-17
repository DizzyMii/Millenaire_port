/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBush
 *  net.minecraft.block.IGrowable
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.MapColor
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyInteger
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.gen.feature.WorldGenBigTree
 *  net.minecraft.world.gen.feature.WorldGenTrees
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.event.terraingen.TerrainGen
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.world.WorldGenAppleTree;
import org.millenaire.common.world.WorldGenCherry;
import org.millenaire.common.world.WorldGenOliveTree;
import org.millenaire.common.world.WorldGenPistachio;
import org.millenaire.common.world.WorldGenSakura;

public class BlockMillSapling
extends BlockBush
implements IGrowable {
    public static final PropertyInteger STAGE = PropertyInteger.func_177719_a((String)"stage", (int)0, (int)1);
    protected static final AxisAlignedBB SAPLING_AABB = new AxisAlignedBB(0.09999999403953552, 0.0, 0.09999999403953552, (double)0.9f, (double)0.8f, (double)0.9f);
    private final EnumMillWoodType type;

    protected BlockMillSapling(String blockName, EnumMillWoodType type) {
        this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a((IProperty)STAGE, (Comparable)Integer.valueOf(0)));
        this.type = type;
        this.func_149663_c("millenaire." + blockName);
        this.setRegistryName(blockName);
        this.func_149647_a(MillBlocks.tabMillenaire);
        this.func_149711_c(0.0f);
        this.func_149672_a(SoundType.field_185850_c);
        this.func_149675_a(true);
    }

    public boolean func_176473_a(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return true;
    }

    public boolean func_180670_a(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return (double)worldIn.field_73012_v.nextFloat() < 0.45;
    }

    protected BlockStateContainer func_180661_e() {
        return new BlockStateContainer((Block)this, new IProperty[]{STAGE});
    }

    public int func_180651_a(IBlockState state) {
        return 0;
    }

    public void generateTree(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!TerrainGen.saplingGrowTree((World)worldIn, (Random)rand, (BlockPos)pos)) {
            return;
        }
        Object worldgenerator = rand.nextInt(10) == 0 ? new WorldGenBigTree(true) : new WorldGenTrees(true);
        boolean i = false;
        boolean j = false;
        boolean flag = false;
        switch (this.type) {
            case APPLETREE: {
                worldgenerator = new WorldGenAppleTree(true);
                break;
            }
            case OLIVETREE: {
                worldgenerator = new WorldGenOliveTree(true);
                break;
            }
            case PISTACHIO: {
                worldgenerator = new WorldGenPistachio(true);
                break;
            }
            case CHERRY: {
                worldgenerator = new WorldGenCherry(true);
                break;
            }
            case SAKURA: {
                worldgenerator = new WorldGenSakura(true);
            }
        }
        IBlockState iblockstate2 = Blocks.field_150350_a.func_176223_P();
        worldIn.func_180501_a(pos, iblockstate2, 4);
        if (!worldgenerator.func_180709_b(worldIn, rand, pos.func_177982_a(0, 0, 0))) {
            worldIn.func_180501_a(pos, state, 4);
        }
    }

    public AxisAlignedBB func_185496_a(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SAPLING_AABB;
    }

    public int func_176201_c(IBlockState state) {
        int i = 0;
        return i |= ((Integer)state.func_177229_b((IProperty)STAGE)).intValue();
    }

    public IBlockState func_176203_a(int meta) {
        return this.func_176223_P().func_177226_a((IProperty)STAGE, (Comparable)Integer.valueOf(meta & 8));
    }

    public void grow(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if ((Integer)state.func_177229_b((IProperty)STAGE) == 0) {
            worldIn.func_180501_a(pos, state.func_177231_a((IProperty)STAGE), 4);
        } else {
            this.generateTree(worldIn, pos, state, rand);
        }
    }

    public void func_176474_b(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        this.grow(worldIn, pos, state, rand);
    }

    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation((Item)Item.func_150898_a((Block)this), (int)0, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), ""));
    }

    public boolean isTypeAt(World worldIn, BlockPos pos, EnumMillWoodType type) {
        IBlockState iblockstate = worldIn.func_180495_p(pos);
        return iblockstate.func_177230_c() == this;
    }

    public void func_180650_b(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.field_72995_K) {
            super.func_180650_b(worldIn, pos, state, rand);
            if (!worldIn.func_175697_a(pos, 1)) {
                return;
            }
            if (worldIn.func_175671_l(pos.func_177984_a()) >= 9 && rand.nextInt(7) == 0) {
                this.grow(worldIn, pos, state, rand);
            }
        }
    }

    public static enum EnumMillWoodType {
        APPLETREE(MapColor.field_151663_o),
        OLIVETREE(MapColor.field_151663_o),
        PISTACHIO(MapColor.field_151663_o),
        CHERRY(MapColor.field_151663_o),
        SAKURA(MapColor.field_151663_o);

        private final MapColor mapColor;

        private EnumMillWoodType(MapColor mapColorIn) {
            this.mapColor = mapColorIn;
        }

        public MapColor getMapColor() {
            return this.mapColor;
        }
    }
}

