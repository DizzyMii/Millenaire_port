package org.dizzymii.millenaire2.entity;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * Wall decoration entity (tapestries, statues, icons, hide hangings).
 * Ported from org.millenaire.common.entity.EntityWallDecoration (Forge 1.12.2).
 *
 * Stubbed as plain Entity for now. Full HangingEntity behaviour, rendering,
 * and drop logic will be implemented in Phase 8 (client rendering & GUI).
 */
public class EntityWallDecoration extends Entity {

    public static final int NORMAN_TAPESTRY = 1;
    public static final int INDIAN_STATUE = 2;
    public static final int MAYAN_STATUE = 3;
    public static final int BYZANTINE_ICON_SMALL = 4;
    public static final int BYZANTINE_ICON_MEDIUM = 5;
    public static final int BYZANTINE_ICON_LARGE = 6;
    public static final int HIDE_HANGING = 7;
    public static final int JAPANESE_PAINTING_SMALL = 8;
    public static final int JAPANESE_PAINTING_MEDIUM = 9;
    public static final int JAPANESE_PAINTING_LARGE = 10;

    private static final EntityDataAccessor<Integer> DATA_DECORATION_TYPE =
            SynchedEntityData.defineId(EntityWallDecoration.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_FACING =
            SynchedEntityData.defineId(EntityWallDecoration.class, EntityDataSerializers.INT);

    public EntityWallDecoration(EntityType<? extends EntityWallDecoration> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_DECORATION_TYPE, 0);
        builder.define(DATA_FACING, Direction.NORTH.get2DDataValue());
    }

    public int getDecorationType() { return this.entityData.get(DATA_DECORATION_TYPE); }
    public void setDecorationType(int type) { this.entityData.set(DATA_DECORATION_TYPE, type); }

    public Direction getFacingDirection() { return Direction.from2DDataValue(this.entityData.get(DATA_FACING)); }
    public void setFacingDirection(Direction dir) { this.entityData.set(DATA_FACING, dir.get2DDataValue()); }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        setDecorationType(tag.getInt("decorationType"));
        setFacingDirection(Direction.from2DDataValue(tag.getInt("facing")));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("decorationType", getDecorationType());
        tag.putInt("facing", getFacingDirection().get2DDataValue());
    }

    @Override
    public void tick() {
        // TODO: implement survival checks, collision, etc.
    }
}
