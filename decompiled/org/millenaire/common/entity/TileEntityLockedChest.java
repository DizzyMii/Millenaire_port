/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.ISidedInventory
 *  net.minecraft.inventory.ItemStackHelper
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.PacketBuffer
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityLockableLoot
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.ITickable
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.datafix.DataFixer
 *  net.minecraft.util.datafix.FixTypes
 *  net.minecraft.util.datafix.IDataWalker
 *  net.minecraft.util.datafix.walkers.ItemStackDataLists
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.util.text.TextComponentTranslation
 *  net.minecraft.world.ILockableContainer
 *  net.minecraft.world.LockCode
 *  net.minecraft.world.World
 *  net.minecraftforge.common.capabilities.Capability
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  net.minecraftforge.items.CapabilityItemHandler
 *  net.minecraftforge.items.IItemHandler
 */
package org.millenaire.common.entity;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IDataWalker;
import net.minecraft.util.datafix.walkers.ItemStackDataLists;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.millenaire.common.block.BlockLockedChest;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.network.StreamReadWrite;
import org.millenaire.common.ui.ContainerLockedChest;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;
import org.millenaire.common.world.MillWorldData;

public class TileEntityLockedChest
extends TileEntityLockableLoot
implements ITickable,
ISidedInventory {
    private NonNullList<ItemStack> chestContents = NonNullList.func_191197_a((int)27, (Object)ItemStack.field_190927_a);
    public boolean adjacentChestChecked;
    public TileEntityLockedChest adjacentChestZNeg;
    public TileEntityLockedChest adjacentChestXPos;
    public TileEntityLockedChest adjacentChestXNeg;
    public TileEntityLockedChest adjacentChestZPos;
    public float lidAngle;
    public float prevLidAngle;
    public int numPlayersUsing;
    private int ticksSinceSync;
    public Point buildingPos = null;
    public boolean loaded = false;
    public boolean serverDevMode = false;

    public static void readUpdatePacket(PacketBuffer data, World world) {
        Point pos = StreamReadWrite.readNullablePoint(data);
        TileEntityLockedChest te = pos.getMillChest(world);
        if (te != null) {
            try {
                Building building;
                te.buildingPos = StreamReadWrite.readNullablePoint(data);
                te.serverDevMode = data.readBoolean();
                int nb = data.readByte();
                for (int i = 0; i < nb; ++i) {
                    ItemStack stack = StreamReadWrite.readNullableItemStack(data);
                    if (stack == null) {
                        MillLog.error((Object)te, "Received a null stack!");
                        stack = ItemStack.field_190927_a;
                    }
                    te.func_70299_a(i, stack);
                }
                te.loaded = true;
                if (Mill.clientWorld != null && (building = Mill.clientWorld.getBuilding(te.buildingPos)) != null) {
                    building.invalidateInventoryCache();
                }
            }
            catch (IOException e) {
                MillLog.printException((Object)((Object)te) + ": Error in readUpdatePacket", e);
            }
        }
    }

    public static void registerFixesChest(DataFixer fixer) {
        fixer.func_188258_a(FixTypes.BLOCK_ENTITY, (IDataWalker)new ItemStackDataLists(TileEntityLockedChest.class, new String[]{"Items"}));
    }

    public boolean func_180461_b(int index, ItemStack stack, EnumFacing direction) {
        return false;
    }

    public boolean func_180462_a(int index, ItemStack itemStackIn, EnumFacing direction) {
        return false;
    }

    public void checkForAdjacentChests() {
        if (!this.adjacentChestChecked) {
            this.adjacentChestChecked = true;
            this.adjacentChestXNeg = this.getAdjacentLockedChest(EnumFacing.WEST);
            this.adjacentChestXPos = this.getAdjacentLockedChest(EnumFacing.EAST);
            this.adjacentChestZNeg = this.getAdjacentLockedChest(EnumFacing.NORTH);
            this.adjacentChestZPos = this.getAdjacentLockedChest(EnumFacing.SOUTH);
        }
    }

    public void func_174886_c(EntityPlayer player) {
        if (!player.func_175149_v() && this.func_145838_q() instanceof BlockLockedChest) {
            --this.numPlayersUsing;
            this.field_145850_b.func_175641_c(this.field_174879_c, this.func_145838_q(), 1, this.numPlayersUsing);
            this.field_145850_b.func_175685_c(this.field_174879_c, this.func_145838_q(), false);
        }
    }

    public Container func_174876_a(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        this.func_184281_d(playerIn);
        Building building = Mill.getMillWorld(this.field_145850_b).getBuilding(this.buildingPos);
        return new ContainerLockedChest((IInventory)playerInventory, (IInventory)this, playerIn, building, this.isLockedFor(playerIn));
    }

    @Nullable
    protected TileEntityLockedChest getAdjacentLockedChest(EnumFacing side) {
        TileEntity tileentity;
        BlockPos blockpos = this.field_174879_c.func_177972_a(side);
        if (this.isLockedChestAt(blockpos) && (tileentity = this.field_145850_b.func_175625_s(blockpos)) instanceof TileEntityLockedChest) {
            TileEntityLockedChest tileentitychest = (TileEntityLockedChest)tileentity;
            tileentitychest.setNeighbor(this, side.func_176734_d());
            return tileentitychest;
        }
        return null;
    }

    public ITextComponent func_145748_c_() {
        if (this.buildingPos == null) {
            return LanguageUtilities.textComponent("ui.unlockedchest");
        }
        Building building = null;
        if (Mill.clientWorld != null) {
            building = Mill.clientWorld.getBuilding(this.buildingPos);
        }
        if (building == null) {
            return LanguageUtilities.textComponent("ui.unlockedchest");
        }
        String s = building.getNativeBuildingName();
        if (building.chestLocked) {
            return new TextComponentString(s + ": " + LanguageUtilities.string("ui.lockedchest"));
        }
        return new TextComponentString(s + ": " + LanguageUtilities.string("ui.unlockedchest"));
    }

    public String func_174875_k() {
        return "minecraft:chest";
    }

    public int func_70297_j_() {
        return 64;
    }

    public String getInvLargeName() {
        if (this.buildingPos == null) {
            return LanguageUtilities.string("ui.largeunlockedchest");
        }
        Building building = null;
        if (Mill.clientWorld != null) {
            building = Mill.clientWorld.getBuilding(this.buildingPos);
        }
        if (building == null) {
            return LanguageUtilities.string("ui.largeunlockedchest");
        }
        String s = building.getNativeBuildingName();
        if (building.chestLocked) {
            return s + ": " + LanguageUtilities.string("ui.largelockedchest");
        }
        return s + ": " + LanguageUtilities.string("ui.largeunlockedchest");
    }

    protected NonNullList<ItemStack> func_190576_q() {
        return this.chestContents;
    }

    public String func_70005_c_() {
        return this.func_145818_k_() ? this.field_190577_o : "container.chest";
    }

    @SideOnly(value=Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.field_174879_c.func_177982_a(-1, 0, -1), this.field_174879_c.func_177982_a(2, 2, 2));
    }

    public IItemHandler getSingleChestHandler() {
        return (IItemHandler)super.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
    }

    public int func_70302_i_() {
        return 27;
    }

    public int[] func_180463_a(EnumFacing side) {
        return new int[0];
    }

    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return false;
        }
        return super.hasCapability(capability, facing);
    }

    public void func_145843_s() {
        super.func_145843_s();
        this.func_145836_u();
        this.checkForAdjacentChests();
    }

    public boolean func_191420_l() {
        for (ItemStack itemstack : this.chestContents) {
            if (itemstack.func_190926_b()) continue;
            return false;
        }
        return true;
    }

    private boolean isLockedChestAt(BlockPos posIn) {
        if (this.field_145850_b == null) {
            return false;
        }
        Block block = this.field_145850_b.func_180495_p(posIn).func_177230_c();
        return block instanceof BlockLockedChest;
    }

    public boolean isLockedFor(EntityPlayer player) {
        if (player == null) {
            MillLog.printException("Null player", new Exception());
            return true;
        }
        if (!this.loaded && this.field_145850_b.field_72995_K) {
            return true;
        }
        if (this.buildingPos == null) {
            return false;
        }
        if (!this.field_145850_b.field_72995_K && MillConfigValues.DEV) {
            return false;
        }
        if (this.serverDevMode) {
            return false;
        }
        MillWorldData mw = Mill.getMillWorld(this.field_145850_b);
        if (mw == null) {
            MillLog.printException("Null MillWorldData", new Exception());
            return true;
        }
        Building building = mw.getBuilding(this.buildingPos);
        if (building == null) {
            return true;
        }
        return building.lockedForPlayer(player);
    }

    public void func_174889_b(EntityPlayer player) {
        if (!player.func_175149_v()) {
            if (this.numPlayersUsing < 0) {
                this.numPlayersUsing = 0;
            }
            ++this.numPlayersUsing;
            this.field_145850_b.func_175641_c(this.field_174879_c, this.func_145838_q(), 1, this.numPlayersUsing);
            this.field_145850_b.func_175685_c(this.field_174879_c, this.func_145838_q(), false);
        }
    }

    public void func_145839_a(NBTTagCompound compound) {
        Building building;
        super.func_145839_a(compound);
        this.chestContents = NonNullList.func_191197_a((int)this.func_70302_i_(), (Object)ItemStack.field_190927_a);
        if (!this.func_184283_b(compound)) {
            ItemStackHelper.func_191283_b((NBTTagCompound)compound, this.chestContents);
        }
        if (compound.func_150297_b("CustomName", 8)) {
            this.field_190577_o = compound.func_74779_i("CustomName");
        }
        this.buildingPos = Point.read(compound, "buildingPos");
        if (Mill.clientWorld != null && (building = Mill.clientWorld.getBuilding(this.buildingPos)) != null) {
            building.invalidateInventoryCache();
        }
    }

    public boolean func_145842_c(int id, int type) {
        if (id == 1) {
            this.numPlayersUsing = type;
            return true;
        }
        return super.func_145842_c(id, type);
    }

    public void sendUpdatePacket(EntityPlayer player) {
        ServerSender.sendLockedChestUpdatePacket(this, player);
    }

    private void setNeighbor(TileEntityLockedChest chestTe, EnumFacing side) {
        if (chestTe.func_145837_r()) {
            this.adjacentChestChecked = false;
        } else if (this.adjacentChestChecked) {
            switch (side) {
                case NORTH: {
                    if (this.adjacentChestZNeg == chestTe) break;
                    this.adjacentChestChecked = false;
                    break;
                }
                case SOUTH: {
                    if (this.adjacentChestZPos == chestTe) break;
                    this.adjacentChestChecked = false;
                    break;
                }
                case EAST: {
                    if (this.adjacentChestXPos == chestTe) break;
                    this.adjacentChestChecked = false;
                    break;
                }
                case WEST: {
                    if (this.adjacentChestXNeg == chestTe) break;
                    this.adjacentChestChecked = false;
                }
            }
        }
    }

    public void func_73660_a() {
        this.checkForAdjacentChests();
        int i = this.field_174879_c.func_177958_n();
        int j = this.field_174879_c.func_177956_o();
        int k = this.field_174879_c.func_177952_p();
        ++this.ticksSinceSync;
        if (!this.field_145850_b.field_72995_K && this.numPlayersUsing != 0 && (this.ticksSinceSync + i + j + k) % 200 == 0) {
            this.numPlayersUsing = 0;
            for (EntityPlayer entityplayer : this.field_145850_b.func_72872_a(EntityPlayer.class, new AxisAlignedBB((double)((float)i - 5.0f), (double)((float)j - 5.0f), (double)((float)k - 5.0f), (double)((float)(i + 1) + 5.0f), (double)((float)(j + 1) + 5.0f), (double)((float)(k + 1) + 5.0f)))) {
                IInventory iinventory;
                if (!(entityplayer.field_71070_bA instanceof ContainerLockedChest) || (iinventory = ((ContainerLockedChest)entityplayer.field_71070_bA).getLowerChestInventory()) != this && (!(iinventory instanceof InventoryLockedLargeChest) || !((InventoryLockedLargeChest)iinventory).isPartOfLargeChest((IInventory)this))) continue;
                ++this.numPlayersUsing;
            }
        }
        this.prevLidAngle = this.lidAngle;
        if (this.numPlayersUsing > 0 && this.lidAngle == 0.0f && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null) {
            double d1 = (double)i + 0.5;
            double d2 = (double)k + 0.5;
            if (this.adjacentChestZPos != null) {
                d2 += 0.5;
            }
            if (this.adjacentChestXPos != null) {
                d1 += 0.5;
            }
            this.field_145850_b.func_184148_a((EntityPlayer)null, d1, (double)j + 0.5, d2, SoundEvents.field_187657_V, SoundCategory.BLOCKS, 0.5f, this.field_145850_b.field_73012_v.nextFloat() * 0.1f + 0.9f);
        }
        if (this.numPlayersUsing <= 0 && this.lidAngle > 0.0f || this.numPlayersUsing > 0 && this.lidAngle < 1.0f) {
            float f2 = this.lidAngle;
            this.lidAngle = this.numPlayersUsing > 0 ? (this.lidAngle += 0.1f) : (this.lidAngle -= 0.1f);
            if (this.lidAngle > 1.0f) {
                this.lidAngle = 1.0f;
            }
            if (this.lidAngle < 0.5f && f2 >= 0.5f && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null) {
                double d3 = (double)i + 0.5;
                double d0 = (double)k + 0.5;
                if (this.adjacentChestZPos != null) {
                    d0 += 0.5;
                }
                if (this.adjacentChestXPos != null) {
                    d3 += 0.5;
                }
                this.field_145850_b.func_184148_a((EntityPlayer)null, d3, (double)j + 0.5, d0, SoundEvents.field_187651_T, SoundCategory.BLOCKS, 0.5f, this.field_145850_b.field_73012_v.nextFloat() * 0.1f + 0.9f);
            }
            if (this.lidAngle < 0.0f) {
                this.lidAngle = 0.0f;
            }
        }
    }

    public void func_145836_u() {
        super.func_145836_u();
        this.adjacentChestChecked = false;
    }

    public NBTTagCompound func_189515_b(NBTTagCompound compound) {
        super.func_189515_b(compound);
        if (!this.func_184282_c(compound)) {
            ItemStackHelper.func_191282_a((NBTTagCompound)compound, this.chestContents);
        }
        if (this.func_145818_k_()) {
            compound.func_74778_a("CustomName", this.field_190577_o);
        }
        if (this.buildingPos != null) {
            this.buildingPos.write(compound, "buildingPos");
        }
        return compound;
    }

    public static class InventoryLockedLargeChest
    implements ILockableContainer,
    ISidedInventory {
        private final String name;
        private final TileEntityLockedChest upperChest;
        private final TileEntityLockedChest lowerChest;

        public InventoryLockedLargeChest(String nameIn, TileEntityLockedChest upperChestIn, TileEntityLockedChest lowerChestIn) {
            this.name = nameIn;
            if (upperChestIn == null) {
                upperChestIn = lowerChestIn;
            }
            if (lowerChestIn == null) {
                lowerChestIn = upperChestIn;
            }
            this.upperChest = upperChestIn;
            this.lowerChest = lowerChestIn;
            if (upperChestIn.func_174893_q_()) {
                lowerChestIn.func_174892_a(upperChestIn.func_174891_i());
            } else if (lowerChestIn.func_174893_q_()) {
                upperChestIn.func_174892_a(lowerChestIn.func_174891_i());
            }
        }

        public boolean func_180461_b(int index, ItemStack stack, EnumFacing direction) {
            return false;
        }

        public boolean func_180462_a(int index, ItemStack itemStackIn, EnumFacing direction) {
            return false;
        }

        public void func_174888_l() {
            this.upperChest.func_174888_l();
            this.lowerChest.func_174888_l();
        }

        public void func_174886_c(EntityPlayer player) {
            this.upperChest.func_174886_c(player);
            this.lowerChest.func_174886_c(player);
        }

        public Container func_174876_a(InventoryPlayer playerInventory, EntityPlayer playerIn) {
            Building building = Mill.getMillWorld(this.upperChest.field_145850_b).getBuilding(this.upperChest.buildingPos);
            return new ContainerLockedChest((IInventory)playerInventory, (IInventory)this, playerIn, building, this.upperChest.isLockedFor(playerIn));
        }

        public ItemStack func_70298_a(int index, int count) {
            return index >= this.upperChest.func_70302_i_() ? this.lowerChest.func_70298_a(index - this.upperChest.func_70302_i_(), count) : this.upperChest.func_70298_a(index, count);
        }

        public ITextComponent func_145748_c_() {
            return this.func_145818_k_() ? new TextComponentString(this.func_70005_c_()) : new TextComponentTranslation(this.func_70005_c_(), new Object[0]);
        }

        public int func_174887_a_(int id) {
            return 0;
        }

        public int func_174890_g() {
            return 0;
        }

        public String func_174875_k() {
            return this.upperChest.func_174875_k();
        }

        public int func_70297_j_() {
            return this.upperChest.func_70297_j_();
        }

        public LockCode func_174891_i() {
            return this.upperChest.func_174891_i();
        }

        public String func_70005_c_() {
            if (this.upperChest.func_145818_k_()) {
                return this.upperChest.func_70005_c_();
            }
            return this.lowerChest.func_145818_k_() ? this.lowerChest.func_70005_c_() : this.name;
        }

        public int func_70302_i_() {
            return this.upperChest.func_70302_i_() + this.lowerChest.func_70302_i_();
        }

        public int[] func_180463_a(EnumFacing side) {
            return new int[0];
        }

        public ItemStack func_70301_a(int index) {
            return index >= this.upperChest.func_70302_i_() ? this.lowerChest.func_70301_a(index - this.upperChest.func_70302_i_()) : this.upperChest.func_70301_a(index);
        }

        public boolean func_145818_k_() {
            return this.upperChest.func_145818_k_() || this.lowerChest.func_145818_k_();
        }

        public boolean func_191420_l() {
            return this.upperChest.func_191420_l() && this.lowerChest.func_191420_l();
        }

        public boolean func_94041_b(int index, ItemStack stack) {
            return true;
        }

        public boolean func_174893_q_() {
            return this.upperChest.func_174893_q_() || this.lowerChest.func_174893_q_();
        }

        public boolean isPartOfLargeChest(IInventory inventoryIn) {
            return this.upperChest == inventoryIn || this.lowerChest == inventoryIn;
        }

        public boolean func_70300_a(EntityPlayer player) {
            return this.upperChest.func_70300_a(player) && this.lowerChest.func_70300_a(player);
        }

        public void func_70296_d() {
            this.upperChest.func_70296_d();
            this.lowerChest.func_70296_d();
        }

        public void func_174889_b(EntityPlayer player) {
            this.upperChest.func_174889_b(player);
            this.lowerChest.func_174889_b(player);
        }

        public ItemStack func_70304_b(int index) {
            return index >= this.upperChest.func_70302_i_() ? this.lowerChest.func_70304_b(index - this.upperChest.func_70302_i_()) : this.upperChest.func_70304_b(index);
        }

        public void func_174885_b(int id, int value) {
        }

        public void func_70299_a(int index, ItemStack stack) {
            if (index >= this.upperChest.func_70302_i_()) {
                this.lowerChest.func_70299_a(index - this.upperChest.func_70302_i_(), stack);
            } else {
                this.upperChest.func_70299_a(index, stack);
            }
        }

        public void func_174892_a(LockCode code) {
            this.upperChest.func_174892_a(code);
            this.lowerChest.func_174892_a(code);
        }
    }
}

