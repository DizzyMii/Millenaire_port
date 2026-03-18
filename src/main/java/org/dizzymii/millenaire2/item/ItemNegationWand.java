package org.dizzymii.millenaire2.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class ItemNegationWand extends Item {
    public ItemNegationWand(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            HitResult hit = player.pick(128.0, 0.0F, false);
            if (hit.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = ((BlockHitResult) hit).getBlockPos();
                // TODO: Replace with actual village negation via MillWorldData
                player.sendSystemMessage(Component.literal(
                        "§6[Millénaire] §rNegation wand activated at " + pos.toShortString()
                        + ". Village prevention not yet implemented."));
            } else {
                player.sendSystemMessage(Component.literal(
                        "§6[Millénaire] §rPoint the wand at the ground to prevent village generation."));
            }
            player.getCooldowns().addCooldown(this, 20);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
