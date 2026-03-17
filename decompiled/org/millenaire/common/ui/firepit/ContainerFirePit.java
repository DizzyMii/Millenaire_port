/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.IContainerListener
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntityFurnace
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  net.minecraftforge.items.IItemHandler
 */
package org.millenaire.common.ui.firepit;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import org.millenaire.common.entity.TileEntityFirePit;
import org.millenaire.common.ui.firepit.SlotFirePitFuel;
import org.millenaire.common.ui.firepit.SlotFirePitInput;
import org.millenaire.common.ui.firepit.SlotFirePitOutput;

public class ContainerFirePit
extends Container {
    private static final int[][] INPUT_POSITIONS = new int[][]{{56, 8}, {44, 28}, {56, 48}};
    private static final int[][] OUTPUT_POSITIONS = new int[][]{{104, 8}, {116, 28}, {104, 48}};
    private static final int[] FUEL_POSITION = new int[]{80, 70};
    private static final int[] INV_POSITION = new int[]{8, 93};
    private final int inputStart;
    private final int inputEnd;
    private final int fuelStart;
    private final int fuelEnd;
    private final int outputStart;
    private final int outputEnd;
    private final int inventoryStart;
    private final int inventoryEnd;
    private final int hotbarStart;
    private final int hotbarEnd;
    private final TileEntityFirePit firePit;

    private static boolean inRange(int index, int start, int end) {
        return start <= index && index < end;
    }

    public ContainerFirePit(EntityPlayer player, TileEntityFirePit firePit) {
        int i;
        this.firePit = firePit;
        InventoryPlayer playerInventory = player.field_71071_by;
        this.inputStart = this.field_75151_b.size();
        for (i = 0; i < 3; ++i) {
            this.func_75146_a((Slot)new SlotFirePitInput((IItemHandler)firePit.inputs, i, INPUT_POSITIONS[i][0], INPUT_POSITIONS[i][1]));
        }
        this.inputEnd = this.field_75151_b.size();
        this.fuelStart = this.field_75151_b.size();
        this.func_75146_a((Slot)new SlotFirePitFuel((IItemHandler)firePit.fuel, 0, FUEL_POSITION[0], FUEL_POSITION[1]));
        this.fuelEnd = this.field_75151_b.size();
        this.outputStart = this.field_75151_b.size();
        for (i = 0; i < 3; ++i) {
            this.func_75146_a((Slot)new SlotFirePitOutput(player, (IItemHandler)firePit.outputs, i, OUTPUT_POSITIONS[i][0], OUTPUT_POSITIONS[i][1]));
        }
        this.outputEnd = this.field_75151_b.size();
        this.inventoryStart = this.field_75151_b.size();
        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                this.func_75146_a(new Slot((IInventory)playerInventory, column + row * 9 + 9, INV_POSITION[0] + column * 18, INV_POSITION[1] + row * 18));
            }
        }
        this.inventoryEnd = this.field_75151_b.size();
        this.hotbarStart = this.field_75151_b.size();
        for (int hotbarIndex = 0; hotbarIndex < 9; ++hotbarIndex) {
            this.func_75146_a(new Slot((IInventory)playerInventory, hotbarIndex, INV_POSITION[0] + hotbarIndex * 18, INV_POSITION[1] + 54 + 4));
        }
        this.hotbarEnd = this.field_75151_b.size();
    }

    public boolean func_75145_c(@Nonnull EntityPlayer playerIn) {
        return true;
    }

    public void func_75142_b() {
        super.func_75142_b();
        for (IContainerListener listener : this.field_75149_d) {
            listener.func_71112_a((Container)this, 0, this.firePit.getCookTime(0));
            listener.func_71112_a((Container)this, 1, this.firePit.getCookTime(1));
            listener.func_71112_a((Container)this, 2, this.firePit.getCookTime(2));
            listener.func_71112_a((Container)this, 3, this.firePit.getBurnTime());
            listener.func_71112_a((Container)this, 4, this.firePit.getTotalBurnTime());
        }
    }

    @Nonnull
    public ItemStack func_82846_b(EntityPlayer playerIn, int index) {
        ItemStack original = ItemStack.field_190927_a;
        Slot slot = (Slot)this.field_75151_b.get(index);
        if (slot != null && slot.func_75216_d()) {
            ItemStack stackInSlot = slot.func_75211_c();
            original = stackInSlot.func_77946_l();
            if (ContainerFirePit.inRange(index, this.outputStart, this.outputEnd)) {
                if (!this.func_75135_a(stackInSlot, this.inventoryStart, this.hotbarEnd, true)) {
                    return ItemStack.field_190927_a;
                }
                slot.func_75220_a(stackInSlot, original);
            } else if (ContainerFirePit.inRange(index, this.inventoryStart, this.hotbarEnd) ? (TileEntityFirePit.isFirePitBurnable(stackInSlot) ? !this.func_75135_a(stackInSlot, this.inputStart, this.inputEnd, false) : (TileEntityFurnace.func_145954_b((ItemStack)stackInSlot) ? !this.func_75135_a(stackInSlot, this.fuelStart, this.fuelEnd, false) : (ContainerFirePit.inRange(index, this.inventoryStart, this.inventoryEnd) ? !this.func_75135_a(stackInSlot, this.hotbarStart, this.hotbarEnd, false) : !this.func_75135_a(stackInSlot, this.inventoryStart, this.inventoryStart, false)))) : !this.func_75135_a(stackInSlot, this.inventoryStart, this.hotbarEnd, false)) {
                return ItemStack.field_190927_a;
            }
            if (stackInSlot.func_190926_b()) {
                slot.func_75215_d(ItemStack.field_190927_a);
            } else {
                slot.func_75218_e();
            }
            if (stackInSlot.func_190916_E() == original.func_190916_E()) {
                return ItemStack.field_190927_a;
            }
            slot.func_190901_a(playerIn, stackInSlot);
        }
        return original;
    }

    @SideOnly(value=Side.CLIENT)
    public void func_75137_b(int id, int data) {
        switch (id) {
            case 0: 
            case 1: 
            case 2: {
                this.firePit.setCookTime(id, data);
                break;
            }
            case 3: {
                this.firePit.setBurnTime(data);
                break;
            }
            case 4: {
                this.firePit.setTotalBurnTime(data);
            }
        }
    }
}

