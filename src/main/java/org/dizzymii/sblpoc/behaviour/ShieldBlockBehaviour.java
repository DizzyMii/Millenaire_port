package org.dizzymii.sblpoc.behaviour;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
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
 * Intelligent shield blocking behaviour.
 * 
 * Triggers (any one is sufficient):
 * 1. REACTIVE: SHOULD_SHIELD memory set by IncomingDamageSensor (projectile or melee wind-up)
 * 2. JUST HIT: HURT_BY memory present — block to catch follow-up attacks
 * 3. OUTNUMBERED: 2+ hostiles within 5 blocks
 * 4. DESPERATE: HP < 30% and target within 4 blocks
 * 
 * Smart blocking:
 * - Holds for 20-60 ticks depending on trigger, then drops to counter-attack
 * - 30-tick cooldown after dropping shield (prevents shield-spam)
 * - Faces the most dangerous attacker while blocking
 */
public class ShieldBlockBehaviour extends ExtendedBehaviour<PocNpc> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT)
            );

    private int blockDuration = 40;

    public ShieldBlockBehaviour() {
        runFor(entity -> blockDuration);
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PocNpc npc) {
        // Don't shield if on cooldown
        Long cooldownEnd = BrainUtils.getMemory(npc, SblPocSetup.SHIELD_COOLDOWN.get());
        if (cooldownEnd != null && level.getGameTime() < cooldownEnd) {
            return false;
        }

        // Must have a shield in offhand
        if (!npc.getOffhandItem().canPerformAction(net.neoforged.neoforge.common.ItemAbilities.SHIELD_BLOCK)) {
            return false;
        }

        LivingEntity target = BrainUtils.getMemory(npc, MemoryModuleType.ATTACK_TARGET);
        if (target == null || !target.isAlive()) return false;

        double distSq = npc.distanceToSqr(target);

        // Trigger 1: Incoming damage detected by sensor (projectile or melee wind-up)
        Boolean shouldShield = BrainUtils.getMemory(npc, SblPocSetup.SHOULD_SHIELD.get());
        if (shouldShield != null && shouldShield) {
            blockDuration = 40; // Hold until projectile passes or attack lands
            return true;
        }

        // Trigger 2: Just got hit — block to catch follow-up attacks
        if (BrainUtils.hasMemory(npc, MemoryModuleType.HURT_BY)) {
            blockDuration = 30; // Brief defensive block
            return true;
        }

        // Trigger 3: Outnumbered — 2+ hostiles within 5 blocks
        Integer hostileCount = BrainUtils.getMemory(npc, SblPocSetup.NEARBY_HOSTILE_COUNT.get());
        if (hostileCount != null && hostileCount >= 2 && distSq < 25.0) { // 5 blocks squared
            blockDuration = 50; // Longer block when surrounded
            return true;
        }

        // Trigger 4: Desperate — HP < 30% and enemy very close
        float hpPercent = npc.getHealth() / npc.getMaxHealth();
        if (hpPercent < 0.3f && distSq < 16.0) { // 4 blocks squared
            blockDuration = 60; // Longest block — buying time
            return true;
        }

        return false;
    }

    @Override
    protected void start(ServerLevel level, PocNpc npc, long gameTime) {
        npc.startUsingItem(InteractionHand.OFF_HAND);

        // Face the attack target
        LivingEntity target = BrainUtils.getMemory(npc, MemoryModuleType.ATTACK_TARGET);
        if (target != null) {
            BehaviorUtils.lookAtEntity(npc, target);
        }
    }

    @Override
    protected boolean shouldKeepRunning(PocNpc npc) {
        LivingEntity target = BrainUtils.getMemory(npc, MemoryModuleType.ATTACK_TARGET);
        return target != null && target.isAlive() && npc.isAlive() && npc.isUsingItem();
    }

    @Override
    protected void tick(ServerLevel level, PocNpc npc, long gameTime) {
        // Keep facing the most dangerous threat
        LivingEntity target = BrainUtils.getMemory(npc, MemoryModuleType.ATTACK_TARGET);
        if (target != null && target.isAlive()) {
            BehaviorUtils.lookAtEntity(npc, target);
        }

        // Parry-riposte: if we just blocked a hit, drop shield and counter immediately
        Boolean justBlocked = BrainUtils.getMemory(npc, SblPocSetup.JUST_BLOCKED_HIT.get());
        if (justBlocked != null && justBlocked && target != null && target.isAlive()) {
            double distSq = npc.distanceToSqr(target);
            if (distSq <= 6.25) { // 2.5 blocks — close enough to counter
                npc.stopUsingItem();
                npc.swing(InteractionHand.MAIN_HAND);
                npc.doHurtTarget(target);
                BrainUtils.clearMemory(npc, SblPocSetup.JUST_BLOCKED_HIT.get());
                // Short cooldown after riposte — don't re-shield instantly
                BrainUtils.setMemory(npc, SblPocSetup.SHIELD_COOLDOWN.get(), gameTime + 15L);
                return;
            }
            BrainUtils.clearMemory(npc, SblPocSetup.JUST_BLOCKED_HIT.get());
        }
    }

    @Override
    protected void stop(ServerLevel level, PocNpc npc, long gameTime) {
        // Lower shield
        npc.stopUsingItem();
        BrainUtils.clearMemory(npc, SblPocSetup.JUST_BLOCKED_HIT.get());

        // Set shield cooldown — 30 ticks before can shield again
        BrainUtils.setMemory(npc, SblPocSetup.SHIELD_COOLDOWN.get(), gameTime + 30L);
    }
}
