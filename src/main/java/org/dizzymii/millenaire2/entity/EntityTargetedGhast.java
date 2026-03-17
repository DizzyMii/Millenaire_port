package org.dizzymii.millenaire2.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.level.Level;
import org.dizzymii.millenaire2.util.Point;

/**
 * Ghast variant summoned by villagers to attack a specific target.
 * Ported from org.millenaire.common.entity.EntityTargetedGhast (Forge 1.12.2).
 * TODO: Implement target-tracking AI in a later phase.
 */
public class EntityTargetedGhast extends Ghast {

    public Point target = null;

    public EntityTargetedGhast(EntityType<? extends EntityTargetedGhast> type, Level level) {
        super(type, level);
    }
}
