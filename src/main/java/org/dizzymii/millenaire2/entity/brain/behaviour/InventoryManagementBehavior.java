package org.dizzymii.millenaire2.entity.brain.behaviour;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.dizzymii.millenaire2.entity.HumanoidNPC;
import org.dizzymii.millenaire2.entity.brain.ModMemoryTypes;
import org.dizzymii.millenaire2.entity.brain.smartbrain.ExtendedBehaviour;

import java.util.Set;

/**
 * Logistics behaviour that frees inventory space by discarding low-value blocks
 * when inventory usage exceeds 80%.
 */
public class InventoryManagementBehavior extends ExtendedBehaviour<HumanoidNPC> {

    private static final double FULLNESS_THRESHOLD = 0.80D;
    private static final int DROPPED_ITEM_PICKUP_DELAY_TICKS = 20;
    private static final Set<net.minecraft.world.item.Item> LOW_VALUE_ITEMS = Set.of(
            Items.DIRT, Items.COBBLESTONE, Items.GRAVEL
    );

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, HumanoidNPC entity) {
        if (entity.getBrain().getMemory(ModMemoryTypes.LAST_KNOWN_DANGER.get()).isPresent()) return false;
        if (entity.getBrain().getMemory(ModMemoryTypes.NEEDS_HEALING.get()).orElse(false)) return false;
        if (entity.getCarriedInventoryFillRatio() <= FULLNESS_THRESHOLD) return false;
        return findLowValueIndex(entity) >= 0;
    }

    @Override
    protected void start(ServerLevel level, HumanoidNPC entity) {
        int slot = findLowValueIndex(entity);
        if (slot < 0) return;
        ItemStack dropped = entity.removeCarriedInventorySlot(slot);
        if (dropped.isEmpty()) return;

        ItemEntity itemEntity = new ItemEntity(
                level,
                entity.getX(),
                entity.getY() + 0.5D,
                entity.getZ(),
                dropped
        );
        itemEntity.setPickUpDelay(DROPPED_ITEM_PICKUP_DELAY_TICKS);
        level.addFreshEntity(itemEntity);
        entity.pruneEmptyCarriedInventory();
    }

    private int findLowValueIndex(HumanoidNPC entity) {
        for (int i = entity.getCarriedInventory().size() - 1; i >= 0; i--) {
            ItemStack stack = entity.getCarriedInventorySlot(i);
            if (!stack.isEmpty() && LOW_VALUE_ITEMS.contains(stack.getItem())) {
                return i;
            }
        }
        return -1;
    }
}
