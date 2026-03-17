package org.dizzymii.millenaire2.ui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class ContainerLockedChest extends AbstractContainerMenu {
    protected ContainerLockedChest(MenuType<?> type, int containerId) {
        super(type, containerId);
    }
    // TODO: Implement locked chest container with village ownership checks

    @Override
    public boolean stillValid(net.minecraft.world.entity.player.Player player) {
        return true; // TODO: Implement proper validity check
    }

    @Override
    public net.minecraft.world.item.ItemStack quickMoveStack(net.minecraft.world.entity.player.Player player, int index) {
        return net.minecraft.world.item.ItemStack.EMPTY; // TODO: Implement shift-click
    }
}
