package org.dizzymii.millenaire2.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.millenaire2.entity.MillGuardNpc;

import java.util.List;

/**
 * Targeting behaviour that promotes NEAREST_HOSTILE → ATTACK_TARGET.
 * Also handles retaliation: if hurt by a living entity, that entity
 * becomes the attack target even if it wasn't the nearest hostile.
 *
 * This is the bridge between the sensor layer and the fight activity.
 * Once ATTACK_TARGET is set, SmartBrainLib automatically transitions
 * the brain from IDLE to FIGHT activity.
 */
public class TargetNearestHostileBehaviour extends ExtendedBehaviour<MillGuardNpc> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.NEAREST_HOSTILE, MemoryStatus.REGISTERED),
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT)
            );

    public TargetNearestHostileBehaviour() {
        runFor(entity -> 1); // instant — just sets the target and exits
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, MillGuardNpc guard) {
        // Trigger if we have a hostile nearby OR we were just hurt
        return guard.getBrain().getMemory(MemoryModuleType.NEAREST_HOSTILE).isPresent()
                || guard.getBrain().getMemory(MemoryModuleType.HURT_BY_ENTITY).isPresent();
    }

    @Override
    protected void start(ServerLevel level, MillGuardNpc guard, long gameTime) {
        LivingEntity target = null;

        // Priority 1: Retaliate against whoever just hurt us
        LivingEntity attacker = guard.getBrain()
                .getMemory(MemoryModuleType.HURT_BY_ENTITY).orElse(null);
        if (attacker != null && attacker.isAlive()) {
            target = attacker;
        }

        // Priority 2: Nearest hostile detected by sensor
        if (target == null) {
            target = guard.getBrain()
                    .getMemory(MemoryModuleType.NEAREST_HOSTILE).orElse(null);
        }

        if (target != null && target.isAlive()) {
            BrainUtils.setMemory(guard, MemoryModuleType.ATTACK_TARGET, target);
        }
    }
}
