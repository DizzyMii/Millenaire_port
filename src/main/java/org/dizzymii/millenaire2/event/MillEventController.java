package org.dizzymii.millenaire2.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.dizzymii.millenaire2.Millenaire2;

/**
 * Central event controller for server-side game events.
 * Ported from org.millenaire.common.forge.MillEventController +
 *             org.millenaire.common.forge.ServerTickHandler (Forge 1.12.2).
 */
@EventBusSubscriber(modid = Millenaire2.MODID)
public class MillEventController {

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        // TODO: Tick all active villages, process villager AI, check for raids,
        //       update building construction, process merchant travel, etc.
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        // TODO: Initialize MillWorldData for the loaded level,
        //       load village data from save files
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        // TODO: Save and clean up MillWorldData for the unloaded level
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        // TODO: Handle right-click on Millenaire blocks (locked chest, import table, etc.)
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        // TODO: Handle villager death (update VillagerRecord, notify building, adjust reputation)
    }
}
