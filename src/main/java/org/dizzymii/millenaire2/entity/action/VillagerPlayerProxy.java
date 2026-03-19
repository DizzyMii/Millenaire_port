package org.dizzymii.millenaire2.entity.action;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.VillagerActionRuntime;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class VillagerPlayerProxy {

    private VillagerPlayerProxy() {}

    public static VillagerActionRuntime.Result useBlock(MillVillager villager, BlockPos pos, Direction face, InteractionHand hand) {
        FakePlayer fakePlayer = prepare(villager, hand);
        if (fakePlayer == null) {
            return VillagerActionRuntime.Result.failure("player_proxy_unavailable", true);
        }
        BlockHitResult hitResult = new BlockHitResult(Vec3.atCenterOf(pos), face, pos, false);
        ItemStack held = fakePlayer.getItemInHand(hand);
        InteractionResult interactionResult = held.isEmpty()
                ? villager.level().getBlockState(pos).useWithoutItem(villager.level(), fakePlayer, hitResult)
                : held.useOn(new UseOnContext(fakePlayer, hand, hitResult));
        fakePlayer.setItemInHand(hand, held);
        writeBack(villager, fakePlayer, hand);
        return mapResult(interactionResult, "use_block");
    }

    public static VillagerActionRuntime.Result breakBlock(MillVillager villager, BlockPos pos) {
        if (villager.level().getBlockState(pos).isAir()) {
            return VillagerActionRuntime.Result.success("break_block");
        }
        FakePlayer fakePlayer = prepare(villager, InteractionHand.MAIN_HAND);
        if (fakePlayer == null) {
            return VillagerActionRuntime.Result.failure("player_proxy_unavailable", true);
        }
        boolean destroyed = fakePlayer.gameMode.destroyBlock(pos);
        writeBack(villager, fakePlayer, InteractionHand.MAIN_HAND);
        if (destroyed) {
            villager.swing(InteractionHand.MAIN_HAND);
            return VillagerActionRuntime.Result.success("break_block");
        }
        return VillagerActionRuntime.Result.failure("break_block_failed", true);
    }

    public static VillagerActionRuntime.Result interactEntity(MillVillager villager, Entity entity, InteractionHand hand) {
        FakePlayer fakePlayer = prepare(villager, hand);
        if (fakePlayer == null) {
            return VillagerActionRuntime.Result.failure("player_proxy_unavailable", true);
        }
        ItemStack held = fakePlayer.getItemInHand(hand);
        InteractionResult interactionResult = entity.interact(fakePlayer, hand);
        if (!interactionResult.consumesAction() && entity instanceof LivingEntity livingEntity && !held.isEmpty()) {
            interactionResult = held.interactLivingEntity(fakePlayer, livingEntity, hand);
        }
        fakePlayer.setItemInHand(hand, held);
        writeBack(villager, fakePlayer, hand);
        return mapResult(interactionResult, "interact_entity");
    }

    @Nullable
    private static FakePlayer prepare(MillVillager villager, InteractionHand hand) {
        if (!(villager.level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        FakePlayer fakePlayer = FakePlayerFactory.get(serverLevel, profileFor(villager));
        fakePlayer.moveTo(villager.getX(), villager.getY(), villager.getZ(), villager.getYRot(), villager.getXRot());
        fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        fakePlayer.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
        fakePlayer.setItemInHand(hand, villager.getSelectedInventoryItem().copy());
        return fakePlayer;
    }

    private static void writeBack(MillVillager villager, FakePlayer fakePlayer, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) {
            return;
        }
        int slot = villager.getSelectedInventorySlot();
        if (slot >= 0) {
            villager.getInventoryContainer().setItem(slot, fakePlayer.getItemInHand(hand).copy());
        }
        villager.syncSelectedItemToHands();
    }

    private static GameProfile profileFor(MillVillager villager) {
        String id = villager.getStringUUID();
        String name = "mill_" + id.replace("-", "").substring(0, 11);
        UUID uuid = UUID.nameUUIDFromBytes(("millenaire2:" + id).getBytes(StandardCharsets.UTF_8));
        return new GameProfile(uuid, name);
    }

    private static VillagerActionRuntime.Result mapResult(InteractionResult interactionResult, String actionKey) {
        if (interactionResult.consumesAction()) {
            return VillagerActionRuntime.Result.success(actionKey);
        }
        return interactionResult == InteractionResult.FAIL
                ? VillagerActionRuntime.Result.failure(actionKey, false)
                : VillagerActionRuntime.Result.failure(actionKey, true);
    }
}
