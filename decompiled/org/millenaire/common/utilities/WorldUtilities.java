/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.item.EntityXPOrb
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntityFurnace
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package org.millenaire.common.utilities;

import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.millenaire.common.entity.TileEntityFirePit;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;

public class WorldUtilities {
    public static boolean checkChunksGenerated(World world, int start_x, int start_z, int end_x, int end_z) {
        start_x >>= 4;
        start_z >>= 4;
        end_x >>= 4;
        end_z >>= 4;
        ++end_x;
        ++end_z;
        for (int k1 = start_x; k1 <= end_x; ++k1) {
            for (int l1 = start_z; l1 <= end_z; ++l1) {
                if (world.func_190526_b(k1, l1)) continue;
                return false;
            }
        }
        return true;
    }

    public static int countBlocksAround(World world, int x, int y, int z, int rx, int ry, int rz) {
        int counter = 0;
        for (int i = x - rx; i <= x + rx; ++i) {
            for (int j = y - ry; j <= y + ry; ++j) {
                for (int k = z - rz; k <= z + rz; ++k) {
                    if (WorldUtilities.getBlock(world, i, j, k) == null || !WorldUtilities.getBlockState(world, i, j, k).func_185904_a().func_76230_c()) continue;
                    ++counter;
                }
            }
        }
        return counter;
    }

    public static Point findRandomStandingPosAround(World world, Point dest) {
        if (dest == null) {
            return null;
        }
        for (int i = 0; i < 50; ++i) {
            Point testdest = dest.getRelative(5 - MillCommonUtilities.randomInt(10), 5 - MillCommonUtilities.randomInt(20), 5 - MillCommonUtilities.randomInt(10));
            if (!BlockItemUtilities.isBlockWalkable(WorldUtilities.getBlock(world, testdest.getiX(), testdest.getiY() - 1, testdest.getiZ())) || BlockItemUtilities.isBlockSolid(WorldUtilities.getBlock(world, testdest.getiX(), testdest.getiY(), testdest.getiZ())) || BlockItemUtilities.isBlockSolid(WorldUtilities.getBlock(world, testdest.getiX(), testdest.getiY() + 1, testdest.getiZ()))) continue;
            return testdest;
        }
        return null;
    }

    public static int findSurfaceBlock(World world, int x, int z) {
        BlockPos pos = new BlockPos(x, world.func_72800_K(), z);
        while (pos.func_177956_o() > -1 && !BlockItemUtilities.isBlockGround(WorldUtilities.getBlock(world, x, pos.func_177956_o(), z)) && !(WorldUtilities.getBlock(world, x, pos.func_177956_o(), z) instanceof BlockLiquid)) {
            pos = new BlockPos(x, pos.func_177956_o() - 1, z);
        }
        if (pos.func_177956_o() > 254) {
            pos = new BlockPos(x, 254, z);
        }
        return pos.func_177956_o() + 1;
    }

    public static Point findTopNonPassableBlock(World world, int x, int z) {
        for (int y = 255; y > 0; --y) {
            if (!WorldUtilities.getBlock(world, x, y, z).func_176223_P().func_185904_a().func_76220_a()) continue;
            return new Point(x, y, z);
        }
        return null;
    }

    public static int findTopSoilBlock(World world, int x, int z) {
        BlockPos pos = world.func_175672_r(new BlockPos(x, 0, z));
        while (pos.func_177956_o() > -1 && !BlockItemUtilities.isBlockGround(WorldUtilities.getBlock(world, x, pos.func_177956_o(), z))) {
            pos = new BlockPos(x, pos.func_177956_o() - 1, z);
        }
        if (pos.func_177956_o() > 254) {
            pos = new BlockPos(x, 254, z);
        }
        return pos.func_177956_o() + 1;
    }

    public static Point findVerticalStandingPos(World world, Point dest) {
        int y;
        if (dest == null) {
            return null;
        }
        for (y = dest.getiY(); y < 250 && (BlockItemUtilities.isBlockSolid(WorldUtilities.getBlock(world, dest.getiX(), y, dest.getiZ())) || BlockItemUtilities.isBlockSolid(WorldUtilities.getBlock(world, dest.getiX(), y + 1, dest.getiZ()))); ++y) {
        }
        while (y > 0 && !BlockItemUtilities.isBlockSolid(WorldUtilities.getBlock(world, dest.getiX(), y - 1, dest.getiZ()))) {
            --y;
        }
        if (y == 250) {
            return null;
        }
        if (!BlockItemUtilities.isBlockWalkable(WorldUtilities.getBlock(world, dest.getiX(), y - 1, dest.getiZ()))) {
            return null;
        }
        return new Point(dest.getiX(), y, dest.getiZ());
    }

    public static Block getBlock(World world, int x, int y, int z) {
        return world.func_180495_p(new BlockPos(x, y, z)).func_177230_c();
    }

    public static Block getBlock(World world, Point p) {
        if (p.x < -3.2E7 || p.z < -3.2E7 || p.x >= 3.2E7 || p.z > 3.2E7) {
            return null;
        }
        if (p.y < 0.0) {
            return null;
        }
        if (p.y >= 256.0) {
            return null;
        }
        return WorldUtilities.getBlock(world, p.getiX(), p.getiY(), p.getiZ());
    }

    public static int getBlockId(Block b) {
        return Block.func_149682_b((Block)b);
    }

    public static int getBlockMeta(World world, int i, int j, int k) {
        return WorldUtilities.getBlockMeta(world, new Point(i, j, k));
    }

    public static int getBlockMeta(World world, Point p) {
        if (p.x < -3.2E7 || p.z < -3.2E7 || p.x >= 3.2E7 || p.z > 3.2E7) {
            return -1;
        }
        if (p.y < 0.0) {
            return -1;
        }
        if (p.y >= 256.0) {
            return -1;
        }
        IBlockState state = p.getBlockActualState(world);
        return state.func_177230_c().func_176201_c(state);
    }

    public static int getBlockMetadata(World world, int x, int y, int z) {
        BlockPos bp = new BlockPos(x, y, z);
        IBlockState bs = world.func_180495_p(bp);
        return bs.func_177230_c().func_176201_c(bs);
    }

    public static IBlockState getBlockState(World world, int x, int y, int z) {
        return world.func_180495_p(new BlockPos(x, y, z));
    }

    public static IBlockState getBlockState(World world, Point p) {
        return world.func_180495_p(new BlockPos(p.x, p.y, p.z));
    }

    public static IBlockState getBlockStateValidGround(IBlockState currentBlockState, boolean surface) {
        Block b = currentBlockState.func_177230_c();
        if (b == Blocks.field_150357_h) {
            return Blocks.field_150346_d.func_176223_P();
        }
        if (b == Blocks.field_150348_b && surface) {
            return Blocks.field_150346_d.func_176223_P();
        }
        if (b == Blocks.field_150348_b && !surface) {
            return currentBlockState;
        }
        if (b == Blocks.field_150346_d) {
            return currentBlockState;
        }
        if (b == Blocks.field_150349_c) {
            return Blocks.field_150346_d.func_176223_P();
        }
        if (b == Blocks.field_150351_n) {
            return currentBlockState;
        }
        if (b == Blocks.field_150354_m) {
            return currentBlockState;
        }
        if (b == Blocks.field_150322_A && surface) {
            return Blocks.field_150354_m.func_176223_P();
        }
        if (b == Blocks.field_150322_A && !surface) {
            return currentBlockState;
        }
        if (b == Blocks.field_150405_ch) {
            return currentBlockState;
        }
        return null;
    }

    public static Point getClosestBlock(World world, Block[] blocks, Point pos, int rx, int ry, int rz) {
        return WorldUtilities.getClosestBlockMeta(world, blocks, -1, pos, rx, ry, rz);
    }

    public static Point getClosestBlockMeta(World world, Block[] blocks, int meta, Point pos, int rx, int ry, int rz) {
        Point closest = null;
        double minDistance = 9.99999999E8;
        for (int i = pos.getiX() - rx; i <= pos.getiX() + rx; ++i) {
            for (int j = pos.getiY() - ry; j <= pos.getiY() + ry; ++j) {
                for (int k = pos.getiZ() - rz; k <= pos.getiZ() + rz; ++k) {
                    for (int l = 0; l < blocks.length; ++l) {
                        if (WorldUtilities.getBlock(world, i, j, k) != blocks[l] || meta != -1 && WorldUtilities.getBlockMeta(world, i, j, k) != meta) continue;
                        Point temp = new Point(i, j, k);
                        if (closest != null && !(temp.distanceTo(pos) < minDistance)) continue;
                        closest = temp;
                        minDistance = closest.distanceTo(pos);
                    }
                }
            }
        }
        if (minDistance < 9.99999999E8) {
            return closest;
        }
        return null;
    }

    public static EntityItem getClosestItemVertical(World world, Point p, List<InvItem> goods, int radius, int vertical) {
        List<Entity> list = WorldUtilities.getEntitiesWithinAABB(world, Entity.class, p, radius, vertical);
        double bestdist = Double.MAX_VALUE;
        EntityItem citem = null;
        for (Entity ent : list) {
            if (ent.getClass() != EntityItem.class) continue;
            EntityItem item = (EntityItem)ent;
            if (item.field_70128_L) continue;
            for (InvItem key : goods) {
                double dist;
                if (item.func_92059_d().func_77973_b() != key.getItem() || item.func_92059_d().func_77952_i() != key.meta || !((dist = item.func_70092_e(p.x, p.y, p.z)) < bestdist)) continue;
                bestdist = dist;
                citem = item;
            }
        }
        if (citem == null) {
            return null;
        }
        return citem;
    }

    public static List<Entity> getEntitiesWithinAABB(World world, Class type, Point p, int hradius, int vradius) {
        AxisAlignedBB area = new AxisAlignedBB(p.x, p.y, p.z, p.x + 1.0, p.y + 1.0, p.z + 1.0).func_72321_a((double)hradius, (double)vradius, (double)hradius).func_72321_a((double)(-hradius), (double)(-vradius), (double)(-hradius));
        return world.func_72872_a(type, area);
    }

    public static List<Entity> getEntitiesWithinAABB(World world, Class type, Point pstart, Point pend) {
        AxisAlignedBB area = new AxisAlignedBB(pstart.x, pstart.y, pstart.z, pend.x, pend.y, pend.z);
        return world.func_72872_a(type, area);
    }

    public static Entity getEntityByUUID(World world, UUID uuid) {
        for (Entity entity : world.func_72910_y()) {
            if (!entity.func_110124_au().equals(uuid)) continue;
            return entity;
        }
        return null;
    }

    public static int getItemsFromChest(IInventory chest, Block block, int meta, int toTake) {
        return WorldUtilities.getItemsFromChest(chest, Item.func_150898_a((Block)block), meta, toTake);
    }

    public static int getItemsFromChest(IInventory chest, IBlockState blockState, int toTake) {
        return WorldUtilities.getItemsFromChest(chest, blockState.func_177230_c(), blockState.func_177230_c().func_176201_c(blockState), toTake);
    }

    public static int getItemsFromChest(IInventory chest, Item item, int meta, int toTake) {
        if (chest == null) {
            return 0;
        }
        int nb = 0;
        int maxSlot = chest.func_70302_i_() - 1;
        if (chest instanceof InventoryPlayer) {
            maxSlot -= 4;
        }
        for (int i = maxSlot; i >= 0 && nb < toTake; --i) {
            ItemStack stack = chest.func_70301_a(i);
            if (stack != null && stack.func_77973_b() == item && (stack.func_77952_i() == meta || meta == -1)) {
                if (stack.func_190916_E() <= toTake - nb) {
                    nb += stack.func_190916_E();
                    chest.func_70299_a(i, ItemStack.field_190927_a);
                } else {
                    chest.func_70298_a(i, toTake - nb);
                    nb = toTake;
                }
            }
            if (item != Item.func_150898_a((Block)Blocks.field_150364_r) || meta != -1 || stack == null || stack.func_77973_b() != Item.func_150898_a((Block)Blocks.field_150363_s)) continue;
            if (stack.func_190916_E() <= toTake - nb) {
                nb += stack.func_190916_E();
                chest.func_70299_a(i, ItemStack.field_190927_a);
                continue;
            }
            chest.func_70298_a(i, toTake - nb);
            nb = toTake;
        }
        return nb;
    }

    public static int getItemsFromFirePit(TileEntityFirePit firepit, Item item, int toTake) {
        if (firepit == null) {
            return 0;
        }
        int taken = 0;
        for (int stackNb = 0; stackNb < 3; ++stackNb) {
            ItemStack stack = firepit.outputs.getStackInSlot(stackNb);
            if (taken >= toTake || stack == null || stack.func_77973_b() != item) continue;
            taken += firepit.outputs.extractItem(stackNb, toTake, false).func_190916_E();
        }
        return taken;
    }

    public static int getItemsFromFurnace(TileEntityFurnace furnace, Item item, int toTake) {
        if (furnace == null) {
            return 0;
        }
        int nb = 0;
        ItemStack stack = furnace.func_70301_a(2);
        if (stack != null && stack.func_77973_b() == item) {
            if (stack.func_190916_E() <= toTake - nb) {
                nb += stack.func_190916_E();
                furnace.func_70299_a(2, ItemStack.field_190927_a);
            } else {
                furnace.func_70298_a(2, toTake - nb);
                nb = toTake;
            }
        }
        return nb;
    }

    public static EnumFacing guessPanelFacing(World world, Point p) {
        boolean northOpen = true;
        boolean southOpen = true;
        boolean eastOpen = true;
        boolean westOpen = true;
        if (WorldUtilities.getBlockState(world, p.getNorth()).func_185913_b()) {
            northOpen = false;
        }
        if (WorldUtilities.getBlockState(world, p.getEast()).func_185913_b()) {
            eastOpen = false;
        }
        if (WorldUtilities.getBlockState(world, p.getSouth()).func_185913_b()) {
            southOpen = false;
        }
        if (WorldUtilities.getBlockState(world, p.getWest()).func_185913_b()) {
            westOpen = false;
        }
        if (!eastOpen) {
            return EnumFacing.WEST;
        }
        if (!westOpen) {
            return EnumFacing.EAST;
        }
        if (!southOpen) {
            return EnumFacing.NORTH;
        }
        if (!northOpen) {
            return EnumFacing.SOUTH;
        }
        return null;
    }

    public static boolean isBlockFullCube(World world, int i, int j, int k) {
        IBlockState bs = WorldUtilities.getBlockState(world, i, j, k);
        if (bs == null) {
            return false;
        }
        return bs.func_185917_h();
    }

    public static void playSound(World world, Point p, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        if (world.field_72995_K) {
            world.func_184134_a((double)((float)p.x + 0.5f), (double)((float)p.y + 0.5f), (double)((float)p.z + 0.5f), sound, category, volume, pitch, false);
        } else {
            world.func_184148_a(null, (double)((float)p.x + 0.5f), (double)((float)p.y + 0.5f), (double)((float)p.z + 0.5f), sound, category, volume, pitch);
        }
    }

    public static void playSoundBlockBreaking(World world, Point p, Block b, float volume) {
        if (b != null && b.func_185467_w() != null) {
            WorldUtilities.playSound(world, p, b.func_185467_w().func_185845_c(), SoundCategory.BLOCKS, b.func_185467_w().func_185843_a() * volume, b.func_185467_w().func_185847_b());
        }
    }

    public static void playSoundBlockPlaced(World world, Point p, Block b, float volume) {
        if (b != null && b.func_185467_w() != null) {
            WorldUtilities.playSound(world, p, b.func_185467_w().func_185841_e(), SoundCategory.BLOCKS, b.func_185467_w().func_185843_a() * volume, b.func_185467_w().func_185847_b());
        }
    }

    public static void playSoundByMillName(World world, Point p, String soundMill, float volume) {
        if (soundMill.equals("metal")) {
            WorldUtilities.playSoundBlockPlaced(world, p, Blocks.field_150339_S, volume);
        } else if (soundMill.equals("wood")) {
            WorldUtilities.playSoundBlockPlaced(world, p, Blocks.field_150364_r, volume);
        } else if (soundMill.equals("wool")) {
            WorldUtilities.playSoundBlockPlaced(world, p, Blocks.field_150325_L, volume);
        } else if (soundMill.equals("glass")) {
            WorldUtilities.playSoundBlockPlaced(world, p, Blocks.field_150359_w, volume);
        } else if (soundMill.equals("stone")) {
            WorldUtilities.playSoundBlockPlaced(world, p, Blocks.field_150348_b, volume);
        } else if (soundMill.equals("earth")) {
            WorldUtilities.playSoundBlockPlaced(world, p, Blocks.field_150346_d, volume);
        } else if (soundMill.equals("sand")) {
            WorldUtilities.playSoundBlockPlaced(world, p, (Block)Blocks.field_150354_m, volume);
        } else {
            MillLog.printException("Tried to play unknown sound: " + soundMill, new Exception());
        }
    }

    public static boolean setBlock(World world, Point p, Block block) {
        return WorldUtilities.setBlock(world, p, block, true, false);
    }

    public static boolean setBlock(World world, Point p, Block block, boolean notify, boolean playSound) {
        Block oldBlock;
        if (p.x < -3.2E7 || p.z < -3.2E7 || p.x >= 3.2E7 || p.z > 3.2E7) {
            return false;
        }
        if (p.y < 0.0) {
            return false;
        }
        if (p.y >= 256.0) {
            return false;
        }
        if (playSound && block == Blocks.field_150350_a && (oldBlock = WorldUtilities.getBlock(world, p.getiX(), p.getiY(), p.getiZ())) != null && oldBlock.func_185467_w() != null) {
            if (oldBlock.func_185467_w() == SoundType.field_185849_b) {
                WorldUtilities.playSoundBlockBreaking(world, p, oldBlock, 0.5f);
            } else {
                WorldUtilities.playSoundBlockBreaking(world, p, oldBlock, 1.0f);
            }
        }
        if (notify) {
            world.func_175656_a(p.getBlockPos(), block.func_176223_P());
        } else {
            world.func_180501_a(p.getBlockPos(), block.func_176223_P(), 2);
        }
        if (playSound && block != Blocks.field_150350_a && block.func_185467_w() != null) {
            if (block.func_185467_w() == SoundType.field_185849_b) {
                WorldUtilities.playSoundBlockBreaking(world, p, block, 0.5f);
            } else {
                WorldUtilities.playSoundBlockBreaking(world, p, block, 1.0f);
            }
        }
        return true;
    }

    public static boolean setBlockAndMetadata(World world, int x, int y, int z, Block block, int metadata, boolean notify, boolean playSound) {
        Block oldBlock;
        if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000) {
            return false;
        }
        if (y < 0) {
            return false;
        }
        if (y >= 256) {
            return false;
        }
        if (playSound && block != Blocks.field_150350_a && (oldBlock = WorldUtilities.getBlock(world, x, y, z)) != null && oldBlock.func_185467_w() != null) {
            WorldUtilities.playSoundBlockBreaking(world, new Point(x, y, z), oldBlock, 1.0f);
        }
        if (block == null) {
            MillLog.printException("Trying to set null block", new Exception());
            return false;
        }
        IBlockState state = block.func_176203_a(metadata);
        if (notify) {
            world.func_175656_a(new BlockPos(x, y, z), state);
        } else {
            world.func_180501_a(new BlockPos(x, y, z), state, 2);
        }
        if (playSound && block != Blocks.field_150350_a && block.func_185467_w() != null) {
            WorldUtilities.playSoundBlockPlaced(world, new Point(x, y, z), block, 2.0f);
        }
        return true;
    }

    public static boolean setBlockAndMetadata(World world, Point p, Block block, int metadata) {
        return WorldUtilities.setBlockAndMetadata(world, p, block, metadata, true, false);
    }

    public static boolean setBlockAndMetadata(World world, Point p, Block block, int metadata, boolean notify, boolean playSound) {
        return WorldUtilities.setBlockAndMetadata(world, p.getiX(), p.getiY(), p.getiZ(), block, metadata, notify, playSound);
    }

    public static boolean setBlockMetadata(World world, int x, int y, int z, int metadata, boolean notify) {
        if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000) {
            return false;
        }
        if (y < 0) {
            return false;
        }
        if (y >= 256) {
            return false;
        }
        Point p = new Point(x, y, z);
        IBlockState state = p.getBlockActualState(world);
        state = state.func_177230_c().func_176203_a(metadata);
        if (notify) {
            world.func_175656_a(p.getBlockPos(), state);
        } else {
            world.func_180501_a(p.getBlockPos(), state, 2);
        }
        return true;
    }

    public static boolean setBlockMetadata(World world, Point p, int metadata) {
        return WorldUtilities.setBlockMetadata(world, p, metadata, true);
    }

    public static boolean setBlockMetadata(World world, Point p, int metadata, boolean notify) {
        return WorldUtilities.setBlockMetadata(world, p.getiX(), p.getiY(), p.getiZ(), metadata, notify);
    }

    public static boolean setBlockstate(World world, Point p, IBlockState bs, boolean b, boolean c) {
        return WorldUtilities.setBlockAndMetadata(world, p, bs.func_177230_c(), bs.func_177230_c().func_176201_c(bs), b, c);
    }

    public static void spawnExp(World world, Point p, int nb) {
        int l;
        if (world.field_72995_K) {
            return;
        }
        for (int j = nb; j > 0; j -= l) {
            l = EntityXPOrb.func_70527_a((int)j);
            world.func_72838_d((Entity)new EntityXPOrb(world, p.x + 0.5, p.y + 5.0, p.z + 0.5, l));
        }
    }

    public static EntityItem spawnItem(World world, Point p, ItemStack itemstack, float f) {
        if (world.field_72995_K) {
            return null;
        }
        EntityItem entityitem = new EntityItem(world, p.x, p.y + (double)f, p.z, itemstack);
        entityitem.func_174869_p();
        world.func_72838_d((Entity)entityitem);
        return entityitem;
    }

    public static void spawnMobsAround(World world, Point p, int radius, ResourceLocation mobType, int minNb, int extraNb) {
        int nb = minNb;
        if (extraNb > 0) {
            nb += MillCommonUtilities.randomInt(extraNb);
        }
        for (int i = 0; i < nb; ++i) {
            EntityLiving entityliving = (EntityLiving)EntityList.func_188429_b((ResourceLocation)mobType, (World)world);
            if (entityliving == null) continue;
            boolean spawned = false;
            for (int j = 0; j < 20 && !spawned; ++j) {
                double ez;
                double ey;
                double ex = p.x + (world.field_73012_v.nextDouble() * 2.0 - 1.0) * (double)radius;
                Point ep = new Point(ex, ey = p.y + (double)world.field_73012_v.nextInt(3) - 1.0, ez = p.z + (world.field_73012_v.nextDouble() * 2.0 - 1.0) * (double)radius);
                if (!ep.getBelow().getBlockActualState(world).func_185898_k()) continue;
                entityliving.func_70012_b(ex, ey, ez, world.field_73012_v.nextFloat() * 360.0f, 0.0f);
                if (!entityliving.func_70601_bi()) continue;
                world.func_72838_d((Entity)entityliving);
                MillLog.major(null, "Entering world: " + entityliving.getClass().getName());
                spawned = true;
            }
            if (!spawned) {
                MillLog.major(null, "No valid space found.");
            }
            entityliving.func_70656_aK();
        }
    }

    public static Entity spawnMobsSpawner(World world, Point p, ResourceLocation mobType) {
        int z;
        int ez;
        int ey;
        EntityLiving entityliving = (EntityLiving)EntityList.func_188429_b((ResourceLocation)mobType, (World)world);
        if (entityliving == null) {
            return null;
        }
        int x = MillCommonUtilities.randomInt(2) - 1;
        int ex = (int)(p.x + (double)x);
        if (WorldUtilities.getBlock(world, ex, ey = (int)p.y, ez = (int)(p.z + (double)(z = MillCommonUtilities.randomInt(2) - 1))) != Blocks.field_150350_a && WorldUtilities.getBlock(world, ex, ey + 1, ez) != Blocks.field_150350_a) {
            return null;
        }
        entityliving.func_70012_b((double)ex, (double)ey, (double)ez, world.field_73012_v.nextFloat() * 360.0f, 0.0f);
        world.func_72838_d((Entity)entityliving);
        entityliving.func_70656_aK();
        return entityliving;
    }
}

