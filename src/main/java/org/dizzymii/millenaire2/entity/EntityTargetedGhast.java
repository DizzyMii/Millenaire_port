package org.dizzymii.millenaire2.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Ghast variant summoned by villagers to attack a specific target.
 * Ported from org.millenaire.common.entity.EntityTargetedGhast (Forge 1.12.2).
 */
public class EntityTargetedGhast extends Ghast {

    @Nullable public Point target = null;
    private int lifetimeTicks = 0;
    private static final int MAX_LIFETIME = 1200; // 60 seconds

    public EntityTargetedGhast(EntityType<? extends EntityTargetedGhast> type, Level level) {
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
                    target.x - 16, target.y - 8, target.z - 16,
                    target.x + 16, target.y + 8, target.z + 16);
            List<LivingEntity> candidates = this.level().getEntitiesOfClass(
                    LivingEntity.class, searchBox,
                    e -> !(e instanceof EntityTargetedGhast) && !(e instanceof MillVillager) && e.isAlive());
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

        // Fly toward target point if no attack target found
        if (this.getTarget() == null) {
            this.getMoveControl().setWantedPosition(target.x + 0.5, target.y + 10.0, target.z + 0.5, 1.0);
        }
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
