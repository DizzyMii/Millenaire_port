package org.dizzymii.millenaire2.item;

import net.minecraft.world.item.Item;

public class ItemParchment extends Item {
    public ItemParchment(Properties props) {
        super(props);
    }

    @Override
    public net.minecraft.world.InteractionResultHolder<net.minecraft.world.item.ItemStack> use(
            net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player,
            net.minecraft.world.InteractionHand hand) {
        net.minecraft.world.item.ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof net.minecraft.server.level.ServerPlayer sp) {
            org.dizzymii.millenaire2.network.ServerPacketSender.sendOpenGui(
                    sp, org.dizzymii.millenaire2.network.MillPacketIds.GUI_VILLAGE_BOOK, 0, null);
        }
        return net.minecraft.world.InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
