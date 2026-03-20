package org.dizzymii.sblpoc.behaviour.survival;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import org.dizzymii.sblpoc.PocNpc;
import org.dizzymii.sblpoc.ai.world.POIType;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Builds a small 9x9 farm: tills soil, places a water source in the center,
 * and plants seeds in rows. Registers the farm as a POI.
 */
public class BuildFarmBehaviour extends ExtendedBehaviour<PocNpc> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT)
            );

    private final Deque<FarmTask> taskQueue = new ArrayDeque<>();
    @Nullable private BlockPos origin;
    private int tickCounter = 0;
    private boolean completed = false;

    public BuildFarmBehaviour() {
        noTimeout();
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PocNpc npc) {
        return npc.getInventoryModel().hasItem(Items.WHEAT_SEEDS, 8);
    }

    @Override
    protected void start(ServerLevel level, PocNpc npc, long gameTime) {
        completed = false;
        taskQueue.clear();
        tickCounter = 0;

        origin = npc.blockPosition().offset(3, 0, 0);
        generateFarmPlan(level);
    }

    @Override
    protected void tick(ServerLevel level, PocNpc npc, long gameTime) {
        if (taskQueue.isEmpty()) {
            if (origin != null) {
                npc.getSpatialMemory().setPOI(POIType.FARM, origin);
            }
            completed = true;
            doStop(level, npc, gameTime);
            return;
        }

        tickCounter++;
        if (tickCounter < 3) return;
        tickCounter = 0;

        FarmTask task = taskQueue.poll();
        if (task == null) return;

        switch (task.type) {
            case TILL -> {
                if (level.getBlockState(task.pos).is(Blocks.GRASS_BLOCK)
                        || level.getBlockState(task.pos).is(Blocks.DIRT)) {
                    level.setBlock(task.pos, Blocks.FARMLAND.defaultBlockState(), 3);
                    npc.swing(InteractionHand.MAIN_HAND);
                }
            }
            case WATER -> {
                level.setBlock(task.pos, Blocks.WATER.defaultBlockState(), 3);
            }
            case PLANT -> {
                if (level.getBlockState(task.pos.below()).is(Blocks.FARMLAND)) {
                    int seedSlot = npc.getInventoryModel().findSlot(s -> s.getItem() == Items.WHEAT_SEEDS);
                    if (seedSlot >= 0) {
                        npc.getInventory().removeItem(seedSlot, 1);
                        level.setBlock(task.pos, Blocks.WHEAT.defaultBlockState(), 3);
                        npc.getInventoryModel().markDirty();
                    }
                }
            }
        }

        npc.getLookControl().setLookAt(task.pos.getX() + 0.5, task.pos.getY() + 0.5, task.pos.getZ() + 0.5);
    }

    @Override
    protected boolean shouldKeepRunning(PocNpc npc) {
        return !completed && !taskQueue.isEmpty();
    }

    @Override
    protected void stop(ServerLevel level, PocNpc npc, long gameTime) {
        taskQueue.clear();
    }

    private void generateFarmPlan(ServerLevel level) {
        if (origin == null) return;

        // 9x9 farm with water in center
        int size = 9;
        int half = size / 2;

        // Find ground level for origin
        while (!level.getBlockState(origin).isAir() && origin.getY() < 320) {
            origin = origin.above();
        }
        while (level.getBlockState(origin.below()).isAir() && origin.getY() > -60) {
            origin = origin.below();
        }

        BlockPos groundOrigin = origin.below();

        // Till all dirt/grass
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                BlockPos pos = groundOrigin.offset(x - half, 0, z - half);
                if (x == half && z == half) {
                    // Water source in center
                    taskQueue.add(new FarmTask(pos, TaskType.WATER));
                } else {
                    taskQueue.add(new FarmTask(pos, TaskType.TILL));
                }
            }
        }

        // Plant seeds on tilled soil
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                if (x == half && z == half) continue; // Skip water
                BlockPos pos = groundOrigin.offset(x - half, 1, z - half);
                taskQueue.add(new FarmTask(pos, TaskType.PLANT));
            }
        }
    }

    private enum TaskType { TILL, WATER, PLANT }

    private record FarmTask(BlockPos pos, TaskType type) {}
}
