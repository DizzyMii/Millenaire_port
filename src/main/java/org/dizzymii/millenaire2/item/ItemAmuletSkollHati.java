package org.dizzymii.millenaire2.item;

import net.minecraft.world.item.Item;

public class ItemAmuletSkollHati extends Item {
    public ItemAmuletSkollHati(Properties props) {
        super(props);
    }

    @Override
    public net.minecraft.world.InteractionResultHolder<net.minecraft.world.item.ItemStack> use(
            net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player,
            net.minecraft.world.InteractionHand hand) {
        net.minecraft.world.item.ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            for (int i = 0; i < 2; i++) {
                net.minecraft.world.entity.animal.Wolf wolf = net.minecraft.world.entity.EntityType.WOLF.create(level);
                if (wolf != null) {
                    wolf.moveTo(player.getX() + (i == 0 ? -1 : 1), player.getY(), player.getZ(), 0, 0);
                    wolf.tame(player);
                    level.addFreshEntity(wolf);
                }
            }
            player.getCooldowns().addCooldown(this, 6000);
            stack.hurtAndBreak(1, player, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
        }
        return net.minecraft.world.InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
