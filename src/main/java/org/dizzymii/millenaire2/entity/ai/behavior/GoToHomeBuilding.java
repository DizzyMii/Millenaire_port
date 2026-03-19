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
import org.dizzymii.millenaire2.entity.ai.MillMemoryTypes;

import java.util.List;

/**
 * Behaviour: Walk to this villager's home building.
 * Equivalent to the original GoalGoHome / night-time "go home" logic.
 */
public class GoToHomeBuilding extends ExtendedBehaviour<MillVillager> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT)
            );

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, MillVillager villager) {
        return villager.housePoint != null;
    }

    @Override
    protected void start(ServerLevel level, MillVillager villager, long gameTime) {
        BlockPos home = villager.getBrain().getMemory(MillMemoryTypes.HOME_BUILDING_POS.get()).orElse(null);
        if (home == null && villager.housePoint != null) {
            home = villager.housePoint.toBlockPos();
        }
        if (home != null) {
            villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET,
                    new WalkTarget(home, 0.6f, 2));
        }
    }
}
