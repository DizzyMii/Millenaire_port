package org.dizzymii.millenaire2.item;

import net.minecraft.world.item.Item;

public class ItemBrickMould extends Item {
    public ItemBrickMould(Properties props) {
        super(props);
    }

    @Override
    public net.minecraft.world.InteractionResult useOn(net.minecraft.world.item.context.UseOnContext ctx) {
        net.minecraft.world.level.Level level = ctx.getLevel();
        net.minecraft.core.BlockPos pos = ctx.getClickedPos();
        if (!level.isClientSide && level.getBlockState(pos).is(net.minecraft.world.level.block.Blocks.CLAY)) {
            level.setBlock(pos, net.minecraft.world.level.block.Blocks.BRICKS.defaultBlockState(), 3);
            ctx.getItemInHand().hurtAndBreak(1, ctx.getPlayer(), net.minecraft.world.entity.EquipmentSlot.MAINHAND);
        }
        return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide);
    }
}
