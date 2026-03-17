package org.dizzymii.millenaire2.ui;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Container for villager trading.
 * Slot layout: 0 = player offer, 1 = villager offer (output-only), then player inventory.
 */
public class ContainerTrade extends AbstractContainerMenu {

    private static final int TRADE_SLOTS = 2;
    private final Container tradeContainer;

    public ContainerTrade(MenuType<?> type, int containerId, Inventory playerInventory) {
        this(type, containerId, playerInventory, new SimpleContainer(TRADE_SLOTS));
    }

    public ContainerTrade(MenuType<?> type, int containerId, Inventory playerInventory, Container tradeContainer) {
        super(type, containerId);
        this.tradeContainer = tradeContainer;

        // Player offer slot
        this.addSlot(new Slot(tradeContainer, 0, 36, 53));
        // Villager offer slot (output-only)
        this.addSlot(new Slot(tradeContainer, 1, 120, 53) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        // Player inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        // Player hotbar
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();
            if (index < TRADE_SLOTS) {
                if (!this.moveItemStackTo(slotStack, TRADE_SLOTS, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }
}
