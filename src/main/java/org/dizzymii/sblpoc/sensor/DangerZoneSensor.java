package org.dizzymii.sblpoc.sensor;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.level.block.Blocks;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.PocNpc;
import org.dizzymii.sblpoc.SblPocSetup;

import java.util.List;

/**
 * Detects environmental hazards near the NPC:
 * - Nearby lava (within 4 blocks)
 * - Standing on an edge / above a ravine (Y drop > 4)
 * - In darkness (light level < 7)
 *
 * Writes boolean flags into brain memories so survival behaviours
 * can react (move away, place torches, avoid ledges).
 *
 * Runs every 20 ticks (1 second).
 */
public class DangerZoneSensor extends ExtendedSensor<PocNpc> {

    private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(
            SblPocSetup.NEAR_LAVA.get(),
            SblPocSetup.NEAR_CLIFF.get(),
            SblPocSetup.IN_DARKNESS.get()
    );

    public DangerZoneSensor() {
        setScanRate(entity -> 20);
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends DangerZoneSensor> type() {
        return SblPocSetup.DANGER_ZONE_SENSOR.get();
    }

    @Override
    protected void doTick(ServerLevel level, PocNpc npc) {
        BlockPos pos = npc.blockPosition();

        // Lava check: scan 4-block radius
        boolean nearLava = false;
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
        outer:
        for (int x = -4; x <= 4; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -4; z <= 4; z++) {
                    mpos.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    if (level.getBlockState(mpos).is(Blocks.LAVA)) {
                        nearLava = true;
                        break outer;
                    }
                }
            }
        }

        // Cliff check: is there a >4 block drop in any cardinal direction?
        boolean nearCliff = false;
        for (var dir : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
            BlockPos edge = pos.offset(dir[0], 0, dir[1]);
            int drop = 0;
            for (int dy = 0; dy < 8; dy++) {
                if (level.getBlockState(edge.below(dy)).isAir()) {
                    drop++;
                } else {
                    break;
                }
            }
            if (drop >= 4) {
                nearCliff = true;
                break;
            }
        }

        // Darkness check
        int lightLevel = level.getMaxLocalRawBrightness(pos);
        boolean inDarkness = lightLevel < 7;

        setOrClear(npc, SblPocSetup.NEAR_LAVA.get(), nearLava);
        setOrClear(npc, SblPocSetup.NEAR_CLIFF.get(), nearCliff);
        setOrClear(npc, SblPocSetup.IN_DARKNESS.get(), inDarkness);
    }

    private static void setOrClear(PocNpc npc, MemoryModuleType<Boolean> mem, boolean value) {
        if (value) {
            BrainUtils.setMemory(npc, mem, true);
        } else {
            BrainUtils.clearMemory(npc, mem);
        }
    }
}
