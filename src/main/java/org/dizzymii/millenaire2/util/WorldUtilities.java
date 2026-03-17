package org.dizzymii.millenaire2.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * World interaction utilities — block access, inventory manipulation, entity search.
 * Ported from org.millenaire.common.utilities.WorldUtilities (Forge 1.12.2).
 */
public final class WorldUtilities {

    private WorldUtilities() {}

    public static boolean checkChunksGenerated(Level level, int startX, int startZ, int endX, int endZ) {
        int sx = startX >> 4, sz = startZ >> 4;
        int ex = (endX >> 4) + 1, ez = (endZ >> 4) + 1;
        for (int cx = sx; cx <= ex; cx++) {
            for (int cz = sz; cz <= ez; cz++) {
                if (!level.hasChunk(cx, cz)) return false;
            }
        }
        return true;
    }

    @Nullable
    public static Block getBlock(Level level, int x, int y, int z) {
        BlockState state = level.getBlockState(new BlockPos(x, y, z));
        return state.getBlock();
    }

    public static BlockState getBlockState(Level level, int x, int y, int z) {
        return level.getBlockState(new BlockPos(x, y, z));
    }

    public static int countBlocksAround(Level level, int x, int y, int z, int rx, int ry, int rz) {
        int count = 0;
        for (int i = x - rx; i <= x + rx; i++) {
            for (int j = y - ry; j <= y + ry; j++) {
                for (int k = z - rz; k <= z + rz; k++) {
                    BlockState state = level.getBlockState(new BlockPos(i, j, k));
                    if (state.isSolid()) count++;
                }
            }
        }
        return count;
    }

    @Nullable
    public static Point findRandomStandingPosAround(Level level, Point dest) {
        if (dest == null) return null;
        for (int i = 0; i < 50; i++) {
            Point test = dest.getRelative(
                    5 - MillCommonUtilities.randomInt(10),
                    5 - MillCommonUtilities.randomInt(20),
                    5 - MillCommonUtilities.randomInt(10)
            );
            BlockPos below = new BlockPos(test.x, test.y - 1, test.z);
            BlockPos at = new BlockPos(test.x, test.y, test.z);
            BlockPos above = new BlockPos(test.x, test.y + 1, test.z);
            if (BlockItemUtilities.isBlockWalkable(level.getBlockState(below).getBlock())
                    && !BlockItemUtilities.isBlockSolid(level.getBlockState(at).getBlock())
                    && !BlockItemUtilities.isBlockSolid(level.getBlockState(above).getBlock())) {
                return test;
            }
        }
        return null;
    }

    public static int countItemsInContainer(Container container, Item item) {
        if (container == null) return 0;
        int count = 0;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && stack.getItem() == item) {
                count += stack.getCount();
            }
        }
        return count;
    }

    public static int getItemsFromContainer(Container container, Item item, int maxToTake) {
        if (container == null) return 0;
        int taken = 0;
        for (int i = 0; i < container.getContainerSize() && taken < maxToTake; i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && stack.getItem() == item) {
                int take = Math.min(stack.getCount(), maxToTake - taken);
                stack.shrink(take);
                if (stack.isEmpty()) container.setItem(i, ItemStack.EMPTY);
                taken += take;
            }
        }
        return taken;
    }

    public static int putItemsInContainer(Container container, ItemStack toInsert) {
        if (container == null || toInsert.isEmpty()) return 0;
        int inserted = 0;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack slot = container.getItem(i);
            if (slot.isEmpty()) {
                int amount = Math.min(toInsert.getCount(), container.getMaxStackSize());
                container.setItem(i, toInsert.split(amount));
                inserted += amount;
            } else if (ItemStack.isSameItemSameComponents(slot, toInsert)) {
                int space = slot.getMaxStackSize() - slot.getCount();
                int amount = Math.min(toInsert.getCount(), space);
                if (amount > 0) {
                    slot.grow(amount);
                    toInsert.shrink(amount);
                    inserted += amount;
                }
            }
            if (toInsert.isEmpty()) break;
        }
        return inserted;
    }

    public static void spawnItemEntity(Level level, BlockPos pos, ItemStack stack) {
        if (!level.isClientSide && !stack.isEmpty()) {
            net.minecraft.world.entity.item.ItemEntity entity = new net.minecraft.world.entity.item.ItemEntity(
                    level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
            entity.setDefaultPickUpDelay();
            level.addFreshEntity(entity);
        }
    }

    public static int getTopSolidBlock(Level level, int x, int z) {
        for (int y = level.getMaxBuildHeight() - 1; y >= level.getMinBuildHeight(); y--) {
            BlockState state = level.getBlockState(new BlockPos(x, y, z));
            if (state.isSolid()) return y;
        }
        return level.getMinBuildHeight();
    }

    public static void playBlockSound(Level level, BlockPos pos, net.minecraft.sounds.SoundEvent sound) {
        level.playSound(null, pos, sound, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}
