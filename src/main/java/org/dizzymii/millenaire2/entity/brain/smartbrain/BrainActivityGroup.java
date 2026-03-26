package org.dizzymii.millenaire2.entity.brain.smartbrain;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.schedule.Activity;

import java.util.List;

/**
 * Groups a set of {@link BehaviorControl} instances under one {@link Activity}.
 *
 * <p><b>SmartBrainLib migration note:</b> when {@code net.tslat.smartbrainlib}
 * becomes available as a dependency, delete this file and replace the import in
 * {@link org.dizzymii.millenaire2.entity.brain.VillagerBrainConfig} with
 * {@code net.tslat.smartbrainlib.api.core.BrainActivityGroup}.  The factory
 * method names and signatures are identical to the upstream library.
 */
public final class BrainActivityGroup<E extends LivingEntity> {

    private final Activity activity;
    private final ImmutableList<BehaviorControl<? super E>> behaviours;

    private BrainActivityGroup(Activity activity, List<BehaviorControl<? super E>> behaviours) {
        this.activity = activity;
        this.behaviours = ImmutableList.copyOf(behaviours);
    }

    // ========== Factory methods ==========

    /** Creates an empty group (used as a no-op placeholder). */
    public static <E extends LivingEntity> BrainActivityGroup<E> empty() {
        return new BrainActivityGroup<>(Activity.IDLE, List.of());
    }

    @SafeVarargs
    public static <E extends LivingEntity> BrainActivityGroup<E> coreTasks(BehaviorControl<? super E>... behaviours) {
        return new BrainActivityGroup<E>(Activity.CORE, java.util.Arrays.asList(behaviours));
    }

    @SafeVarargs
    public static <E extends LivingEntity> BrainActivityGroup<E> idleTasks(BehaviorControl<? super E>... behaviours) {
        return new BrainActivityGroup<E>(Activity.IDLE, java.util.Arrays.asList(behaviours));
    }

    @SafeVarargs
    public static <E extends LivingEntity> BrainActivityGroup<E> workTasks(BehaviorControl<? super E>... behaviours) {
        return new BrainActivityGroup<E>(Activity.WORK, java.util.Arrays.asList(behaviours));
    }

    @SafeVarargs
    public static <E extends LivingEntity> BrainActivityGroup<E> restTasks(BehaviorControl<? super E>... behaviours) {
        return new BrainActivityGroup<E>(Activity.REST, java.util.Arrays.asList(behaviours));
    }

    @SafeVarargs
    public static <E extends LivingEntity> BrainActivityGroup<E> fightTasks(BehaviorControl<? super E>... behaviours) {
        return new BrainActivityGroup<E>(Activity.FIGHT, java.util.Arrays.asList(behaviours));
    }

    @SafeVarargs
    public static <E extends LivingEntity> BrainActivityGroup<E> customTasks(
            Activity activity, BehaviorControl<? super E>... behaviours) {
        return new BrainActivityGroup<E>(activity, java.util.Arrays.asList(behaviours));
    }

    // ========== Accessors ==========

    public Activity activity() {
        return activity;
    }

    public ImmutableList<BehaviorControl<? super E>> behaviours() {
        return behaviours;
    }

    public boolean isEmpty() {
        return behaviours.isEmpty();
    }
}
