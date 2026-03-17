package org.dizzymii.millenaire2.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.world.MillWorldData;

/**
 * Handles server-side tick events for village updates and villager AI.
 * Ported from org.millenaire.common.forge.ServerTickHandler (Forge 1.12.2).
 */
@EventBusSubscriber(modid = Millenaire2.MODID)
public class ServerTickHandler {

    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        tickCounter++;

        MillWorldData worldData = Millenaire2.getWorldData();
        if (worldData == null) return;

        // Every tick: update active buildings (construction, villager goals)
        worldData.tick();

        // Reset counter periodically to avoid overflow
        if (tickCounter >= 200) {
            tickCounter = 0;
        }
    }
}
