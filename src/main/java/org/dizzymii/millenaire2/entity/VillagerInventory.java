package org.dizzymii.millenaire2.entity;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.MillCommonUtilities;

import javax.annotation.Nullable;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class VillagerInventory extends AbstractMap<InvItem, Integer> {

    public static final int SLOT_COUNT = 36;
    private final SimpleContainer container = new SimpleContainer(SLOT_COUNT);
    private int selectedSlot = -1;

    public SimpleContainer asContainer() {
        return container;
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }

    public void setSelectedSlot(int selectedSlot) {
        this.selectedSlot = selectedSlot < 0 ? -1 : MillCommonUtilities.clamp(selectedSlot, 0, 8);
    }

    public ItemStack getSelectedItem() {
        return selectedSlot >= 0 && selectedSlot < container.getContainerSize()
                ? container.getItem(selectedSlot)
                : ItemStack.EMPTY;
    }

    public void setSelectedItem(ItemStack stack) {
        if (selectedSlot >= 0 && selectedSlot < container.getContainerSize()) {
            container.setItem(selectedSlot, stack.copy());
        }
    }

    public int add(InvItem item, int count) {
        if (item == null || count <= 0) {
            return count;
        }
        ItemStack template = item.getItemStack(1);
        if (template.isEmpty()) {
            return count;
        }
        int remaining = count;
        for (int i = 0; i < container.getContainerSize() && remaining > 0; i++) {
            ItemStack existing = container.getItem(i);
            if (existing.isEmpty() || !ItemStack.isSameItemSameComponents(existing, template)) {
                continue;
            }
            int maxSize = Math.min(existing.getMaxStackSize(), container.getMaxStackSize());
            int space = maxSize - existing.getCount();
            if (space <= 0) {
                continue;
            }
            int moved = Math.min(space, remaining);
            existing.grow(moved);
            container.setItem(i, existing);
            remaining -= moved;
        }
        for (int i = 0; i < container.getContainerSize() && remaining > 0; i++) {
            ItemStack existing = container.getItem(i);
            if (!existing.isEmpty()) {
                continue;
            }
            int moved = Math.min(template.getMaxStackSize(), remaining);
            container.setItem(i, item.getItemStack(moved));
            remaining -= moved;
        }
        return remaining;
    }

    public int add(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        ItemStack working = stack.copy();
        for (int i = 0; i < container.getContainerSize() && !working.isEmpty(); i++) {
            ItemStack existing = container.getItem(i);
            if (existing.isEmpty() || !ItemStack.isSameItemSameComponents(existing, working)) {
                continue;
            }
            int maxSize = Math.min(existing.getMaxStackSize(), container.getMaxStackSize());
            int space = maxSize - existing.getCount();
            if (space <= 0) {
                continue;
            }
            int moved = Math.min(space, working.getCount());
            existing.grow(moved);
            working.shrink(moved);
            container.setItem(i, existing);
        }
        for (int i = 0; i < container.getContainerSize() && !working.isEmpty(); i++) {
            if (!container.getItem(i).isEmpty()) {
                continue;
            }
            int moved = Math.min(working.getMaxStackSize(), working.getCount());
            ItemStack inserted = working.copyWithCount(moved);
            container.setItem(i, inserted);
            working.shrink(moved);
        }
        return working.getCount();
    }

    public int remove(InvItem item, int count) {
        if (item == null || count <= 0) {
            return 0;
        }
        ItemStack template = item.getItemStack(1);
        if (template.isEmpty()) {
            return 0;
        }
        int remaining = count;
        for (int i = container.getContainerSize() - 1; i >= 0 && remaining > 0; i--) {
            ItemStack existing = container.getItem(i);
            if (existing.isEmpty() || !ItemStack.isSameItemSameComponents(existing, template)) {
                continue;
            }
            int moved = Math.min(existing.getCount(), remaining);
            existing.shrink(moved);
            remaining -= moved;
            if (existing.isEmpty()) {
                container.setItem(i, ItemStack.EMPTY);
            } else {
                container.setItem(i, existing);
            }
        }
        return count - remaining;
    }

    public int count(InvItem item) {
        if (item == null) {
            return 0;
        }
        ItemStack template = item.getItemStack(1);
        if (template.isEmpty()) {
            return 0;
        }
        int total = 0;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack existing = container.getItem(i);
            if (!existing.isEmpty() && ItemStack.isSameItemSameComponents(existing, template)) {
                total += existing.getCount();
            }
        }
        return total;
    }

    public Map<InvItem, Integer> snapshotCounts() {
        LinkedHashMap<InvItem, Integer> counts = new LinkedHashMap<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            InvItem item = InvItem.fromItem(stack.getItem());
            if (item == null) {
                continue;
            }
            counts.merge(item, stack.getCount(), Integer::sum);
        }
        return counts;
    }

    @Override
    public Set<Entry<InvItem, Integer>> entrySet() {
        return new LinkedHashSet<>(snapshotCounts().entrySet());
    }

    @Override
    public Integer get(Object key) {
        if (!(key instanceof InvItem item)) {
            return null;
        }
        int count = count(item);
        return count > 0 ? count : null;
    }

    @Override
    public Integer put(InvItem key, Integer value) {
        int previous = count(key);
        int target = value == null ? 0 : Math.max(0, value);
        if (target > previous) {
            add(key, target - previous);
        } else if (target < previous) {
            remove(key, previous - target);
        }
        return previous > 0 ? previous : null;
    }

    @Override
    public Integer remove(Object key) {
        if (!(key instanceof InvItem item)) {
            return null;
        }
        int previous = count(item);
        if (previous > 0) {
            remove(item, previous);
        }
        return previous > 0 ? previous : null;
    }

    @Override
    public void clear() {
        container.clearContent();
    }

    @Override
    public boolean isEmpty() {
        return container.isEmpty();
    }

    @Override
    public int size() {
        return snapshotCounts().size();
    }

    @Nullable
    public InvItem findMatchingInvItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        return InvItem.fromItem(stack.getItem());
    }
}
