/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockWallSign
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.NetworkManager
 *  net.minecraft.network.play.server.SPacketUpdateTileEntity
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntitySign
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.text.TextComponentString
 */
package org.millenaire.common.entity;

import net.minecraft.block.BlockWallSign;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentString;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.buildingplan.BuildingImportExport;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;

public class TileEntityImportTable
extends TileEntity {
    private String buildingKey = null;
    private int variation = 0;
    private int upgradeLevel = 0;
    private int length;
    private int width;
    private int startingLevel = -1;
    private int orientation = 0;
    private boolean exportSnow = false;
    private boolean importMockBlocks = true;
    private boolean autoconvertToPreserveGround = true;
    private boolean exportRegularChests = false;
    private Point parentTablePos = null;

    public void activate(EntityPlayer player) {
        if (player.field_71071_by.func_70448_g() != ItemStack.field_190927_a && player.field_71071_by.func_70448_g().func_77973_b() == MillItems.SUMMONING_WAND) {
            if (player.field_70170_p.field_72995_K && this.buildingKey != null) {
                MillLog.temp((Object)this, "Activating. Building key: " + this.buildingKey);
                ClientSender.importTableImportBuildingPlan(player, new Point(this.field_174879_c), BuildingImportExport.EXPORT_DIR, this.buildingKey, false, this.variation, this.upgradeLevel, this.orientation, this.importMockBlocks);
            } else {
                this.sendUpdates();
            }
        } else if (player.field_71071_by.func_70448_g() != ItemStack.field_190927_a && player.field_71071_by.func_70448_g().func_77973_b() == MillItems.NEGATION_WAND) {
            if (player.field_70170_p.field_72995_K && this.buildingKey != null) {
                BuildingImportExport.importTableExportBuildingPlan(player.field_70170_p, this, this.getUpgradeLevel());
            } else {
                this.sendUpdates();
            }
        } else if (!player.field_70170_p.field_72995_K) {
            this.sendUpdates();
            ServerSender.displayImportTableGUI(player, new Point(this.func_174877_v()));
        }
    }

    public boolean autoconvertToPreserveGround() {
        return this.autoconvertToPreserveGround;
    }

    public boolean exportRegularChests() {
        return this.exportRegularChests;
    }

    public boolean exportSnow() {
        return this.exportSnow;
    }

    public String getBuildingKey() {
        return this.buildingKey;
    }

    public int getLength() {
        return this.length;
    }

    public int getOrientation() {
        return this.orientation;
    }

    public Point getParentTablePos() {
        return this.parentTablePos;
    }

    public Point getPosPoint() {
        return new Point(this.field_174879_c);
    }

    public int getStartingLevel() {
        return this.startingLevel;
    }

    private IBlockState getState() {
        return this.field_145850_b.func_180495_p(this.field_174879_c);
    }

    public SPacketUpdateTileEntity func_189518_D_() {
        return new SPacketUpdateTileEntity(this.field_174879_c, 3, this.func_189517_E_());
    }

    public NBTTagCompound func_189517_E_() {
        return this.func_189515_b(new NBTTagCompound());
    }

    public int getUpgradeLevel() {
        return this.upgradeLevel;
    }

    public int getVariation() {
        return this.variation;
    }

    public int getWidth() {
        return this.width;
    }

    public boolean importMockBlocks() {
        return this.importMockBlocks;
    }

    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        this.handleUpdateTag(pkt.func_148857_g());
    }

    public void func_145839_a(NBTTagCompound compound) {
        super.func_145839_a(compound);
        this.buildingKey = compound.func_74779_i("buildingKey");
        this.variation = compound.func_74762_e("variation");
        this.length = compound.func_74762_e("length");
        this.width = compound.func_74762_e("width");
        this.upgradeLevel = compound.func_74762_e("upgradeLevel");
        this.startingLevel = compound.func_74762_e("startingLevel");
        this.orientation = compound.func_74762_e("orientation");
        this.exportSnow = compound.func_74767_n("exportSnow");
        this.importMockBlocks = compound.func_74767_n("importMockBlocks");
        this.autoconvertToPreserveGround = compound.func_74767_n("autoconvertToPreserveGround");
        this.exportRegularChests = compound.func_74767_n("exportRegularChests");
        this.parentTablePos = Point.read(compound, "parentTablePos");
    }

    private void sendUpdates() {
        this.field_145850_b.func_175704_b(this.field_174879_c, this.field_174879_c);
        this.field_145850_b.func_184138_a(this.field_174879_c, this.getState(), this.getState(), 3);
        this.field_145850_b.func_180497_b(this.field_174879_c, this.func_145838_q(), 0, 0);
        this.func_70296_d();
    }

    public void setAutoconvertToPreserveGround(boolean autoconvertToPreserveGround) {
        this.autoconvertToPreserveGround = autoconvertToPreserveGround;
    }

    public void setBuildingKey(String buildingKey) {
        this.buildingKey = buildingKey;
    }

    public void setExportRegularChests(boolean exportRegularChests) {
        this.exportRegularChests = exportRegularChests;
    }

    public void setExportSnow(boolean exportSnow) {
        this.exportSnow = exportSnow;
    }

    public void setImportMockBlocks(boolean importMockBlocks) {
        this.importMockBlocks = importMockBlocks;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void setParentTablePos(Point parentTablePos) {
        this.parentTablePos = parentTablePos;
    }

    public void setStartingLevel(int startingLevel) {
        this.startingLevel = startingLevel;
    }

    public void setUpgradeLevel(int upgradeLevel) {
        this.upgradeLevel = upgradeLevel;
    }

    private void updateAttachedSign() {
        Point signPos = new Point(this.field_174879_c).getRelative(-1.0, 0.0, 0.0);
        signPos.setBlockState(this.field_145850_b, Blocks.field_150444_as.func_176223_P().func_177226_a((IProperty)BlockWallSign.field_176412_a, (Comparable)EnumFacing.WEST));
        TileEntitySign sign = signPos.getSign(this.field_145850_b);
        sign.field_145915_a[0] = this.buildingKey != null ? new TextComponentString(this.buildingKey + "_" + (char)(65 + this.variation) + this.upgradeLevel) : new TextComponentString("");
        sign.field_145915_a[1] = new TextComponentString("Start level: " + this.startingLevel);
        sign.field_145915_a[2] = new TextComponentString(this.length + "x" + this.width);
        sign.func_70296_d();
        IBlockState iblockstate = this.field_145850_b.func_180495_p(sign.func_174877_v());
        this.field_145850_b.func_184138_a(sign.func_174877_v(), iblockstate, iblockstate, 3);
    }

    public void updatePlan(String buildingKey, int length, int width, int variation, int level, int startLevel, EntityPlayer player) {
        this.buildingKey = buildingKey;
        MillLog.temp((Object)this, "updatePlan : Updating buildingKey to: " + buildingKey);
        this.length = length;
        this.width = width;
        this.variation = variation;
        this.upgradeLevel = level;
        this.startingLevel = startLevel;
        this.updateAttachedSign();
        if (player != null) {
            this.sendUpdates();
        }
    }

    public void updateSettings(int upgradeLevel, int orientation, int startingLevel, boolean exportSnow, boolean importMockBlocks, boolean autoconvertToPreserveGround, boolean exportRegularChests, EntityPlayer player) {
        this.upgradeLevel = upgradeLevel;
        this.orientation = orientation;
        this.startingLevel = startingLevel;
        this.exportSnow = exportSnow;
        this.importMockBlocks = importMockBlocks;
        this.autoconvertToPreserveGround = autoconvertToPreserveGround;
        this.exportRegularChests = exportRegularChests;
        this.updateAttachedSign();
        if (player != null) {
            this.sendUpdates();
        }
    }

    public NBTTagCompound func_189515_b(NBTTagCompound compound) {
        super.func_189515_b(compound);
        if (this.buildingKey != null) {
            compound.func_74778_a("buildingKey", this.buildingKey);
        }
        compound.func_74768_a("variation", this.variation);
        compound.func_74768_a("length", this.length);
        compound.func_74768_a("width", this.width);
        compound.func_74768_a("upgradeLevel", this.upgradeLevel);
        compound.func_74768_a("startingLevel", this.startingLevel);
        compound.func_74768_a("orientation", this.orientation);
        compound.func_74757_a("exportSnow", this.exportSnow);
        compound.func_74757_a("importMockBlocks", this.importMockBlocks);
        compound.func_74757_a("autoconvertToPreserveGround", this.autoconvertToPreserveGround);
        compound.func_74757_a("exportRegularChests", this.exportRegularChests);
        if (this.parentTablePos != null) {
            this.parentTablePos.write(compound, "parentTablePos");
        }
        return compound;
    }
}

