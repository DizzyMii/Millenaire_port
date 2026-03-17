package org.dizzymii.millenaire2.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.world.MillWorldData;
import org.dizzymii.millenaire2.world.WorldGenVillage;

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

        // Every 200 ticks (~10 seconds): attempt village generation near players
        if (tickCounter >= 200) {
            tickCounter = 0;
            if (worldData.generateVillages && worldData.world instanceof ServerLevel serverLevel) {
                attemptVillageGenerationNearPlayers(serverLevel, worldData);
            }
        }
    }

    /**
     * Check all online player positions and attempt village generation
     * in their surrounding chunks.
     */
    private static void attemptVillageGenerationNearPlayers(ServerLevel level, MillWorldData worldData) {
        for (ServerPlayer player : level.players()) {
            int chunkX = player.getBlockX() >> 4;
            int chunkZ = player.getBlockZ() >> 4;

            // Try generation in a radius of chunks around the player
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    WorldGenVillage.attemptVillageGeneration(
                            level, chunkX + dx, chunkZ + dz,
                            level.random, worldData);
                }
            }
        }
    }
}
