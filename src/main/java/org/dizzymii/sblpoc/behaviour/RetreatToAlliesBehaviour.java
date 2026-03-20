package org.dizzymii.sblpoc.behaviour;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.PocNpc;
import org.dizzymii.sblpoc.SblPocSetup;

import java.util.List;

/**
 * When low HP and allies are nearby, retreat toward the nearest friendly NPC.
 *
 * Safety in numbers — the ally's threat sensor will pick up the pursuer,
 * and both NPCs will fight together.
 *
 * Activates when HP < 35% and at least 1 ally within 20 blocks.
 * Runs toward ally for up to 60 ticks, then re-evaluates.
 */
public class RetreatToAlliesBehaviour extends ExtendedBehaviour<PocNpc> {

    private static final float RETREAT_SPEED = 1.3f;
    private static final double ALLY_SCAN_RANGE = 20.0;
    private static final int MAX_RETREAT_TICKS = 60;

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT),
                    Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED)
            );

    private int retreatTimer = 0;
    private PocNpc targetAlly = null;

    public RetreatToAlliesBehaviour() {
        runFor(entity -> MAX_RETREAT_TICKS + 5);
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PocNpc npc) {
        // Only retreat when low HP
        float hpPercent = npc.getHealth() / npc.getMaxHealth();
        if (hpPercent >= 0.35f) return false;

        // Must have allies nearby
        Integer allyCount = BrainUtils.getMemory(npc, SblPocSetup.ALLY_COUNT.get());
        if (allyCount == null || allyCount < 1) return false;

        // Don't retreat if already using an item
        if (npc.isUsingItem()) return false;

        // Find the nearest ally
        PocNpc nearest = findNearestAlly(level, npc);
        if (nearest == null) return false;

        // Only retreat if ally is at least 4 blocks away (don't retreat if already next to them)
        if (npc.distanceToSqr(nearest) < 16.0) return false;

        targetAlly = nearest;
        return true;
    }

    @Override
    protected void start(ServerLevel level, PocNpc npc, long gameTime) {
        retreatTimer = MAX_RETREAT_TICKS;

        if (targetAlly != null && targetAlly.isAlive()) {
            BehaviorUtils.setWalkAndLookTargetMemories(npc, targetAlly, RETREAT_SPEED, 2);

            // Share our attack target with the ally so they engage too
            LivingEntity myTarget = BrainUtils.getMemory(npc, MemoryModuleType.ATTACK_TARGET);
            if (myTarget != null && myTarget.isAlive()) {
                LivingEntity allyTarget = BrainUtils.getMemory(targetAlly, MemoryModuleType.ATTACK_TARGET);
                if (allyTarget == null || !allyTarget.isAlive()) {
                    BrainUtils.setMemory(targetAlly, MemoryModuleType.ATTACK_TARGET, myTarget);
                }
            }
        }
    }

    @Override
    protected boolean shouldKeepRunning(PocNpc npc) {
        if (retreatTimer <= 0) return false;
        if (targetAlly == null || !targetAlly.isAlive()) return false;
        // Stop once we're close to the ally
        return npc.distanceToSqr(targetAlly) > 9.0; // 3 blocks
    }

    @Override
    protected void tick(ServerLevel level, PocNpc npc, long gameTime) {
        retreatTimer--;

        // Re-path toward ally every 15 ticks
        if (retreatTimer % 15 == 0 && targetAlly != null && targetAlly.isAlive()) {
            BehaviorUtils.setWalkAndLookTargetMemories(npc, targetAlly, RETREAT_SPEED, 2);
        }
    }

    @Override
    protected void stop(ServerLevel level, PocNpc npc, long gameTime) {
        retreatTimer = 0;
        targetAlly = null;
    }

    private PocNpc findNearestAlly(ServerLevel level, PocNpc npc) {
        PocNpc nearest = null;
        double nearestDistSq = Double.MAX_VALUE;

        for (PocNpc ally : level.getEntitiesOfClass(PocNpc.class,
                npc.getBoundingBox().inflate(ALLY_SCAN_RANGE),
                a -> a != npc && a.isAlive())) {
            double distSq = npc.distanceToSqr(ally);
            if (distSq < nearestDistSq) {
                nearestDistSq = distSq;
                nearest = ally;
            }
        }
        return nearest;
    }
}
