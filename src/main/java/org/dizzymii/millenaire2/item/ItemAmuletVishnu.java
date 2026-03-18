package org.dizzymii.millenaire2.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemAmuletVishnu extends Item {
    public ItemAmuletVishnu(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 1));
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 1200, 0));
            player.sendSystemMessage(Component.literal(
                    "§6[Millénaire] §rThe Amulet of Vishnu grants you protection."));
            stack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(stack));
            player.getCooldowns().addCooldown(this, 1200);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
