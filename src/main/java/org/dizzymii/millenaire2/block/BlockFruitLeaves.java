package org.dizzymii.millenaire2.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Leaves block that occasionally drops fruit items (apple, olive, cherry).
 * The specific fruit type is determined by loot tables configured per block variant.
 * This class provides a bonus random-tick fruit drop on top of standard loot.
 */
public class BlockFruitLeaves extends LeavesBlock {
    public BlockFruitLeaves(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
        // 5% chance per random tick to drop a fruit item
        if (random.nextFloat() < 0.05F) {
            popResource(level, pos, new ItemStack(Items.APPLE));
        }
    }
}
