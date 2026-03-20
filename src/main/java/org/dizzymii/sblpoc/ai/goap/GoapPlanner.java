package org.dizzymii.sblpoc.ai.goap;

import javax.annotation.Nullable;
import java.util.*;

/**
 * A* forward-search GOAP planner.
 *
 * Given a current WorldState and a goal condition, searches through available
 * actions to find the cheapest sequence that satisfies the goal.
 *
 * Budget: max 50ms per plan, max 15 actions deep, max 2000 nodes expanded.
 */
public class GoapPlanner {

    private static final int MAX_DEPTH = 15;
    private static final int MAX_NODES = 2000;
    private static final long MAX_TIME_MS = 50;

    private final List<GoapAction> availableActions;

    public GoapPlanner(List<GoapAction> availableActions) {
        this.availableActions = List.copyOf(availableActions);
    }

    /**
     * Plan a sequence of actions to achieve the given goal.
     *
     * @param startState The current world state snapshot
     * @param goal       The goal to satisfy
     * @return Ordered list of actions, or null if no plan found within budget
     */
    @Nullable
    public List<GoapAction> plan(WorldState startState, GoalCondition goal) {
        long startTime = System.currentTimeMillis();

        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(n -> n.f));
        Set<Integer> closed = new HashSet<>();

        WorldState.MutableWorldState initialState = startState.toMutable();
        Node startNode = new Node(initialState, null, null, 0, goal.heuristic(initialState));

        if (goal.isSatisfied(initialState)) {
            return Collections.emptyList(); // Already satisfied
        }

        open.add(startNode);
        int nodesExpanded = 0;

        while (!open.isEmpty()) {
            // Budget checks
            if (nodesExpanded >= MAX_NODES) break;
            if (System.currentTimeMillis() - startTime > MAX_TIME_MS) break;

            Node current = open.poll();
            nodesExpanded++;

            // Depth limit
            if (current.depth >= MAX_DEPTH) continue;

            // State hash for cycle detection
            int stateHash = stateHash(current.state);
            if (closed.contains(stateHash)) continue;
            closed.add(stateHash);

            // Try each available action
            for (GoapAction action : availableActions) {
                if (!action.canExecute(current.state)) continue;

                // Forward-simulate: clone state and apply effects
                WorldState.MutableWorldState nextState = cloneState(current.state);
                action.applyEffects(nextState);

                // Check if goal is now satisfied
                float g = current.g + action.getCost();
                float h = goal.heuristic(nextState);
                Node next = new Node(nextState, current, action, g, h);

                if (goal.isSatisfied(nextState)) {
                    // Reconstruct plan
                    return reconstructPlan(next);
                }

                // Only add if we haven't seen this state
                int nextHash = stateHash(nextState);
                if (!closed.contains(nextHash)) {
                    open.add(next);
                }
            }
        }

        return null; // No plan found within budget
    }

    private List<GoapAction> reconstructPlan(Node goalNode) {
        List<GoapAction> plan = new ArrayList<>();
        Node current = goalNode;
        while (current.action != null) {
            plan.add(current.action);
            current = current.parent;
        }
        Collections.reverse(plan);
        return plan;
    }

    /**
     * Clone a mutable world state for branching during search.
     */
    private WorldState.MutableWorldState cloneState(WorldState.MutableWorldState source) {
        // Build a temporary WorldState then convert back to mutable
        WorldState.Builder builder = WorldState.builder()
                .inventory(source.inventory)
                .flags(source.flags)
                .toolTier(source.toolTier)
                .armorTier(source.armorTier)
                .healthPercent(source.healthPercent)
                .foodSupply(source.foodSupply)
                .dayNumber(source.dayNumber)
                .isNight(source.isNight)
                .dimension(source.dimension);
        for (var station : source.nearbyStations) {
            builder.nearbyStation(station);
        }
        return builder.build().toMutable();
    }

    /**
     * Simple hash for cycle detection based on key state attributes.
     */
    private int stateHash(WorldState.MutableWorldState state) {
        int hash = state.inventory.hashCode();
        hash = 31 * hash + state.flags.hashCode();
        hash = 31 * hash + state.toolTier.ordinal();
        hash = 31 * hash + state.armorTier.ordinal();
        return hash;
    }

    // ========== Search Node ==========

    private static class Node {
        final WorldState.MutableWorldState state;
        final Node parent;
        final GoapAction action;
        final float g; // Cost so far
        final float f; // g + heuristic
        final int depth;

        Node(WorldState.MutableWorldState state, @Nullable Node parent,
             @Nullable GoapAction action, float g, float h) {
            this.state = state;
            this.parent = parent;
            this.action = action;
            this.g = g;
            this.f = g + h;
            this.depth = parent != null ? parent.depth + 1 : 0;
        }
    }

    // ========== Goal Condition ==========

    /**
     * Represents a goal the planner tries to achieve.
     * Provides both a satisfaction check and a heuristic for A*.
     */
    public interface GoalCondition {
        /**
         * Is this goal satisfied in the given state?
         */
        boolean isSatisfied(WorldState.MutableWorldState state);

        /**
         * Estimated remaining cost to satisfy this goal (admissible heuristic).
         * Return 0 if satisfied.
         */
        float heuristic(WorldState.MutableWorldState state);
    }
}
