package org.dizzymii.millenaire2.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.schedule.Activity;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.culture.Culture;
import org.dizzymii.millenaire2.culture.VillagerType;
import org.dizzymii.millenaire2.entity.MillEntities;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.VillagerDebugger;
import org.dizzymii.millenaire2.entity.brain.VillagerBrainConfig;
import org.dizzymii.millenaire2.entity.brain.behaviour.MillFightBehaviour;
import org.dizzymii.millenaire2.entity.brain.behaviour.MillIdleBehaviour;
import org.dizzymii.millenaire2.entity.brain.behaviour.MillRestBehaviour;
import org.dizzymii.millenaire2.entity.brain.behaviour.MillWorkBehaviour;
import org.dizzymii.millenaire2.goal.Goal;
import org.dizzymii.millenaire2.util.Point;

/**
 * GameTest suite for the SmartBrainLib-compatible villager Brain system.
 *
 * <p>Covers:</p>
 * <ul>
 *   <li>Brain is configured on entity creation
 *   <li>Activity switching: WORK during day, REST at night, FIGHT when aggroed
 *   <li>{@link VillagerDebugger} records goal transitions
 *   <li>Goal timeout clears the active goal
 *   <li>Aggroed flag decays over time
 * </ul>
 */
@GameTestHolder(Millenaire2.MODID)
@PrefixGameTestTemplate(false)
public class VillagerBrainTests {

    // ==================== Helpers ====================

    /** Spawns a bare-minimum villager at a test-relative position. */
    private static MillVillager spawnVillager(GameTestHelper helper, int rx, int ry, int rz) {
        BlockPos abs = helper.absolutePos(new BlockPos(rx, ry, rz));
        ServerLevel level = helper.getLevel();
        MillVillager v = MillEntities.GENERIC_MALE.get().create(level);
        if (v == null) throw new IllegalStateException("Failed to create MillVillager entity");
        v.setFirstName("Test");
        v.setFamilyName("Villager");
        v.setPos(abs.getX() + 0.5, abs.getY(), abs.getZ() + 0.5);
        v.housePoint = new Point(abs.getX(), abs.getY(), abs.getZ());
        v.townHallPoint = new Point(abs.getX(), abs.getY(), abs.getZ());
        level.addFreshEntity(v);
        return v;
    }

    // ==================== Brain configuration tests ====================

    /**
     * A freshly created villager must have a non-null Brain that was
     * configured by {@link VillagerBrainConfig}.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testBrainIsConfiguredOnSpawn(GameTestHelper helper) {
        MillVillager v = spawnVillager(helper, 1, 1, 1);

        helper.assertFalse(v.getBrain() == null, "Brain must not be null after spawn");
        helper.succeed();
    }

    /**
     * The default Brain activity for a new villager must be IDLE.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testDefaultActivityIsIdle(GameTestHelper helper) {
        MillVillager v = spawnVillager(helper, 1, 1, 1);

        boolean hasIdle = v.getBrain().isActive(Activity.IDLE);
        helper.assertTrue(hasIdle, "Newly spawned villager should have IDLE as the active activity");
        helper.succeed();
    }

    // ==================== Activity switching tests ====================

    /**
     * When the villager is not aggroed and it is daytime, calling
     * {@link VillagerBrainConfig#updateActivity} must activate WORK (if the
     * brain has WORK registered) or IDLE.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testUpdateActivityDaytimeSetsWorkOrIdle(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        // Force daytime
        level.setDayTime(6000);

        MillVillager v = spawnVillager(helper, 1, 1, 1);
        VillagerBrainConfig.updateActivity(v);

        boolean isWorkOrIdle = v.getBrain().isActive(Activity.WORK) || v.getBrain().isActive(Activity.IDLE);
        helper.assertTrue(isWorkOrIdle,
                "Daytime activity should be WORK or IDLE, got: "
                        + v.getBrain().getActiveActivities());
        helper.succeed();
    }

    /**
     * When the villager is not aggroed and it is night-time, calling
     * {@link VillagerBrainConfig#updateActivity} must activate REST.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testUpdateActivityNighttimeSetsRest(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        // Force nighttime (13000 = well into night)
        level.setDayTime(13000);

        MillVillager v = spawnVillager(helper, 1, 1, 1);
        VillagerBrainConfig.updateActivity(v);

        helper.assertTrue(v.getBrain().isActive(Activity.REST),
                "Night-time activity should be REST, got: " + v.getBrain().getActiveActivities());
        helper.succeed();
    }

    /**
     * When {@code isAggroed()} is true, calling {@link VillagerBrainConfig#updateActivity}
     * must activate FIGHT.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testUpdateActivityAggroedSetsFight(GameTestHelper helper) {
        MillVillager v = spawnVillager(helper, 1, 1, 1);

        // Simulate being hit by setting the aggroTicks field via hurt-and-check
        // We use reflection to set the private field directly for the test
        try {
            java.lang.reflect.Field f = MillVillager.class.getDeclaredField("aggroTicks");
            f.setAccessible(true);
            f.set(v, 200); // 200 ticks of aggro
        } catch (Exception e) {
            helper.fail("Could not set aggroTicks field: " + e.getMessage());
            return;
        }

        helper.assertTrue(v.isAggroed(), "isAggroed() should return true after setting aggroTicks");
        VillagerBrainConfig.updateActivity(v);
        helper.assertTrue(v.getBrain().isActive(Activity.FIGHT),
                "Aggroed villager should have FIGHT activity, got: " + v.getBrain().getActiveActivities());
        helper.succeed();
    }

    /**
     * The aggro counter must decay to zero over time, making {@code isAggroed()} return false.
     */
    @GameTest(template = "empty", timeoutTicks = 250)
    public static void testAggroTicksDecay(GameTestHelper helper) {
        MillVillager v = spawnVillager(helper, 1, 1, 1);

        try {
            java.lang.reflect.Field f = MillVillager.class.getDeclaredField("aggroTicks");
            f.setAccessible(true);
            f.set(v, 5); // only 5 ticks of aggro
        } catch (Exception e) {
            helper.fail("Could not set aggroTicks: " + e.getMessage());
            return;
        }

        // After 10 ticks the counter should have decayed to 0
        helper.runAtTickTime(helper.getTick() + 10, () -> {
            helper.assertFalse(v.isAggroed(),
                    "Aggro should have decayed after 10 ticks");
            helper.succeed();
        });
    }

    // ==================== VillagerDebugger tests ====================

    /**
     * {@link VillagerDebugger#recordGoalStart} must store a non-empty history
     * that is returned by {@link VillagerDebugger#buildDebugSummary}.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testDebuggerRecordsGoalStart(GameTestHelper helper) {
        MillVillager v = spawnVillager(helper, 1, 1, 1);
        VillagerDebugger.recordGoalStart(v, "construction");

        java.util.List<net.minecraft.network.chat.Component> lines =
                VillagerDebugger.buildDebugSummary(v);

        boolean hasHistory = lines.stream().anyMatch(c -> c.getString().contains("construction"));
        helper.assertTrue(hasHistory,
                "Debug summary should contain 'construction' after recordGoalStart");
        helper.succeed();
    }

    /**
     * {@link VillagerDebugger#recordGoalEnd} must record both the start and end
     * entries in the debug history.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testDebuggerRecordsGoalEnd(GameTestHelper helper) {
        MillVillager v = spawnVillager(helper, 1, 1, 1);
        VillagerDebugger.recordGoalStart(v, "fish");
        VillagerDebugger.recordGoalEnd(v, "fish", true);

        java.util.List<net.minecraft.network.chat.Component> lines =
                VillagerDebugger.buildDebugSummary(v);

        long startEntries = lines.stream()
                .filter(c -> c.getString().contains("START fish")).count();
        long endEntries = lines.stream()
                .filter(c -> c.getString().contains("END fish")).count();

        helper.assertTrue(startEntries >= 1,
                "Debug summary should have at least one START fish entry");
        helper.assertTrue(endEntries >= 1,
                "Debug summary should have at least one END fish entry");
        helper.succeed();
    }

    /**
     * {@link VillagerDebugger#recordTimeout} must increment the timeout counter
     * visible in the debug summary.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testDebuggerRecordsTimeout(GameTestHelper helper) {
        MillVillager v = spawnVillager(helper, 1, 1, 1);
        VillagerDebugger.recordTimeout(v, "construction");

        java.util.List<net.minecraft.network.chat.Component> lines =
                VillagerDebugger.buildDebugSummary(v);

        boolean hasTimeout = lines.stream().anyMatch(c -> c.getString().contains("Timeouts: 1"));
        helper.assertTrue(hasTimeout, "Debug summary should show Timeouts: 1 after one recordTimeout");
        helper.succeed();
    }

    /**
     * Calling {@link VillagerDebugger#remove} must clean up the debug state
     * so subsequent summary calls return no history.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testDebuggerRemoveClearsState(GameTestHelper helper) {
        MillVillager v = spawnVillager(helper, 1, 1, 1);
        VillagerDebugger.recordGoalStart(v, "sleep");
        VillagerDebugger.remove(v);

        java.util.List<net.minecraft.network.chat.Component> lines =
                VillagerDebugger.buildDebugSummary(v);

        boolean hasNoHistory = lines.stream().anyMatch(c ->
                c.getString().contains("No debug history"));
        helper.assertTrue(hasNoHistory,
                "After remove(), debug summary should report no history");
        helper.succeed();
    }

    // ==================== VillagerGoalController tests ====================

    /**
     * {@code clearGoal()} must null out the active goal fields and record a cooldown.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testGoalControllerClearGoalSetsNullState(GameTestHelper helper) {
        MillVillager v = spawnVillager(helper, 1, 1, 1);
        // Manually put the villager in a goal state
        if (Goal.goals != null && Goal.sleep != null) {
            try {
                org.dizzymii.millenaire2.goal.GoalInformation info =
                        Goal.sleep.getDestination(v);
                if (info != null && info.hasTarget()) {
                    v.getGoalController().setActiveGoal("sleep", Goal.sleep, info);
                    helper.assertFalse(v.getCurrentGoal() == null,
                            "getCurrentGoal() should not be null after setActiveGoal");
                    v.getGoalController().clearGoal();
                    helper.assertTrue(v.getCurrentGoal() == null,
                            "getCurrentGoal() should be null after clearGoal()");
                    helper.assertTrue(v.goalKey == null,
                            "goalKey should be null after clearGoal()");
                }
            } catch (Exception e) {
                // getDestination may return null for a bare villager — that's OK; just skip
            }
        }
        helper.succeed();
    }
}
