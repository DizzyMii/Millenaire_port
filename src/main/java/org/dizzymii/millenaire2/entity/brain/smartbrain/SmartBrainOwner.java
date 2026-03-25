package org.dizzymii.millenaire2.entity.brain.smartbrain;

import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Marks a mob as owning a SmartBrain-managed AI.
 *
 * <p><b>SmartBrainLib migration note:</b> when {@code net.tslat.smartbrainlib}
 * is added as a Gradle dependency (see build.gradle), delete this file and
 * change the import in {@link org.dizzymii.millenaire2.entity.MillVillager} to
 * {@code net.tslat.smartbrainlib.api.SmartBrainOwner}.  All method signatures
 * are intentionally identical to the upstream library so the swap is mechanical.
 *
 * <p>Upstream: <a href="https://github.com/Tslat/SmartBrainLib">SmartBrainLib</a>
 * (Maven: {@code net.tslat.smartbrainlib:SmartBrainLib-neoforge:<mc_version>-<lib_version>}
 * from {@code https://maven.tslat.net}).
 */
public interface SmartBrainOwner<E extends LivingEntity> {

    /** Sensors that populate Brain memories for this entity (may return empty list). */
    default List<ExtendedSensor<? super E>> getSensors() {
        return List.of();
    }

    /**
     * Core tasks — run every tick regardless of current activity.
     * Must not be empty; return {@link BrainActivityGroup#empty()} if unused.
     */
    BrainActivityGroup<E> getCoreTasks();

    /** Behaviours executed while the entity is idle (default daytime activity). */
    BrainActivityGroup<E> getIdleTasks();

    /**
     * Behaviours executed during the WORK activity (daytime productive work).
     * Override to add job-specific behaviours.
     */
    default BrainActivityGroup<E> getWorkTasks() {
        return BrainActivityGroup.empty();
    }

    /**
     * Behaviours executed during the REST activity (sleep at night).
     * Override to add rest/sleep behaviours.
     */
    default BrainActivityGroup<E> getRestTasks() {
        return BrainActivityGroup.empty();
    }

    /**
     * Behaviours executed during the FIGHT activity (combat / defend village).
     * Override to add combat behaviours.
     */
    default BrainActivityGroup<E> getFightTasks() {
        return BrainActivityGroup.empty();
    }

    /**
     * Returns all non-empty activity groups in priority order.
     * Core is always first; fight always before work; work before rest; rest before idle.
     */
    default List<BrainActivityGroup<E>> getAllBrainActivities() {
        List<BrainActivityGroup<E>> list = new ArrayList<>();
        BrainActivityGroup<E> core  = getCoreTasks();
        BrainActivityGroup<E> fight = getFightTasks();
        BrainActivityGroup<E> work  = getWorkTasks();
        BrainActivityGroup<E> rest  = getRestTasks();
        BrainActivityGroup<E> idle  = getIdleTasks();
        if (!core.isEmpty())  list.add(core);
        if (!fight.isEmpty()) list.add(fight);
        if (!work.isEmpty())  list.add(work);
        if (!rest.isEmpty())  list.add(rest);
        if (!idle.isEmpty())  list.add(idle);
        return list;
    }
}
