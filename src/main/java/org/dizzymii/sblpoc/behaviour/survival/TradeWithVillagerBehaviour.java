package org.dizzymii.sblpoc.behaviour.survival;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.phys.AABB;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.PocNpc;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Finds the nearest villager, approaches them, and executes the best available trade.
 * Prioritizes trades where the NPC has the input items and wants the output.
 */
public class TradeWithVillagerBehaviour extends ExtendedBehaviour<PocNpc> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT)
            );

    private static final double SEARCH_RANGE = 32.0;
    private static final double INTERACT_RANGE_SQ = 3.0 * 3.0;
    private static final int MAX_TRADES = 3;

    @Nullable private AbstractVillager villager;
    private boolean navigating = false;
    private int tradesCompleted = 0;
    private int ticksRunning = 0;
    private static final int MAX_TICKS = 300;

    public TradeWithVillagerBehaviour() {
        noTimeout();
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PocNpc npc) {
        villager = findNearestVillager(level, npc);
        return villager != null;
    }

    @Override
    protected void start(ServerLevel level, PocNpc npc, long gameTime) {
        tradesCompleted = 0;
        ticksRunning = 0;
        navigating = false;

        if (villager == null) return;

        double distSq = npc.distanceToSqr(villager);
        if (distSq > INTERACT_RANGE_SQ) {
            BrainUtils.setMemory(npc, MemoryModuleType.WALK_TARGET,
                    new WalkTarget(villager.position(), 1.0f, 1));
            navigating = true;
        }
    }

    @Override
    protected void tick(ServerLevel level, PocNpc npc, long gameTime) {
        ticksRunning++;

        if (villager == null || !villager.isAlive() || ticksRunning > MAX_TICKS) {
            doStop(level, npc, gameTime);
            return;
        }

        if (navigating) {
            double distSq = npc.distanceToSqr(villager);
            if (distSq <= INTERACT_RANGE_SQ) {
                navigating = false;
            } else {
                if (ticksRunning % 20 == 0) {
                    BrainUtils.setMemory(npc, MemoryModuleType.WALK_TARGET,
                            new WalkTarget(villager.position(), 1.0f, 1));
                }
                return;
            }
        }

        npc.getLookControl().setLookAt(villager);

        // Find and execute a trade
        MerchantOffer bestOffer = findBestTrade(npc);
        if (bestOffer == null || bestOffer.isOutOfStock()) {
            doStop(level, npc, gameTime);
            return;
        }

        // Consume cost items
        ItemStack costA = bestOffer.getCostA();
        ItemStack costB = bestOffer.getCostB();

        if (!consumeItems(npc, costA)) {
            doStop(level, npc, gameTime);
            return;
        }
        if (!costB.isEmpty()) {
            if (!consumeItems(npc, costB)) {
                doStop(level, npc, gameTime);
                return;
            }
        }

        // Get result
        ItemStack result = bestOffer.getResult().copy();
        addToInventory(npc, result);

        // Update the trade
        bestOffer.increaseUses();
        npc.swing(InteractionHand.MAIN_HAND);
        npc.getInventoryModel().markDirty();
        tradesCompleted++;

        if (tradesCompleted >= MAX_TRADES) {
            doStop(level, npc, gameTime);
        }
    }

    @Override
    protected boolean shouldKeepRunning(PocNpc npc) {
        return villager != null && villager.isAlive() && tradesCompleted < MAX_TRADES && ticksRunning < MAX_TICKS;
    }

    @Override
    protected void stop(ServerLevel level, PocNpc npc, long gameTime) {
        villager = null;
    }

    @Nullable
    private MerchantOffer findBestTrade(PocNpc npc) {
        if (villager == null) return null;

        for (MerchantOffer offer : villager.getOffers()) {
            if (offer.isOutOfStock()) continue;

            ItemStack costA = offer.getCostA();
            ItemStack costB = offer.getCostB();

            if (npc.getInventoryModel().countItem(costA.getItem()) >= costA.getCount()) {
                if (costB.isEmpty() || npc.getInventoryModel().countItem(costB.getItem()) >= costB.getCount()) {
                    return offer;
                }
            }
        }
        return null;
    }

    private boolean consumeItems(PocNpc npc, ItemStack required) {
        int remaining = required.getCount();
        for (int i = 0; i < npc.getInventory().getContainerSize() && remaining > 0; i++) {
            ItemStack slot = npc.getInventory().getItem(i);
            if (slot.getItem() == required.getItem()) {
                int take = Math.min(remaining, slot.getCount());
                npc.getInventory().removeItem(i, take);
                remaining -= take;
            }
        }
        return remaining <= 0;
    }

    private void addToInventory(PocNpc npc, ItemStack stack) {
        for (int i = 0; i < npc.getInventory().getContainerSize(); i++) {
            ItemStack slot = npc.getInventory().getItem(i);
            if (slot.isEmpty()) {
                npc.getInventory().setItem(i, stack);
                return;
            } else if (ItemStack.isSameItemSameComponents(slot, stack)
                    && slot.getCount() + stack.getCount() <= slot.getMaxStackSize()) {
                slot.grow(stack.getCount());
                return;
            }
        }
        // Inventory full, drop
        npc.spawnAtLocation(stack);
    }

    @Nullable
    private static AbstractVillager findNearestVillager(ServerLevel level, PocNpc npc) {
        AABB searchBox = npc.getBoundingBox().inflate(SEARCH_RANGE);
        List<Entity> entities = level.getEntities(npc, searchBox,
                e -> e instanceof AbstractVillager && e.isAlive());

        Entity nearest = null;
        double nearestDist = Double.MAX_VALUE;
        for (Entity e : entities) {
            double dist = npc.distanceToSqr(e);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = e;
            }
        }
        return nearest instanceof AbstractVillager av ? av : null;
    }
}
