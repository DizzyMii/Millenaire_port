package org.dizzymii.millenaire2.item;

import net.minecraft.world.item.Item;

public class ItemPaintBucket extends Item {
    public ItemPaintBucket(Properties props) {
        super(props);
    }

    @Override
    public net.minecraft.world.InteractionResult useOn(net.minecraft.world.item.context.UseOnContext ctx) {
        // Paint bucket recolors Millenaire blocks; logs the action for now
        if (!ctx.getLevel().isClientSide) {
            org.dizzymii.millenaire2.util.MillLog.minor("PaintBucket",
                    "Paint used at " + ctx.getClickedPos() + " by " + (ctx.getPlayer() != null ? ctx.getPlayer().getName().getString() : "unknown"));
        }
        return net.minecraft.world.InteractionResult.sidedSuccess(ctx.getLevel().isClientSide);
    }
}
