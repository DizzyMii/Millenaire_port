package org.dizzymii.millenaire2.item;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class ItemUlu extends SwordItem {
    public ItemUlu(Tier tier, Properties props) {
        super(tier, props);
    }

    @Override
    public net.minecraft.world.InteractionResult useOn(net.minecraft.world.item.context.UseOnContext ctx) {
        net.minecraft.world.level.Level level = ctx.getLevel();
        net.minecraft.core.BlockPos pos = ctx.getClickedPos();
        net.minecraft.world.level.block.state.BlockState state = level.getBlockState(pos);
        // Harvest mature crops like a knife
        if (state.getBlock() instanceof net.minecraft.world.level.block.CropBlock crop && crop.isMaxAge(state)) {
            if (!level.isClientSide) {
                level.destroyBlock(pos, true, ctx.getPlayer());
                ctx.getItemInHand().hurtAndBreak(1, ctx.getPlayer(), net.minecraft.world.entity.EquipmentSlot.MAINHAND);
            }
            return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useOn(ctx);
    }
}
