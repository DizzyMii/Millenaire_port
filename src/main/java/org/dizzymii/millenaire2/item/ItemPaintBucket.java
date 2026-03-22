package org.dizzymii.millenaire2.item;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import net.minecraft.world.item.Item;

public class ItemPaintBucket extends Item {
    private static final Logger LOGGER = LogUtils.getLogger();
    public ItemPaintBucket(Properties props) {
        super(props);
    }

    @Override
    public net.minecraft.world.InteractionResult useOn(net.minecraft.world.item.context.UseOnContext ctx) {
        // Paint bucket recolors Millenaire blocks; logs the action for now
        if (!ctx.getLevel().isClientSide) {
            org.dizzymii.millenaire2.util.LOGGER.debug("Paint used at " + ctx.getClickedPos() + " by " + (ctx.getPlayer() != null ? ctx.getPlayer().getName().getString() : "unknown"));
        }
        return net.minecraft.world.InteractionResult.sidedSuccess(ctx.getLevel().isClientSide);
    }
}
