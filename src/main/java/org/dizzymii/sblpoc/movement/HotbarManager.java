package org.dizzymii.sblpoc.movement;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import org.dizzymii.sblpoc.PocNpc;

import javax.annotation.Nullable;

/**
 * Manages the NPC's main-hand item like a player manages their hotbar.
 *
 * Automatically swaps to the best tool/weapon for the current task:
 * - Combat: sword (melee) or bow (ranged)
 * - Mining: best pickaxe for the target block
 * - Chopping: best axe
 * - Building: block item to place
 * - Idle: empty hand or sword for safety
 *
 * Swap cooldown prevents unrealistic instant switching.
 */
public class HotbarManager {

    private static final int SWAP_COOLDOWN_TICKS = 6; // ~300ms, realistic hotbar switch time

    private int swapCooldown = 0;
    private HotbarIntent currentIntent = HotbarIntent.IDLE;

    public enum HotbarIntent {
        IDLE,
        MELEE_COMBAT,
        RANGED_COMBAT,
        MINING,
        CHOPPING,
        BUILDING,
        EATING,
        SHIELDING
    }

    /**
     * Tick the hotbar manager. Should be called every server tick.
     */
    public void tick(PocNpc npc) {
        if (swapCooldown > 0) {
            swapCooldown--;
            return;
        }

        ItemStack desired = getDesiredItem(npc, currentIntent);
        if (desired == null) return;

        ItemStack currentMain = npc.getMainHandItem();
        if (currentMain.getItem() == desired.getItem()) return; // Already holding it

        // Find the desired item in inventory and swap
        int slot = findInInventory(npc, desired.getItem());
        if (slot >= 0) {
            swapToSlot(npc, slot);
            swapCooldown = SWAP_COOLDOWN_TICKS;
        }
    }

    /**
     * Request that the NPC switch to an appropriate item for the given intent.
     */
    public void setIntent(HotbarIntent intent) {
        this.currentIntent = intent;
    }

    public HotbarIntent getIntent() {
        return currentIntent;
    }

    public boolean isOnCooldown() {
        return swapCooldown > 0;
    }

    // ========== Internal ==========

    @Nullable
    private ItemStack getDesiredItem(PocNpc npc, HotbarIntent intent) {
        switch (intent) {
            case MELEE_COMBAT:
                return findBestMelee(npc);
            case RANGED_COMBAT:
                return findItem(npc, BowItem.class);
            case MINING:
                return findItem(npc, PickaxeItem.class);
            case CHOPPING:
                return findItem(npc, AxeItem.class);
            case BUILDING:
                return findItem(npc, BlockItem.class);
            case EATING:
                return findFood(npc);
            case SHIELDING:
                return findItem(npc, ShieldItem.class);
            case IDLE:
            default:
                // Prefer sword for safety, else empty
                var sword = findItem(npc, SwordItem.class);
                return sword != null ? sword : ItemStack.EMPTY;
        }
    }

    @Nullable
    private ItemStack findBestMelee(PocNpc npc) {
        // Prefer sword, then axe
        var sword = findItem(npc, SwordItem.class);
        if (sword != null) return sword;
        return findItem(npc, AxeItem.class);
    }

    @Nullable
    private ItemStack findFood(PocNpc npc) {
        // Main hand first
        if (npc.getMainHandItem().getFoodProperties(npc) != null) {
            return npc.getMainHandItem();
        }
        for (int i = 0; i < npc.getInventory().getContainerSize(); i++) {
            ItemStack stack = npc.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.getFoodProperties(npc) != null) {
                return stack;
            }
        }
        return null;
    }

    @Nullable
    private ItemStack findItem(PocNpc npc, Class<? extends Item> type) {
        // Check main hand first
        if (type.isInstance(npc.getMainHandItem().getItem())) {
            return npc.getMainHandItem();
        }
        // Check inventory
        for (int i = 0; i < npc.getInventory().getContainerSize(); i++) {
            ItemStack stack = npc.getInventory().getItem(i);
            if (!stack.isEmpty() && type.isInstance(stack.getItem())) {
                return stack;
            }
        }
        return null;
    }

    private int findInInventory(PocNpc npc, Item item) {
        for (int i = 0; i < npc.getInventory().getContainerSize(); i++) {
            if (npc.getInventory().getItem(i).getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    private void swapToSlot(PocNpc npc, int invSlot) {
        ItemStack fromInv = npc.getInventory().getItem(invSlot).copy();
        ItemStack fromHand = npc.getMainHandItem().copy();

        npc.setItemSlot(EquipmentSlot.MAINHAND, fromInv);
        npc.getInventory().setItem(invSlot, fromHand);
        npc.getInventoryModel().markDirty();
    }
}
