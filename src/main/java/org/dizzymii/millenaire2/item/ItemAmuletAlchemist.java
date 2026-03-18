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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.dizzymii.millenaire2.entity.MillEntities;

public class ItemAmuletAlchemist extends Item {
    public ItemAmuletAlchemist(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            HitResult hit = player.pick(64.0, 0.0F, false);
            if (hit.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = ((BlockHitResult) hit).getBlockPos().above();
                var blaze = MillEntities.TARGETED_BLAZE.get().create(serverLevel);
                if (blaze != null) {
                    blaze.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
                    serverLevel.addFreshEntity(blaze);
                    player.sendSystemMessage(Component.literal(
                            "§6[Millénaire] §rThe Alchemist's Amulet summons a blaze!"));
                    stack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(stack));
                    player.getCooldowns().addCooldown(this, 600);
                }
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
