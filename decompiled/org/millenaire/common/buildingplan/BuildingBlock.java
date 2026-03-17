/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBed
 *  net.minecraft.block.BlockChest
 *  net.minecraft.block.BlockDoor
 *  net.minecraft.block.BlockDoor$EnumDoorHalf
 *  net.minecraft.block.BlockDoor$EnumHingePosition
 *  net.minecraft.block.BlockDoublePlant
 *  net.minecraft.block.BlockDoublePlant$EnumBlockHalf
 *  net.minecraft.block.BlockFlowerPot
 *  net.minecraft.block.BlockFlowerPot$EnumFlowerType
 *  net.minecraft.block.BlockFurnace
 *  net.minecraft.block.BlockHorizontal
 *  net.minecraft.block.BlockLeaves
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.block.BlockLog
 *  net.minecraft.block.BlockOldLeaf
 *  net.minecraft.block.BlockOldLog
 *  net.minecraft.block.BlockPlanks$EnumType
 *  net.minecraft.block.BlockTorch
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.Blocks
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.JsonToNBT
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTException
 *  net.minecraft.nbt.NBTTagInt
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityDispenser
 *  net.minecraft.tileentity.TileEntityFlowerPot
 *  net.minecraft.tileentity.TileEntityMobSpawner
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 *  net.minecraft.world.gen.feature.WorldGenBirchTree
 *  net.minecraft.world.gen.feature.WorldGenCanopyTree
 *  net.minecraft.world.gen.feature.WorldGenSavannaTree
 *  net.minecraft.world.gen.feature.WorldGenTaiga2
 *  net.minecraft.world.gen.feature.WorldGenTrees
 */
package org.millenaire.common.buildingplan;

import java.io.DataInputStream;
import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBirchTree;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenTrees;
import org.millenaire.common.block.IBlockPath;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.block.mock.MockBlockBannerHanging;
import org.millenaire.common.block.mock.MockBlockBannerStanding;
import org.millenaire.common.entity.EntityWallDecoration;
import org.millenaire.common.entity.TileEntityMockBanner;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.ItemMockBanner;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.PathUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;
import org.millenaire.common.world.WorldGenAppleTree;
import org.millenaire.common.world.WorldGenCherry;
import org.millenaire.common.world.WorldGenOliveTree;
import org.millenaire.common.world.WorldGenPistachio;
import org.millenaire.common.world.WorldGenSakura;

public class BuildingBlock {
    public static byte TAPESTRY = 1;
    public static byte OAKSPAWN = (byte)2;
    public static byte PINESPAWN = (byte)3;
    public static byte BIRCHSPAWN = (byte)4;
    public static byte INDIANSTATUE = (byte)5;
    public static byte PRESERVEGROUNDDEPTH = (byte)6;
    public static byte CLEARTREE = (byte)7;
    public static byte MAYANSTATUE = (byte)8;
    public static byte SPAWNERSKELETON = (byte)9;
    public static byte SPAWNERZOMBIE = (byte)10;
    public static byte SPAWNERSPIDER = (byte)11;
    public static byte SPAWNERCAVESPIDER = (byte)12;
    public static byte SPAWNERCREEPER = (byte)13;
    public static byte DISPENDERUNKNOWNPOWDER = (byte)14;
    public static byte JUNGLESPAWN = (byte)15;
    public static byte INVERTED_DOOR = (byte)16;
    public static byte CLEARGROUND = (byte)17;
    public static byte BYZANTINEICONSMALL = (byte)18;
    public static byte BYZANTINEICONMEDIUM = (byte)19;
    public static byte BYZANTINEICONLARGE = (byte)20;
    public static byte PRESERVEGROUNDSURFACE = (byte)21;
    public static byte SPAWNERBLAZE = (byte)22;
    public static byte ACACIASPAWN = (byte)23;
    public static byte DARKOAKSPAWN = (byte)24;
    public static byte TORCHGUESS = (byte)25;
    public static byte CHESTGUESS = (byte)26;
    public static byte FURNACEGUESS = (byte)27;
    public static byte CLEARGROUNDOUTSIDEBUILDING = (byte)28;
    public static byte HIDEHANGING = (byte)29;
    public static byte APPLETREESPAWN = (byte)30;
    public static byte CLEARGROUNDBORDER = (byte)31;
    public static byte OLIVETREESPAWN = (byte)32;
    public static byte PISTACHIOTREESPAWN = (byte)33;
    public static byte WALLCARPETSMALL = (byte)40;
    public static byte WALLCARPETMEDIUM = (byte)41;
    public static byte WALLCARPETLARGE = (byte)42;
    public static byte CHERRYTREESPAWN = (byte)43;
    public static byte SAKURATREESPAWN = (byte)43;
    public final Block block;
    private byte meta;
    public final Point p;
    private IBlockState blockState;
    public byte special;

    public BuildingBlock(Point p, Block block, int meta) {
        this.p = p;
        this.block = block;
        this.meta = (byte)meta;
        this.blockState = block.func_176203_a(meta);
        this.special = 0;
    }

    public BuildingBlock(Point p, DataInputStream ds) throws IOException {
        this.p = p;
        this.block = Block.func_149729_e((int)ds.readInt());
        this.meta = ds.readByte();
        this.special = ds.readByte();
        this.blockState = this.block != null ? this.block.func_176203_a((int)this.meta) : Blocks.field_150350_a.func_176223_P();
    }

    public BuildingBlock(Point p, IBlockState bs) {
        this.p = p;
        this.block = bs.func_177230_c();
        this.meta = (byte)bs.func_177230_c().func_176201_c(bs);
        this.blockState = bs;
        this.special = 0;
    }

    public BuildingBlock(Point p, int special) {
        this.p = p;
        this.block = Blocks.field_150350_a;
        this.meta = 0;
        this.special = (byte)special;
        this.blockState = Blocks.field_150350_a.func_176223_P();
    }

    public boolean alreadyDone(World world) {
        if (this.special != 0) {
            return false;
        }
        Block block = WorldUtilities.getBlock(world, this.p);
        if (this.block != block) {
            return false;
        }
        int meta = WorldUtilities.getBlockMeta(world, this.p);
        return this.meta == meta;
    }

    public boolean build(World world, Building townHall, boolean worldGeneration, boolean wandimport) {
        boolean blockSet = false;
        try {
            boolean playSound;
            boolean notifyBlocks = true;
            boolean bl = playSound = !worldGeneration && !wandimport;
            if (this.special == 0) {
                blockSet = this.buildNormalBlock(world, townHall, wandimport, true, playSound);
            } else if (this.special == PRESERVEGROUNDDEPTH || this.special == PRESERVEGROUNDSURFACE) {
                blockSet = this.buildPreserveGround(world, worldGeneration, true, playSound);
            } else if (this.special == CLEARTREE) {
                blockSet = this.buildClearTree(world, worldGeneration, true, playSound);
            } else if (this.special == CLEARGROUND || this.special == CLEARGROUNDOUTSIDEBUILDING || this.special == CLEARGROUNDBORDER) {
                blockSet = this.buildClearGround(world, worldGeneration, wandimport, true, playSound);
            } else if (this.special == TAPESTRY || this.special == INDIANSTATUE || this.special == MAYANSTATUE || this.special == BYZANTINEICONSMALL || this.special == BYZANTINEICONMEDIUM || this.special == BYZANTINEICONLARGE || this.special == HIDEHANGING || this.special == WALLCARPETSMALL || this.special == WALLCARPETMEDIUM || this.special == WALLCARPETLARGE) {
                blockSet = this.buildPicture(world);
            } else if (this.special == OAKSPAWN || this.special == PINESPAWN || this.special == BIRCHSPAWN || this.special == JUNGLESPAWN || this.special == ACACIASPAWN || this.special == DARKOAKSPAWN || this.special == APPLETREESPAWN || this.special == OLIVETREESPAWN || this.special == PISTACHIOTREESPAWN || this.special == CHERRYTREESPAWN || this.special == SAKURATREESPAWN) {
                blockSet = this.buildTreeSpawn(world, worldGeneration);
            } else if (this.special == SPAWNERSKELETON) {
                WorldUtilities.setBlockAndMetadata(world, this.p, Blocks.field_150474_ac, 0);
                TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner)this.p.getTileEntity(world);
                tileentitymobspawner.func_145881_a().func_190894_a(Mill.ENTITY_SKELETON);
                blockSet = true;
            } else if (this.special == SPAWNERZOMBIE) {
                WorldUtilities.setBlockAndMetadata(world, this.p, Blocks.field_150474_ac, 0);
                TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner)this.p.getTileEntity(world);
                tileentitymobspawner.func_145881_a().func_190894_a(Mill.ENTITY_ZOMBIE);
                blockSet = true;
            } else if (this.special == SPAWNERSPIDER) {
                WorldUtilities.setBlockAndMetadata(world, this.p, Blocks.field_150474_ac, 0);
                TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner)this.p.getTileEntity(world);
                tileentitymobspawner.func_145881_a().func_190894_a(Mill.ENTITY_SPIDER);
                blockSet = true;
            } else if (this.special == SPAWNERCAVESPIDER) {
                WorldUtilities.setBlockAndMetadata(world, this.p, Blocks.field_150474_ac, 0);
                TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner)this.p.getTileEntity(world);
                tileentitymobspawner.func_145881_a().func_190894_a(Mill.ENTITY_CAVESPIDER);
                blockSet = true;
            } else if (this.special == SPAWNERCREEPER) {
                WorldUtilities.setBlockAndMetadata(world, this.p, Blocks.field_150474_ac, 0);
                TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner)this.p.getTileEntity(world);
                tileentitymobspawner.func_145881_a().func_190894_a(new ResourceLocation("creeper"));
                blockSet = true;
            } else if (this.special == SPAWNERBLAZE) {
                WorldUtilities.setBlockAndMetadata(world, this.p, Blocks.field_150474_ac, 0);
                TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner)this.p.getTileEntity(world);
                tileentitymobspawner.func_145881_a().func_190894_a(new ResourceLocation("blaze"));
                blockSet = true;
            } else if (this.special == DISPENDERUNKNOWNPOWDER) {
                WorldUtilities.setBlockAndMetadata(world, this.p, Blocks.field_150367_z, 0);
                TileEntityDispenser dispenser = this.p.getDispenser(world);
                MillCommonUtilities.putItemsInChest((IInventory)dispenser, MillItems.UNKNOWN_POWDER, 2);
                blockSet = true;
            } else if (this.special == FURNACEGUESS) {
                EnumFacing facing = this.guessChestFurnaceFacing(world, this.p);
                IBlockState furnaceBS = Blocks.field_150460_al.func_176223_P().func_177226_a((IProperty)BlockFurnace.field_176447_a, (Comparable)facing);
                world.func_175656_a(this.p.getBlockPos(), furnaceBS);
                blockSet = true;
            } else if (this.special == CHESTGUESS) {
                EnumFacing facing = this.guessChestFurnaceFacing(world, this.p);
                IBlockState chestBS = MillBlocks.LOCKED_CHEST.func_176223_P().func_177226_a((IProperty)BlockChest.field_176459_a, (Comparable)facing);
                world.func_175656_a(this.p.getBlockPos(), chestBS);
                blockSet = true;
            } else if (this.special == TORCHGUESS) {
                BlockTorch blockTorch = (BlockTorch)Blocks.field_150478_aa;
                IBlockState bs = blockTorch.func_180642_a(world, this.p.getBlockPos(), EnumFacing.UP, 0.0f, 0.0f, 0.0f, 0, null);
                world.func_175656_a(this.p.getBlockPos(), bs);
                blockSet = true;
            } else if (this.special == INVERTED_DOOR) {
                world.func_175656_a(this.p.getBlockPos(), this.blockState);
                if (this.blockState.func_177229_b((IProperty)BlockDoor.field_176523_O) == BlockDoor.EnumDoorHalf.LOWER) {
                    IBlockState bs = this.p.getBlockActualState(world).func_177226_a((IProperty)BlockDoor.field_176523_O, (Comparable)BlockDoor.EnumDoorHalf.UPPER);
                    bs = bs.func_177226_a((IProperty)BlockDoor.field_176521_M, (Comparable)BlockDoor.EnumHingePosition.RIGHT);
                    WorldUtilities.setBlockstate(world, this.p.getAbove(), bs, true, playSound);
                }
                blockSet = true;
            }
        }
        catch (Exception e) {
            MillLog.printException("Exception in BuildingBlock.build():", e);
        }
        return blockSet;
    }

    private boolean buildClearGround(World world, boolean worldGeneration, boolean wandimport, boolean notifyBlocks, boolean playSound) {
        IBlockState blockStateBelow;
        boolean shouldSetBlock = false;
        boolean shouldSetBlockBelow = false;
        Block existingBlock = WorldUtilities.getBlock(world, this.p);
        IBlockState targetBlockState = null;
        IBlockState targetBelowBlockState = null;
        if ((!wandimport || existingBlock != Blocks.field_150472_an && existingBlock != MillBlocks.IMPORT_TABLE) && !BlockItemUtilities.isBlockDecorativePlant(existingBlock)) {
            if (this.special == CLEARGROUNDBORDER && !(existingBlock instanceof BlockLeaves) && existingBlock != Blocks.field_150350_a) {
                if (this.p.getEast().getBlock(world) instanceof BlockLiquid || this.p.getWest().getBlock(world) instanceof BlockLiquid || this.p.getNorth().getBlock(world) instanceof BlockLiquid || this.p.getSouth().getBlock(world) instanceof BlockLiquid) {
                    blockStateBelow = WorldUtilities.getBlockState(world, this.p.getBelow());
                    targetBlockState = WorldUtilities.getBlockStateValidGround(blockStateBelow, true);
                    if (targetBlockState == null) {
                        targetBlockState = Blocks.field_150346_d.func_176223_P();
                    }
                    if (existingBlock != targetBlockState.func_177230_c()) {
                        shouldSetBlock = true;
                    }
                } else if (existingBlock != Blocks.field_150350_a) {
                    targetBlockState = Blocks.field_150350_a.func_176223_P();
                    shouldSetBlock = true;
                }
            } else if (existingBlock != Blocks.field_150350_a && (this.special != CLEARGROUNDOUTSIDEBUILDING && this.special != CLEARGROUNDBORDER || !(existingBlock instanceof BlockLeaves))) {
                targetBlockState = Blocks.field_150350_a.func_176223_P();
                shouldSetBlock = true;
            }
        }
        blockStateBelow = WorldUtilities.getBlockState(world, this.p.getBelow());
        targetBelowBlockState = WorldUtilities.getBlockStateValidGround(blockStateBelow, true);
        if (worldGeneration && targetBelowBlockState == Blocks.field_150346_d.func_176223_P()) {
            targetBelowBlockState = Blocks.field_150349_c.func_176223_P();
            shouldSetBlockBelow = true;
        } else if (targetBlockState != null && (targetBlockState != Blocks.field_150346_d.func_176223_P() || blockStateBelow.func_177230_c() != Blocks.field_150349_c)) {
            shouldSetBlock = true;
        }
        if (shouldSetBlock) {
            WorldUtilities.setBlockstate(world, this.p, targetBlockState, notifyBlocks, playSound);
        }
        if (shouldSetBlockBelow) {
            WorldUtilities.setBlockstate(world, this.p.getBelow(), targetBelowBlockState, notifyBlocks, playSound);
        }
        return shouldSetBlock || shouldSetBlockBelow;
    }

    private boolean buildClearTree(World world, boolean worldGeneration, boolean notifyBlocks, boolean playSound) {
        Block block = WorldUtilities.getBlock(world, this.p);
        if (block instanceof BlockLog) {
            WorldUtilities.setBlockAndMetadata(world, this.p, Blocks.field_150350_a, 0, notifyBlocks, playSound);
            IBlockState blockStateBelow = WorldUtilities.getBlockState(world, this.p.getBelow());
            IBlockState targetBlockState = WorldUtilities.getBlockStateValidGround(blockStateBelow, true);
            if (worldGeneration && targetBlockState != null && targetBlockState.func_177230_c() == Blocks.field_150346_d) {
                WorldUtilities.setBlock(world, this.p.getBelow(), (Block)Blocks.field_150349_c, notifyBlocks, playSound);
            } else if (targetBlockState != null && (targetBlockState != Blocks.field_150346_d.func_176223_P() || block != Blocks.field_150349_c)) {
                WorldUtilities.setBlockstate(world, this.p.getBelow(), targetBlockState, notifyBlocks, playSound);
            }
            return true;
        }
        return false;
    }

    private boolean buildNormalBlock(World world, Building townHall, boolean wandimport, boolean notifyBlocks, boolean playSound) {
        boolean blockSet;
        block65: {
            TileEntity bannerEntity;
            Object bannerBlock;
            IBlockState bs;
            EnumFacing facing;
            blockSet = false;
            if (this.block instanceof BlockDoor) {
                if (this.blockState.func_177229_b((IProperty)BlockDoor.field_176523_O) == BlockDoor.EnumDoorHalf.LOWER) {
                    WorldUtilities.setBlockAndMetadata(world, this.p.getAbove(), Blocks.field_150350_a, 0, notifyBlocks, playSound);
                }
            } else if (this.block instanceof BlockBed) {
                facing = (EnumFacing)this.blockState.func_177229_b((IProperty)BlockHorizontal.field_185512_D);
                if (facing == EnumFacing.EAST) {
                    WorldUtilities.setBlockAndMetadata(world, this.p.getWest(), Blocks.field_150350_a, 0, notifyBlocks, playSound);
                } else if (facing == EnumFacing.SOUTH) {
                    WorldUtilities.setBlockAndMetadata(world, this.p.getNorth(), Blocks.field_150350_a, 0, notifyBlocks, playSound);
                } else if (facing == EnumFacing.WEST) {
                    WorldUtilities.setBlockAndMetadata(world, this.p.getEast(), Blocks.field_150350_a, 0, notifyBlocks, playSound);
                } else if (facing == EnumFacing.NORTH) {
                    WorldUtilities.setBlockAndMetadata(world, this.p.getSouth(), Blocks.field_150350_a, 0, notifyBlocks, playSound);
                }
            }
            if (!wandimport || this.block != Blocks.field_150350_a || WorldUtilities.getBlock(world, this.p) != Blocks.field_150472_an) {
                Block existingBlock = WorldUtilities.getBlock(world, this.p);
                if (this.block == Blocks.field_150350_a) {
                    if (!BlockItemUtilities.isBlockDecorativePlant(existingBlock)) {
                        WorldUtilities.setBlockAndMetadata(world, this.p, this.block, this.meta, notifyBlocks, playSound);
                        blockSet = true;
                    }
                } else if (this.block instanceof BlockFlowerPot) {
                    if (this.meta == -1) {
                        this.meta = 0;
                    }
                    WorldUtilities.setBlockstate(world, this.p, this.blockState.func_177226_a((IProperty)BlockFlowerPot.field_176443_b, (Comparable)BlockFlowerPot.EnumFlowerType.values()[this.meta]), notifyBlocks, playSound);
                } else {
                    if (existingBlock instanceof BlockBed) {
                        existingBlock.func_180663_b(world, this.p.getBlockPos(), world.func_180495_p(this.p.getBlockPos()));
                    }
                    if (this.block instanceof BlockBed) {
                        WorldUtilities.setBlockAndMetadata(world, this.p, Blocks.field_150350_a, 0, notifyBlocks, playSound);
                        EnumFacing facing2 = (EnumFacing)this.blockState.func_177229_b((IProperty)BlockHorizontal.field_185512_D);
                        if (facing2 == EnumFacing.EAST) {
                            WorldUtilities.setBlockAndMetadata(world, this.p.getWest(), Blocks.field_150350_a, 0, notifyBlocks, playSound);
                        } else if (facing2 == EnumFacing.SOUTH) {
                            WorldUtilities.setBlockAndMetadata(world, this.p.getNorth(), Blocks.field_150350_a, 0, notifyBlocks, playSound);
                        } else if (facing2 == EnumFacing.WEST) {
                            WorldUtilities.setBlockAndMetadata(world, this.p.getEast(), Blocks.field_150350_a, 0, notifyBlocks, playSound);
                        } else if (facing2 == EnumFacing.NORTH) {
                            WorldUtilities.setBlockAndMetadata(world, this.p.getSouth(), Blocks.field_150350_a, 0, notifyBlocks, playSound);
                        }
                    }
                    if (this.blockState != Blocks.field_150346_d.func_176223_P() || existingBlock != Blocks.field_150349_c) {
                        WorldUtilities.setBlockAndMetadata(world, this.p, this.block, this.meta, notifyBlocks, playSound);
                        blockSet = true;
                    }
                }
            }
            if (this.block instanceof BlockDoor) {
                if (this.blockState.func_177229_b((IProperty)BlockDoor.field_176523_O) == BlockDoor.EnumDoorHalf.LOWER) {
                    bs = this.blockState.func_177226_a((IProperty)BlockDoor.field_176523_O, (Comparable)BlockDoor.EnumDoorHalf.UPPER);
                    if (this.special == INVERTED_DOOR) {
                        bs = bs.func_177226_a((IProperty)BlockDoor.field_176521_M, (Comparable)BlockDoor.EnumHingePosition.RIGHT);
                    }
                    WorldUtilities.setBlockstate(world, this.p.getAbove(), bs, notifyBlocks, playSound);
                }
            } else if (this.block instanceof BlockBed) {
                facing = (EnumFacing)this.blockState.func_177229_b((IProperty)BlockHorizontal.field_185512_D);
                if (facing == EnumFacing.EAST) {
                    WorldUtilities.setBlockAndMetadata(world, this.p.getWest(), this.block, this.meta - 8, notifyBlocks, playSound);
                } else if (facing == EnumFacing.SOUTH) {
                    WorldUtilities.setBlockAndMetadata(world, this.p.getNorth(), this.block, this.meta - 8, notifyBlocks, playSound);
                } else if (facing == EnumFacing.WEST) {
                    WorldUtilities.setBlockAndMetadata(world, this.p.getEast(), this.block, this.meta - 8, notifyBlocks, playSound);
                } else if (facing == EnumFacing.NORTH) {
                    WorldUtilities.setBlockAndMetadata(world, this.p.getSouth(), this.block, this.meta - 8, notifyBlocks, playSound);
                }
            } else if (this.block == Blocks.field_150355_j) {
                world.func_180501_a(this.p.getBlockPos(), Blocks.field_150358_i.func_176223_P(), 11);
            } else if (this.block == Blocks.field_150427_aO) {
                Blocks.field_150427_aO.func_176548_d(world, this.p.getBlockPos());
            } else if (this.block instanceof BlockDoublePlant) {
                bs = this.blockState.func_177226_a((IProperty)BlockDoublePlant.field_176492_b, (Comparable)BlockDoublePlant.EnumBlockHalf.UPPER);
                WorldUtilities.setBlockstate(world, this.p.getAbove(), bs, notifyBlocks, playSound);
            } else if (this.block instanceof BlockFlowerPot) {
                TileEntity te = this.p.getTileEntity(world);
                if (te instanceof TileEntityFlowerPot) {
                    TileEntityFlowerPot teFlowerPot = (TileEntityFlowerPot)te;
                    teFlowerPot.func_190614_a(BlockItemUtilities.getFlowerpotItemStackFromEnum(BlockFlowerPot.EnumFlowerType.values()[this.meta]));
                    teFlowerPot.func_70296_d();
                    world.func_184138_a(this.p.getBlockPos(), this.blockState, this.blockState.func_177226_a((IProperty)BlockFlowerPot.field_176443_b, (Comparable)BlockFlowerPot.EnumFlowerType.values()[this.meta]), 3);
                }
            } else if (this.block instanceof MockBlockBannerHanging) {
                bannerBlock = (MockBlockBannerHanging)this.block;
                bannerEntity = world.func_175625_s(this.p.getBlockPos());
                if (bannerEntity instanceof TileEntityMockBanner) {
                    try {
                        if (townHall == null) {
                            ItemStack bannerStack = ItemMockBanner.makeBanner(Item.func_150898_a((Block)bannerBlock), ItemMockBanner.BANNER_COLOURS[bannerBlock.bannerType], JsonToNBT.func_180713_a((String)ItemMockBanner.BANNER_DESIGNS[bannerBlock.bannerType]));
                            bannerStack.func_190925_c("BlockEntityTag").func_74782_a("Base", (NBTBase)new NBTTagInt(ItemMockBanner.BANNER_COLOURS[bannerBlock.bannerType].func_176767_b()));
                            ((TileEntityMockBanner)bannerEntity).func_175112_a(bannerStack, true);
                            break block65;
                        }
                        if (bannerBlock.bannerType == ItemMockBanner.BANNER_VILLAGE) {
                            ((TileEntityMockBanner)bannerEntity).func_175112_a(townHall.getBannerStack(), true);
                            break block65;
                        }
                        ((TileEntityMockBanner)bannerEntity).func_175112_a(townHall.culture.cultureBannerItemStack, true);
                    }
                    catch (NBTException e) {
                        MillLog.printException(e);
                    }
                }
            } else if (this.block instanceof MockBlockBannerStanding) {
                bannerBlock = (MockBlockBannerStanding)this.block;
                bannerEntity = world.func_175625_s(this.p.getBlockPos());
                if (bannerEntity instanceof TileEntityMockBanner) {
                    try {
                        if (townHall == null) {
                            ItemStack bannerStack = ItemMockBanner.makeBanner(Item.func_150898_a((Block)bannerBlock), ItemMockBanner.BANNER_COLOURS[((MockBlockBannerStanding)((Object)bannerBlock)).bannerType], JsonToNBT.func_180713_a((String)ItemMockBanner.BANNER_DESIGNS[((MockBlockBannerStanding)((Object)bannerBlock)).bannerType]));
                            bannerStack.func_190925_c("BlockEntityTag").func_74782_a("Base", (NBTBase)new NBTTagInt(ItemMockBanner.BANNER_COLOURS[((MockBlockBannerStanding)((Object)bannerBlock)).bannerType].func_176767_b()));
                            ((TileEntityMockBanner)bannerEntity).func_175112_a(bannerStack, true);
                        } else if (((MockBlockBannerStanding)((Object)bannerBlock)).bannerType == ItemMockBanner.BANNER_VILLAGE) {
                            ((TileEntityMockBanner)bannerEntity).func_175112_a(townHall.getBannerStack(), true);
                        } else {
                            ((TileEntityMockBanner)bannerEntity).func_175112_a(townHall.culture.cultureBannerItemStack, true);
                        }
                    }
                    catch (NBTException e) {
                        MillLog.printException(e);
                    }
                }
            }
        }
        return blockSet;
    }

    private boolean buildPicture(World world) {
        EntityWallDecoration art = null;
        if (this.special == TAPESTRY) {
            art = EntityWallDecoration.createWallDecoration(world, this.p, 1);
        } else if (this.special == INDIANSTATUE) {
            art = EntityWallDecoration.createWallDecoration(world, this.p, 2);
        } else if (this.special == MAYANSTATUE) {
            art = EntityWallDecoration.createWallDecoration(world, this.p, 3);
        } else if (this.special == BYZANTINEICONSMALL) {
            art = EntityWallDecoration.createWallDecoration(world, this.p, 4);
        } else if (this.special == BYZANTINEICONMEDIUM) {
            art = EntityWallDecoration.createWallDecoration(world, this.p, 5);
        } else if (this.special == BYZANTINEICONLARGE) {
            art = EntityWallDecoration.createWallDecoration(world, this.p, 6);
        } else if (this.special == HIDEHANGING) {
            art = EntityWallDecoration.createWallDecoration(world, this.p, 7);
        } else if (this.special == WALLCARPETSMALL) {
            art = EntityWallDecoration.createWallDecoration(world, this.p, 8);
        } else if (this.special == WALLCARPETMEDIUM) {
            art = EntityWallDecoration.createWallDecoration(world, this.p, 9);
        } else if (this.special == WALLCARPETLARGE) {
            art = EntityWallDecoration.createWallDecoration(world, this.p, 10);
        }
        if (art.func_70518_d() && !world.field_72995_K) {
            world.func_72838_d((Entity)art);
            return true;
        }
        return false;
    }

    private boolean buildPreserveGround(World world, boolean worldGeneration, boolean notifyBlocks, boolean playSound) {
        Material material;
        boolean surface;
        IBlockState existingBlockState = WorldUtilities.getBlockState(world, this.p);
        boolean bl = surface = this.special == PRESERVEGROUNDSURFACE;
        if (!surface && existingBlockState.func_185913_b() && existingBlockState.func_185917_h() && ((material = existingBlockState.func_185904_a()) == Material.field_151578_c || material == Material.field_151576_e || material == Material.field_151595_p || material == Material.field_151571_B)) {
            return false;
        }
        IBlockState targetGroundBlockState = WorldUtilities.getBlockStateValidGround(existingBlockState, surface);
        if (targetGroundBlockState == null) {
            Point below = this.p.getBelow();
            while (targetGroundBlockState == null && below.getiY() > 0) {
                this.blockState = WorldUtilities.getBlockState(world, below);
                if (WorldUtilities.getBlockStateValidGround(this.blockState, surface) != null) {
                    targetGroundBlockState = WorldUtilities.getBlockStateValidGround(this.blockState, surface);
                }
                below = below.getBelow();
            }
            if (targetGroundBlockState == null) {
                targetGroundBlockState = Blocks.field_150346_d.func_176223_P();
            }
        }
        if (targetGroundBlockState.func_177230_c() == Blocks.field_150346_d && worldGeneration && surface) {
            targetGroundBlockState = Blocks.field_150349_c.func_176223_P();
        }
        if (targetGroundBlockState.func_177230_c() == Blocks.field_150349_c && !worldGeneration) {
            targetGroundBlockState = Blocks.field_150346_d.func_176223_P();
        }
        if (targetGroundBlockState == null || targetGroundBlockState.func_177230_c() == Blocks.field_150350_a) {
            targetGroundBlockState = worldGeneration && surface ? Blocks.field_150349_c.func_176223_P() : Blocks.field_150346_d.func_176223_P();
        }
        if (targetGroundBlockState == existingBlockState) {
            return false;
        }
        if (existingBlockState.func_177230_c() == Blocks.field_150349_c && targetGroundBlockState.func_177230_c() == Blocks.field_150346_d) {
            return false;
        }
        WorldUtilities.setBlockstate(world, this.p, targetGroundBlockState, notifyBlocks, playSound);
        return true;
    }

    private boolean buildTreeSpawn(World world, boolean worldGeneration) {
        if (worldGeneration) {
            Object wg = null;
            if (this.special == OAKSPAWN) {
                wg = new WorldGenTrees(false);
            } else if (this.special == PINESPAWN) {
                wg = new WorldGenTaiga2(false);
            } else if (this.special == BIRCHSPAWN) {
                wg = new WorldGenBirchTree(false, true);
            } else if (this.special == JUNGLESPAWN) {
                IBlockState iblockstate = Blocks.field_150364_r.func_176223_P().func_177226_a((IProperty)BlockOldLog.field_176301_b, (Comparable)BlockPlanks.EnumType.JUNGLE);
                IBlockState iblockstate1 = Blocks.field_150362_t.func_176223_P().func_177226_a((IProperty)BlockOldLeaf.field_176239_P, (Comparable)BlockPlanks.EnumType.JUNGLE).func_177226_a((IProperty)BlockLeaves.field_176236_b, (Comparable)Boolean.valueOf(false));
                wg = new WorldGenTrees(true, 4 + MillCommonUtilities.random.nextInt(7), iblockstate, iblockstate1, false);
            } else if (this.special == ACACIASPAWN) {
                wg = new WorldGenSavannaTree(false);
            } else if (this.special == DARKOAKSPAWN) {
                wg = new WorldGenCanopyTree(true);
            } else if (this.special == APPLETREESPAWN) {
                wg = new WorldGenAppleTree(true);
            } else if (this.special == OLIVETREESPAWN) {
                wg = new WorldGenOliveTree(true);
            } else if (this.special == PISTACHIOTREESPAWN) {
                wg = new WorldGenPistachio(true);
            } else if (this.special == CHERRYTREESPAWN) {
                wg = new WorldGenCherry(true);
            } else if (this.special == SAKURATREESPAWN) {
                wg = new WorldGenSakura(true);
            }
            wg.func_180709_b(world, MillCommonUtilities.random, this.p.getBlockPos());
            return true;
        }
        return false;
    }

    public IBlockState getBlockstate() {
        return this.blockState;
    }

    public byte getMeta() {
        return this.meta;
    }

    private EnumFacing guessChestFurnaceFacing(World world, Point p) {
        IBlockState bsNorth = p.getNorth().getBlockActualState(world);
        IBlockState bsSouth = p.getSouth().getBlockActualState(world);
        IBlockState bsWest = p.getWest().getBlockActualState(world);
        IBlockState bsEast = p.getEast().getBlockActualState(world);
        if (bsNorth.func_185914_p() && bsNorth.func_177230_c() != Blocks.field_150460_al && bsNorth.func_177230_c() != MillBlocks.LOCKED_CHEST && !bsSouth.func_185914_p()) {
            return EnumFacing.SOUTH;
        }
        if (bsSouth.func_185914_p() && bsSouth.func_177230_c() != Blocks.field_150460_al && bsSouth.func_177230_c() != MillBlocks.LOCKED_CHEST && !bsNorth.func_185914_p()) {
            return EnumFacing.NORTH;
        }
        if (bsWest.func_185914_p() && bsWest.func_177230_c() != Blocks.field_150460_al && bsWest.func_177230_c() != MillBlocks.LOCKED_CHEST && !bsEast.func_185914_p()) {
            return EnumFacing.EAST;
        }
        if (bsEast.func_185914_p() && bsEast.func_177230_c() != Blocks.field_150460_al && bsEast.func_177230_c() != MillBlocks.LOCKED_CHEST && !bsWest.func_185914_p()) {
            return EnumFacing.WEST;
        }
        if (!bsSouth.func_185914_p()) {
            return EnumFacing.SOUTH;
        }
        if (!bsNorth.func_185914_p()) {
            return EnumFacing.NORTH;
        }
        if (!bsEast.func_185914_p()) {
            return EnumFacing.EAST;
        }
        if (!bsWest.func_185914_p()) {
            return EnumFacing.WEST;
        }
        return EnumFacing.NORTH;
    }

    public void pathBuild(Building th) {
        IBlockState currentBlockState = this.p.getBlockActualState(th.world);
        if (!BlockItemUtilities.isPath(currentBlockState.func_177230_c()) && PathUtilities.canPathBeBuiltHere(currentBlockState)) {
            this.build(th.world, null, false, false);
        } else if (BlockItemUtilities.isPath(currentBlockState.func_177230_c())) {
            int targetPathLevel = 0;
            IBlockPath bp = (IBlockPath)this.block;
            for (int i = 0; i < th.villageType.pathMaterial.size(); ++i) {
                if (!BlockItemUtilities.isPath(this.block) || th.villageType.pathMaterial.get(i).getBlock() != bp.getSingleSlab() && th.villageType.pathMaterial.get(i).getBlock() != bp.getDoubleSlab()) continue;
                targetPathLevel = i;
            }
            int currentPathLevel = Integer.MAX_VALUE;
            IBlockPath currentPathBlock = (IBlockPath)currentBlockState.func_177230_c();
            for (int i = 0; i < th.villageType.pathMaterial.size(); ++i) {
                if (th.villageType.pathMaterial.get(i).getBlock() != currentPathBlock.getDoubleSlab() && th.villageType.pathMaterial.get(i).getBlock() != currentPathBlock.getSingleSlab()) continue;
                currentPathLevel = i;
            }
            if (currentPathLevel < targetPathLevel) {
                this.build(th.world, null, false, false);
            }
        }
    }

    public void setBlockstate(IBlockState bs) {
        this.blockState = bs;
        this.meta = (byte)this.block.func_176201_c(bs);
    }

    public void setMeta(byte meta) {
        this.meta = meta;
        this.blockState = this.block.func_176203_a((int)meta);
    }

    public String toString() {
        return "(block: " + this.block + " meta: " + this.meta + " pos:" + this.p + ")";
    }
}

