package org.dizzymii.millenaire2.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.client.book.BookManager;

/**
 * Handles client-side tick events for HUD updates, animations, etc.
 * Registered on the NeoForge event bus to receive PlayerTickEvent.
 * Ported from org.millenaire.client.ClientTickHandler (Forge 1.12.2).
 */
@EventBusSubscriber(modid = Millenaire2.MODID, value = Dist.CLIENT)
public class ClientTickHandler {

    private static int tickCounter = 0;
    private static boolean booksLoaded = false;

    private static final int VILLAGE_PROXIMITY_CHECK_INTERVAL = 40;
    private static final int HUD_REFRESH_INTERVAL = 20;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof Player player)) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.player != player) return;

        tickCounter++;

        // One-time book loading
        if (!booksLoaded) {
            BookManager.loadBooks();
            booksLoaded = true;
        }

        // Periodic village proximity check
        if (tickCounter % VILLAGE_PROXIMITY_CHECK_INTERVAL == 0) {
            checkVillageProximity(mc);
        }

        // Periodic HUD refresh
        if (tickCounter % HUD_REFRESH_INTERVAL == 0) {
            DisplayActions.refreshHud(mc);
        }
    }

    private static void checkVillageProximity(Minecraft mc) {
        // Check if the player is near any village for HUD overlay display
        // Uses the cached village list from ClientPacketHandler
        DisplayActions.updateNearbyVillage(mc);
    }

    public static void reset() {
        tickCounter = 0;
        booksLoaded = false;
        BookManager.clearBooks();
    }
}
