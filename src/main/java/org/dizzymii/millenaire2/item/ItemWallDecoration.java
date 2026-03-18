package org.dizzymii.millenaire2.item;

import net.minecraft.world.item.Item;

public class ItemWallDecoration extends Item {
    public ItemWallDecoration(Properties props) {
        super(props);
    }

    @Override
    public net.minecraft.world.InteractionResult useOn(net.minecraft.world.item.context.UseOnContext ctx) {
        net.minecraft.world.level.Level level = ctx.getLevel();
        if (!level.isClientSide) {
            net.minecraft.core.BlockPos pos = ctx.getClickedPos().relative(ctx.getClickedFace());
            org.dizzymii.millenaire2.entity.EntityWallDecoration decoration =
                    new org.dizzymii.millenaire2.entity.EntityWallDecoration(
                            org.dizzymii.millenaire2.entity.MillEntities.WALL_DECORATION.get(), level);
            decoration.moveTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0);
            level.addFreshEntity(decoration);
            ctx.getItemInHand().shrink(1);
        }
        return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide);
    }
}
