package org.dizzymii.millenaire2.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemParchment extends Item {

    private final String cultureKey;

    public ItemParchment(Properties props, String cultureKey) {
        super(props);
        this.cultureKey = cultureKey;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            // TODO: Open parchment GUI with culture-specific information
            player.sendSystemMessage(Component.literal(
                    "§6[Millénaire] §rReading parchment: §e" + cultureKey
                    + "§r. GUI not yet implemented."));
            player.getCooldowns().addCooldown(this, 10);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    public String getCultureKey() { return cultureKey; }
}
