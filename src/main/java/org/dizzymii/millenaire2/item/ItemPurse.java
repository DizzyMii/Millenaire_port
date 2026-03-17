package org.dizzymii.millenaire2.item;

import net.minecraft.world.item.Item;

public class ItemPurse extends Item {
    public ItemPurse(Properties props) {
        super(props);
    }

    @Override
    public net.minecraft.world.InteractionResultHolder<net.minecraft.world.item.ItemStack> use(
            net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player,
            net.minecraft.world.InteractionHand hand) {
        net.minecraft.world.item.ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            // Display denier count stored in NBT
            int deniers = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                    net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getInt("deniers");
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "\u00a76[Mill\u00e9naire]\u00a7r Purse contains " + deniers + " deniers"));
        }
        return net.minecraft.world.InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
