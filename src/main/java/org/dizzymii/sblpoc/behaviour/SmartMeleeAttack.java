package org.dizzymii.sblpoc.behaviour;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.PocNpc;
import org.dizzymii.sblpoc.SblPocSetup;

import java.util.List;

/**
 * Player-like melee combat. Moves in committed arcs, not twitchy micro-adjustments.
 *
 * Phases:
 * - APPROACH: Walk toward target. Only re-path every ~15 ticks.
 * - ENGAGE: Stand and fight. Slight repositioning between swings, not constant strafing.
 * - KITE: After landing a hit, commit to backing off in one direction, then pause and re-engage.
 */
public class SmartMeleeAttack extends ExtendedBehaviour<PocNpc> {

    private static final double MELEE_RANGE_SQ = 4.5;     // ~2.1 blocks
    private static final double CLOSE_ENOUGH_SQ = 49.0;   // 7 blocks — start slowing down
    private static final int ATTACK_COOLDOWN = 18;
    private static final float WALK_SPEED = 1.0f;
    private static final float RUN_SPEED = 1.25f;
    private static final double KITE_DISTANCE = 4.0;

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT),
                    Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED)
            );

    private static final int JUMP_LEAD_TICKS = 7; // Jump this many ticks before attack

    private enum Phase { APPROACH, ENGAGE, KITE, PAUSE }

    private long lastAttackTime = 0;
    private Phase phase = Phase.APPROACH;
    private int phaseTimer = 0;
    private int repathCooldown = 0;
    private boolean strafingRight = true;
    private int hitsLanded = 0;
    private boolean jumpedForCrit = false;

    public SmartMeleeAttack() {
        runFor(entity -> 400);
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
        return distSq < CLOSE_ENOUGH_SQ || !hasRangedWeapon(npc);
    }

    @Override
    protected void start(ServerLevel level, PocNpc npc, long gameTime) {
        phase = Phase.APPROACH;
        phaseTimer = 0;
        repathCooldown = 0;
        hitsLanded = 0;
        jumpedForCrit = false;
        strafingRight = npc.getRandom().nextBoolean();

        // Pick the right weapon for the target
        chooseWeaponForTarget(npc);

        // Call for help — share target with nearby allies (like wolves)
        callForHelp(level, npc);

        BrainUtils.setMemory(npc, SblPocSetup.COMBAT_STATE.get(), "melee");
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
        repathCooldown--;

        switch (phase) {
            case APPROACH -> tickApproach(npc, target, distSq);
            case ENGAGE -> tickEngage(npc, target, distSq, gameTime);
            case KITE -> tickKite(npc, target, distSq);
            case PAUSE -> tickPause(npc, target, distSq);
        }
    }

    private void tickApproach(PocNpc npc, LivingEntity target, double distSq) {
        if (distSq <= MELEE_RANGE_SQ) {
            phase = Phase.ENGAGE;
            phaseTimer = 0;
            return;
        }

        // Only re-path periodically — not every tick
        if (repathCooldown <= 0) {
            float speed = distSq > CLOSE_ENOUGH_SQ ? RUN_SPEED : WALK_SPEED;
            BehaviorUtils.setWalkAndLookTargetMemories(npc, target, speed, 1);
            repathCooldown = 12 + npc.getRandom().nextInt(8); // 12-20 ticks between re-paths
        }
    }

    private void tickEngage(PocNpc npc, LivingEntity target, double distSq, long gameTime) {
        // If target backed away, re-approach
        if (distSq > MELEE_RANGE_SQ * 3.0) {
            phase = Phase.APPROACH;
            repathCooldown = 0;
            return;
        }

        // Close the gap if slightly out of range — walk, don't sprint
        if (distSq > MELEE_RANGE_SQ && repathCooldown <= 0) {
            BehaviorUtils.setWalkAndLookTargetMemories(npc, target, WALK_SPEED, 0);
            repathCooldown = 10;
        }

        long ticksSinceAttack = gameTime - lastAttackTime;

        // Jump before attack lands — wind up for a crit
        if (distSq <= MELEE_RANGE_SQ && ticksSinceAttack >= (ATTACK_COOLDOWN - JUMP_LEAD_TICKS)
                && !jumpedForCrit && npc.onGround()) {
            npc.getJumpControl().jump();
            jumpedForCrit = true;
        }

        // Attack when in range and cooldown elapsed
        if (distSq <= MELEE_RANGE_SQ && ticksSinceAttack >= ATTACK_COOLDOWN) {
            boolean isCrit = !npc.onGround() && npc.fallDistance > 0.0f;

            // Boost damage for crit — temporarily modify attribute like vanilla Player.attack()
            if (isCrit) {
                AttributeInstance attackAttr = npc.getAttribute(Attributes.ATTACK_DAMAGE);
                if (attackAttr != null) {
                    double baseDmg = attackAttr.getBaseValue();
                    attackAttr.setBaseValue(baseDmg * 1.5);
                    npc.swing(InteractionHand.MAIN_HAND);
                    npc.doHurtTarget(target);
                    attackAttr.setBaseValue(baseDmg);

                    // Send crit particles (animate packet ID 4 = critical hit)
                    if (npc.level() instanceof ServerLevel serverLevel) {
                        serverLevel.getChunkSource().broadcastAndSend(npc,
                                new ClientboundAnimatePacket(target, 4));
                    }
                }
            } else {
                npc.swing(InteractionHand.MAIN_HAND);
                npc.doHurtTarget(target);
            }

            lastAttackTime = gameTime;
            jumpedForCrit = false;
            hitsLanded++;

            // After 2-3 hits, kite back. A player doesn't just stand and spam.
            float hpPercent = npc.getHealth() / npc.getMaxHealth();
            int hitsBeforeKite = hpPercent < 0.4f ? 1 : (hpPercent < 0.7f ? 2 : 3);

            if (hitsLanded >= hitsBeforeKite) {
                startKite(npc, target);
            } else {
                // Small reposition between swings — just a step to one side
                if (npc.getRandom().nextFloat() < 0.4f) {
                    repositionSlightly(npc, target);
                }
            }
        }
    }

    private void startKite(PocNpc npc, LivingEntity target) {
        phase = Phase.KITE;
        hitsLanded = 0;

        // Commit to a retreat direction — pick once, follow through
        Vec3 retreatDir = npc.position().subtract(target.position()).normalize();
        // Slight angle variation so it's not always straight back
        double angle = (npc.getRandom().nextDouble() - 0.5) * 0.6;
        Vec3 rotated = new Vec3(
                retreatDir.x * Math.cos(angle) - retreatDir.z * Math.sin(angle),
                0,
                retreatDir.x * Math.sin(angle) + retreatDir.z * Math.cos(angle)
        );
        Vec3 retreatPos = npc.position().add(rotated.scale(KITE_DISTANCE));

        // Hazard check — don't retreat into lava, deep water, or off a cliff
        retreatPos = validateRetreatPosition(npc, retreatPos);

        BrainUtils.setMemory(npc, MemoryModuleType.WALK_TARGET,
                new WalkTarget(retreatPos, RUN_SPEED, 1));

        // Hold the kite for a set duration — don't re-path during it
        float hpPercent = npc.getHealth() / npc.getMaxHealth();
        phaseTimer = hpPercent < 0.3f ? 45 : (hpPercent < 0.6f ? 35 : 25);
        repathCooldown = phaseTimer; // Don't interrupt the retreat
    }

    private void tickKite(PocNpc npc, LivingEntity target, double distSq) {
        phaseTimer--;
        if (phaseTimer <= 0) {
            // Done retreating — brief pause before re-engaging (like a player reading the fight)
            phase = Phase.PAUSE;
            phaseTimer = 10 + npc.getRandom().nextInt(15); // 0.5-1.25 second pause
        }
    }

    private void tickPause(PocNpc npc, LivingEntity target, double distSq) {
        // Just stand still and look at target — deciding what to do next
        phaseTimer--;
        if (phaseTimer <= 0) {
            // If far enough and we have a bow, let ranged behaviour take over
            if (distSq > CLOSE_ENOUGH_SQ && hasRangedWeapon(npc)) {
                return; // behaviour will end, ranged can start
            }
            phase = Phase.APPROACH;
            repathCooldown = 0;
        }
    }

    private void repositionSlightly(PocNpc npc, LivingEntity target) {
        Vec3 toTarget = target.position().subtract(npc.position()).normalize();
        Vec3 perpendicular = strafingRight
                ? new Vec3(-toTarget.z, 0, toTarget.x)
                : new Vec3(toTarget.z, 0, -toTarget.x);
        Vec3 stepPos = npc.position().add(perpendicular.scale(1.2));
        BrainUtils.setMemory(npc, MemoryModuleType.WALK_TARGET,
                new WalkTarget(stepPos, WALK_SPEED, 0));

        // Occasionally swap strafe direction — but not every time
        if (npc.getRandom().nextFloat() < 0.3f) {
            strafingRight = !strafingRight;
        }
    }

    @Override
    protected void stop(ServerLevel level, PocNpc npc, long gameTime) {
        LivingEntity target = BrainUtils.getMemory(npc, MemoryModuleType.ATTACK_TARGET);
        if (target == null || !target.isAlive()) {
            BrainUtils.clearMemory(npc, MemoryModuleType.ATTACK_TARGET);
        }
        phase = Phase.APPROACH;
        phaseTimer = 0;
        hitsLanded = 0;
    }

    private boolean hasRangedWeapon(PocNpc npc) {
        if (npc.getMainHandItem().getItem() instanceof net.minecraft.world.item.BowItem) return true;
        for (int i = 0; i < npc.getInventory().getContainerSize(); i++) {
            if (npc.getInventory().getItem(i).getItem() instanceof net.minecraft.world.item.BowItem) return true;
        }
        return false;
    }

    // ========== Weapon Switching ==========

    private void chooseWeaponForTarget(PocNpc npc) {
        LivingEntity target = BrainUtils.getMemory(npc, MemoryModuleType.ATTACK_TARGET);
        if (target == null) return;

        boolean targetShielding = target.isBlocking();
        ItemStack mainHand = npc.getMainHandItem();

        if (targetShielding && !(mainHand.getItem() instanceof AxeItem)) {
            // Target is shielding — switch to axe (axes disable shields in vanilla)
            swapToWeapon(npc, AxeItem.class);
        } else if (!targetShielding && !(mainHand.getItem() instanceof SwordItem)) {
            // Target not shielding — sword does more DPS
            swapToWeapon(npc, SwordItem.class);
        }
    }

    private void swapToWeapon(PocNpc npc, Class<?> weaponClass) {
        ItemStack currentMain = npc.getMainHandItem();

        for (int i = 0; i < npc.getInventory().getContainerSize(); i++) {
            ItemStack stack = npc.getInventory().getItem(i);
            if (weaponClass.isInstance(stack.getItem())) {
                // Swap main hand with inventory slot
                npc.setItemInHand(InteractionHand.MAIN_HAND, stack.copy());
                npc.getInventory().setItem(i, currentMain.copy());
                return;
            }
        }
    }

    // ========== Hazard Avoidance ==========

    private Vec3 validateRetreatPosition(PocNpc npc, Vec3 proposed) {
        BlockPos targetPos = BlockPos.containing(proposed);

        // Check for lava
        FluidState fluid = npc.level().getFluidState(targetPos);
        if (fluid.is(FluidTags.LAVA)) {
            return npc.position(); // Stay put rather than walk into lava
        }

        // Check for deep water (2+ blocks deep)
        if (fluid.is(FluidTags.WATER)) {
            FluidState below = npc.level().getFluidState(targetPos.below());
            if (below.is(FluidTags.WATER)) {
                return npc.position();
            }
        }

        // Check for cliff (3+ block drop)
        BlockPos ground = targetPos;
        for (int y = 0; y < 4; y++) {
            BlockState state = npc.level().getBlockState(ground.below());
            if (!state.isAir()) break;
            ground = ground.below();
        }
        if (targetPos.getY() - ground.getY() >= 3) {
            return npc.position();
        }

        return proposed;
    }

    // ========== Call for Help ==========

    private void callForHelp(ServerLevel level, PocNpc npc) {
        Boolean helpCalled = BrainUtils.getMemory(npc, SblPocSetup.HELP_CALLED.get());
        if (helpCalled != null && helpCalled) return; // Already called

        LivingEntity myTarget = BrainUtils.getMemory(npc, MemoryModuleType.ATTACK_TARGET);
        if (myTarget == null) return;

        // Alert nearby allies within 16 blocks
        for (PocNpc ally : level.getEntitiesOfClass(PocNpc.class,
                npc.getBoundingBox().inflate(16.0),
                a -> a != npc && a.isAlive())) {
            LivingEntity allyTarget = BrainUtils.getMemory(ally, MemoryModuleType.ATTACK_TARGET);
            if (allyTarget == null || !allyTarget.isAlive()) {
                BrainUtils.setMemory(ally, MemoryModuleType.ATTACK_TARGET, myTarget);
            }
        }

        BrainUtils.setMemory(npc, SblPocSetup.HELP_CALLED.get(), true);
    }
}
