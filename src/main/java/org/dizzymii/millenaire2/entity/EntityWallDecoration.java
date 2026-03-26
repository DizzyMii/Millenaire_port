package org.dizzymii.millenaire2.entity;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.dizzymii.millenaire2.item.MillItems;

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

    // ==================== Interaction ====================

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide) {
            dropItem();
            this.discard();
        }
        return true;
    }

    @Override
    public ItemStack getPickResult() {
        ItemStack drop = getDropItem();
        return drop.isEmpty() ? super.getPickResult() : drop;
    }

    // ==================== Drop Logic ====================

    /**
     * Maps the current decoration type constant to the corresponding
     * {@link MillItems} entry so the correct item is dropped / picked.
     */
    private ItemStack getDropItem() {
        return switch (getDecorationType()) {
            case NORMAN_TAPESTRY        -> new ItemStack(MillItems.TAPESTRY.get());
            case INDIAN_STATUE          -> new ItemStack(MillItems.INDIAN_STATUE.get());
            case MAYAN_STATUE           -> new ItemStack(MillItems.MAYAN_STATUE.get());
            case BYZANTINE_ICON_SMALL   -> new ItemStack(MillItems.BYZANTINE_ICON_SMALL.get());
            case BYZANTINE_ICON_MEDIUM  -> new ItemStack(MillItems.BYZANTINE_ICON_MEDIUM.get());
            case BYZANTINE_ICON_LARGE   -> new ItemStack(MillItems.BYZANTINE_ICON_LARGE.get());
            case HIDE_HANGING           -> new ItemStack(MillItems.HIDE_HANGING.get());
            case JAPANESE_PAINTING_SMALL  -> new ItemStack(MillItems.WALL_CARPET_SMALL.get());
            case JAPANESE_PAINTING_MEDIUM -> new ItemStack(MillItems.WALL_CARPET_MEDIUM.get());
            case JAPANESE_PAINTING_LARGE  -> new ItemStack(MillItems.WALL_CARPET_LARGE.get());
            default -> ItemStack.EMPTY;
        };
    }

    private void dropItem() {
        ItemStack drop = getDropItem();
        if (!drop.isEmpty()) {
            Level lvl = this.level();
            lvl.addFreshEntity(new ItemEntity(lvl, this.getX(), this.getY(), this.getZ(), drop));
        }
    }

    // ==================== Tick / Survival ====================

    @Override
    public void tick() {
        // Survival check: discard if block behind is air (decoration fell off wall)
        if (!this.level().isClientSide && this.tickCount % 100 == 0) {
            net.minecraft.core.BlockPos behind = this.blockPosition().relative(getFacingDirection().getOpposite());
            if (this.level().getBlockState(behind).isAir()) {
                dropItem();
                this.discard();
            }
        }
    }
}
