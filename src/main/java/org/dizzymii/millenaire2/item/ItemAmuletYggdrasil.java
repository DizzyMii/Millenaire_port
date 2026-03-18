package org.dizzymii.millenaire2.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ItemAmuletYggdrasil extends Item {
    public ItemAmuletYggdrasil(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            BlockPos center = player.blockPosition();
            int grown = 0;
            for (BlockPos pos : BlockPos.betweenClosed(center.offset(-8, -2, -8), center.offset(8, 4, 8))) {
                BlockState state = level.getBlockState(pos);
                if (state.getBlock() instanceof BonemealableBlock bonemealable) {
                    if (bonemealable.isValidBonemealTarget(level, pos, state)) {
                        bonemealable.performBonemeal(serverLevel, serverLevel.random, pos, state);
                        grown++;
                    }
                }
            }
            if (grown > 0) {
                player.sendSystemMessage(Component.literal(
                        "§6[Millénaire] §rThe Amulet of Yggdrasil nourishes §e" + grown + "§r plants!"));
                stack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(stack));
            } else {
                player.sendSystemMessage(Component.literal(
                        "§6[Millénaire] §rNo plants nearby to nourish."));
            }
            player.getCooldowns().addCooldown(this, 600);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
