package org.dizzymii.sblpoc.behaviour;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
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
 * Dodge sideways when an enemy is drawing a bow.
 *
 * Activates when ENEMY_DRAWING_BOW is true and NPC is not already shielding.
 * Commits to one sidestep direction, holds it for ~12 ticks, then stops.
 * Brief cooldown before dodging again to avoid twitching.
 */
public class DodgeProjectileBehaviour extends ExtendedBehaviour<PocNpc> {

    private static final float DODGE_SPEED = 1.3f;
    private static final double DODGE_DISTANCE = 2.5;
    private static final int DODGE_DURATION = 12;

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT),
                    Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED)
            );

    private int dodgeTimer = 0;
    private long lastDodgeEnd = 0;

    public DodgeProjectileBehaviour() {
        runFor(entity -> DODGE_DURATION + 5);
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PocNpc npc) {
        // Only dodge if enemy is drawing bow
        Boolean drawing = BrainUtils.getMemory(npc, SblPocSetup.ENEMY_DRAWING_BOW.get());
        if (drawing == null || !drawing) return false;

        // Don't dodge if already shielding
        if (npc.isUsingItem()) return false;

        // Cooldown — don't dodge-spam
        if (level.getGameTime() - lastDodgeEnd < 25) return false;

        return true;
    }

    @Override
    protected void start(ServerLevel level, PocNpc npc, long gameTime) {
        dodgeTimer = DODGE_DURATION;

        LivingEntity target = BrainUtils.getMemory(npc, MemoryModuleType.ATTACK_TARGET);
        if (target == null) return;

        // Pick a perpendicular direction to dodge
        Vec3 toEnemy = target.position().subtract(npc.position()).normalize();
        boolean dodgeRight = npc.getRandom().nextBoolean();
        Vec3 perpendicular = dodgeRight
                ? new Vec3(-toEnemy.z, 0, toEnemy.x)
                : new Vec3(toEnemy.z, 0, -toEnemy.x);

        Vec3 dodgePos = npc.position().add(perpendicular.scale(DODGE_DISTANCE));
        BrainUtils.setMemory(npc, MemoryModuleType.WALK_TARGET,
                new WalkTarget(dodgePos, DODGE_SPEED, 0));

        // Keep looking at the enemy while dodging
        BehaviorUtils.lookAtEntity(npc, target);
    }

    @Override
    protected boolean shouldKeepRunning(PocNpc npc) {
        return dodgeTimer > 0;
    }

    @Override
    protected void tick(ServerLevel level, PocNpc npc, long gameTime) {
        dodgeTimer--;
        LivingEntity target = BrainUtils.getMemory(npc, MemoryModuleType.ATTACK_TARGET);
        if (target != null) {
            BehaviorUtils.lookAtEntity(npc, target);
        }
    }

    @Override
    protected void stop(ServerLevel level, PocNpc npc, long gameTime) {
        lastDodgeEnd = gameTime;
        dodgeTimer = 0;
    }
}
