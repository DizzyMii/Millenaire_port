package org.dizzymii.millenaire2.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Per-villager AI debug tracker.
 *
 * <p>Tracks goal transitions, stuck events, and timeouts for every active
 * {@link MillVillager}.  Data is kept in memory only (no persistence) and is
 * bounded to prevent unbounded growth.
 *
 * <h3>Usage</h3>
 * <ul>
 *   <li>Brain behaviours call {@link #recordGoalStart}, {@link #recordGoalEnd},
 *       {@link #recordTimeout}, and {@link #checkStuck} during execution.
 *   <li>The {@code /millenaire debug villager} command calls
 *       {@link #buildDebugSummary} and sends the result to the requesting player.
 * </ul>
 */
public final class VillagerDebugger {

    // ========== Constants ==========

    private static final int MAX_HISTORY = 20;
    /** Ticks without position change before a villager is declared stuck. */
    private static final int STUCK_THRESHOLD_TICKS = 100;
    /** Distance² the villager must travel to be considered "moving". */
    private static final double MOVEMENT_THRESHOLD_SQ = 0.25;

    // ========== State ==========

    /** Keyed by UUID of the villager entity. */
    private static final Map<UUID, DebugState> STATES = new ConcurrentHashMap<>();

    private VillagerDebugger() {}

    // ========== Public API ==========

    /** Called by brain behaviours when a goal starts. */
    public static void recordGoalStart(MillVillager villager, String goalKey) {
        DebugState state = getOrCreate(villager);
        state.lastGoalKey = goalKey;
        state.goalStartTime = villager.level().getGameTime();
        state.stuckCounter = 0;
        state.lastPos = new Point(villager.blockPosition());

        String entry = "[+" + state.goalStartTime + "] START " + goalKey;
        pushHistory(state, entry);

        if (villager.extraLog) {
            MillLog.major(villager, entry);
        }
    }

    /** Called by brain behaviours when a goal ends. */
    public static void recordGoalEnd(MillVillager villager, @Nullable String goalKey, boolean success) {
        DebugState state = getOrCreate(villager);
        long duration = villager.level().getGameTime() - state.goalStartTime;
        String entry = "[+" + villager.level().getGameTime() + "] END " + goalKey
                + (success ? " OK" : " FAIL") + " (" + duration + " ticks)";
        pushHistory(state, entry);

        if (villager.extraLog) {
            MillLog.major(villager, entry);
        }
    }

    /** Called when a goal exceeds the timeout threshold. */
    public static void recordTimeout(MillVillager villager, @Nullable String goalKey) {
        DebugState state = getOrCreate(villager);
        state.timeoutCount++;
        String entry = "[+" + villager.level().getGameTime() + "] TIMEOUT " + goalKey;
        pushHistory(state, entry);
        MillLog.major(villager, entry);
    }

    /**
     * Called from the work behaviour during travel.  Increments a stuck counter
     * and emits a warning once the threshold is reached.
     */
    public static void checkStuck(MillVillager villager) {
        DebugState state = getOrCreate(villager);
        Point current = new Point(villager.blockPosition());

        if (state.lastPos != null && current.distanceToSq(state.lastPos) < MOVEMENT_THRESHOLD_SQ) {
            state.stuckCounter++;
            if (state.stuckCounter == STUCK_THRESHOLD_TICKS) {
                state.stuckCount++;
                String warn = "[+" + villager.level().getGameTime() + "] STUCK goal="
                        + villager.goalKey + " at " + current;
                pushHistory(state, warn);
                MillLog.major(villager, warn);
            }
        } else {
            state.stuckCounter = 0;
        }

        state.lastPos = current;
    }

    /** Removes tracking state for a villager (call on death or unload). */
    public static void remove(MillVillager villager) {
        STATES.remove(villager.getUUID());
    }

    // ========== Debug output ==========

    /**
     * Builds a multi-line debug summary for the given villager and sends it
     * to {@code player} as system chat messages.
     */
    public static void sendDebugToPlayer(MillVillager villager, ServerPlayer player) {
        List<Component> lines = buildDebugSummary(villager);
        for (Component line : lines) {
            player.sendSystemMessage(line);
        }
    }

    /** Returns a list of chat component lines describing the villager's debug state. */
    public static List<Component> buildDebugSummary(MillVillager villager) {
        List<Component> out = new ArrayList<>();
        DebugState state = STATES.get(villager.getUUID());

        String name = villager.getFirstName() + " " + villager.getFamilyName();
        String culture = villager.getCultureKey().isEmpty() ? "?" : villager.getCultureKey();
        String vtype = villager.vtypeKey != null ? villager.vtypeKey : "?";
        String goal = villager.goalKey != null ? villager.goalKey : "idle";
        String activity = getActivityName(villager);
        Point pos = new Point(villager.blockPosition());

        out.add(Component.literal("§6━━━ Villager Debug: §e" + name + " §6━━━"));
        out.add(Component.literal("§7Culture: §f" + culture + "  Type: §f" + vtype));
        out.add(Component.literal("§7Activity: §f" + activity + "  Goal: §f" + goal));
        out.add(Component.literal("§7Position: §f" + pos.x + ", " + pos.y + ", " + pos.z));

        if (villager.housePoint != null) {
            out.add(Component.literal("§7Home: §f" + villager.housePoint));
        }

        if (state != null) {
            long goalAge = villager.level() != null
                    ? villager.level().getGameTime() - state.goalStartTime : 0;
            out.add(Component.literal("§7Goal age: §f" + goalAge + " ticks"
                    + "  Stuck: §f" + state.stuckCount
                    + "  Timeouts: §f" + state.timeoutCount));
            out.add(Component.literal("§7Goal history (most recent first):"));
            List<String> history = state.history();
            for (int i = history.size() - 1; i >= 0; i--) {
                out.add(Component.literal("  §8" + history.get(i)));
            }
        } else {
            out.add(Component.literal("§7No debug history recorded yet."));
        }

        out.add(Component.literal("§6━━━━━━━━━━━━━━━━━━━━━━━━━━"));
        return out;
    }

    // ========== Private helpers ==========

    private static DebugState getOrCreate(MillVillager villager) {
        return STATES.computeIfAbsent(villager.getUUID(), k -> new DebugState());
    }

    private static void pushHistory(DebugState state, String entry) {
        if (state.historyDeque.size() >= MAX_HISTORY) {
            state.historyDeque.pollFirst();
        }
        state.historyDeque.addLast(entry);
    }

    private static String getActivityName(MillVillager villager) {
        try {
            return villager.getBrain().getActiveActivities()
                    .stream()
                    .map(a -> a.getName())
                    .reduce((a, b) -> a + "+" + b)
                    .orElse("none");
        } catch (Exception e) {
            return "?";
        }
    }

    // ========== Inner state record ==========

    private static final class DebugState {
        String lastGoalKey = "";
        long goalStartTime = 0;
        int stuckCounter = 0;
        int stuckCount = 0;
        int timeoutCount = 0;
        @Nullable Point lastPos = null;
        final Deque<String> historyDeque = new ArrayDeque<>(MAX_HISTORY);

        List<String> history() {
            return new ArrayList<>(historyDeque);
        }
    }
}
