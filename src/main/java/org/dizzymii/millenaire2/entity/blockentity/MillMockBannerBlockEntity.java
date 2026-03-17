package org.dizzymii.millenaire2.entity.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Block entity for mock banners (custom village banners with culture patterns).
 * Ported from org.millenaire.common.entity.TileEntityMockBanner (Forge 1.12.2).
 */
public class MillMockBannerBlockEntity extends BlockEntity {
    public MillMockBannerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    // TODO: Implement custom banner pattern storage and NBT save/load
}
