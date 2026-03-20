package org.dizzymii.millenaire2.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import org.dizzymii.millenaire2.entity.MillGuardNpc;

import java.util.List;

/**
 * Tactical retreat behaviour. When the guard's health drops below 20%,
 * this behaviour takes priority over melee attack: it clears the attack
 * target and sets a walk target in the opposite direction from the threat.
 *
 * The guard will flee for ~5 seconds (100 ticks) before reconsidering.
 */
public class GuardFleeWhenLow extends ExtendedBehaviour<MillGuardNpc> {

    private static final float HEALTH_THRESHOLD_PERCENT = 0.20f;
    private static final double FLEE_DISTANCE = 16.0;

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT)
            );

    public GuardFleeWhenLow() {
        runFor(entity -> 100); // flee for 5 seconds
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, MillGuardNpc guard) {
        // Only flee when health is critically low
        return guard.getHealth() / guard.getMaxHealth() < HEALTH_THRESHOLD_PERCENT;
    }

    @Override
    protected void start(ServerLevel level, MillGuardNpc guard, long gameTime) {
        LivingEntity target = guard.getBrain()
                .getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);

        // Clear the attack target — stops the fight activity
        guard.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);

        // Calculate flee direction: directly away from the threat
        if (target != null) {
            Vec3 guardPos = guard.position();
            Vec3 threatPos = target.position();
            Vec3 fleeDir = guardPos.subtract(threatPos).normalize();
            Vec3 fleeTarget = guardPos.add(fleeDir.scale(FLEE_DISTANCE));

            BlockPos fleePos = BlockPos.containing(fleeTarget.x, guard.getY(), fleeTarget.z);
            // Find a valid surface position
            BlockPos surface = level.getHeightmapPos(
                    net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    fleePos);

            guard.getBrain().setMemory(MemoryModuleType.WALK_TARGET,
                    new WalkTarget(surface, 1.4f, 1)); // sprint speed
        }
    }

    @Override
    protected boolean shouldKeepRunning(MillGuardNpc guard) {
        // Keep fleeing until the walk target is reached or erased
        return guard.getBrain().getMemory(MemoryModuleType.WALK_TARGET).isPresent()
                && guard.getHealth() / guard.getMaxHealth() < HEALTH_THRESHOLD_PERCENT;
    }
}
