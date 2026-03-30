package org.dizzymii.millenaire2.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;

/**
 * Ghast variant associated with a village defense position rather than a specific combat target.
 * The village position is stored and persisted for future AI-based targeting; current AI uses
 * standard Ghast targeting (nearest players).
 * Ported from org.millenaire.common.entity.EntityTargetedGhast (Forge 1.12.2).
 */
public class EntityTargetedGhast extends Ghast {

    private static final String NBT_VILLAGE_TARGET = "villageTarget";

    /** Village position this entity was summoned to defend — distinct from AI combat targeting. */
    @Nullable
    private Point target = null;

    public EntityTargetedGhast(EntityType<? extends EntityTargetedGhast> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // Placeholder: target players until per-entity village-defense targeting is implemented.
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Nullable
    public Point getTarget() { return target; }

    public void setTarget(@Nullable Point target) { this.target = target; }
    public Point getVillageTarget() { return target; }

    public void setVillageTarget(@Nullable Point target) { this.target = target; }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (target != null) {
            target.writeToNBT(tag, "Target");
            target.writeToNBT(tag, NBT_VILLAGE_TARGET);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        target = Point.readFromNBT(tag, "Target");
        target = Point.readFromNBT(tag, NBT_VILLAGE_TARGET);
    }
}
