package org.dizzymii.millenaire2.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemPurse extends Item {
    public ItemPurse(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            Inventory inv = player.getInventory();
            int deniers = 0;
            int deniersArgent = 0;
            int deniersOr = 0;
            for (int i = 0; i < inv.getContainerSize(); i++) {
                ItemStack slot = inv.getItem(i);
                if (slot.is(MillItems.DENIER.get())) deniers += slot.getCount();
                else if (slot.is(MillItems.DENIER_ARGENT.get())) deniersArgent += slot.getCount();
                else if (slot.is(MillItems.DENIER_OR.get())) deniersOr += slot.getCount();
            }
            int total = deniers + (deniersArgent * 64) + (deniersOr * 4096);
            player.sendSystemMessage(Component.literal(
                    "§6[Millénaire] §rPurse: §e" + deniers + "§r denier, §f" + deniersArgent
                    + "§r denier argent, §6" + deniersOr + "§r denier or (total: §e" + total + "§r denier)"));
            player.getCooldowns().addCooldown(this, 10);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
