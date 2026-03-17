/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockLeaves
 *  net.minecraft.block.BlockOldLog
 *  net.minecraft.block.BlockPlanks$EnumType
 *  net.minecraft.block.BlockSapling
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumFacing$Plane
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.gen.feature.WorldGenAbstractTree
 *  net.minecraftforge.common.IPlantable
 */
package org.millenaire.common.world;

import java.util.Random;
import net.minecraft.block.BlockLeaves;
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

public class WorldGenSakura
extends WorldGenAbstractTree {
    private final int minTreeHeight;
    private final IBlockState metaWood = Blocks.field_150364_r.func_176223_P().func_177226_a((IProperty)BlockOldLog.field_176301_b, (Comparable)BlockPlanks.EnumType.SPRUCE);
    private final IBlockState metaLeaves = MillBlocks.SAKURA_LEAVES.func_176223_P().func_177226_a((IProperty)BlockLeaves.field_176236_b, (Comparable)Boolean.valueOf(false));

    public WorldGenSakura(boolean notify) {
        super(notify);
        this.minTreeHeight = 5;
    }

    public boolean func_180709_b(World worldIn, Random rand, BlockPos position) {
        int treeHeight = rand.nextInt(2) + this.minTreeHeight;
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
                for (int yPos = position.func_177956_o() + 2; yPos <= position.func_177956_o() + treeHeight + 1; ++yPos) {
                    int leavesRadius = 3;
                    if (yPos < position.func_177956_o() + 4) {
                        leavesRadius -= position.func_177956_o() + 4 - yPos;
                    } else if (yPos > position.func_177956_o() + treeHeight - 2) {
                        leavesRadius -= yPos - (position.func_177956_o() + treeHeight - 2);
                    }
                    for (int xPos = position.func_177958_n() - leavesRadius; xPos <= position.func_177958_n() + leavesRadius; ++xPos) {
                        int distanceFromTrunkX = xPos - position.func_177958_n();
                        for (int zPos = position.func_177952_p() - leavesRadius; zPos <= position.func_177952_p() + leavesRadius; ++zPos) {
                            BlockPos blockpos;
                            int distanceFromTrunkZ = zPos - position.func_177952_p();
                            int chanceOn100 = 95;
                            if (Math.abs(distanceFromTrunkX) == leavesRadius && Math.abs(distanceFromTrunkZ) == leavesRadius) {
                                chanceOn100 = 0;
                            } else if (Math.abs(distanceFromTrunkX) == leavesRadius || Math.abs(distanceFromTrunkZ) == leavesRadius) {
                                chanceOn100 = 80;
                            }
                            if (MillCommonUtilities.randomInt(100) >= chanceOn100 || !(state = worldIn.func_180495_p(blockpos = new BlockPos(xPos, yPos, zPos))).func_177230_c().isAir(state, (IBlockAccess)worldIn, blockpos) && !state.func_177230_c().isLeaves(state, (IBlockAccess)worldIn, blockpos) && state.func_185904_a() != Material.field_151582_l) continue;
                            this.func_175903_a(worldIn, blockpos, this.metaLeaves);
                        }
                    }
                }
                for (int j3 = 0; j3 < treeHeight; ++j3) {
                    BlockPos upN = position.func_177981_b(j3);
                    state = worldIn.func_180495_p(upN);
                    if (!state.func_177230_c().isAir(state, (IBlockAccess)worldIn, upN) && !state.func_177230_c().isLeaves(state, (IBlockAccess)worldIn, upN) && state.func_185904_a() != Material.field_151582_l) continue;
                    this.func_175903_a(worldIn, position.func_177981_b(j3), this.metaWood);
                }
                for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL.func_179516_a()) {
                    if (MillCommonUtilities.randomInt(100) >= 60) continue;
                    int branchMaxY = treeHeight - rand.nextInt(2);
                    int branchMinY = 3 + rand.nextInt(2);
                    int horizontalOffset = 2 - rand.nextInt(2);
                    int xPos = position.func_177958_n();
                    int zPos = position.func_177952_p();
                    for (int yPos = 0; yPos < branchMaxY; ++yPos) {
                        BlockPos blockpos;
                        int i2 = position.func_177956_o() + yPos;
                        if (yPos >= branchMinY && horizontalOffset > 0) {
                            xPos += enumfacing.func_82601_c();
                            zPos += enumfacing.func_82599_e();
                            --horizontalOffset;
                        }
                        if (!(state = worldIn.func_180495_p(blockpos = new BlockPos(xPos, i2, zPos))).func_177230_c().isAir(state, (IBlockAccess)worldIn, blockpos) && !state.func_177230_c().isLeaves(state, (IBlockAccess)worldIn, blockpos)) continue;
                        this.func_175903_a(worldIn, blockpos, this.metaWood);
                    }
                }
                return true;
            }
            return false;
        }
        return false;
    }
}

