package org.dizzymii.millenaire2.entity.brain.sensor;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.Monster;
import org.dizzymii.millenaire2.entity.HumanoidNPC;
import org.dizzymii.millenaire2.entity.brain.ModMemoryTypes;
import org.dizzymii.millenaire2.entity.brain.smartbrain.ExtendedSensor;

import java.util.List;

/**
 * Sensor that evaluates immediate self-preservation state.
 *
 * <p>Writes:
 * <ul>
 *   <li>{@link ModMemoryTypes#NEEDS_HEALING} — {@code true} if health &lt; 40 %.
 *   <li>{@link ModMemoryTypes#LAST_KNOWN_DANGER} — nearest hostile position
 *       within 15 blocks, erased when no hostiles are nearby.
 * </ul>
 */
public class SelfPreservationSensor extends ExtendedSensor<HumanoidNPC> {

    private static final float LOW_HEALTH_THRESHOLD = 0.40f;
    private static final double HOSTILE_SCAN_RADIUS = 15.0D;

    @Override
    public int getScanRate(HumanoidNPC entity) {
        return 5;
    }

    @Override
    protected void doTick(ServerLevel level, HumanoidNPC entity) {
        float healthPct = entity.getHealth() / entity.getMaxHealth();
        boolean needsHealing = healthPct < LOW_HEALTH_THRESHOLD;
        entity.getBrain().setMemory(ModMemoryTypes.NEEDS_HEALING.get(), needsHealing);

        List<Monster> hostiles = level.getEntitiesOfClass(
                Monster.class,
                entity.getBoundingBox().inflate(HOSTILE_SCAN_RADIUS),
                Monster::isAlive
        );

        if (hostiles.isEmpty()) {
            entity.getBrain().eraseMemory(ModMemoryTypes.LAST_KNOWN_DANGER.get());
            return;
        }

        Monster closest = hostiles.stream()
                .min((a, b) -> Double.compare(a.distanceToSqr(entity), b.distanceToSqr(entity)))
                .orElse(null);
        if (closest != null) {
            BlockPos dangerPos = closest.blockPosition();
            entity.getBrain().setMemory(ModMemoryTypes.LAST_KNOWN_DANGER.get(), dangerPos);
        }
    }
}
