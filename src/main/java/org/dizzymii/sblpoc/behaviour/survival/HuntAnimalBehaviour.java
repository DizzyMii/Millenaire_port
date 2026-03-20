package org.dizzymii.sblpoc.behaviour.survival;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.PocNpc;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Finds the nearest passive animal, approaches it, and kills it for food drops.
 * Equips a sword if available for faster killing.
 */
public class HuntAnimalBehaviour extends ExtendedBehaviour<PocNpc> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT)
            );

    private static final double SEARCH_RANGE = 24.0;
    private static final double ATTACK_RANGE_SQ = 2.5 * 2.5;
    private static final int ATTACK_COOLDOWN = 15;

    @Nullable private LivingEntity target;
    private int attackCooldown = 0;
    private int ticksRunning = 0;
    private static final int MAX_TICKS = 400; // 20 seconds

    public HuntAnimalBehaviour() {
        noTimeout();
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PocNpc npc) {
        target = findNearestAnimal(level, npc);
        return target != null;
    }

    @Override
    protected void start(ServerLevel level, PocNpc npc, long gameTime) {
        ticksRunning = 0;
        attackCooldown = 0;
        equipSword(npc);

        if (target != null) {
            BrainUtils.setMemory(npc, MemoryModuleType.WALK_TARGET,
                    new WalkTarget(target.position(), 1.2f, 1));
        }
    }

    @Override
    protected void tick(ServerLevel level, PocNpc npc, long gameTime) {
        ticksRunning++;

        if (target == null || !target.isAlive() || ticksRunning > MAX_TICKS) {
            doStop(level, npc, gameTime);
            return;
        }

        double distSq = npc.distanceToSqr(target);

        if (distSq > ATTACK_RANGE_SQ) {
            // Chase
            if (ticksRunning % 10 == 0) {
                BrainUtils.setMemory(npc, MemoryModuleType.WALK_TARGET,
                        new WalkTarget(target.position(), 1.2f, 1));
            }
            npc.getLookControl().setLookAt(target);
        } else {
            // Attack
            attackCooldown--;
            if (attackCooldown <= 0) {
                npc.getLookControl().setLookAt(target);
                npc.swing(InteractionHand.MAIN_HAND);
                npc.doHurtTarget(target);
                attackCooldown = ATTACK_COOLDOWN;
            }
        }
    }

    @Override
    protected boolean shouldKeepRunning(PocNpc npc) {
        return target != null && target.isAlive() && ticksRunning < MAX_TICKS;
    }

    @Override
    protected void stop(ServerLevel level, PocNpc npc, long gameTime) {
        target = null;
    }

    @Nullable
    private static LivingEntity findNearestAnimal(ServerLevel level, PocNpc npc) {
        AABB searchBox = npc.getBoundingBox().inflate(SEARCH_RANGE);
        List<Entity> entities = level.getEntities(npc, searchBox,
                e -> e instanceof Animal && e.isAlive());

        Entity nearest = null;
        double nearestDist = Double.MAX_VALUE;
        for (Entity e : entities) {
            double dist = npc.distanceToSqr(e);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = e;
            }
        }
        return nearest instanceof LivingEntity le ? le : null;
    }

    private void equipSword(PocNpc npc) {
        if (npc.getMainHandItem().getItem() instanceof SwordItem) return;

        int swordSlot = npc.getInventoryModel().findSlot(s -> s.getItem() instanceof SwordItem);
        if (swordSlot >= 0) {
            var current = npc.getMainHandItem();
            npc.setItemInHand(InteractionHand.MAIN_HAND, npc.getInventory().getItem(swordSlot));
            npc.getInventory().setItem(swordSlot, current);
        }
    }
}
