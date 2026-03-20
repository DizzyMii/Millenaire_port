package org.dizzymii.sblpoc.sensor;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.PocNpc;
import org.dizzymii.sblpoc.SblPocSetup;
import org.dizzymii.sblpoc.ai.world.InventoryModel;

import java.util.List;

/**
 * Periodically snapshots the NPC's inventory state into brain memories:
 * food supply level, tool tier, whether inventory is nearly full, etc.
 *
 * Runs every 40 ticks (~2 seconds).
 */
public class InventoryStateSensor extends ExtendedSensor<PocNpc> {

    private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(
            SblPocSetup.FOOD_LEVEL.get(),
            SblPocSetup.TOOL_TIER.get(),
            SblPocSetup.INVENTORY_FULL.get(),
            SblPocSetup.HAS_WEAPON.get()
    );

    public InventoryStateSensor() {
        setScanRate(entity -> 40);
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends InventoryStateSensor> type() {
        return SblPocSetup.INVENTORY_STATE_SENSOR.get();
    }

    @Override
    protected void doTick(ServerLevel level, PocNpc npc) {
        InventoryModel inv = npc.getInventoryModel();

        BrainUtils.setMemory(npc, SblPocSetup.FOOD_LEVEL.get(), inv.getFoodSupply());
        BrainUtils.setMemory(npc, SblPocSetup.TOOL_TIER.get(), inv.getHighestToolTier().ordinal());

        // Check how full the inventory is
        int emptySlots = 0;
        for (int i = 0; i < npc.getInventory().getContainerSize(); i++) {
            if (npc.getInventory().getItem(i).isEmpty()) emptySlots++;
        }
        if (emptySlots <= 2) {
            BrainUtils.setMemory(npc, SblPocSetup.INVENTORY_FULL.get(), true);
        } else {
            BrainUtils.clearMemory(npc, SblPocSetup.INVENTORY_FULL.get());
        }

        // Has any weapon?
        boolean hasWeapon = inv.hasSword() || inv.hasPickaxe();
        if (hasWeapon) {
            BrainUtils.setMemory(npc, SblPocSetup.HAS_WEAPON.get(), true);
        } else {
            BrainUtils.clearMemory(npc, SblPocSetup.HAS_WEAPON.get());
        }
    }
}
