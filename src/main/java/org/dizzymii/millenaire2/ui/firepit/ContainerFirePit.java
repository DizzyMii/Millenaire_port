package org.dizzymii.millenaire2.ui.firepit;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class ContainerFirePit extends AbstractContainerMenu {
    protected ContainerFirePit(MenuType<?> type, int containerId) {
        super(type, containerId);
    }
    // TODO: Implement fire pit container with fuel, input, and output slots

    @Override
    public boolean stillValid(net.minecraft.world.entity.player.Player player) {
        return true;
    }

    @Override
    public net.minecraft.world.item.ItemStack quickMoveStack(net.minecraft.world.entity.player.Player player, int index) {
        return net.minecraft.world.item.ItemStack.EMPTY;
    }
}
