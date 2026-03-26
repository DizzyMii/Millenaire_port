package org.dizzymii.millenaire2.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;

/**
 * Blaze variant summoned by villagers to attack a specific target.
 * Ported from org.millenaire.common.entity.EntityTargetedBlaze (Forge 1.12.2).
 */
public class EntityTargetedBlaze extends Blaze {

    /** Village position this entity was summoned to defend — distinct from AI combat targeting. */
    @Nullable
    private Point target = null;

    public EntityTargetedBlaze(EntityType<? extends EntityTargetedBlaze> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Nullable
    public Point getTarget() { return target; }

    public void setTarget(@Nullable Point target) { this.target = target; }

    @Override
    public boolean isOnFire() { return false; }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (target != null) {
            target.writeToNBT(tag, "Target");
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        target = Point.readFromNBT(tag, "Target");
    }
}
