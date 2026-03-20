package org.dizzymii.sblpoc.behaviour;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.PocNpc;

import java.util.List;

/**
 * Intelligent melee attack behaviour.
 * 
 * - Approaches target at 1.2x speed
 * - Swings every 20 ticks when within melee range
 * - After landing a hit, briefly retreats 2 blocks if HP < 50% (kite pattern)
 * - Stops if target dies
 */
public class SmartMeleeAttack extends ExtendedBehaviour<PocNpc> {

    private static final double MELEE_RANGE_SQ = 4.0; // 2 blocks
    private static final int ATTACK_COOLDOWN = 20;
    private static final float APPROACH_SPEED = 1.2f;
    private static final double KITE_DISTANCE = 2.0;

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT),
                    Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED)
            );

    private long lastAttackTime = 0;
    private boolean justHit = false;
    private int kiteTimer = 0;

    public SmartMeleeAttack() {
        runFor(entity -> 200); // Max 10 seconds per engagement cycle
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PocNpc npc) {
        LivingEntity target = BrainUtils.getMemory(npc, MemoryModuleType.ATTACK_TARGET);
        return target != null && target.isAlive() && !npc.isUsingItem();
    }

    @Override
    protected void start(ServerLevel level, PocNpc npc, long gameTime) {
        justHit = false;
        kiteTimer = 0;

        LivingEntity target = BrainUtils.getMemory(npc, MemoryModuleType.ATTACK_TARGET);
        if (target != null) {
            BehaviorUtils.setWalkAndLookTargetMemories(npc, target, APPROACH_SPEED, 0);
        }
    }

    @Override
    protected boolean shouldKeepRunning(PocNpc npc) {
        LivingEntity target = BrainUtils.getMemory(npc, MemoryModuleType.ATTACK_TARGET);
        return target != null && target.isAlive() && npc.isAlive() && !npc.isUsingItem();
    }

    @Override
    protected void tick(ServerLevel level, PocNpc npc, long gameTime) {
        LivingEntity target = BrainUtils.getMemory(npc, MemoryModuleType.ATTACK_TARGET);
        if (target == null || !target.isAlive()) return;

        double distSq = npc.distanceToSqr(target);

        // If kiting (retreating after a hit), move away briefly
        if (justHit && kiteTimer > 0) {
            kiteTimer--;
            if (kiteTimer <= 0) {
                justHit = false;
                // Re-approach
                BehaviorUtils.setWalkAndLookTargetMemories(npc, target, APPROACH_SPEED, 0);
            }
            return;
        }

        // Not in range — approach
        if (distSq > MELEE_RANGE_SQ) {
            BehaviorUtils.setWalkAndLookTargetMemories(npc, target, APPROACH_SPEED, 0);
            return;
        }

        // In range — attack if cooldown elapsed
        BehaviorUtils.lookAtEntity(npc, target);

        if (gameTime - lastAttackTime >= ATTACK_COOLDOWN) {
            npc.swing(InteractionHand.MAIN_HAND);
            npc.doHurtTarget(target);
            lastAttackTime = gameTime;

            // Kite pattern: retreat after hitting if HP < 50%
            float hpPercent = npc.getHealth() / npc.getMaxHealth();
            if (hpPercent < 0.5f) {
                justHit = true;
                kiteTimer = 15; // Retreat for 15 ticks

                // Calculate retreat direction (away from target)
                Vec3 retreatDir = npc.position().subtract(target.position()).normalize();
                Vec3 retreatPos = npc.position().add(retreatDir.scale(KITE_DISTANCE));

                BrainUtils.setMemory(npc, MemoryModuleType.WALK_TARGET,
                        new WalkTarget(retreatPos, APPROACH_SPEED, 1));
            }
        }
    }

    @Override
    protected void stop(ServerLevel level, PocNpc npc, long gameTime) {
        LivingEntity target = BrainUtils.getMemory(npc, MemoryModuleType.ATTACK_TARGET);
        if (target == null || !target.isAlive()) {
            BrainUtils.clearMemory(npc, MemoryModuleType.ATTACK_TARGET);
        }
        justHit = false;
        kiteTimer = 0;
    }
}
