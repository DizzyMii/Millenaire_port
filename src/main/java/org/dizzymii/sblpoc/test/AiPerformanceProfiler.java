package org.dizzymii.sblpoc.test;

import net.minecraft.world.item.Items;
import org.dizzymii.sblpoc.ai.goap.*;
import org.dizzymii.sblpoc.ai.world.BlockCategory;
import org.dizzymii.sblpoc.ai.world.InventoryModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Lightweight performance profiler for the NPC AI systems.
 * Can be triggered via a command or called during development.
 *
 * Profiles:
 * 1. GOAP planner latency across various starting states
 * 2. Utility evaluator throughput
 * 3. Memory overhead of WorldState snapshots
 *
 * Usage: Call {@link #runFullProfile()} and check logs for results.
 */
public class AiPerformanceProfiler {

    private static final Logger LOGGER = LoggerFactory.getLogger("SblPocProfiler");
    private static final int WARMUP_ITERATIONS = 50;
    private static final int BENCH_ITERATIONS = 200;

    /**
     * Run all profiling benchmarks and log results.
     */
    public static void runFullProfile() {
        LOGGER.info("=== SBL PoC AI Performance Profile ===");

        profilePlannerLatency();
        profileUtilityEvaluator();
        profileWorldStateAllocation();

        LOGGER.info("=== Profile Complete ===");
    }

    // ========== Planner Latency ==========

    private static void profilePlannerLatency() {
        GoapPlanner planner = new GoapPlanner(GoapActionRegistry.getActions());

        // Scenario 1: Easy plan (chop tree)
        benchmarkPlan(planner, "GET_WOOD (easy)",
                WorldState.builder().healthPercent(1.0f).build(),
                NpcGoal.GET_WOOD);

        // Scenario 2: Medium plan (stone tools from nothing)
        benchmarkPlan(planner, "GET_STONE_TOOLS (medium)",
                WorldState.builder().healthPercent(1.0f).build(),
                NpcGoal.GET_STONE_TOOLS);

        // Scenario 3: Complex plan (iron tools with prerequisites)
        benchmarkPlan(planner, "GET_IRON_TOOLS (complex)",
                WorldState.builder()
                        .healthPercent(1.0f)
                        .toolTier(InventoryModel.ToolTier.STONE)
                        .nearbyStation(BlockCategory.CRAFTING_TABLE)
                        .nearbyStation(BlockCategory.FURNACE)
                        .flag("knows_iron_location")
                        .addItem(Items.STICK, 10)
                        .addItem(Items.COAL, 4)
                        .build(),
                NpcGoal.GET_IRON_TOOLS);

        // Scenario 4: Impossible plan (diamond tools from nothing — tests budget enforcement)
        benchmarkPlan(planner, "GET_DIAMOND_TOOLS (impossible/deep)",
                WorldState.builder().healthPercent(1.0f).build(),
                NpcGoal.GET_DIAMOND_TOOLS);
    }

    private static void benchmarkPlan(GoapPlanner planner, String label, WorldState state, NpcGoal goal) {
        GoapPlanner.GoalCondition condition = goal.createGoalCondition(state);

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            planner.plan(state, condition);
        }

        // Benchmark
        long totalNs = 0;
        int successCount = 0;
        int maxSteps = 0;

        for (int i = 0; i < BENCH_ITERATIONS; i++) {
            long start = System.nanoTime();
            List<GoapAction> plan = planner.plan(state, condition);
            totalNs += System.nanoTime() - start;

            if (plan != null) {
                successCount++;
                maxSteps = Math.max(maxSteps, plan.size());
            }
        }

        double avgUs = (totalNs / (double) BENCH_ITERATIONS) / 1000.0;
        LOGGER.info("  [Planner] {}: avg={:.1f}µs, success={}/{}, maxSteps={}",
                label, avgUs, successCount, BENCH_ITERATIONS, maxSteps);
    }

    // ========== Utility Evaluator ==========

    private static void profileUtilityEvaluator() {
        UtilityEvaluator eval = new UtilityEvaluator();

        WorldState[] states = {
                WorldState.builder().healthPercent(0.15f).foodSupply(0).build(),
                WorldState.builder().healthPercent(1.0f).foodSupply(20)
                        .toolTier(InventoryModel.ToolTier.STONE).build(),
                WorldState.builder().healthPercent(1.0f).foodSupply(40)
                        .toolTier(InventoryModel.ToolTier.DIAMOND)
                        .flag("has_home").build(),
        };

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            for (WorldState s : states) eval.evaluate(s);
        }

        // Benchmark
        long totalNs = 0;
        int iterations = BENCH_ITERATIONS * states.length;

        for (int i = 0; i < BENCH_ITERATIONS; i++) {
            for (WorldState s : states) {
                long start = System.nanoTime();
                eval.evaluate(s);
                totalNs += System.nanoTime() - start;
            }
        }

        double avgUs = (totalNs / (double) iterations) / 1000.0;
        LOGGER.info("  [UtilityEval] avg={:.1f}µs per evaluate() across {} states",
                avgUs, states.length);
    }

    // ========== WorldState Allocation ==========

    private static void profileWorldStateAllocation() {
        // Measure cost of creating and cloning WorldStates
        long totalNs = 0;

        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            createSampleState();
        }

        for (int i = 0; i < BENCH_ITERATIONS; i++) {
            long start = System.nanoTime();
            WorldState state = createSampleState();
            state.toMutable(); // Clone for planner use
            totalNs += System.nanoTime() - start;
        }

        double avgUs = (totalNs / (double) BENCH_ITERATIONS) / 1000.0;
        LOGGER.info("  [WorldState] create+toMutable avg={:.1f}µs", avgUs);
    }

    private static WorldState createSampleState() {
        return WorldState.builder()
                .healthPercent(0.8f)
                .foodSupply(12)
                .toolTier(InventoryModel.ToolTier.IRON)
                .armorTier(WorldState.ArmorTier.IRON)
                .nearbyStation(BlockCategory.CRAFTING_TABLE)
                .nearbyStation(BlockCategory.FURNACE)
                .flag("has_home")
                .flag("knows_iron_location")
                .addItem(Items.OAK_LOG, 32)
                .addItem(Items.COBBLESTONE, 64)
                .addItem(Items.IRON_INGOT, 8)
                .addItem(Items.COAL, 16)
                .addItem(Items.STICK, 20)
                .addItem(Items.COOKED_BEEF, 12)
                .build();
    }
}
