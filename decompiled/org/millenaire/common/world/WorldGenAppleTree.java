/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockLeaves
 *  net.minecraft.block.BlockLog
 *  net.minecraft.block.BlockLog$EnumAxis
 *  net.minecraft.block.BlockOldLog
 *  net.minecraft.block.BlockPlanks$EnumType
 *  net.minecraft.block.BlockSapling
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumFacing$Axis
 *  net.minecraft.util.EnumFacing$Plane
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.gen.feature.WorldGenAbstractTree
 *  net.minecraftforge.common.IPlantable
 */
package org.millenaire.common.world;

import java.util.HashSet;
import java.util.Random;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.common.IPlantable;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.utilities.MillCommonUtilities;

public class WorldGenAppleTree
extends WorldGenAbstractTree {
    private static final int MIN_TREE_HEIGHT = 5;
    private static final IBlockState WOOD_BS = Blocks.field_150364_r.func_176223_P().func_177226_a((IProperty)BlockOldLog.field_176301_b, (Comparable)BlockPlanks.EnumType.OAK);
    private static final IBlockState LEAVES_BS = MillBlocks.LEAVES_APPLETREE.func_176223_P().func_177226_a((IProperty)BlockLeaves.field_176236_b, (Comparable)Boolean.valueOf(false));

    public WorldGenAppleTree(boolean notify) {
        super(notify);
    }

    public boolean func_180709_b(World worldIn, Random rand, BlockPos position) {
        int treeHeight = rand.nextInt(2) + 5;
        boolean obstacleMet = true;
        if (position.func_177956_o() >= 1 && position.func_177956_o() + treeHeight + 1 <= worldIn.func_72800_K()) {
            for (int j = position.func_177956_o(); j <= position.func_177956_o() + 1 + treeHeight; ++j) {
                int k = 1;
                if (j == position.func_177956_o()) {
                    k = 0;
                }
                if (j >= position.func_177956_o() + 1 + treeHeight - 2) {
                    k = 2;
                }
                BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
                for (int l = position.func_177958_n() - k; l <= position.func_177958_n() + k && obstacleMet; ++l) {
                    for (int i1 = position.func_177952_p() - k; i1 <= position.func_177952_p() + k && obstacleMet; ++i1) {
                        if (j >= 0 && j < worldIn.func_72800_K()) {
                            if (this.isReplaceable(worldIn, (BlockPos)blockpos$mutableblockpos.func_181079_c(l, j, i1))) continue;
                            obstacleMet = false;
                            continue;
                        }
                        obstacleMet = false;
                    }
                }
            }
            if (!obstacleMet) {
                return false;
            }
            IBlockState state = worldIn.func_180495_p(position.func_177977_b());
            if (state.func_177230_c().canSustainPlant(state, (IBlockAccess)worldIn, position.func_177977_b(), EnumFacing.UP, (IPlantable)((BlockSapling)Blocks.field_150345_g)) && position.func_177956_o() < worldIn.func_72800_K() - treeHeight - 1) {
                state.func_177230_c().onPlantGrow(state, worldIn, position.func_177977_b(), position);
                for (int yPos = 0; yPos < 5; ++yPos) {
                    BlockPos upN = position.func_177981_b(yPos);
                    state = worldIn.func_180495_p(upN);
                    if (!state.func_177230_c().isAir(state, (IBlockAccess)worldIn, upN) && !state.func_177230_c().isLeaves(state, (IBlockAccess)worldIn, upN) && state.func_185904_a() != Material.field_151582_l) continue;
                    this.func_175903_a(worldIn, position.func_177981_b(yPos), WOOD_BS);
                }
                HashSet<EnumFacing> branchFacings = new HashSet<EnumFacing>();
                branchFacings.add(EnumFacing.Plane.HORIZONTAL.func_179518_a(rand));
                for (int i = 0; i < 3; ++i) {
                    branchFacings.add(EnumFacing.Plane.HORIZONTAL.func_179518_a(rand));
                }
                for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                    int branchStartY = 3 + rand.nextInt(1);
                    int horizontalSize = 3 - rand.nextInt(2);
                    int xPos = position.func_177958_n();
                    int zPos = position.func_177952_p();
                    int yPos = position.func_177956_o() + branchStartY;
                    int curve = Math.random() < 0.5 ? 1 : -1;
                    for (int hPos = 0; hPos < horizontalSize; ++hPos) {
                        BlockPos blockpos;
                        if (yPos < position.func_177956_o() + treeHeight && Math.random() < 0.7) {
                            ++yPos;
                        }
                        if (enumfacing.func_82601_c() != 0) {
                            xPos += enumfacing.func_82601_c();
                            if (Math.random() < 0.15) {
                                zPos += curve;
                            }
                        } else {
                            zPos += enumfacing.func_82599_e();
                            if (Math.random() < 0.15) {
                                xPos += curve;
                            }
                        }
                        if (!(state = worldIn.func_180495_p(blockpos = new BlockPos(xPos, yPos, zPos))).func_177230_c().isAir(state, (IBlockAccess)worldIn, blockpos) && !state.func_177230_c().isLeaves(state, (IBlockAccess)worldIn, blockpos)) continue;
                        this.func_175903_a(worldIn, blockpos, WOOD_BS.func_177226_a((IProperty)BlockLog.field_176299_a, (Comparable)BlockLog.EnumAxis.func_176870_a((EnumFacing.Axis)enumfacing.func_176740_k())));
                        for (int dx = -1; dx < 2; ++dx) {
                            for (int dz = -1; dz < 2; ++dz) {
                                for (int dy = -1; dy < 2; ++dy) {
                                    BlockPos leavePos = blockpos.func_177982_a(dx, dy, dz);
                                    state = worldIn.func_180495_p(leavePos);
                                    if (!state.func_177230_c().isAir(state, (IBlockAccess)worldIn, leavePos) || MillCommonUtilities.randomInt(100) >= 50) continue;
                                    this.func_175903_a(worldIn, leavePos, LEAVES_BS);
                                }
                            }
                        }
                    }
                }
                return true;
            }
            return false;
        }
        return false;
    }
}

