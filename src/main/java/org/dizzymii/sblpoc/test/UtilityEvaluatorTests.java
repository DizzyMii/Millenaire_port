package org.dizzymii.sblpoc.test;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.sblpoc.ai.goap.*;
import org.dizzymii.sblpoc.ai.world.InventoryModel;

/**
 * GameTests for utility score evaluation and goal selection.
 * Verifies the NPC picks the right goal for its situation.
 */
@GameTestHolder(Millenaire2.MODID)
@PrefixGameTestTemplate(false)
public class UtilityEvaluatorTests {

    // ========== Goal Priority Tests ==========

    @GameTest(template = "empty")
    public static void testSurvivalGoalHighestWhenLowHP(GameTestHelper helper) {
        WorldState state = WorldState.builder()
                .healthPercent(0.15f)
                .foodSupply(0)
                .build();

        UtilityEvaluator eval = new UtilityEvaluator();
        NpcGoal goal = eval.evaluate(state);

        helper.assertTrue(goal == NpcGoal.SURVIVE_IMMEDIATE,
                "Low HP should trigger SURVIVE_IMMEDIATE, got " + goal);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void testFoodGoalWhenHungry(GameTestHelper helper) {
        WorldState state = WorldState.builder()
                .healthPercent(1.0f)
                .foodSupply(2)
                .toolTier(InventoryModel.ToolTier.STONE) // Has tools already
                .addItem(Items.OAK_LOG, 20) // Has wood
                .build();

        UtilityEvaluator eval = new UtilityEvaluator();
        NpcGoal goal = eval.evaluate(state);

        helper.assertTrue(goal == NpcGoal.EAT_FOOD,
                "Low food should trigger EAT_FOOD, got " + goal);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void testWoodGoalWhenNewSpawn(GameTestHelper helper) {
        WorldState state = WorldState.builder()
                .healthPercent(1.0f)
                .foodSupply(10)
                .build();

        UtilityEvaluator eval = new UtilityEvaluator();
        NpcGoal goal = eval.evaluate(state);

        // With moderate food and no wood/tools, should want wood or shelter
        helper.assertTrue(goal == NpcGoal.GET_WOOD || goal == NpcGoal.FIND_SHELTER,
                "New spawn should want wood or shelter, got " + goal);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void testShelterGoalAtNight(GameTestHelper helper) {
        WorldState state = WorldState.builder()
                .healthPercent(1.0f)
                .foodSupply(20)
                .isNight(true)
                .addItem(Items.OAK_LOG, 20)
                .toolTier(InventoryModel.ToolTier.STONE)
                .build();

        UtilityEvaluator eval = new UtilityEvaluator();
        NpcGoal goal = eval.evaluate(state);

        // At night without home, shelter should be high priority
        helper.assertTrue(goal == NpcGoal.FIND_SHELTER,
                "Night with no home should trigger FIND_SHELTER, got " + goal);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void testHysteresisPreventFlipFlop(GameTestHelper helper) {
        // Two goals with similar scores should not flip-flop due to hysteresis
        WorldState state = WorldState.builder()
                .healthPercent(1.0f)
                .foodSupply(10)
                .addItem(Items.OAK_LOG, 8) // Partial wood → GET_WOOD has moderate score
                .build();

        UtilityEvaluator eval = new UtilityEvaluator();
        NpcGoal first = eval.evaluate(state);

        // Evaluate again with same state — should stick to the same goal
        NpcGoal second = eval.evaluate(state);
        helper.assertTrue(first == second,
                "Hysteresis should prevent goal change on same state");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void testProgressionOrder(GameTestHelper helper) {
        // Test that goals are selected in a logical progression:
        // wood → stone tools → iron tools → diamond tools

        // Phase 1: No tools, should want wood
        WorldState fresh = WorldState.builder().healthPercent(1.0f).foodSupply(20).build();
        float woodScore = NpcGoal.GET_WOOD.score(fresh);
        float stoneScore = NpcGoal.GET_STONE_TOOLS.score(fresh);
        helper.assertTrue(woodScore > stoneScore,
                "Wood should score higher than stone tools when starting fresh");

        // Phase 2: Has wood + wood tools, should want stone tools
        WorldState hasWood = WorldState.builder()
                .healthPercent(1.0f).foodSupply(20)
                .addItem(Items.OAK_LOG, 20)
                .toolTier(InventoryModel.ToolTier.WOOD)
                .build();
        float stoneScore2 = NpcGoal.GET_STONE_TOOLS.score(hasWood);
        float ironScore2 = NpcGoal.GET_IRON_TOOLS.score(hasWood);
        helper.assertTrue(stoneScore2 > ironScore2,
                "Stone tools should score higher than iron when at wood tier");

        // Phase 3: Has stone tools, should want iron
        WorldState hasStone = WorldState.builder()
                .healthPercent(1.0f).foodSupply(20)
                .toolTier(InventoryModel.ToolTier.STONE)
                .flag("knows_iron_location")
                .build();
        float ironScore3 = NpcGoal.GET_IRON_TOOLS.score(hasStone);
        float diamondScore3 = NpcGoal.GET_DIAMOND_TOOLS.score(hasStone);
        helper.assertTrue(ironScore3 > diamondScore3,
                "Iron tools should score higher than diamond when at stone tier");

        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void testSatisfiedGoalsScoreZero(GameTestHelper helper) {
        WorldState fullyEquipped = WorldState.builder()
                .healthPercent(1.0f)
                .foodSupply(40)
                .toolTier(InventoryModel.ToolTier.DIAMOND)
                .armorTier(WorldState.ArmorTier.IRON)
                .flag("has_home")
                .addItem(Items.OAK_LOG, 64)
                .build();

        helper.assertTrue(NpcGoal.GET_WOOD.score(fullyEquipped) == 0f,
                "GET_WOOD should score 0 with 64 logs");
        helper.assertTrue(NpcGoal.GET_STONE_TOOLS.score(fullyEquipped) == 0f,
                "GET_STONE_TOOLS should score 0 with diamond tools");
        helper.assertTrue(NpcGoal.GET_IRON_TOOLS.score(fullyEquipped) == 0f,
                "GET_IRON_TOOLS should score 0 with diamond tools");
        helper.assertTrue(NpcGoal.GET_DIAMOND_TOOLS.score(fullyEquipped) == 0f,
                "GET_DIAMOND_TOOLS should score 0 with diamond tools");
        helper.assertTrue(NpcGoal.FIND_SHELTER.score(fullyEquipped) == 0f,
                "FIND_SHELTER should score 0 with has_home flag");
        helper.assertTrue(NpcGoal.EAT_FOOD.score(fullyEquipped) == 0f,
                "EAT_FOOD should score 0 with 40 food");
        helper.succeed();
    }
}
