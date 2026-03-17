package org.dizzymii.millenaire2.entity.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.entity.MillEntities;

import java.util.UUID;

/**
 * Block entity for the Locked Chest (village storage).
 * Ported from org.millenaire.common.entity.TileEntityLockedChest.
 * 27-slot inventory, locked to the owning village.
 */
public class MillLockedChestBlockEntity extends BaseContainerBlockEntity {

    private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
    private UUID ownerVillageId = null;

    public MillLockedChestBlockEntity(BlockPos pos, BlockState state) {
        super(MillEntities.LOCKED_CHEST_BE.get(), pos, state);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.millenaire2.locked_chest");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public int getContainerSize() {
        return 27;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory playerInventory) {
        return ChestMenu.threeRows(containerId, playerInventory, this);
    }

    @Override
    public boolean stillValid(Player player) {
        // Only villagers (or creative players) should access this
        if (player.isCreative()) return super.stillValid(player);
        return false;
    }

    public void setOwnerVillageId(UUID id) {
        this.ownerVillageId = id;
        setChanged();
    }

    public UUID getOwnerVillageId() {
        return ownerVillageId;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
        if (ownerVillageId != null) {
            tag.putUUID("OwnerVillage", ownerVillageId);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
        if (tag.hasUUID("OwnerVillage")) {
            this.ownerVillageId = tag.getUUID("OwnerVillage");
        }
    }
}
