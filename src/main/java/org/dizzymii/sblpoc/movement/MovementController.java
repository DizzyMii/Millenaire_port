package org.dizzymii.sblpoc.movement;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.dizzymii.sblpoc.PocNpc;

/**
 * Manages sprint/sneak/swim state transitions for human-like movement.
 *
 * Rules:
 * - Sprint when travelling long distances (>16 blocks to target) and not sneaking
 * - Sneak near edges (DangerZoneSensor NEAR_CLIFF), near hostile creepers, or
 *   when approaching a target silently
 * - Swim when in water: surface swim if heading to surface, underwater swim if diving
 * - Stamina: sprinting drains stamina, walking recharges it
 */
public class MovementController {

    private static final double SPRINT_THRESHOLD_SQ = 16.0 * 16.0;
    private static final int MAX_STAMINA = 200; // 10 seconds of sprinting
    private static final int STAMINA_DRAIN = 1;
    private static final int STAMINA_REGEN = 2;

    private int stamina = MAX_STAMINA;
    private MovementMode currentMode = MovementMode.WALK;
    private boolean forceSneak = false;
    private boolean forceSprint = false;

    public enum MovementMode {
        WALK,
        SPRINT,
        SNEAK,
        SWIM
    }

    /**
     * Called every tick to update movement mode based on context.
     */
    public void tick(PocNpc npc) {
        // Detect swimming
        if (npc.isInWater()) {
            currentMode = MovementMode.SWIM;
            npc.setSprinting(false);
            npc.setShiftKeyDown(false);
            handleSwimming(npc);
            regenStamina();
            return;
        }

        // Forced states
        if (forceSneak) {
            applySneak(npc);
            regenStamina();
            return;
        }

        if (forceSprint && stamina > 0) {
            applySprint(npc);
            return;
        }

        // Auto-determine mode based on context
        Vec3 target = getWalkTarget(npc);
        if (target != null) {
            double distSq = npc.position().distanceToSqr(target);

            // Sprint for long distances if we have stamina
            if (distSq > SPRINT_THRESHOLD_SQ && stamina > 20 && !isNearDanger(npc)) {
                applySprint(npc);
                return;
            }
        }

        // Near cliff or other danger — sneak
        if (isNearDanger(npc)) {
            applySneak(npc);
            regenStamina();
            return;
        }

        // Default: walk
        applyWalk(npc);
        regenStamina();
    }

    // ========== State Application ==========

    private void applySprint(PocNpc npc) {
        currentMode = MovementMode.SPRINT;
        npc.setSprinting(true);
        npc.setShiftKeyDown(false);
        stamina -= STAMINA_DRAIN;
        if (stamina <= 0) {
            stamina = 0;
            applyWalk(npc); // Exhausted, fall back to walk
        }
    }

    private void applySneak(PocNpc npc) {
        currentMode = MovementMode.SNEAK;
        npc.setSprinting(false);
        npc.setShiftKeyDown(true);
    }

    private void applyWalk(PocNpc npc) {
        currentMode = MovementMode.WALK;
        npc.setSprinting(false);
        npc.setShiftKeyDown(false);
    }

    private void handleSwimming(PocNpc npc) {
        // Swim upward when submerged and targeting surface
        if (npc.isUnderWater()) {
            Vec3 motion = npc.getDeltaMovement();
            npc.setDeltaMovement(motion.x, Math.max(motion.y, 0.04), motion.z);
        }
    }

    private void regenStamina() {
        stamina = Math.min(MAX_STAMINA, stamina + STAMINA_REGEN);
    }

    // ========== Context Queries ==========

    private boolean isNearDanger(PocNpc npc) {
        // Check brain memory for NEAR_CLIFF or NEAR_LAVA
        var nearCliff = net.tslat.smartbrainlib.util.BrainUtils.getMemory(
                npc, org.dizzymii.sblpoc.SblPocSetup.NEAR_CLIFF.get());
        var nearLava = net.tslat.smartbrainlib.util.BrainUtils.getMemory(
                npc, org.dizzymii.sblpoc.SblPocSetup.NEAR_LAVA.get());
        return Boolean.TRUE.equals(nearCliff) || Boolean.TRUE.equals(nearLava);
    }

    @javax.annotation.Nullable
    private Vec3 getWalkTarget(PocNpc npc) {
        var walkTarget = net.tslat.smartbrainlib.util.BrainUtils.getMemory(
                npc, net.minecraft.world.entity.ai.memory.MemoryModuleType.WALK_TARGET);
        if (walkTarget != null) {
            BlockPos pos = walkTarget.getTarget().currentBlockPosition();
            return new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        }
        return null;
    }

    // ========== Public API ==========

    public MovementMode getCurrentMode() {
        return currentMode;
    }

    public int getStamina() {
        return stamina;
    }

    public int getMaxStamina() {
        return MAX_STAMINA;
    }

    public void setForceSneak(boolean sneak) {
        this.forceSneak = sneak;
    }

    public void setForceSprint(boolean sprint) {
        this.forceSprint = sprint;
    }
}
