package org.dizzymii.sblpoc.ai.goap;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.PocNpc;
import org.dizzymii.sblpoc.SblPocSetup;
import org.dizzymii.sblpoc.ai.world.GameClock;
import org.dizzymii.sblpoc.ai.world.InventoryModel;
import org.dizzymii.sblpoc.ai.world.SpatialMemory;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Top-level SBL behaviour that drives the Utility AI + GOAP pipeline.
 *
 * Lifecycle:
 * 1. Every EVAL_INTERVAL ticks, snapshot the WorldState
 * 2. Run UtilityEvaluator to pick the best NpcGoal
 * 3. If goal changed, run GoapPlanner to build a new action plan
 * 4. Execute plan steps one at a time, advancing when each completes
 * 5. If a step fails or the plan is empty, re-evaluate
 *
 * This behaviour runs in the IDLE activity group with low priority,
 * so combat behaviours (FIGHT activity) always take precedence.
 */
public class PlanExecutor extends ExtendedBehaviour<PocNpc> {

    private static final int EVAL_INTERVAL = 100; // 5 seconds

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT)
            );

    private final UtilityEvaluator utilityEvaluator = new UtilityEvaluator();
    private final GoapPlanner planner = new GoapPlanner(GoapActionRegistry.getActions());

    @Nullable private NpcGoal currentGoal = null;
    @Nullable private List<GoapAction> currentPlan = null;
    private int planStepIndex = 0;
    private int stepTicksElapsed = 0;
    private int evalCooldown = 0;

    public PlanExecutor() {
        noTimeout(); // Runs indefinitely until combat interrupts
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PocNpc npc) {
        // Run when not in combat
        return !npc.isUsingItem();
    }

    @Override
    protected void start(ServerLevel level, PocNpc npc, long gameTime) {
        evalCooldown = 0; // Evaluate immediately on start
    }

    @Override
    protected void tick(ServerLevel level, PocNpc npc, long gameTime) {
        evalCooldown--;

        // Periodic goal evaluation
        if (evalCooldown <= 0) {
            evalCooldown = EVAL_INTERVAL;
            evaluateAndPlan(npc);
        }

        // Execute current plan step
        if (currentPlan != null && planStepIndex < currentPlan.size()) {
            GoapAction currentAction = currentPlan.get(planStepIndex);
            stepTicksElapsed++;

            // Log current action to memory for debug visibility
            BrainUtils.setMemory(npc, SblPocSetup.CURRENT_GOAP_ACTION.get(), currentAction.getName());

            // Check if step should complete (estimated duration elapsed)
            if (stepTicksElapsed >= currentAction.getEstimatedTicks()) {
                advancePlanStep(npc);
            }
        } else if (currentPlan != null && planStepIndex >= currentPlan.size()) {
            // Plan completed successfully
            onPlanCompleted(npc);
        }
    }

    @Override
    protected boolean shouldKeepRunning(PocNpc npc) {
        // Keep running as long as we're not in combat
        return npc.isAlive() && !BrainUtils.hasMemory(npc, MemoryModuleType.ATTACK_TARGET);
    }

    @Override
    protected void stop(ServerLevel level, PocNpc npc, long gameTime) {
        BrainUtils.clearMemory(npc, SblPocSetup.CURRENT_GOAP_ACTION.get());
    }

    // ========== Planning ==========

    private void evaluateAndPlan(PocNpc npc) {
        WorldState state = buildWorldState(npc);
        NpcGoal previousGoal = currentGoal;
        NpcGoal bestGoal = utilityEvaluator.evaluate(state);

        if (bestGoal == null) {
            currentGoal = null;
            currentPlan = null;
            return;
        }

        // Only re-plan if goal changed or we have no plan
        if (bestGoal != previousGoal || currentPlan == null) {
            currentGoal = bestGoal;
            BrainUtils.setMemory(npc, SblPocSetup.CURRENT_NPC_GOAL.get(), bestGoal.name());

            GoapPlanner.GoalCondition goalCondition = bestGoal.createGoalCondition(state);
            currentPlan = planner.plan(state, goalCondition);
            planStepIndex = 0;
            stepTicksElapsed = 0;

            if (currentPlan == null || currentPlan.isEmpty()) {
                // Planner couldn't find a path — will retry next eval
                currentPlan = null;
            }
        }
    }

    private void advancePlanStep(PocNpc npc) {
        planStepIndex++;
        stepTicksElapsed = 0;

        if (currentPlan != null && planStepIndex < currentPlan.size()) {
            GoapAction next = currentPlan.get(planStepIndex);
            BrainUtils.setMemory(npc, SblPocSetup.CURRENT_GOAP_ACTION.get(), next.getName());
        }
    }

    private void onPlanCompleted(PocNpc npc) {
        currentPlan = null;
        planStepIndex = 0;
        stepTicksElapsed = 0;
        evalCooldown = 0; // Re-evaluate immediately
        BrainUtils.clearMemory(npc, SblPocSetup.CURRENT_GOAP_ACTION.get());
    }

    // ========== WorldState Construction ==========

    /**
     * Build a WorldState snapshot from the NPC's current state.
     */
    private WorldState buildWorldState(PocNpc npc) {
        SpatialMemory spatial = npc.getSpatialMemory();
        InventoryModel invModel = npc.getInventoryModel();
        GameClock clock = npc.getGameClock();

        WorldState.Builder builder = WorldState.builder()
                .inventory(invModel.getItemCounts())
                .toolTier(invModel.getHighestToolTier())
                .healthPercent(npc.getHealth() / npc.getMaxHealth())
                .foodSupply(invModel.getFoodSupply())
                .dayNumber(clock.getDayNumber())
                .isNight(clock.isNight())
                .dimension("overworld"); // TODO: detect actual dimension

        // Flags from spatial memory
        if (spatial.hasPOI(org.dizzymii.sblpoc.ai.world.POIType.HOME_BASE)) {
            builder.flag("has_home");
        }
        if (spatial.knows(org.dizzymii.sblpoc.ai.world.BlockCategory.IRON_ORE)) {
            builder.flag("knows_iron_location");
        }
        if (spatial.knows(org.dizzymii.sblpoc.ai.world.BlockCategory.DIAMOND_ORE)) {
            builder.flag("knows_diamond_location");
        }

        // Nearby stations (within 8 blocks)
        for (org.dizzymii.sblpoc.ai.world.BlockCategory station : new org.dizzymii.sblpoc.ai.world.BlockCategory[]{
                org.dizzymii.sblpoc.ai.world.BlockCategory.CRAFTING_TABLE,
                org.dizzymii.sblpoc.ai.world.BlockCategory.FURNACE,
                org.dizzymii.sblpoc.ai.world.BlockCategory.ANVIL,
                org.dizzymii.sblpoc.ai.world.BlockCategory.ENCHANTING_TABLE,
                org.dizzymii.sblpoc.ai.world.BlockCategory.BREWING_STAND}) {
            var nearest = spatial.findNearest(station, npc.blockPosition());
            if (nearest != null && nearest.distSqr(npc.blockPosition()) < 64) {
                builder.nearbyStation(station);
            }
        }

        return builder.build();
    }
}
