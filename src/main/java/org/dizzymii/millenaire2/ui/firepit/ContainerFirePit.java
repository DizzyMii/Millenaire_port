package org.dizzymii.millenaire2.ui.firepit;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Legacy fire pit container in ui/ package.
 * The canonical implementation is in menu/FirePitMenu.
 * Slot 0 = input, Slot 1 = fuel, Slot 2 = output.
 */
public class ContainerFirePit extends AbstractContainerMenu {

    private static final int FIREPIT_SLOTS = 3;
    private final Container container;

    public ContainerFirePit(MenuType<?> type, int containerId, Inventory playerInventory) {
        this(type, containerId, playerInventory, new SimpleContainer(FIREPIT_SLOTS));
    }

    public ContainerFirePit(MenuType<?> type, int containerId, Inventory playerInventory, Container container) {
        super(type, containerId);
        this.container = container;

        // Input slot
        this.addSlot(new SlotFirePitInput(container, 0, 56, 17));
        // Fuel slot
        this.addSlot(new SlotFirePitFuel(container, 1, 56, 53));
        // Output slot
        this.addSlot(new SlotFirePitOutput(container, 2, 116, 35));

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
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();
            if (index < FIREPIT_SLOTS) {
                if (!this.moveItemStackTo(slotStack, FIREPIT_SLOTS, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(slotStack, 0, 2, false)) {
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
