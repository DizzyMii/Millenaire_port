package org.dizzymii.millenaire2.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.culture.Culture;
import org.dizzymii.millenaire2.world.BiomeCultureMapper;
import org.dizzymii.millenaire2.world.MillWorldData;
import org.dizzymii.millenaire2.world.WorldGenVillage;

/**
 * Summoning wand — right-click to generate a Millénaire village at the target position.
 * Selects a culture based on the biome at the target location.
 */
public class ItemSummoningWand extends Item {
    public ItemSummoningWand(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide && level instanceof ServerLevel serverLevel && player instanceof ServerPlayer sp) {
            // Raycast up to 128 blocks
            BlockHitResult hit = level.clip(new ClipContext(
                    player.getEyePosition(1.0F),
                    player.getEyePosition(1.0F).add(player.getLookAngle().scale(128)),
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));

            if (hit.getType() == HitResult.Type.MISS) {
                sp.sendSystemMessage(Component.literal("§6[Millénaire]§r No block in range."));
                return InteractionResultHolder.fail(stack);
            }

            BlockPos targetPos = hit.getBlockPos().above();
            MillWorldData worldData = Millenaire2.getWorldData();
            if (worldData == null) {
                sp.sendSystemMessage(Component.literal("§6[Millénaire]§r World data not loaded."));
                return InteractionResultHolder.fail(stack);
            }

            // Select culture based on biome
            Culture culture = BiomeCultureMapper.selectCulture(serverLevel, targetPos, serverLevel.random);
            if (culture == null) {
                sp.sendSystemMessage(Component.literal("§6[Millénaire]§r No cultures loaded — cannot generate village."));
                return InteractionResultHolder.fail(stack);
            }

            boolean success = WorldGenVillage.generateNewVillage(serverLevel, targetPos, culture, worldData, serverLevel.random);
            if (success) {
                sp.sendSystemMessage(Component.literal(
                        "§6[Millénaire]§a Village generated! §r(" + culture.key + " at "
                                + targetPos.getX() + ", " + targetPos.getY() + ", " + targetPos.getZ() + ")"));
                // Small cooldown
                sp.getCooldowns().addCooldown(this, 40);
            } else {
                sp.sendSystemMessage(Component.literal(
                        "§6[Millénaire]§c Failed to generate village. §r(culture: " + culture.key
                                + " — no valid village type or plan)"));
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
