package org.dizzymii.sblpoc.behaviour.survival;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.PocNpc;
import org.dizzymii.sblpoc.ai.world.BlockCategory;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Finds the nearest tree, navigates to it, and chops all connected logs.
 * Collects saplings and drops automatically via vanilla drop mechanics.
 */
public class ChopTreeBehaviour extends ExtendedBehaviour<PocNpc> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT)
            );

    private static final double REACH_DISTANCE_SQ = 4.5 * 4.5;
    private static final int MAX_LOGS_PER_TREE = 30;

    private final Deque<BlockPos> logQueue = new ArrayDeque<>();
    @Nullable private BlockPos currentLog;
    private int breakProgress = 0;
    private int breakTime = 0;
    private boolean navigating = false;
    private int logsChopped = 0;

    public ChopTreeBehaviour() {
        noTimeout();
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PocNpc npc) {
        // Find nearest tree from spatial memory
        BlockPos treePos = npc.getSpatialMemory().findNearest(BlockCategory.LOG, npc.blockPosition());
        if (treePos == null) {
            // Scan nearby for logs as fallback
            treePos = findNearbyLog(level, npc.blockPosition(), 16);
        }
        if (treePos != null) {
            logQueue.clear();
            logQueue.add(treePos);
            return true;
        }
        return false;
    }

    @Override
    protected void start(ServerLevel level, PocNpc npc, long gameTime) {
        logsChopped = 0;
        advanceToNextLog(level, npc);
    }

    @Override
    protected void tick(ServerLevel level, PocNpc npc, long gameTime) {
        if (currentLog == null) {
            if (logQueue.isEmpty()) {
                doStop(level, npc, gameTime);
                return;
            }
            advanceToNextLog(level, npc);
            return;
        }

        BlockState state = level.getBlockState(currentLog);
        if (!state.is(BlockTags.LOGS)) {
            currentLog = null;
            return;
        }

        if (navigating) {
            double distSq = npc.blockPosition().distSqr(currentLog);
            if (distSq <= REACH_DISTANCE_SQ) {
                navigating = false;
                startMiningLog(level, npc);
            }
            return;
        }

        npc.getLookControl().setLookAt(currentLog.getX() + 0.5, currentLog.getY() + 0.5, currentLog.getZ() + 0.5);

        if (breakProgress % 5 == 0) {
            npc.swing(InteractionHand.MAIN_HAND);
        }

        breakProgress++;
        level.destroyBlockProgress(npc.getId(), currentLog, (int) ((float) breakProgress / breakTime * 9));

        if (breakProgress >= breakTime) {
            // Destroy the log
            level.destroyBlock(currentLog, true, npc);
            logsChopped++;

            // Scan for connected logs
            discoverConnectedLogs(level, currentLog);

            currentLog = null;
            breakProgress = 0;
        }
    }

    @Override
    protected boolean shouldKeepRunning(PocNpc npc) {
        return (currentLog != null || !logQueue.isEmpty()) && logsChopped < MAX_LOGS_PER_TREE && npc.isAlive();
    }

    @Override
    protected void stop(ServerLevel level, PocNpc npc, long gameTime) {
        if (currentLog != null) {
            level.destroyBlockProgress(npc.getId(), currentLog, -1);
        }
        logQueue.clear();
        currentLog = null;
    }

    private void advanceToNextLog(ServerLevel level, PocNpc npc) {
        currentLog = logQueue.poll();
        if (currentLog == null) return;

        BlockState state = level.getBlockState(currentLog);
        if (!state.is(BlockTags.LOGS)) {
            currentLog = null;
            return;
        }

        // Equip axe if available
        equipAxe(npc);

        double distSq = npc.blockPosition().distSqr(currentLog);
        if (distSq > REACH_DISTANCE_SQ) {
            BrainUtils.setMemory(npc, MemoryModuleType.WALK_TARGET,
                    new WalkTarget(Vec3.atCenterOf(currentLog), 1.0f, 1));
            navigating = true;
        } else {
            navigating = false;
            startMiningLog(level, npc);
        }
    }

    private void startMiningLog(ServerLevel level, PocNpc npc) {
        if (currentLog == null) return;
        BlockState state = level.getBlockState(currentLog);

        ItemStack tool = npc.getMainHandItem();
        float miningSpeed = tool.getDestroySpeed(state);
        if (miningSpeed <= 1.0f) miningSpeed = 1.0f;

        float destroySpeed = state.getDestroySpeed(level, currentLog);
        breakTime = (int) Math.ceil(destroySpeed * 1.5f / miningSpeed * 20);
        if (breakTime < 1) breakTime = 1;
        breakProgress = 0;
    }

    private void discoverConnectedLogs(ServerLevel level, BlockPos center) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = 0; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    BlockPos neighbor = center.offset(dx, dy, dz);
                    if (level.getBlockState(neighbor).is(BlockTags.LOGS) && !logQueue.contains(neighbor)) {
                        logQueue.add(neighbor);
                    }
                }
            }
        }
    }

    private void equipAxe(PocNpc npc) {
        var lookup = npc.getInventoryModel().getBestToolFor(
                net.minecraft.world.level.block.Blocks.OAK_LOG.defaultBlockState());
        if (lookup != null && lookup.inventorySlot() >= 0) {
            ItemStack current = npc.getMainHandItem();
            npc.setItemInHand(InteractionHand.MAIN_HAND, lookup.stack());
            npc.getInventory().setItem(lookup.inventorySlot(), current);
        }
    }

    @Nullable
    private static BlockPos findNearbyLog(ServerLevel level, BlockPos center, int radius) {
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
        double bestDist = Double.MAX_VALUE;
        BlockPos best = null;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -4; y <= 12; y++) {
                for (int z = -radius; z <= radius; z++) {
                    mpos.set(center.getX() + x, center.getY() + y, center.getZ() + z);
                    if (level.getBlockState(mpos).is(BlockTags.LOGS)) {
                        double dist = center.distSqr(mpos);
                        if (dist < bestDist) {
                            bestDist = dist;
                            best = mpos.immutable();
                        }
                    }
                }
            }
        }
        return best;
    }
}
