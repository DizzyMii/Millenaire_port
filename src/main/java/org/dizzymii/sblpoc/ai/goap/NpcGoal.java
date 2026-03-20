package org.dizzymii.sblpoc.ai.goap;

import net.minecraft.world.item.Items;
import org.dizzymii.sblpoc.ai.world.InventoryModel;

import java.util.function.Function;

/**
 * High-level goals the NPC can pursue. Each goal has a dynamic score
 * function and a GOAP GoalCondition the planner uses to build a plan.
 */
public enum NpcGoal {

    // ========== Survival (highest urgency) ==========

    SURVIVE_IMMEDIATE(
            "Survive immediate danger",
            state -> {
                float score = 0;
                if (state.getHealthPercent() < 0.2f) score += 100;
                if (state.getFoodSupply() <= 0 && state.getHealthPercent() < 0.5f) score += 80;
                return score;
            },
            state -> new GoapPlanner.GoalCondition() {
                @Override
                public boolean isSatisfied(WorldState.MutableWorldState s) {
                    return s.healthPercent > 0.5f;
                }
                @Override
                public float heuristic(WorldState.MutableWorldState s) {
                    return s.healthPercent > 0.5f ? 0 : 3;
                }
            }
    ),

    EAT_FOOD(
            "Find and eat food",
            state -> {
                // Score increases as food supply drops
                int food = state.getFoodSupply();
                if (food >= 16) return 0f;
                return 60f * (1f - food / 16f);
            },
            state -> new GoapPlanner.GoalCondition() {
                @Override
                public boolean isSatisfied(WorldState.MutableWorldState s) {
                    return s.foodSupply >= 16;
                }
                @Override
                public float heuristic(WorldState.MutableWorldState s) {
                    return s.foodSupply >= 16 ? 0 : 2;
                }
            }
    ),

    // ========== Shelter ==========

    FIND_SHELTER(
            "Build or find shelter",
            state -> {
                if (state.hasFlag("has_home")) return 0f;
                float score = 50f;
                if (state.isNight()) score += 30f;
                return score;
            },
            state -> new GoapPlanner.GoalCondition() {
                @Override
                public boolean isSatisfied(WorldState.MutableWorldState s) {
                    return s.hasFlag("has_home");
                }
                @Override
                public float heuristic(WorldState.MutableWorldState s) {
                    return s.hasFlag("has_home") ? 0 : 5;
                }
            }
    ),

    // ========== Early Game Progression ==========

    GET_WOOD(
            "Gather wood logs",
            state -> {
                int logs = state.getItemCount(Items.OAK_LOG) + state.getItemCount(Items.BIRCH_LOG)
                        + state.getItemCount(Items.SPRUCE_LOG) + state.getItemCount(Items.DARK_OAK_LOG)
                        + state.getItemCount(Items.JUNGLE_LOG) + state.getItemCount(Items.ACACIA_LOG)
                        + state.getItemCount(Items.MANGROVE_LOG) + state.getItemCount(Items.CHERRY_LOG);
                if (logs >= 16) return 0f;
                return 40f * (1f - logs / 16f);
            },
            state -> new GoapPlanner.GoalCondition() {
                @Override
                public boolean isSatisfied(WorldState.MutableWorldState s) {
                    // Any 16 logs total
                    int logs = 0;
                    for (var entry : s.inventory.entrySet()) {
                        String name = entry.getKey().toString();
                        if (name.contains("log")) logs += entry.getValue();
                    }
                    return logs >= 16;
                }
                @Override
                public float heuristic(WorldState.MutableWorldState s) {
                    int logs = 0;
                    for (var entry : s.inventory.entrySet()) {
                        String name = entry.getKey().toString();
                        if (name.contains("log")) logs += entry.getValue();
                    }
                    return logs >= 16 ? 0 : (16 - logs) * 0.3f;
                }
            }
    ),

    GET_STONE_TOOLS(
            "Craft stone tools",
            state -> {
                if (state.getToolTier().ordinal() >= InventoryModel.ToolTier.STONE.ordinal()) return 0f;
                return 35f;
            },
            state -> new GoapPlanner.GoalCondition() {
                @Override
                public boolean isSatisfied(WorldState.MutableWorldState s) {
                    return s.toolTier.ordinal() >= InventoryModel.ToolTier.STONE.ordinal();
                }
                @Override
                public float heuristic(WorldState.MutableWorldState s) {
                    return s.toolTier.ordinal() >= InventoryModel.ToolTier.STONE.ordinal() ? 0 : 4;
                }
            }
    ),

    // ========== Mid Game Progression ==========

    GET_IRON_TOOLS(
            "Mine iron and craft iron tools",
            state -> {
                if (state.getToolTier().ordinal() >= InventoryModel.ToolTier.IRON.ordinal()) return 0f;
                if (state.getToolTier().ordinal() < InventoryModel.ToolTier.STONE.ordinal()) return 0f;
                return 30f;
            },
            state -> new GoapPlanner.GoalCondition() {
                @Override
                public boolean isSatisfied(WorldState.MutableWorldState s) {
                    return s.toolTier.ordinal() >= InventoryModel.ToolTier.IRON.ordinal();
                }
                @Override
                public float heuristic(WorldState.MutableWorldState s) {
                    return s.toolTier.ordinal() >= InventoryModel.ToolTier.IRON.ordinal() ? 0 : 6;
                }
            }
    ),

    GET_FOOD_SUPPLY(
            "Ensure steady food supply",
            state -> {
                int food = state.getFoodSupply();
                if (food >= 32) return 0f;
                if (food >= 16) return 10f;
                return 25f * (1f - food / 16f);
            },
            state -> new GoapPlanner.GoalCondition() {
                @Override
                public boolean isSatisfied(WorldState.MutableWorldState s) {
                    return s.foodSupply >= 32;
                }
                @Override
                public float heuristic(WorldState.MutableWorldState s) {
                    return s.foodSupply >= 32 ? 0 : 3;
                }
            }
    ),

    GET_ARMOR(
            "Craft or find armor",
            state -> {
                if (state.getArmorTier().ordinal() >= WorldState.ArmorTier.IRON.ordinal()) return 0f;
                if (state.getToolTier().ordinal() < InventoryModel.ToolTier.IRON.ordinal()) return 0f;
                return 20f;
            },
            state -> new GoapPlanner.GoalCondition() {
                @Override
                public boolean isSatisfied(WorldState.MutableWorldState s) {
                    return s.armorTier.ordinal() >= WorldState.ArmorTier.IRON.ordinal();
                }
                @Override
                public float heuristic(WorldState.MutableWorldState s) {
                    return s.armorTier.ordinal() >= WorldState.ArmorTier.IRON.ordinal() ? 0 : 8;
                }
            }
    ),

    // ========== Late Game ==========

    GET_DIAMOND_TOOLS(
            "Mine diamonds and craft diamond tools",
            state -> {
                if (state.getToolTier().ordinal() >= InventoryModel.ToolTier.DIAMOND.ordinal()) return 0f;
                if (state.getToolTier().ordinal() < InventoryModel.ToolTier.IRON.ordinal()) return 0f;
                return 15f;
            },
            state -> new GoapPlanner.GoalCondition() {
                @Override
                public boolean isSatisfied(WorldState.MutableWorldState s) {
                    return s.toolTier.ordinal() >= InventoryModel.ToolTier.DIAMOND.ordinal();
                }
                @Override
                public float heuristic(WorldState.MutableWorldState s) {
                    return s.toolTier.ordinal() >= InventoryModel.ToolTier.DIAMOND.ordinal() ? 0 : 10;
                }
            }
    ),

    // ========== Exploration ==========

    EXPLORE(
            "Explore new territory",
            state -> {
                // Always some desire to explore, more if we lack ore knowledge
                float score = 10f;
                if (!state.hasFlag("knows_iron_location")) score += 8f;
                if (!state.hasFlag("knows_diamond_location")) score += 5f;
                return score;
            },
            state -> new GoapPlanner.GoalCondition() {
                @Override
                public boolean isSatisfied(WorldState.MutableWorldState s) {
                    return s.hasFlag("explored_enough");
                }
                @Override
                public float heuristic(WorldState.MutableWorldState s) {
                    return s.hasFlag("explored_enough") ? 0 : 2;
                }
            }
    ),

    // ========== Idle Improvement ==========

    IDLE_IMPROVE(
            "Improve base or stockpile",
            state -> 2f, // Always available, lowest priority
            state -> new GoapPlanner.GoalCondition() {
                @Override
                public boolean isSatisfied(WorldState.MutableWorldState s) {
                    return s.hasFlag("base_improved");
                }
                @Override
                public float heuristic(WorldState.MutableWorldState s) {
                    return 1;
                }
            }
    );

    // ========== Fields ==========

    private final String description;
    private final Function<WorldState, Float> scoreFunction;
    private final Function<WorldState, GoapPlanner.GoalCondition> goalConditionFactory;

    NpcGoal(String description,
            Function<WorldState, Float> scoreFunction,
            Function<WorldState, GoapPlanner.GoalCondition> goalConditionFactory) {
        this.description = description;
        this.scoreFunction = scoreFunction;
        this.goalConditionFactory = goalConditionFactory;
    }

    public String getDescription() { return description; }

    /**
     * Score this goal given the current world state. Higher = more urgent.
     */
    public float score(WorldState state) {
        return scoreFunction.apply(state);
    }

    /**
     * Create a GOAP GoalCondition for the planner.
     */
    public GoapPlanner.GoalCondition createGoalCondition(WorldState state) {
        return goalConditionFactory.apply(state);
    }
}
