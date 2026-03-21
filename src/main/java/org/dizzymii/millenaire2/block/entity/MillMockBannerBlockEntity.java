package org.dizzymii.millenaire2.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Block entity for mock banners (custom village banners with culture patterns).
 * Ported from org.millenaire.common.entity.TileEntityMockBanner (Forge 1.12.2).
 * Stores culture ID and banner pattern name for rendering.
 */
public class MillMockBannerBlockEntity extends BlockEntity {

    private String cultureId = "";
    private String patternName = "";

    public MillMockBannerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public String getCultureId() { return cultureId; }
    public void setCultureId(String id) { this.cultureId = id; setChanged(); }

    public String getPatternName() { return patternName; }
    public void setPatternName(String name) { this.patternName = name; setChanged(); }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("CultureId", cultureId);
        tag.putString("PatternName", patternName);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.cultureId = tag.getString("CultureId");
        this.patternName = tag.getString("PatternName");
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
