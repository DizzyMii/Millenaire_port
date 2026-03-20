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
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import org.dizzymii.sblpoc.PocNpc;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Builds a nether portal frame using obsidian from inventory,
 * then lights it with flint and steel. Standard 4x5 portal.
 */
public class BuildNetherPortalBehaviour extends ExtendedBehaviour<PocNpc> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT)
            );

    private static final int OBSIDIAN_NEEDED = 10; // Minimum for portal corners-optional frame

    private final Deque<BlockPos> buildQueue = new ArrayDeque<>();
    @Nullable private BlockPos origin;
    private int tickCounter = 0;
    private boolean completed = false;

    public BuildNetherPortalBehaviour() {
        noTimeout();
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PocNpc npc) {
        return npc.getInventoryModel().countItem(Items.OBSIDIAN) >= OBSIDIAN_NEEDED
                && npc.getInventoryModel().hasItem(Items.FLINT_AND_STEEL);
    }

    @Override
    protected void start(ServerLevel level, PocNpc npc, long gameTime) {
        completed = false;
        buildQueue.clear();
        tickCounter = 0;

        origin = npc.blockPosition().offset(2, 0, 0);
        generatePortalFrame();
    }

    @Override
    protected void tick(ServerLevel level, PocNpc npc, long gameTime) {
        if (buildQueue.isEmpty()) {
            // Light the portal
            if (origin != null) {
                BlockPos firePos = origin.offset(1, 1, 0);
                level.setBlock(firePos, Blocks.NETHER_PORTAL.defaultBlockState(), 3);
            }
            npc.swing(InteractionHand.MAIN_HAND);
            completed = true;
            doStop(level, npc, gameTime);
            return;
        }

        tickCounter++;
        if (tickCounter < 4) return;
        tickCounter = 0;

        BlockPos pos = buildQueue.poll();
        if (pos == null) return;

        // Consume obsidian
        int slot = npc.getInventoryModel().findSlot(s -> s.getItem() == Items.OBSIDIAN);
        if (slot >= 0) {
            npc.getInventory().removeItem(slot, 1);
            npc.getInventoryModel().markDirty();
        }

        level.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), 3);
        npc.swing(InteractionHand.MAIN_HAND);
        npc.getLookControl().setLookAt(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    @Override
    protected boolean shouldKeepRunning(PocNpc npc) {
        return !completed && !buildQueue.isEmpty();
    }

    @Override
    protected void stop(ServerLevel level, PocNpc npc, long gameTime) {
        buildQueue.clear();
    }

    private void generatePortalFrame() {
        if (origin == null) return;

        // Standard 4-wide x 5-tall portal frame
        // Bottom row
        for (int x = 0; x < 4; x++) {
            buildQueue.add(origin.offset(x, 0, 0));
        }
        // Left pillar
        for (int y = 1; y <= 3; y++) {
            buildQueue.add(origin.offset(0, y, 0));
        }
        // Right pillar
        for (int y = 1; y <= 3; y++) {
            buildQueue.add(origin.offset(3, y, 0));
        }
        // Top row
        for (int x = 0; x < 4; x++) {
            buildQueue.add(origin.offset(x, 4, 0));
        }
    }
}
