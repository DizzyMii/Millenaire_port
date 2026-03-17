package org.dizzymii.millenaire2.item;

import net.minecraft.world.item.Item;

public class ItemAmuletAlchemist extends Item {
    public ItemAmuletAlchemist(Properties props) {
        super(props);
    }

    @Override
    public net.minecraft.world.InteractionResultHolder<net.minecraft.world.item.ItemStack> use(
            net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player,
            net.minecraft.world.InteractionHand hand) {
        net.minecraft.world.item.ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            net.minecraft.world.phys.Vec3 look = player.getLookAngle();
            double spawnX = player.getX() + look.x * 10;
            double spawnY = player.getY() + look.y * 10 + 1;
            double spawnZ = player.getZ() + look.z * 10;
            net.minecraft.world.entity.monster.Blaze blaze = net.minecraft.world.entity.EntityType.BLAZE.create(level);
            if (blaze != null) {
                blaze.moveTo(spawnX, spawnY, spawnZ, 0, 0);
                level.addFreshEntity(blaze);
            }
            player.getCooldowns().addCooldown(this, 200);
            stack.hurtAndBreak(1, player, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
        }
        return net.minecraft.world.InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
