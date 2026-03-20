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
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.PocNpc;
import org.dizzymii.sblpoc.SblPocSetup;

import java.util.List;

/**
 * Player-like ranged attack using a bow.
 *
 * - Equips bow when target is 6-24 blocks away
 * - Stands still to draw and aim (like a player would)
 * - Commits to one strafe direction between shots, not constant toggling
 * - Only repositions after firing, not during draw
 * - Backs away deliberately if target approaches, not every tick
 */
public class SmartRangedAttack extends ExtendedBehaviour<PocNpc> {

    private static final double MIN_RANGE_SQ = 36.0;   // 6 blocks
    private static final double MAX_RANGE_SQ = 576.0;  // 24 blocks
    private static final int DRAW_TICKS = 22;
    private static final float STRAFE_SPEED = 0.6f;
    private static final float BACKPEDAL_SPEED = 0.8f;

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT),
                    Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED)
            );

    private enum Phase { AIM, REPOSITION, BACKPEDAL }

    private int drawTimer = 0;
    private boolean strafingRight = true;
    private boolean hasBow = false;
    private Phase phase = Phase.AIM;
    private int repositionTimer = 0;
    private int shotsFired = 0;

    public SmartRangedAttack() {
        runFor(entity -> 400);
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PocNpc npc) {
        LivingEntity target = BrainUtils.getMemory(npc, MemoryModuleType.ATTACK_TARGET);
        if (target == null || !target.isAlive()) return false;

        double distSq = npc.distanceToSqr(target);
        if (distSq < MIN_RANGE_SQ || distSq > MAX_RANGE_SQ) return false;

        if (npc.isUsingItem()) return false;

        return hasBowInInventory(npc) && hasArrows(npc);
    }

    @Override
    protected void start(ServerLevel level, PocNpc npc, long gameTime) {
        drawTimer = 0;
        hasBow = false;
        phase = Phase.AIM;
        repositionTimer = 0;
        shotsFired = 0;
        strafingRight = npc.getRandom().nextBoolean();

        equipBow(npc);
        BrainUtils.setMemory(npc, SblPocSetup.COMBAT_STATE.get(), "ranged");
    }

    @Override
    protected boolean shouldKeepRunning(PocNpc npc) {
        LivingEntity target = BrainUtils.getMemory(npc, MemoryModuleType.ATTACK_TARGET);
        if (target == null || !target.isAlive() || !npc.isAlive()) return false;

        double distSq = npc.distanceToSqr(target);
        if (distSq < MIN_RANGE_SQ * 0.5) return false;

        return hasBow && hasArrows(npc);
    }

    @Override
    protected void tick(ServerLevel level, PocNpc npc, long gameTime) {
        LivingEntity target = BrainUtils.getMemory(npc, MemoryModuleType.ATTACK_TARGET);
        if (target == null || !target.isAlive()) return;

        double distSq = npc.distanceToSqr(target);
        BehaviorUtils.lookAtEntity(npc, target);

        switch (phase) {
            case AIM -> tickAim(level, npc, target, distSq);
            case REPOSITION -> tickReposition(npc, target);
            case BACKPEDAL -> tickBackpedal(npc, target, distSq);
        }
    }

    private void tickAim(ServerLevel level, PocNpc npc, LivingEntity target, double distSq) {
        // If target is approaching, switch to backpedal
        if (distSq < MIN_RANGE_SQ * 1.2) {
            phase = Phase.BACKPEDAL;
            commitBackpedal(npc, target);
            return;
        }

        // Stand still and draw — a player stops moving to aim
        if (!npc.isUsingItem() && npc.getMainHandItem().getItem() instanceof BowItem) {
            npc.startUsingItem(InteractionHand.MAIN_HAND);
            drawTimer = 0;
        }

        if (npc.isUsingItem()) {
            drawTimer++;
            if (drawTimer >= DRAW_TICKS) {
                shootArrow(level, npc, target);
                npc.stopUsingItem();
                drawTimer = 0;
                consumeArrow(npc);
                shotsFired++;

                // After shooting, reposition to a new spot (like a player peeks from different angles)
                phase = Phase.REPOSITION;
                repositionTimer = 15 + npc.getRandom().nextInt(10); // Brief move

                // Pick a strafe direction — commit to it for the whole reposition
                if (npc.getRandom().nextFloat() < 0.35f) {
                    strafingRight = !strafingRight;
                }
                commitStrafe(npc, target);
            }
        }
    }

    private void tickReposition(PocNpc npc, LivingEntity target) {
        repositionTimer--;
        if (repositionTimer <= 0) {
            // Done moving, aim again
            phase = Phase.AIM;
        }
    }

    private void tickBackpedal(PocNpc npc, LivingEntity target, double distSq) {
        repositionTimer--;
        // Once we've backed up enough or timer expired, aim again
        if (distSq > MIN_RANGE_SQ * 1.5 || repositionTimer <= 0) {
            phase = Phase.AIM;
        }
    }

    private void commitStrafe(PocNpc npc, LivingEntity target) {
        Vec3 toTarget = target.position().subtract(npc.position()).normalize();
        Vec3 perpendicular = strafingRight
                ? new Vec3(-toTarget.z, 0, toTarget.x)
                : new Vec3(toTarget.z, 0, -toTarget.x);
        Vec3 strafePos = npc.position().add(perpendicular.scale(3.0));
        BrainUtils.setMemory(npc, MemoryModuleType.WALK_TARGET,
                new WalkTarget(strafePos, STRAFE_SPEED, 1));
    }

    private void commitBackpedal(PocNpc npc, LivingEntity target) {
        Vec3 away = npc.position().subtract(target.position()).normalize();
        Vec3 retreatPos = npc.position().add(away.scale(5.0));
        BrainUtils.setMemory(npc, MemoryModuleType.WALK_TARGET,
                new WalkTarget(retreatPos, BACKPEDAL_SPEED, 1));
        repositionTimer = 25;
    }

    @Override
    protected void stop(ServerLevel level, PocNpc npc, long gameTime) {
        npc.stopUsingItem();
        restoreMeleeLoadout(npc);
        drawTimer = 0;
        hasBow = false;
        shotsFired = 0;
        BrainUtils.setMemory(npc, SblPocSetup.COMBAT_STATE.get(), "melee");
    }

    // ========== Helpers ==========

    private void shootArrow(ServerLevel level, PocNpc npc, LivingEntity target) {
        ItemStack bowStack = npc.getMainHandItem();
        if (!(bowStack.getItem() instanceof BowItem)) return;

        // Calculate aim with predictive lead
        double dist = npc.distanceTo(target);
        Vec3 targetVel = target.getDeltaMovement();
        double travelTime = dist / 3.0; // rough arrow speed estimate
        Vec3 predictedPos = target.getEyePosition().add(targetVel.scale(travelTime));

        double dx = predictedPos.x - npc.getX();
        double dy = predictedPos.y - npc.getEyeY();
        double dz = predictedPos.z - npc.getZ();
        double horizontalDist = Math.sqrt(dx * dx + dz * dz);

        AbstractArrow arrow = new net.minecraft.world.entity.projectile.Arrow(level, npc, new ItemStack(Items.ARROW), bowStack);
        // Shoot with slight inaccuracy for realism
        arrow.shootFromRotation(npc, npc.getXRot(), npc.getYRot(), 0.0F, 2.5F, 3.0F);

        // Manually aim toward predicted position
        arrow.shoot(dx, dy + horizontalDist * 0.2, dz, 2.5F, 3.0F);

        level.addFreshEntity(arrow);
        npc.swing(InteractionHand.MAIN_HAND);
    }

    private boolean hasBowInInventory(PocNpc npc) {
        if (npc.getMainHandItem().getItem() instanceof BowItem) return true;
        for (int i = 0; i < npc.getInventory().getContainerSize(); i++) {
            if (npc.getInventory().getItem(i).getItem() instanceof BowItem) return true;
        }
        return false;
    }

    private boolean hasArrows(PocNpc npc) {
        for (int i = 0; i < npc.getInventory().getContainerSize(); i++) {
            if (npc.getInventory().getItem(i).is(Items.ARROW)) return true;
        }
        return false;
    }

    private void consumeArrow(PocNpc npc) {
        for (int i = 0; i < npc.getInventory().getContainerSize(); i++) {
            ItemStack stack = npc.getInventory().getItem(i);
            if (stack.is(Items.ARROW)) {
                stack.shrink(1);
                return;
            }
        }
    }

    private void equipBow(PocNpc npc) {
        // Save current main hand (sword) to inventory
        ItemStack currentMainHand = npc.getMainHandItem().copy();
        for (int i = 0; i < npc.getInventory().getContainerSize(); i++) {
            ItemStack stack = npc.getInventory().getItem(i);
            if (stack.getItem() instanceof BowItem) {
                npc.setItemInHand(InteractionHand.MAIN_HAND, stack.copy());
                npc.getInventory().setItem(i, currentMainHand);
                hasBow = true;
                return;
            }
        }
    }

    private void restoreMeleeLoadout(PocNpc npc) {
        ItemStack mainHand = npc.getMainHandItem();
        if (mainHand.getItem() instanceof BowItem) {
            // Find sword in inventory
            for (int i = 0; i < npc.getInventory().getContainerSize(); i++) {
                ItemStack stack = npc.getInventory().getItem(i);
                if (stack.getItem() instanceof net.minecraft.world.item.SwordItem) {
                    npc.setItemInHand(InteractionHand.MAIN_HAND, stack.copy());
                    npc.getInventory().setItem(i, mainHand.copy());
                    return;
                }
            }
            // No sword found — just stash the bow
            for (int i = 0; i < npc.getInventory().getContainerSize(); i++) {
                if (npc.getInventory().getItem(i).isEmpty()) {
                    npc.getInventory().setItem(i, mainHand.copy());
                    npc.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                    return;
                }
            }
        }
    }
}
