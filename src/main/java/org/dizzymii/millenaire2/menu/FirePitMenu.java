package org.dizzymii.millenaire2.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.dizzymii.millenaire2.init.ModMenuTypes;

/**
 * Container menu for the Fire Pit.
 * Slot 0 = input, Slot 1 = fuel, Slot 2 = output.
 * Data slots: 0 = burnTime, 1 = burnTimeTotal, 2 = cookTime, 3 = cookTimeTotal.
 */
public class FirePitMenu extends AbstractContainerMenu {

    private final Container container;
    private final ContainerData data;

    // Server-side constructor (from block entity)
    public FirePitMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(ModMenuTypes.FIRE_PIT.get(), containerId);
        checkContainerSize(container, 3);
        checkContainerDataCount(data, 4);
        this.container = container;
        this.data = data;

        // Input slot (0)
        this.addSlot(new Slot(container, 0, 56, 17));
        // Fuel slot (1)
        this.addSlot(new FuelSlot(container, 1, 56, 53));
        // Output slot (2)
        this.addSlot(new OutputSlot(container, 2, 116, 35));

        // Player inventory (9-35)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        // Player hotbar (0-8)
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        addDataSlots(data);
    }

    // Client-side constructor (from network)
    public FirePitMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, new SimpleContainer(3), new SimpleContainerData(4));
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    public boolean isBurning() {
        return data.get(0) > 0;
    }

    public int getBurnProgress() {
        int burnTime = data.get(0);
        int burnTimeTotal = data.get(1);
        return burnTimeTotal != 0 && burnTime != 0 ? burnTime * 13 / burnTimeTotal : 0;
    }

    public int getCookProgress() {
        int cookTime = data.get(2);
        int cookTimeTotal = data.get(3);
        return cookTimeTotal != 0 && cookTime != 0 ? cookTime * 24 / cookTimeTotal : 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();
            if (index == 2) {
                // Output slot -> player inventory
                if (!this.moveItemStackTo(slotStack, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(slotStack, result);
            } else if (index != 0 && index != 1) {
                // Player inventory -> fire pit slots
                if (isFuel(slotStack)) {
                    if (!this.moveItemStackTo(slotStack, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else {
                // Fire pit slot -> player inventory
                if (!this.moveItemStackTo(slotStack, 3, 39, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }
        return result;
    }

    private static boolean isFuel(ItemStack stack) {
        return stack.getBurnTime(RecipeType.SMELTING) > 0;
    }

    // Custom slot that only accepts fuel items
    private static class FuelSlot extends Slot {
        public FuelSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return isFuel(stack);
        }
    }

    // Custom slot for output only (no placing items)
    private static class OutputSlot extends Slot {
        public OutputSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }
}

