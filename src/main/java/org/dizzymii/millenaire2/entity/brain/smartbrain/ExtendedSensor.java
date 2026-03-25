package org.dizzymii.millenaire2.entity.brain.smartbrain;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

/**
 * Stub sensor interface matching the SmartBrainLib {@code ExtendedSensor} contract.
 *
 * <p>Sensors are polled every {@link #getScanRate} ticks and should write results
 * into Brain memory slots.
 *
 * <p><b>SmartBrainLib migration note:</b> replace with
 * {@code net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor} and remove this file.
 */
public abstract class ExtendedSensor<E extends LivingEntity> {

    /** How often (in ticks) this sensor is polled. Default: every 20 ticks. */
    public int getScanRate(E entity) {
        return 20;
    }

    /** Perform the sense operation and write memories to entity.getBrain(). */
    protected abstract void doTick(ServerLevel level, E entity);

    /** Called by the brain to maybe poll this sensor. */
    public final void tick(ServerLevel level, E entity) {
        if (level.getGameTime() % getScanRate(entity) == 0) {
            doTick(level, entity);
        }
    }
}
