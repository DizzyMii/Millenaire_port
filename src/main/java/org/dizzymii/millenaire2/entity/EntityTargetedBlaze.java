package org.dizzymii.millenaire2.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.level.Level;
import org.dizzymii.millenaire2.util.Point;

/**
 * Blaze variant summoned by villagers to attack a specific target.
 * Ported from org.millenaire.common.entity.EntityTargetedBlaze (Forge 1.12.2).
 * TODO: Implement target-tracking AI in a later phase.
 */
public class EntityTargetedBlaze extends Blaze {

    public Point target = null;

    public EntityTargetedBlaze(EntityType<? extends EntityTargetedBlaze> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean isOnFire() {
        return false;
    }
}
