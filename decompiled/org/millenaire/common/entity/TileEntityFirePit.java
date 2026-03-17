/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.inventory.InventoryHelper
 *  net.minecraft.item.ItemFood
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.FurnaceRecipes
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.NetworkManager
 *  net.minecraft.network.play.server.SPacketUpdateTileEntity
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityFurnace
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.ITickable
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 *  net.minecraftforge.common.capabilities.Capability
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  net.minecraftforge.items.CapabilityItemHandler
 *  net.minecraftforge.items.IItemHandlerModifiable
 *  net.minecraftforge.items.ItemHandlerHelper
 *  net.minecraftforge.items.ItemStackHandler
 *  net.minecraftforge.items.wrapper.RangedWrapper
 */
package org.millenaire.common.entity;

import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.millenaire.common.block.BlockFirePit;
import org.millenaire.common.block.MillBlocks;

public class TileEntityFirePit
extends TileEntity
implements ITickable {
    private final ItemStackHandler items = new ItemStackHandler(7){

        protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
            if (0 <= slot && slot < 3 && !TileEntityFirePit.isFirePitBurnable(stack) || 3 <= slot && slot < 4 && !TileEntityFurnace.func_145954_b((ItemStack)stack) || 4 <= slot && slot < 7) {
                return 0;
            }
            return super.getStackLimit(slot, stack);
        }

        protected void onContentsChanged(int slot) {
            TileEntityFirePit.this.func_70296_d();
            IBlockState state = TileEntityFirePit.this.field_145850_b.func_180495_p(TileEntityFirePit.this.field_174879_c);
            TileEntityFirePit.this.field_145850_b.func_184138_a(TileEntityFirePit.this.field_174879_c, state, state, 18);
        }
    };
    public final IItemHandlerModifiable inputs = new RangedWrapper((IItemHandlerModifiable)this.items, 0, 3);
    public final IItemHandlerModifiable fuel = new RangedWrapper((IItemHandlerModifiable)this.items, 3, 4);
    public final IItemHandlerModifiable outputs = new RangedWrapper((IItemHandlerModifiable)this.items, 4, 7);
    private int[] cookTimes = new int[3];
    private int burnTime = 0;
    private int totalBurnTime = 0;

    public static boolean isFirePitBurnable(ItemStack stack) {
        boolean food = stack.func_77973_b() instanceof ItemFood;
        ItemStack result = FurnaceRecipes.func_77602_a().func_151395_a(stack);
        return !result.func_190926_b() && (food || result.func_77973_b() instanceof ItemFood);
    }

    private boolean canSmelt(int idx) {
        ItemStack stack = this.inputs.getStackInSlot(idx);
        if (stack.func_190926_b()) {
            return false;
        }
        ItemStack result = FurnaceRecipes.func_77602_a().func_151395_a(stack);
        if (result.func_190926_b()) {
            return false;
        }
        ItemStack output = this.outputs.getStackInSlot(idx);
        return output.func_190926_b() || ItemHandlerHelper.canItemStacksStack((ItemStack)result, (ItemStack)output) && output.func_190916_E() + result.func_190916_E() <= result.func_77976_d();
    }

    public void dropAll() {
        for (int i = 0; i < this.items.getSlots(); ++i) {
            ItemStack stack = this.items.getStackInSlot(i);
            if (stack.func_190926_b()) continue;
            InventoryHelper.func_180173_a((World)this.field_145850_b, (double)this.field_174879_c.func_177958_n(), (double)this.field_174879_c.func_177956_o(), (double)this.field_174879_c.func_177952_p(), (ItemStack)stack);
        }
    }

    public int getBurnTime() {
        return this.burnTime;
    }

    @Nullable
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == null) {
                return (T)CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast((Object)this.items);
            }
            if (facing == EnumFacing.UP) {
                return (T)CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast((Object)this.inputs);
            }
            if (facing == EnumFacing.DOWN) {
                return (T)CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast((Object)this.outputs);
            }
            return (T)CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast((Object)this.fuel);
        }
        return (T)super.getCapability(capability, facing);
    }

    public int getCookTime(int idx) {
        return this.cookTimes[idx];
    }

    public int getTotalBurnTime() {
        return this.totalBurnTime;
    }

    @Nullable
    public SPacketUpdateTileEntity func_189518_D_() {
        return new SPacketUpdateTileEntity(this.field_174879_c, -1, this.func_189517_E_());
    }

    @Nonnull
    public NBTTagCompound func_189517_E_() {
        return this.serializeNBT();
    }

    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @SideOnly(value=Side.CLIENT)
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.handleUpdateTag(pkt.func_148857_g());
    }

    public void func_145839_a(NBTTagCompound compound) {
        NBTTagCompound inventory = compound.func_74775_l("Inventory");
        inventory.func_82580_o("Size");
        this.items.deserializeNBT(inventory);
        this.burnTime = compound.func_74762_e("BurnTime");
        this.cookTimes = Arrays.copyOf(compound.func_74759_k("CookTime"), 3);
        this.totalBurnTime = compound.func_74762_e("TotalBurnTime");
        super.func_145839_a(compound);
    }

    public void setBurnTime(int burnTime) {
        this.burnTime = burnTime;
    }

    public void setCookTime(int idx, int cookTime) {
        this.cookTimes[idx] = cookTime;
    }

    public void setTotalBurnTime(int totalBurnTime) {
        this.totalBurnTime = totalBurnTime;
    }

    public boolean shouldRefresh(World world, BlockPos pos, @Nonnull IBlockState oldState, @Nonnull IBlockState newState) {
        return oldState.func_177230_c() != newState.func_177230_c();
    }

    public void smeltItem(int idx) {
        if (this.canSmelt(idx)) {
            ItemStack input = this.inputs.getStackInSlot(idx);
            ItemStack result = FurnaceRecipes.func_77602_a().func_151395_a(input);
            ItemStack output = this.outputs.getStackInSlot(idx);
            if (output.func_190926_b()) {
                this.outputs.setStackInSlot(idx, result.func_77946_l());
            } else {
                output.func_190917_f(result.func_190916_E());
            }
            input.func_190918_g(1);
        }
    }

    public void func_73660_a() {
        boolean burning = this.burnTime > 0;
        boolean dirty = false;
        if (burning) {
            --this.burnTime;
        }
        if (!this.field_145850_b.field_72995_K) {
            ItemStack fuelStack = this.fuel.getStackInSlot(0);
            for (int i = 0; i < 3; ++i) {
                ItemStack inputStack = this.inputs.getStackInSlot(i);
                if (!(this.burnTime <= 0 && fuelStack.func_190926_b() || inputStack.func_190926_b())) {
                    if (this.burnTime <= 0 && this.canSmelt(i)) {
                        this.totalBurnTime = this.burnTime = TileEntityFurnace.func_145952_a((ItemStack)fuelStack);
                        if (this.burnTime > 0) {
                            dirty = true;
                            if (!fuelStack.func_190926_b()) {
                                fuelStack.func_190918_g(1);
                                if (fuelStack.func_190926_b()) {
                                    this.fuel.setStackInSlot(0, fuelStack.func_77973_b().getContainerItem(fuelStack));
                                }
                            }
                        }
                    }
                    if (this.burnTime > 0 && this.canSmelt(i)) {
                        int n = i;
                        this.cookTimes[n] = this.cookTimes[n] + 1;
                        if (this.cookTimes[i] != 200) continue;
                        this.cookTimes[i] = 0;
                        this.smeltItem(i);
                        dirty = true;
                        continue;
                    }
                    this.cookTimes[i] = 0;
                    continue;
                }
                if (this.burnTime > 0 || this.cookTimes[i] <= 0) continue;
                dirty = true;
                this.cookTimes[i] = MathHelper.func_76125_a((int)(this.cookTimes[i] - 2), (int)0, (int)200);
            }
            if (burning != this.burnTime > 0) {
                dirty = true;
                IBlockState state = this.field_145850_b.func_180495_p(this.field_174879_c);
                if (!(state.func_177230_c() instanceof BlockFirePit)) {
                    state = MillBlocks.FIRE_PIT.func_176223_P();
                }
                this.field_145850_b.func_175656_a(this.field_174879_c, state.func_177226_a((IProperty)BlockFirePit.LIT, (Comparable)Boolean.valueOf(this.burnTime > 0)));
            }
        }
        if (dirty) {
            this.func_70296_d();
        }
    }

    @Nonnull
    public NBTTagCompound func_189515_b(NBTTagCompound compound) {
        NBTTagCompound inventory = this.items.serializeNBT();
        inventory.func_82580_o("Size");
        compound.func_74782_a("Inventory", (NBTBase)inventory);
        compound.func_74768_a("BurnTime", this.burnTime);
        compound.func_74783_a("CookTime", this.cookTimes);
        compound.func_74768_a("TotalBurnTime", this.totalBurnTime);
        return super.func_189515_b(compound);
    }
}

