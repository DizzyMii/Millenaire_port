package org.dizzymii.millenaire2.item;

import net.minecraft.world.item.Item;

public class ItemAmuletYggdrasil extends Item {
    public ItemAmuletYggdrasil(Properties props) {
        super(props);
    }

    @Override
    public net.minecraft.world.InteractionResult useOn(net.minecraft.world.item.context.UseOnContext ctx) {
        net.minecraft.world.level.Level level = ctx.getLevel();
        if (!level.isClientSide) {
            net.minecraft.core.BlockPos center = ctx.getClickedPos();
            // Grow saplings in a 5x5 area
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    net.minecraft.core.BlockPos pos = center.offset(dx, 1, dz);
                    net.minecraft.world.level.block.state.BlockState state = level.getBlockState(pos);
                    if (state.getBlock() instanceof net.minecraft.world.level.block.SaplingBlock sapling) {
                        sapling.advanceTree((net.minecraft.server.level.ServerLevel) level, pos, state, level.random);
                    }
                }
            }
            if (ctx.getPlayer() != null) {
                ctx.getPlayer().getCooldowns().addCooldown(this, 1200);
                ctx.getItemInHand().hurtAndBreak(1, ctx.getPlayer(), net.minecraft.world.entity.EquipmentSlot.MAINHAND);
            }
        }
        return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide);
    }
}
