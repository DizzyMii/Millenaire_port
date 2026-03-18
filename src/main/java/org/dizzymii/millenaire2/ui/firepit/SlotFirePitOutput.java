package org.dizzymii.millenaire2.ui.firepit;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class SlotFirePitOutput extends Slot {
    private final net.minecraft.world.entity.player.Player player;
    private int removeCount;

    public SlotFirePitOutput(Container container, int index, int x, int y) {
        super(container, index, x, y);
        this.player = null;
    }

    public SlotFirePitOutput(net.minecraft.world.entity.player.Player player, Container container, int index, int x, int y) {
        super(container, index, x, y);
        this.player = player;
    }

    @Override
    public boolean mayPlace(net.minecraft.world.item.ItemStack stack) {
        return false;
    }

    @Override
    public net.minecraft.world.item.ItemStack remove(int amount) {
        if (this.hasItem()) {
            this.removeCount += Math.min(amount, this.getItem().getCount());
        }
        return super.remove(amount);
    }

    @Override
    public void onTake(net.minecraft.world.entity.player.Player player, net.minecraft.world.item.ItemStack stack) {
        this.checkTakeAchievements(stack);
        super.onTake(player, stack);
    }

    @Override
    protected void checkTakeAchievements(net.minecraft.world.item.ItemStack stack) {
        stack.onCraftedBy(this.player != null ? this.player.level() : null, this.player, this.removeCount);
        this.removeCount = 0;
    }
}
