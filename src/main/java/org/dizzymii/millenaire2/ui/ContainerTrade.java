package org.dizzymii.millenaire2.ui;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class ContainerTrade extends AbstractContainerMenu {
    protected ContainerTrade(MenuType<?> type, int containerId) {
        super(type, containerId);
    }
    // TODO: Implement trade container with buy/sell slots and denier handling

    @Override
    public boolean stillValid(net.minecraft.world.entity.player.Player player) {
        return true;
    }

    @Override
    public net.minecraft.world.item.ItemStack quickMoveStack(net.minecraft.world.entity.player.Player player, int index) {
        return net.minecraft.world.item.ItemStack.EMPTY;
    }
}
