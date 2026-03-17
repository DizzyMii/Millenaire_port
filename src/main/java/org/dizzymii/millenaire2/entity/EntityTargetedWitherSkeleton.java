package org.dizzymii.millenaire2.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.level.Level;
import org.dizzymii.millenaire2.util.Point;

/**
 * Wither Skeleton variant summoned by villagers to attack a specific target.
 * Ported from org.millenaire.common.entity.EntityTargetedWitherSkeleton (Forge 1.12.2).
 * TODO: Implement target-tracking AI in a later phase.
 */
public class EntityTargetedWitherSkeleton extends WitherSkeleton {

    public Point target = null;

    public EntityTargetedWitherSkeleton(EntityType<? extends EntityTargetedWitherSkeleton> type, Level level) {
        super(type, level);
    }
}
