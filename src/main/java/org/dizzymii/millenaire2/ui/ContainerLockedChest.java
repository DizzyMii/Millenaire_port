package org.dizzymii.millenaire2.ui;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ContainerLockedChest extends AbstractContainerMenu {

    private static final int CHEST_ROWS = 3;
    private static final int CHEST_COLS = 9;
    private static final int CHEST_SIZE = CHEST_ROWS * CHEST_COLS;

    private final Container chestContainer;

    public ContainerLockedChest(MenuType<?> type, int containerId, Inventory playerInventory) {
        this(type, containerId, playerInventory, new SimpleContainer(CHEST_SIZE));
    }

    public ContainerLockedChest(MenuType<?> type, int containerId, Inventory playerInventory, Container chestContainer) {
        super(type, containerId);
        this.chestContainer = chestContainer;
        chestContainer.startOpen(playerInventory.player);

        // Chest slots
        for (int row = 0; row < CHEST_ROWS; ++row) {
            for (int col = 0; col < CHEST_COLS; ++col) {
                this.addSlot(new Slot(chestContainer, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }

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
        return this.chestContainer.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();
            if (index < CHEST_SIZE) {
                if (!this.moveItemStackTo(slotStack, CHEST_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(slotStack, 0, CHEST_SIZE, false)) {
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

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.chestContainer.stopOpen(player);
    }
}
