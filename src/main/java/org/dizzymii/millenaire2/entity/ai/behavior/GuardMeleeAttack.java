package org.dizzymii.millenaire2.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import org.dizzymii.millenaire2.entity.MillGuardNpc;

import java.util.List;

/**
 * Melee combat behaviour for the guard NPC.
 *
 * Walk toward the ATTACK_TARGET. When within melee range (2 blocks),
 * swing the main hand and call doHurtTarget(). Respects a short cooldown
 * between swings to avoid instant-kill spam.
 */
public class GuardMeleeAttack extends ExtendedBehaviour<MillGuardNpc> {

    private static final double MELEE_RANGE_SQ = 4.0; // 2 blocks squared
    private static final int ATTACK_COOLDOWN_TICKS = 20; // 1 second between swings

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT),
                    Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED)
            );

    private long lastAttackTime = 0;

    public GuardMeleeAttack() {
        runFor(entity -> 200); // keep running while fighting
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, MillGuardNpc guard) {
        LivingEntity target = guard.getBrain()
                .getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
        return target != null && target.isAlive();
    }

    @Override
    protected void start(ServerLevel level, MillGuardNpc guard, long gameTime) {
        LivingEntity target = guard.getBrain()
                .getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
        if (target != null) {
            BehaviorUtils.setWalkAndLookTargetMemories(guard, target, 1.2f, 0);
        }
    }

    @Override
    protected boolean shouldKeepRunning(MillGuardNpc guard) {
        LivingEntity target = guard.getBrain()
                .getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
        return target != null && target.isAlive() && guard.isAlive();
    }

    @Override
    protected void tick(ServerLevel level, MillGuardNpc guard, long gameTime) {
        LivingEntity target = guard.getBrain()
                .getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
        if (target == null || !target.isAlive()) return;

        double distSq = guard.distanceToSqr(target);

        // Keep walking toward target if not in melee range
        if (distSq > MELEE_RANGE_SQ) {
            BehaviorUtils.setWalkAndLookTargetMemories(guard, target, 1.2f, 0);
            return;
        }

        // In melee range — attack on cooldown
        if (gameTime - lastAttackTime >= ATTACK_COOLDOWN_TICKS) {
            guard.swing(InteractionHand.MAIN_HAND);
            guard.doHurtTarget(target);
            lastAttackTime = gameTime;
        }

        // Face the target while fighting
        BehaviorUtils.lookAtEntity(guard, target);
    }

    @Override
    protected void stop(ServerLevel level, MillGuardNpc guard, long gameTime) {
        // Clear attack target if the target died
        LivingEntity target = guard.getBrain()
                .getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
        if (target == null || !target.isAlive()) {
            guard.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
        }
    }
}
