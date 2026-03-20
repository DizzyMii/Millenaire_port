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
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import org.dizzymii.sblpoc.PocNpc;
import org.dizzymii.sblpoc.ai.world.POIType;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Builds a minimal 5x5x3 enclosed shelter with a door, torch, and bed.
 * Uses planks from inventory. Places blocks one at a time for visual effect.
 */
public class BuildShelterBehaviour extends ExtendedBehaviour<PocNpc> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT)
            );

    private static final int MIN_PLANKS = 40;

    private final Deque<PlaceTask> buildQueue = new ArrayDeque<>();
    private BlockPos origin;
    private int ticksPerBlock = 4;
    private int tickCounter = 0;
    private boolean completed = false;

    public BuildShelterBehaviour() {
        noTimeout();
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PocNpc npc) {
        return npc.getInventoryModel().countItem(Items.OAK_PLANKS) >= MIN_PLANKS
                || npc.getInventoryModel().countItem(Items.SPRUCE_PLANKS) >= MIN_PLANKS
                || npc.getInventoryModel().countItem(Items.BIRCH_PLANKS) >= MIN_PLANKS
                || npc.getInventoryModel().countItem(Items.DARK_OAK_PLANKS) >= MIN_PLANKS
                || npc.getInventoryModel().countItem(Items.JUNGLE_PLANKS) >= MIN_PLANKS
                || npc.getInventoryModel().countItem(Items.ACACIA_PLANKS) >= MIN_PLANKS;
    }

    @Override
    protected void start(ServerLevel level, PocNpc npc, long gameTime) {
        completed = false;
        buildQueue.clear();
        tickCounter = 0;

        // Find a flat spot near the NPC
        origin = findBuildSite(level, npc.blockPosition());
        if (origin == null) {
            origin = npc.blockPosition();
        }

        generateBuildPlan();
    }

    @Override
    protected void tick(ServerLevel level, PocNpc npc, long gameTime) {
        if (buildQueue.isEmpty()) {
            // Register home POI
            npc.getSpatialMemory().setPOI(POIType.HOME_BASE, origin);
            completed = true;
            doStop(level, npc, gameTime);
            return;
        }

        tickCounter++;
        if (tickCounter < ticksPerBlock) return;
        tickCounter = 0;

        PlaceTask task = buildQueue.poll();
        if (task == null) return;

        // Consume material from inventory if needed
        if (task.consumeItem != null) {
            int slot = npc.getInventoryModel().findSlot(s -> s.getItem() == task.consumeItem);
            if (slot >= 0) {
                npc.getInventory().removeItem(slot, 1);
                npc.getInventoryModel().markDirty();
            }
        }

        level.setBlock(task.pos, task.state, 3);
        npc.swing(InteractionHand.MAIN_HAND);
        npc.getLookControl().setLookAt(task.pos.getX() + 0.5, task.pos.getY() + 0.5, task.pos.getZ() + 0.5);
    }

    @Override
    protected boolean shouldKeepRunning(PocNpc npc) {
        return !completed && !buildQueue.isEmpty();
    }

    @Override
    protected void stop(ServerLevel level, PocNpc npc, long gameTime) {
        buildQueue.clear();
    }

    private void generateBuildPlan() {
        // Build a 5x5x3 box shelter
        // Floor
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                buildQueue.add(new PlaceTask(origin.offset(x, -1, z),
                        Blocks.OAK_PLANKS.defaultBlockState(), Items.OAK_PLANKS));
            }
        }

        // Walls (y=0 and y=1)
        for (int y = 0; y <= 2; y++) {
            for (int x = 0; x < 5; x++) {
                for (int z = 0; z < 5; z++) {
                    boolean isWall = x == 0 || x == 4 || z == 0 || z == 4;
                    boolean isDoor = x == 2 && z == 0 && (y == 0 || y == 1);
                    if (isWall && !isDoor) {
                        buildQueue.add(new PlaceTask(origin.offset(x, y, z),
                                Blocks.OAK_PLANKS.defaultBlockState(), Items.OAK_PLANKS));
                    }
                }
            }
        }

        // Roof (y=3)
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                buildQueue.add(new PlaceTask(origin.offset(x, 3, z),
                        Blocks.OAK_PLANKS.defaultBlockState(), Items.OAK_PLANKS));
            }
        }

        // Door
        buildQueue.add(new PlaceTask(origin.offset(2, 0, 0),
                Blocks.OAK_DOOR.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER), null));
        buildQueue.add(new PlaceTask(origin.offset(2, 1, 0),
                Blocks.OAK_DOOR.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), null));

        // Torch inside
        buildQueue.add(new PlaceTask(origin.offset(2, 1, 2),
                Blocks.TORCH.defaultBlockState(), null));

        // Bed if we have wool
        int woolSlot = npc_findWool();
        if (woolSlot >= 0) {
            buildQueue.add(new PlaceTask(origin.offset(3, 0, 3),
                    Blocks.WHITE_BED.defaultBlockState(), null));
        }
    }

    private int npc_findWool() {
        // Placeholder — bed placement is best-effort
        return -1;
    }

    private BlockPos findBuildSite(ServerLevel level, BlockPos near) {
        // Search for a flat 5x5 area near the NPC
        for (int r = 0; r < 10; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (Math.abs(dx) != r && Math.abs(dz) != r) continue;
                    BlockPos base = near.offset(dx, 0, dz);
                    // Find ground level
                    while (!level.getBlockState(base).isSolid() && base.getY() > near.getY() - 5) {
                        base = base.below();
                    }
                    base = base.above();
                    if (isFlatEnough(level, base, 5, 5)) {
                        return base;
                    }
                }
            }
        }
        return near;
    }

    private boolean isFlatEnough(ServerLevel level, BlockPos base, int width, int depth) {
        int baseY = base.getY();
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                BlockPos check = base.offset(x, -1, z);
                if (!level.getBlockState(check).isSolid()) return false;
                if (level.getBlockState(check.above()).isSolid()) return false;
            }
        }
        return true;
    }

    private record PlaceTask(BlockPos pos, net.minecraft.world.level.block.state.BlockState state,
                             net.minecraft.world.item.Item consumeItem) {}
}
