package org.dizzymii.millenaire2.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.ai.MillMemoryTypes;
import org.dizzymii.millenaire2.goal.Goal;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Combat behaviour that drives the villager toward the attack target
 * and delegates to the legacy defendVillage goal for action execution.
 */
public class DefendVillageBehaviour extends ExtendedBehaviour<MillVillager> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT)
            );

    @Nullable private LivingEntity target;

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, MillVillager villager) {
        if (villager.vtype == null || !villager.vtype.helpInAttacks) return false;
        this.target = BrainUtils.getMemory(villager, MemoryModuleType.ATTACK_TARGET);
        return target != null && target.isAlive();
    }

    @Override
    protected void start(MillVillager villager) {
        if (target == null) return;
        // Store active goal key in brain
        BrainUtils.setMemory(villager, MillMemoryTypes.ACTIVE_GOAL_KEY.get(), "defendvillage");
        // Walk toward target
        BehaviorUtils.setWalkAndLookTargetMemories(villager, target, 1.0f, 2);
    }

    @Override
    protected boolean shouldKeepRunning(MillVillager villager) {
        LivingEntity t = BrainUtils.getMemory(villager, MemoryModuleType.ATTACK_TARGET);
        return t != null && t.isAlive() && villager.isAlive();
    }

    @Override
    protected void tick(MillVillager villager) {
        LivingEntity t = BrainUtils.getMemory(villager, MemoryModuleType.ATTACK_TARGET);
        if (t == null || !t.isAlive()) return;

        double distSq = villager.distanceToSqr(t);

        // Keep walking toward target if far
        if (distSq > 4.0) {
            BehaviorUtils.setWalkAndLookTargetMemories(villager, t, 1.0f, 2);
        }

        // Melee attack when in range
        if (distSq <= 4.0 && villager.isUsingHandToHand) {
            villager.doHurtTarget(t);
        }

        // Bow attack at range
        if (distSq <= MillVillager.ARCHER_RANGE * MillVillager.ARCHER_RANGE && villager.isUsingBow) {
            // Delegate to legacy goal if available
            if (Goal.defendVillage != null) {
                try {
                    Goal.defendVillage.performAction(villager);
                } catch (Exception ignored) {}
            }
        }
    }

    @Override
    protected void stop(MillVillager villager) {
        this.target = null;
        villager.getBrain().eraseMemory(MillMemoryTypes.ACTIVE_GOAL_KEY.get());
    }
}
