package org.dizzymii.sblpoc.behaviour;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.PocNpc;
import org.dizzymii.sblpoc.SblPocSetup;

import java.util.List;

/**
 * Seeks cover behind solid blocks when under ranged fire.
 *
 * - Activates when UNDER_RANGED_FIRE is true and NPC has an attack target
 * - Scans nearby positions (8-block radius) for spots where a solid block
 *   breaks line-of-sight to the attacker
 * - Scores candidates: closer to NPC = better, must block LOS
 * - Moves to the best cover position and peeks out to shoot/re-engage
 */
public class SeekCoverBehaviour extends ExtendedBehaviour<PocNpc> {

    private static final int SEARCH_RADIUS = 8;
    private static final float MOVE_SPEED = 1.3f;
    private static final int COVER_HOLD_TICKS = 40; // Stay in cover briefly

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT),
                    Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED)
            );

    private BlockPos coverPos = null;
    private int holdTimer = 0;
    private boolean reachedCover = false;

    public SeekCoverBehaviour() {
        runFor(entity -> COVER_HOLD_TICKS + 60); // Time to reach + hold
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PocNpc npc) {
        // Only seek cover when under ranged fire
        Boolean underFire = BrainUtils.getMemory(npc, SblPocSetup.UNDER_RANGED_FIRE.get());
        if (underFire == null || !underFire) return false;

        // Must not already be using shield or eating
        if (npc.isUsingItem()) return false;

        LivingEntity target = BrainUtils.getMemory(npc, MemoryModuleType.ATTACK_TARGET);
        if (target == null || !target.isAlive()) return false;

        // Try to find a cover position
        coverPos = findCoverPosition(level, npc, target);
        return coverPos != null;
    }

    @Override
    protected void start(ServerLevel level, PocNpc npc, long gameTime) {
        holdTimer = 0;
        reachedCover = false;

        if (coverPos != null) {
            BrainUtils.setMemory(npc, MemoryModuleType.WALK_TARGET,
                    new WalkTarget(coverPos, MOVE_SPEED, 1));
            BrainUtils.setMemory(npc, SblPocSetup.COMBAT_STATE.get(), "cover");
        }
    }

    @Override
    protected boolean shouldKeepRunning(PocNpc npc) {
        LivingEntity target = BrainUtils.getMemory(npc, MemoryModuleType.ATTACK_TARGET);
        return target != null && target.isAlive() && npc.isAlive() && coverPos != null;
    }

    @Override
    protected void tick(ServerLevel level, PocNpc npc, long gameTime) {
        LivingEntity target = BrainUtils.getMemory(npc, MemoryModuleType.ATTACK_TARGET);
        if (target == null) return;

        BehaviorUtils.lookAtEntity(npc, target);

        if (!reachedCover) {
            // Check if we've reached cover
            double distToCover = npc.distanceToSqr(coverPos.getX() + 0.5, coverPos.getY(), coverPos.getZ() + 0.5);
            if (distToCover < 4.0) { // Within 2 blocks of cover pos
                reachedCover = true;
                holdTimer = COVER_HOLD_TICKS;
            }
        } else {
            holdTimer--;
            // While in cover, stay put
            if (holdTimer <= 0) {
                // Done holding — exit cover to re-engage
                return;
            }
        }
    }

    @Override
    protected void stop(ServerLevel level, PocNpc npc, long gameTime) {
        coverPos = null;
        holdTimer = 0;
        reachedCover = false;
        BrainUtils.clearMemory(npc, SblPocSetup.UNDER_RANGED_FIRE.get());
    }

    /**
     * Finds the best cover position near the NPC that breaks line-of-sight
     * to the attacker. Searches in a grid around the NPC and raycasts
     * from each candidate to the attacker to check if a solid block is in the way.
     */
    private BlockPos findCoverPosition(ServerLevel level, PocNpc npc, LivingEntity attacker) {
        BlockPos npcPos = npc.blockPosition();
        Vec3 attackerEye = attacker.getEyePosition();

        BlockPos bestPos = null;
        double bestScore = Double.MAX_VALUE;

        for (int dx = -SEARCH_RADIUS; dx <= SEARCH_RADIUS; dx += 2) {
            for (int dz = -SEARCH_RADIUS; dz <= SEARCH_RADIUS; dz += 2) {
                for (int dy = -2; dy <= 2; dy++) {
                    BlockPos candidate = npcPos.offset(dx, dy, dz);

                    // Must be a standable position: solid below, air at feet and head
                    if (!isStandable(level, candidate)) continue;

                    Vec3 candidateEye = new Vec3(candidate.getX() + 0.5, candidate.getY() + 1.5, candidate.getZ() + 0.5);

                    // Raycast from candidate eye to attacker — if blocked, this is cover
                    BlockHitResult hit = level.clip(new ClipContext(
                            candidateEye, attackerEye,
                            ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, npc));

                    if (hit.getType() == HitResult.Type.BLOCK) {
                        // This position has LOS blocked — it's cover!
                        double distToNpc = npcPos.distSqr(candidate);
                        double distToAttacker = candidate.distSqr(attacker.blockPosition());

                        // Score: prefer closer to NPC but not too close to attacker
                        double score = distToNpc - (distToAttacker * 0.1);
                        if (score < bestScore) {
                            bestScore = score;
                            bestPos = candidate;
                        }
                    }
                }
            }
        }
        return bestPos;
    }

    private boolean isStandable(ServerLevel level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        BlockState atFeet = level.getBlockState(pos);
        BlockState atHead = level.getBlockState(pos.above());

        return below.isSolid()
                && !atFeet.isSolid()
                && !atHead.isSolid();
    }
}
