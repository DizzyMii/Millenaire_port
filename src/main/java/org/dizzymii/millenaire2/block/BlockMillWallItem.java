package org.dizzymii.millenaire2.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Item form of Millenaire wall blocks with variant support.
 * Ported from net.minecraft.block.BlockMillWallItem (Forge 1.12.2).
 */
public class BlockMillWallItem extends BlockItem {

    @Nullable
    private final String variantKey;

    public BlockMillWallItem(Block block, Properties props) {
        this(block, props, null);
    }

    public BlockMillWallItem(Block block, Properties props, @Nullable String variantKey) {
        super(block, props);
        this.variantKey = variantKey;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, ctx, tooltip, flag);
        if (variantKey != null) {
            tooltip.add(Component.translatable("millenaire2.wall.variant." + variantKey).withStyle(net.minecraft.ChatFormatting.GRAY));
        }
    }
}
