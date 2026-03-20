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
import org.dizzymii.sblpoc.SblPocSetup;

import java.util.List;

/**
 * Intelligent melee attack behaviour with multiple combat phases.
 *
 * Phases:
 * - APPROACH: Sprint toward target, strafing slightly to avoid projectiles
 * - ENGAGE: Circle-strafe within melee range, attack on cooldown
 * - KITE: After landing a hit, retreat 3-4 blocks before re-engaging
 * - DISENGAGE: If target is far and NPC has a bow, stop to let ranged take over
 *
 * Always kites after hitting (not just when low HP). Strafes during approach
 * to make the NPC a harder target for skeletons.
 */
public class SmartMeleeAttack extends ExtendedBehaviour<PocNpc> {

    private static final double MELEE_RANGE_SQ = 4.0;    // 2 blocks
    private static final double APPROACH_RANGE_SQ = 36.0; // 6 blocks — within this, start circle-strafing
    private static final int ATTACK_COOLDOWN = 16;
    private static final float SPRINT_SPEED = 1.4f;
    private static final float STRAFE_SPEED = 1.0f;
    private static final double KITE_DISTANCE = 3.5;

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT),
                    Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED)
            );

    private enum Phase { APPROACH, ENGAGE, KITE }

    private long lastAttackTime = 0;
    private Phase phase = Phase.APPROACH;
    private int kiteTimer = 0;
    private boolean strafingRight = true;
    private int strafeToggle = 0;

    public SmartMeleeAttack() {
        runFor(entity -> 300); // Max 15 seconds per engagement cycle
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PocNpc npc) {
        LivingEntity target = BrainUtils.getMemory(npc, MemoryModuleType.ATTACK_TARGET);
        if (target == null || !target.isAlive() || npc.isUsingItem()) return false;

        double distSq = npc.distanceToSqr(target);
        // Only activate for melee range — let SmartRangedAttack handle distant targets
        return distSq < APPROACH_RANGE_SQ || !hasRangedWeapon(npc);
    }

    @Override
    protected void start(ServerLevel level, PocNpc npc, long gameTime) {
        phase = Phase.APPROACH;
        kiteTimer = 0;
        strafingRight = npc.getRandom().nextBoolean();
        strafeToggle = 0;

        BrainUtils.setMemory(npc, SblPocSetup.COMBAT_STATE.get(), "melee");

        LivingEntity target = BrainUtils.getMemory(npc, MemoryModuleType.ATTACK_TARGET);
        if (target != null) {
            BehaviorUtils.setWalkAndLookTargetMemories(npc, target, SPRINT_SPEED, 0);
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
        BehaviorUtils.lookAtEntity(npc, target);

        // Toggle strafe direction periodically
        strafeToggle--;
        if (strafeToggle <= 0) {
            strafingRight = !strafingRight;
            strafeToggle = 15 + npc.getRandom().nextInt(25);
        }

        switch (phase) {
            case KITE -> tickKite(npc, target, distSq, gameTime);
            case ENGAGE -> tickEngage(npc, target, distSq, gameTime);
            default -> tickApproach(npc, target, distSq);
        }
    }

    private void tickApproach(PocNpc npc, LivingEntity target, double distSq) {
        if (distSq <= MELEE_RANGE_SQ) {
            phase = Phase.ENGAGE;
            return;
        }

        // Approach with slight strafing to dodge projectiles
        if (distSq < APPROACH_RANGE_SQ) {
            // Close enough to circle-strafe toward target
            Vec3 toTarget = target.position().subtract(npc.position()).normalize();
            Vec3 perpendicular = strafingRight
                    ? new Vec3(-toTarget.z, 0, toTarget.x)
                    : new Vec3(toTarget.z, 0, -toTarget.x);
            // Blend: 70% toward target, 30% strafe
            Vec3 moveDir = toTarget.scale(0.7).add(perpendicular.scale(0.3)).normalize();
            Vec3 movePos = npc.position().add(moveDir.scale(3.0));
            BrainUtils.setMemory(npc, MemoryModuleType.WALK_TARGET,
                    new WalkTarget(movePos, SPRINT_SPEED, 1));
        } else {
            // Far away — sprint straight
            BehaviorUtils.setWalkAndLookTargetMemories(npc, target, SPRINT_SPEED, 0);
        }
    }

    private void tickEngage(PocNpc npc, LivingEntity target, double distSq, long gameTime) {
        // If target moved out of range, re-approach
        if (distSq > MELEE_RANGE_SQ * 2.25) { // ~3 blocks
            phase = Phase.APPROACH;
            return;
        }

        // Circle-strafe around target while in melee range
        if (distSq <= MELEE_RANGE_SQ * 2.25) {
            Vec3 toTarget = target.position().subtract(npc.position()).normalize();
            Vec3 perpendicular = strafingRight
                    ? new Vec3(-toTarget.z, 0, toTarget.x)
                    : new Vec3(toTarget.z, 0, -toTarget.x);
            Vec3 strafePos = npc.position().add(perpendicular.scale(1.5));
            BrainUtils.setMemory(npc, MemoryModuleType.WALK_TARGET,
                    new WalkTarget(strafePos, STRAFE_SPEED, 0));
        }

        // Attack if in range and cooldown elapsed
        if (distSq <= MELEE_RANGE_SQ && gameTime - lastAttackTime >= ATTACK_COOLDOWN) {
            npc.swing(InteractionHand.MAIN_HAND);
            npc.doHurtTarget(target);
            lastAttackTime = gameTime;

            // Always kite after hitting — duration scales with danger
            phase = Phase.KITE;
            float hpPercent = npc.getHealth() / npc.getMaxHealth();
            if (hpPercent < 0.3f) {
                kiteTimer = 25; // Desperate — long retreat
            } else if (hpPercent < 0.6f) {
                kiteTimer = 18; // Cautious
            } else {
                kiteTimer = 10; // Confident — brief backstep
            }

            // Move away from target
            Vec3 retreatDir = npc.position().subtract(target.position()).normalize();
            // Add some randomness to retreat angle
            double angle = (npc.getRandom().nextDouble() - 0.5) * 0.8;
            Vec3 rotated = new Vec3(
                    retreatDir.x * Math.cos(angle) - retreatDir.z * Math.sin(angle),
                    0,
                    retreatDir.x * Math.sin(angle) + retreatDir.z * Math.cos(angle)
            );
            Vec3 retreatPos = npc.position().add(rotated.scale(KITE_DISTANCE));
            BrainUtils.setMemory(npc, MemoryModuleType.WALK_TARGET,
                    new WalkTarget(retreatPos, SPRINT_SPEED, 1));
        }
    }

    private void tickKite(PocNpc npc, LivingEntity target, double distSq, long gameTime) {
        kiteTimer--;
        if (kiteTimer <= 0) {
            // Kite complete — decide next phase
            if (distSq > APPROACH_RANGE_SQ && hasRangedWeapon(npc)) {
                // Far enough for ranged — let SmartRangedAttack take over
                return;
            }
            phase = Phase.APPROACH;
            BehaviorUtils.setWalkAndLookTargetMemories(npc, target, SPRINT_SPEED, 0);
        }
    }

    @Override
    protected void stop(ServerLevel level, PocNpc npc, long gameTime) {
        LivingEntity target = BrainUtils.getMemory(npc, MemoryModuleType.ATTACK_TARGET);
        if (target == null || !target.isAlive()) {
            BrainUtils.clearMemory(npc, MemoryModuleType.ATTACK_TARGET);
        }
        phase = Phase.APPROACH;
        kiteTimer = 0;
    }

    private boolean hasRangedWeapon(PocNpc npc) {
        if (npc.getMainHandItem().getItem() instanceof net.minecraft.world.item.BowItem) return true;
        for (int i = 0; i < npc.getInventory().getContainerSize(); i++) {
            if (npc.getInventory().getItem(i).getItem() instanceof net.minecraft.world.item.BowItem) return true;
        }
        return false;
    }
}
