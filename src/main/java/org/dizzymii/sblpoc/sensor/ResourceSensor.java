package org.dizzymii.sblpoc.sensor;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.PocNpc;
import org.dizzymii.sblpoc.SblPocSetup;
import org.dizzymii.sblpoc.ai.world.BlockCategory;
import org.dizzymii.sblpoc.ai.world.SpatialMemory;

import java.util.List;

/**
 * Periodically summarises what resources the NPC knows about from SpatialMemory
 * and writes boolean flags into brain memories so behaviours and the utility
 * evaluator can read them cheaply.
 *
 * Runs every 60 ticks (~3 seconds).
 */
public class ResourceSensor extends ExtendedSensor<PocNpc> {

    private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(
            SblPocSetup.KNOWS_IRON.get(),
            SblPocSetup.KNOWS_DIAMOND.get(),
            SblPocSetup.KNOWS_WATER.get(),
            SblPocSetup.NEARBY_CRAFTING_TABLE.get(),
            SblPocSetup.NEARBY_FURNACE.get()
    );

    public ResourceSensor() {
        setScanRate(entity -> 60);
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends ResourceSensor> type() {
        return SblPocSetup.RESOURCE_SENSOR.get();
    }

    @Override
    protected void doTick(ServerLevel level, PocNpc npc) {
        SpatialMemory spatial = npc.getSpatialMemory();

        setOrClear(npc, SblPocSetup.KNOWS_IRON.get(), spatial.knows(BlockCategory.IRON_ORE));
        setOrClear(npc, SblPocSetup.KNOWS_DIAMOND.get(), spatial.knows(BlockCategory.DIAMOND_ORE));
        setOrClear(npc, SblPocSetup.KNOWS_WATER.get(), spatial.knows(BlockCategory.WATER));

        var craftPos = spatial.findNearest(BlockCategory.CRAFTING_TABLE, npc.blockPosition());
        setOrClear(npc, SblPocSetup.NEARBY_CRAFTING_TABLE.get(),
                craftPos != null && craftPos.distSqr(npc.blockPosition()) < 256);

        var furnacePos = spatial.findNearest(BlockCategory.FURNACE, npc.blockPosition());
        setOrClear(npc, SblPocSetup.NEARBY_FURNACE.get(),
                furnacePos != null && furnacePos.distSqr(npc.blockPosition()) < 256);
    }

    private static void setOrClear(PocNpc npc, MemoryModuleType<Boolean> mem, boolean value) {
        if (value) {
            BrainUtils.setMemory(npc, mem, true);
        } else {
            BrainUtils.clearMemory(npc, mem);
        }
    }
}
