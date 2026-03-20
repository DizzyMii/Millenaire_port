package org.dizzymii.sblpoc.ai.goap;

import net.minecraft.world.item.Item;
import org.dizzymii.sblpoc.ai.world.BlockCategory;
import org.dizzymii.sblpoc.ai.world.InventoryModel;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A single atomic action in the GOAP planning system.
 * Each action has preconditions that must be true to execute,
 * and effects that become true after execution.
 *
 * Actions are matched against the current WorldState during A* planning.
 */
public class GoapAction {

    private final String name;
    private final float cost;
    private final int estimatedTicks;
    private final ActionType type;

    // Preconditions: predicates that must all be true on the world state
    private final List<Predicate<WorldState.MutableWorldState>> preconditions;

    // Effects: mutations applied to a mutable world state during forward simulation
    private final List<Consumer<WorldState.MutableWorldState>> effects;

    // Goal conditions this action can satisfy (for backward relevance check)
    private final Set<String> satisfiesFlags;
    private final Map<Item, Integer> producesItems;
    private final InventoryModel.ToolTier producesToolTier;

    private GoapAction(Builder builder) {
        this.name = builder.name;
        this.cost = builder.cost;
        this.estimatedTicks = builder.estimatedTicks;
        this.type = builder.type;
        this.preconditions = List.copyOf(builder.preconditions);
        this.effects = List.copyOf(builder.effects);
        this.satisfiesFlags = Set.copyOf(builder.satisfiesFlags);
        this.producesItems = Map.copyOf(builder.producesItems);
        this.producesToolTier = builder.producesToolTier;
    }

    // ========== Getters ==========

    public String getName() { return name; }
    public float getCost() { return cost; }
    public int getEstimatedTicks() { return estimatedTicks; }
    public ActionType getType() { return type; }
    public Set<String> getSatisfiesFlags() { return satisfiesFlags; }
    public Map<Item, Integer> getProducesItems() { return producesItems; }
    public InventoryModel.ToolTier getProducesToolTier() { return producesToolTier; }

    /**
     * Check if all preconditions are met in the given state.
     */
    public boolean canExecute(WorldState.MutableWorldState state) {
        for (Predicate<WorldState.MutableWorldState> pre : preconditions) {
            if (!pre.test(state)) return false;
        }
        return true;
    }

    /**
     * Apply this action's effects to a mutable state (forward simulation).
     */
    public void applyEffects(WorldState.MutableWorldState state) {
        for (Consumer<WorldState.MutableWorldState> effect : effects) {
            effect.accept(state);
        }
    }

    @Override
    public String toString() {
        return "GoapAction[" + name + " cost=" + cost + "]";
    }

    // ========== Action Types ==========

    public enum ActionType {
        GATHER,     // Mining, chopping, harvesting
        CRAFT,      // Crafting, smelting, brewing, enchanting
        BUILD,      // Placing blocks, building structures
        NAVIGATE,   // Moving to a location
        COMBAT,     // Fighting
        SURVIVE,    // Eating, healing, sleeping
        INTERACT    // Trading, opening containers
    }

    // ========== Builder ==========

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static class Builder {
        private final String name;
        private float cost = 1.0f;
        private int estimatedTicks = 20;
        private ActionType type = ActionType.GATHER;
        private final List<Predicate<WorldState.MutableWorldState>> preconditions = new ArrayList<>();
        private final List<Consumer<WorldState.MutableWorldState>> effects = new ArrayList<>();
        private final Set<String> satisfiesFlags = new HashSet<>();
        private final Map<Item, Integer> producesItems = new HashMap<>();
        private InventoryModel.ToolTier producesToolTier = null;

        Builder(String name) {
            this.name = name;
        }

        public Builder cost(float cost) { this.cost = cost; return this; }
        public Builder estimatedTicks(int ticks) { this.estimatedTicks = ticks; return this; }
        public Builder type(ActionType type) { this.type = type; return this; }

        // --- Preconditions ---

        public Builder requireItem(Item item, int count) {
            preconditions.add(s -> s.hasItem(item, count));
            return this;
        }

        public Builder requireFlag(String flag) {
            preconditions.add(s -> s.hasFlag(flag));
            return this;
        }

        public Builder requireNearbyStation(BlockCategory station) {
            preconditions.add(s -> s.hasNearbyStation(station));
            return this;
        }

        public Builder requireToolTier(InventoryModel.ToolTier minTier) {
            preconditions.add(s -> s.toolTier.ordinal() >= minTier.ordinal());
            return this;
        }

        public Builder requireCustom(Predicate<WorldState.MutableWorldState> predicate) {
            preconditions.add(predicate);
            return this;
        }

        // --- Effects ---

        public Builder produceItem(Item item, int count) {
            producesItems.put(item, count);
            effects.add(s -> s.addItem(item, count));
            return this;
        }

        public Builder consumeItem(Item item, int count) {
            // Also add as precondition
            preconditions.add(s -> s.hasItem(item, count));
            effects.add(s -> s.removeItem(item, count));
            return this;
        }

        public Builder setFlag(String flag) {
            satisfiesFlags.add(flag);
            effects.add(s -> s.setFlag(flag));
            return this;
        }

        public Builder upgradeToolTier(InventoryModel.ToolTier tier) {
            this.producesToolTier = tier;
            effects.add(s -> {
                if (tier.ordinal() > s.toolTier.ordinal()) {
                    s.toolTier = tier;
                }
            });
            return this;
        }

        public Builder customEffect(Consumer<WorldState.MutableWorldState> effect) {
            effects.add(effect);
            return this;
        }

        public GoapAction build() {
            return new GoapAction(this);
        }
    }
}
