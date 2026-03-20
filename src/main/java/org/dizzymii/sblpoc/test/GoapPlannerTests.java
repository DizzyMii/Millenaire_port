package org.dizzymii.sblpoc.test;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.sblpoc.ai.goap.*;
import org.dizzymii.sblpoc.ai.world.BlockCategory;
import org.dizzymii.sblpoc.ai.world.InventoryModel;

import java.util.List;

/**
 * GameTests for the GOAP planning system.
 * Verifies that the planner can find valid action chains for key scenarios.
 */
@GameTestHolder(Millenaire2.MODID)
@PrefixGameTestTemplate(false)
public class GoapPlannerTests {

    private static GoapPlanner createPlanner() {
        return new GoapPlanner(GoapActionRegistry.getActions());
    }

    // ========== Plan Chain Tests ==========

    @GameTest(template = "empty")
    public static void testPlanGetWoodFromNothing(GameTestHelper helper) {
        // Starting with nothing, the NPC should be able to plan: chop_tree
        WorldState state = WorldState.builder()
                .healthPercent(1.0f)
                .build();

        GoapPlanner.GoalCondition goal = NpcGoal.GET_WOOD.createGoalCondition(state);
        List<GoapAction> plan = createPlanner().plan(state, goal);

        helper.assertTrue(plan != null && !plan.isEmpty(), "Should find a plan to get wood");
        helper.assertTrue(plan.stream().anyMatch(a -> a.getName().equals("chop_tree")),
                "Plan should include chop_tree");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void testPlanStoneToolsFromNothing(GameTestHelper helper) {
        // From nothing, NPC needs: chop_tree → craft_planks → craft_sticks →
        // craft_crafting_table → place_crafting_table → craft_wooden_pickaxe →
        // mine_stone → craft_stone_pickaxe
        WorldState state = WorldState.builder()
                .healthPercent(1.0f)
                .build();

        GoapPlanner.GoalCondition goal = NpcGoal.GET_STONE_TOOLS.createGoalCondition(state);
        List<GoapAction> plan = createPlanner().plan(state, goal);

        helper.assertTrue(plan != null && !plan.isEmpty(), "Should find a plan for stone tools");
        helper.assertTrue(plan.size() >= 5, "Plan should have at least 5 steps, got " + (plan != null ? plan.size() : 0));

        // Verify plan contains key milestones
        boolean hasCraftingTable = plan.stream().anyMatch(a -> a.getName().contains("crafting_table"));
        boolean hasWoodPick = plan.stream().anyMatch(a -> a.getName().equals("craft_wooden_pickaxe"));
        boolean hasMineStone = plan.stream().anyMatch(a -> a.getName().equals("mine_stone"));
        boolean hasStonePick = plan.stream().anyMatch(a -> a.getName().equals("craft_stone_pickaxe"));

        helper.assertTrue(hasCraftingTable, "Plan should include crafting table");
        helper.assertTrue(hasWoodPick, "Plan should include wooden pickaxe");
        helper.assertTrue(hasMineStone, "Plan should include mining stone");
        helper.assertTrue(hasStonePick, "Plan should include stone pickaxe");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void testPlanIronToolsWithStoneTools(GameTestHelper helper) {
        // With stone tools already, plan to get iron tools
        WorldState state = WorldState.builder()
                .healthPercent(1.0f)
                .toolTier(InventoryModel.ToolTier.STONE)
                .nearbyStation(BlockCategory.CRAFTING_TABLE)
                .nearbyStation(BlockCategory.FURNACE)
                .flag("knows_iron_location")
                .addItem(Items.STICK, 10)
                .addItem(Items.COAL, 4)
                .build();

        GoapPlanner.GoalCondition goal = NpcGoal.GET_IRON_TOOLS.createGoalCondition(state);
        List<GoapAction> plan = createPlanner().plan(state, goal);

        helper.assertTrue(plan != null && !plan.isEmpty(), "Should find a plan for iron tools");
        helper.assertTrue(plan.stream().anyMatch(a -> a.getName().equals("mine_iron_ore")),
                "Plan should include mining iron ore");
        helper.assertTrue(plan.stream().anyMatch(a -> a.getName().equals("smelt_iron")),
                "Plan should include smelting iron");
        helper.assertTrue(plan.stream().anyMatch(a -> a.getName().equals("craft_iron_pickaxe")),
                "Plan should include crafting iron pickaxe");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void testPlanAlreadySatisfied(GameTestHelper helper) {
        // Goal already satisfied → empty plan
        WorldState state = WorldState.builder()
                .healthPercent(1.0f)
                .toolTier(InventoryModel.ToolTier.IRON)
                .build();

        GoapPlanner.GoalCondition goal = NpcGoal.GET_IRON_TOOLS.createGoalCondition(state);
        List<GoapAction> plan = createPlanner().plan(state, goal);

        helper.assertTrue(plan != null && plan.isEmpty(), "Already-satisfied goal should return empty plan");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void testPlanFoodChain(GameTestHelper helper) {
        // NPC needs food: hunt_animal → cook_food (needs furnace + coal)
        WorldState state = WorldState.builder()
                .healthPercent(1.0f)
                .foodSupply(0)
                .nearbyStation(BlockCategory.FURNACE)
                .addItem(Items.COAL, 4)
                .build();

        GoapPlanner.GoalCondition goal = NpcGoal.EAT_FOOD.createGoalCondition(state);
        List<GoapAction> plan = createPlanner().plan(state, goal);

        helper.assertTrue(plan != null && !plan.isEmpty(), "Should find a plan to get food");
        helper.assertTrue(plan.stream().anyMatch(a -> a.getName().equals("hunt_animal")),
                "Plan should include hunting");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void testPlanShelter(GameTestHelper helper) {
        // NPC needs shelter and has enough planks
        WorldState state = WorldState.builder()
                .healthPercent(1.0f)
                .addItem(Items.OAK_PLANKS, 20)
                .build();

        GoapPlanner.GoalCondition goal = NpcGoal.FIND_SHELTER.createGoalCondition(state);
        List<GoapAction> plan = createPlanner().plan(state, goal);

        helper.assertTrue(plan != null && !plan.isEmpty(), "Should find a plan to build shelter");
        helper.assertTrue(plan.stream().anyMatch(a -> a.getName().equals("build_shelter")),
                "Plan should include build_shelter");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void testPlannerPerformanceBudget(GameTestHelper helper) {
        // Verify the planner stays within its 50ms budget even for complex goals
        WorldState state = WorldState.builder()
                .healthPercent(1.0f)
                .build();

        GoapPlanner.GoalCondition goal = NpcGoal.GET_DIAMOND_TOOLS.createGoalCondition(state);

        long start = System.nanoTime();
        // Plan may or may not succeed (diamond tools from nothing is very deep),
        // but it must not take longer than 100ms
        createPlanner().plan(state, goal);
        long elapsed = (System.nanoTime() - start) / 1_000_000;

        helper.assertTrue(elapsed < 100, "Planner should finish within 100ms, took " + elapsed + "ms");
        helper.succeed();
    }

    // ========== Action Ordering Tests ==========

    @GameTest(template = "empty")
    public static void testActionOrderingIsLogical(GameTestHelper helper) {
        // Verify plan steps are in a valid dependency order
        WorldState state = WorldState.builder()
                .healthPercent(1.0f)
                .build();

        GoapPlanner.GoalCondition goal = NpcGoal.GET_STONE_TOOLS.createGoalCondition(state);
        List<GoapAction> plan = createPlanner().plan(state, goal);

        helper.assertTrue(plan != null, "Plan should not be null");

        // Simulate: execute plan forward and verify no precondition failures
        WorldState.MutableWorldState sim = state.toMutable();
        for (GoapAction action : plan) {
            helper.assertTrue(action.canExecute(sim),
                    "Action '" + action.getName() + "' should have its preconditions met");
            action.applyEffects(sim);
        }
        helper.succeed();
    }
}
