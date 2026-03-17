package org.dizzymii.millenaire2.entity.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.block.BlockFirePit;
import org.dizzymii.millenaire2.entity.MillEntities;

import java.util.Optional;

/**
 * Block entity for the Fire Pit.
 * Ported from org.millenaire.common.entity.TileEntityFirePit.
 * Has a 3-slot inventory: input (0), fuel (1), output (2).
 */
public class MillFirePitBlockEntity extends BaseContainerBlockEntity {

    private NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
    private int burnTime;
    private int burnDuration;
    private int cookTime;
    private int cookDuration;

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_FUEL = 1;
    public static final int SLOT_OUTPUT = 2;
    public static final int DEFAULT_COOK_TIME = 200;

    public MillFirePitBlockEntity(BlockPos pos, BlockState state) {
        super(MillEntities.FIRE_PIT_BE.get(), pos, state);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.millenaire2.fire_pit");
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
        return 3;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory playerInventory) {
        return new ChestMenu(MenuType.GENERIC_9x1, containerId, playerInventory, this, 1);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
        tag.putInt("BurnTime", this.burnTime);
        tag.putInt("BurnDuration", this.burnDuration);
        tag.putInt("CookTime", this.cookTime);
        tag.putInt("CookDuration", this.cookDuration);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
        this.burnTime = tag.getInt("BurnTime");
        this.burnDuration = tag.getInt("BurnDuration");
        this.cookTime = tag.getInt("CookTime");
        this.cookDuration = tag.getInt("CookDuration");
    }

    public boolean isLit() {
        return this.burnTime > 0;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MillFirePitBlockEntity be) {
        boolean wasLit = be.isLit();
        boolean changed = false;

        if (be.isLit()) {
            --be.burnTime;
        }

        ItemStack inputStack = be.items.get(SLOT_INPUT);
        ItemStack fuelStack = be.items.get(SLOT_FUEL);

        boolean hasInput = !inputStack.isEmpty();
        boolean hasFuel = !fuelStack.isEmpty();

        if (be.isLit() || (hasFuel && hasInput)) {
            Optional<SmeltingRecipe> recipe = Optional.empty();
            if (hasInput) {
                recipe = level.getRecipeManager()
                        .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(inputStack), level)
                        .map(r -> r.value());
            }

            if (!be.isLit() && recipe.isPresent()) {
                be.burnTime = fuelStack.getBurnTime(RecipeType.SMELTING);
                be.burnDuration = be.burnTime;
                if (be.isLit()) {
                    changed = true;
                    if (fuelStack.hasCraftingRemainingItem()) {
                        be.items.set(SLOT_FUEL, fuelStack.getCraftingRemainingItem());
                    } else {
                        fuelStack.shrink(1);
                        if (fuelStack.isEmpty()) {
                            be.items.set(SLOT_FUEL, ItemStack.EMPTY);
                        }
                    }
                }
            }

            if (be.isLit() && recipe.isPresent()) {
                ++be.cookTime;
                be.cookDuration = DEFAULT_COOK_TIME;
                if (be.cookTime >= be.cookDuration) {
                    be.cookTime = 0;
                    ItemStack result = recipe.get().assemble(new SingleRecipeInput(inputStack), level.registryAccess());
                    ItemStack outputStack = be.items.get(SLOT_OUTPUT);
                    if (outputStack.isEmpty()) {
                        be.items.set(SLOT_OUTPUT, result.copy());
                    } else if (ItemStack.isSameItemSameComponents(outputStack, result)) {
                        outputStack.grow(result.getCount());
                    }
                    inputStack.shrink(1);
                    changed = true;
                }
            } else {
                be.cookTime = 0;
            }
        } else if (be.cookTime > 0) {
            be.cookTime = Math.max(be.cookTime - 2, 0);
        }

        if (wasLit != be.isLit()) {
            changed = true;
            state = state.setValue(BlockFirePit.LIT, be.isLit());
            level.setBlock(pos, state, 3);
        }

        if (changed) {
            setChanged(level, pos, state);
        }
    }
}
