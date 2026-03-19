package org.dizzymii.millenaire2.entity.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.entity.MillEntities;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;

/**
 * Block entity for the Village Stone.
 * Stores the townhall position this stone is linked to.
 */
public class VillageStoneBlockEntity extends BlockEntity {

    @Nullable
    private Point townHallPos;

    public VillageStoneBlockEntity(BlockPos pos, BlockState state) {
        super(MillEntities.VILLAGE_STONE_BE.get(), pos, state);
    }

    @Nullable
    public Point getTownHallPos() {
        return townHallPos;
    }

    public void setTownHallPos(@Nullable Point pos) {
        this.townHallPos = pos;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (townHallPos != null) {
            townHallPos.writeToNBT(tag, "townhall");
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        townHallPos = Point.readFromNBT(tag, "townhall");
    }
}
