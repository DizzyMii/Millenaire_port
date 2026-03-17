package org.dizzymii.millenaire2.item;

import net.minecraft.world.item.Item;

public class ItemMillSeeds extends Item {
    public ItemMillSeeds(Properties props) {
        super(props);
    }

    @Override
    public net.minecraft.world.InteractionResult useOn(net.minecraft.world.item.context.UseOnContext ctx) {
        net.minecraft.world.level.Level level = ctx.getLevel();
        net.minecraft.core.BlockPos pos = ctx.getClickedPos();
        // Plant on farmland
        if (level.getBlockState(pos).is(net.minecraft.world.level.block.Blocks.FARMLAND)
                && level.getBlockState(pos.above()).isAir()) {
            if (!level.isClientSide) {
                // Place crop block above farmland (default to wheat for now)
                level.setBlock(pos.above(), net.minecraft.world.level.block.Blocks.WHEAT.defaultBlockState(), 3);
                ctx.getItemInHand().shrink(1);
            }
            return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useOn(ctx);
    }
}
