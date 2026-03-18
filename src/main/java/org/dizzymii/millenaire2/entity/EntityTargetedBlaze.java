package org.dizzymii.millenaire2.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Blaze variant summoned by villagers to attack a specific target.
 * Ported from org.millenaire.common.entity.EntityTargetedBlaze (Forge 1.12.2).
 */
public class EntityTargetedBlaze extends Blaze {

    @Nullable public Point target = null;
    private int lifetimeTicks = 0;
    private static final int MAX_LIFETIME = 1200; // 60 seconds
    private static final double ATTACK_RANGE_SQ = 64.0; // 8 blocks

    public EntityTargetedBlaze(EntityType<? extends EntityTargetedBlaze> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) return;

        lifetimeTicks++;
        if (lifetimeTicks > MAX_LIFETIME) {
            this.discard();
            return;
        }

        if (target == null) return;

        // Find nearest living entity near the target point to attack
        if (this.getTarget() == null || !this.getTarget().isAlive()) {
            AABB searchBox = new AABB(
                    target.x - 8, target.y - 4, target.z - 8,
                    target.x + 8, target.y + 4, target.z + 8);
            List<LivingEntity> candidates = this.level().getEntitiesOfClass(
                    LivingEntity.class, searchBox,
                    e -> !(e instanceof EntityTargetedBlaze) && !(e instanceof MillVillager) && e.isAlive());
            if (!candidates.isEmpty()) {
                LivingEntity nearest = candidates.get(0);
                double bestDist = this.distanceToSqr(nearest);
                for (int i = 1; i < candidates.size(); i++) {
                    double d = this.distanceToSqr(candidates.get(i));
                    if (d < bestDist) { bestDist = d; nearest = candidates.get(i); }
                }
                this.setTarget(nearest);
            }
        }

        // Navigate toward target point if no attack target found
        if (this.getTarget() == null) {
            this.getNavigation().moveTo(target.x + 0.5, target.y + 1.0, target.z + 0.5, 1.0);
        }
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("lifetimeTicks", lifetimeTicks);
        if (target != null) target.writeToNBT(tag, "target");
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        lifetimeTicks = tag.getInt("lifetimeTicks");
        target = Point.readFromNBT(tag, "target");
    }
}
