package org.dizzymii.millenaire2.ui;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.dizzymii.millenaire2.menu.MillMenuTypes;

/**
 * Container for puja sacrifice rituals.
 * Slot 0 = offering input. Player places items to sacrifice.
 */
public class ContainerPuja extends AbstractContainerMenu {

    private static final int OFFERING_SLOTS = 1;
    private final Container offeringContainer;

    public ContainerPuja(MenuType<?> type, int containerId, Inventory playerInventory) {
        this(type, containerId, playerInventory, new SimpleContainer(OFFERING_SLOTS));
    }

    /** Client-side factory constructor used by {@link net.neoforged.neoforge.common.extensions.IMenuTypeExtension}. */
    public ContainerPuja(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(MillMenuTypes.PUJA.get(), containerId, playerInventory);
    }

    public ContainerPuja(MenuType<?> type, int containerId, Inventory playerInventory, Container offeringContainer) {
        super(type, containerId);
        this.offeringContainer = offeringContainer;

        // Offering slot (center)
        this.addSlot(new Slot(offeringContainer, 0, 80, 35));

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
            if (index < OFFERING_SLOTS) {
                if (!this.moveItemStackTo(slotStack, OFFERING_SLOTS, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(slotStack, 0, OFFERING_SLOTS, false)) {
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
