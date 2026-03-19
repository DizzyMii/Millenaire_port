package org.dizzymii.millenaire2.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import org.dizzymii.millenaire2.entity.MillVillager;

import java.util.List;

/**
 * Simple fallback behaviour that sets a random walk target near the villager.
 * Guaranteed to run when no other idle behaviour is applicable.
 */
public class RandomVillagerStroll extends ExtendedBehaviour<MillVillager> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT)
            );

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected void start(ServerLevel level, MillVillager villager, long gameTime) {
        BlockPos pos = villager.blockPosition();
        int dx = villager.getRandom().nextInt(11) - 5;
        int dz = villager.getRandom().nextInt(11) - 5;
        BlockPos target = pos.offset(dx, 0, dz);
        // Find ground level
        BlockPos ground = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, target);
        villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET,
                new WalkTarget(ground, 0.5f, 2));
    }
}
