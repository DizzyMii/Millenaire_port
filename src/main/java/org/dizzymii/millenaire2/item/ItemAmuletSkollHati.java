package org.dizzymii.millenaire2.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemAmuletSkollHati extends Item {
    public ItemAmuletSkollHati(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 2; i++) {
                Wolf wolf = EntityType.WOLF.create(serverLevel);
                if (wolf != null) {
                    double offsetX = (level.random.nextDouble() - 0.5) * 4;
                    double offsetZ = (level.random.nextDouble() - 0.5) * 4;
                    wolf.moveTo(player.getX() + offsetX, player.getY(), player.getZ() + offsetZ, 0, 0);
                    wolf.tame(player);
                    serverLevel.addFreshEntity(wolf);
                }
            }
            player.sendSystemMessage(Component.literal(
                    "§6[Millénaire] §rSkoll and Hati answer your call!"));
            stack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(stack));
            player.getCooldowns().addCooldown(this, 2400);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
