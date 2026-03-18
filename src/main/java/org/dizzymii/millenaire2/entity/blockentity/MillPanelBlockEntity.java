package org.dizzymii.millenaire2.entity.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.entity.MillEntities;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Block entity for the village Panel (info sign).
 * Ported from org.millenaire.common.entity.TileEntityPanel.
 * Stores village name and culture for display on the panel.
 */
public class MillPanelBlockEntity extends BlockEntity {

    private String villageName = "";
    private String cultureName = "";
    private UUID villageId = null;

    public MillPanelBlockEntity(BlockPos pos, BlockState state) {
        super(MillEntities.PANEL_BE.get(), pos, state);
    }

    public String getVillageName() { return villageName; }
    public void setVillageName(String name) { this.villageName = name; setChanged(); }

    public String getCultureName() { return cultureName; }
    public void setCultureName(String name) { this.cultureName = name; setChanged(); }

    public UUID getVillageId() { return villageId; }
    public void setVillageId(UUID id) { this.villageId = id; setChanged(); }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("VillageName", villageName);
        tag.putString("CultureName", cultureName);
        if (villageId != null) {
            tag.putUUID("VillageId", villageId);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.villageName = tag.getString("VillageName");
        this.cultureName = tag.getString("CultureName");
        if (tag.hasUUID("VillageId")) {
            this.villageId = tag.getUUID("VillageId");
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
