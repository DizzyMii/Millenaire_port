package org.dizzymii.millenaire2.entity.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.schedule.Activity;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.brain.behaviour.MillFightBehaviour;
import org.dizzymii.millenaire2.entity.brain.behaviour.MillIdleBehaviour;
import org.dizzymii.millenaire2.entity.brain.behaviour.MillRestBehaviour;
import org.dizzymii.millenaire2.entity.brain.behaviour.MillWorkBehaviour;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.Swim;
import org.dizzymii.millenaire2.entity.brain.smartbrain.BrainActivityGroup;

import java.util.List;

/**
 * Wires up the {@link Brain} for every {@link MillVillager}.
 *
 * <p>Activities are priority-ordered:
 * <ol>
 *   <li>CORE  – always active (basic navigation, look-at)
 *   <li>FIGHT – highest-priority interrupting activity when under attack
 *   <li>WORK  – daytime job loop
 *   <li>REST  – nighttime sleep
 *   <li>IDLE  – socialise / wander when nothing else applies
 * </ol>
 *
 * <p><b>SmartBrainLib migration note:</b> when the dependency is available,
 * call {@code SmartBrainProvider.of(villager)} inside
 * {@code MillVillager.brainProvider()} and delete this class.
 */
public final class VillagerBrainConfig {

    private VillagerBrainConfig() {}

    /**
     * Configures {@code brain} with all activities and behaviours.
     * Must be called from {@link MillVillager#makeBrain} after the Brain is created.
     */
    public static void configureBrain(Brain<MillVillager> brain) {
        for (BrainActivityGroup<MillVillager> group : buildGroups()) {
            registerGroup(brain, group);
        }

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.setActiveActivityIfPossible(Activity.IDLE);
    }

    /**
     * Updates the active Brain activity based on the villager's state.
     * Call this once per second (every 20 ticks) from {@link MillVillager}.
     */
    public static void updateActivity(MillVillager villager) {
        Brain<MillVillager> brain = villager.getBrain();

        if (villager.isAggroed()) {
            brain.setActiveActivityIfPossible(Activity.FIGHT);
            return;
        }

        long dayTime = villager.level().getDayTime() % 24000;
        // Night: ticks 12000–24000 (Minecraft day is 24000 ticks, night starts at 12000)
        boolean isNight = dayTime >= 12000;

        if (isNight) {
            brain.setActiveActivityIfPossible(Activity.REST);
        } else {
            // If villager has work goals, prefer WORK activity; otherwise fall back to IDLE
            if (villager.vtype != null && !villager.vtype.goals.isEmpty()) {
                brain.setActiveActivityIfPossible(Activity.WORK);
            } else {
                brain.setActiveActivityIfPossible(Activity.IDLE);
            }
        }
    }

    // ========== Private helpers ==========

    /**
     * Constructs the ordered list of {@link BrainActivityGroup}s that describe
     * all activities and their associated behaviour instances.
     * Groups with no behaviours are silently skipped by {@link #registerGroup}.
     *
     * @return the list of activity groups to register
     */
    private static List<BrainActivityGroup<MillVillager>> buildGroups() {
        return List.of(
            BrainActivityGroup.coreTasks(
                    new Swim(0.8F),
                    new LookAtTargetSink(45, 90),
                    new MoveToTargetSink()
            ),
            BrainActivityGroup.workTasks(new MillWorkBehaviour()),
            BrainActivityGroup.restTasks(new MillRestBehaviour()),
            BrainActivityGroup.idleTasks(new MillIdleBehaviour()),
            BrainActivityGroup.fightTasks(new MillFightBehaviour())
        );
    }

    /**
     * Registers all behaviours in {@code group} with the given {@code brain} under the
     * group's activity, assigning sequential integer priorities starting at 0.
     * Empty groups (no behaviours) are skipped.
     *
     * @param brain the brain to register the group with
     * @param group the activity group whose behaviours should be added
     */
    private static void registerGroup(Brain<MillVillager> brain,
                                       BrainActivityGroup<MillVillager> group) {
        if (group.isEmpty()) return;

        Activity activity = group.activity();
        ImmutableList<net.minecraft.world.entity.ai.behavior.BehaviorControl<? super MillVillager>> behaviours =
                group.behaviours();

        // addActivityWithConditions requires Pair<Integer, BehaviorControl> — assign sequential priorities
        ImmutableList.Builder<com.mojang.datafixers.util.Pair<Integer,
                net.minecraft.world.entity.ai.behavior.BehaviorControl<? super MillVillager>>> builder =
                ImmutableList.builder();

        for (int i = 0; i < behaviours.size(); i++) {
            builder.add(com.mojang.datafixers.util.Pair.of(i, behaviours.get(i)));
        }

        brain.addActivityWithConditions(activity, builder.build(), java.util.Set.of());
    }
}
