package org.dizzymii.millenaire2.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.dizzymii.millenaire2.client.network.ClientNetworkCache;
import org.dizzymii.millenaire2.client.network.ClientNetworkCache.VillageListClientEntry;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;

/**
 * Manages client-side display actions (HUD overlays, action bar messages, etc.).
 * Renders village name when nearby, quest status, and notification messages.
 * Ported from org.millenaire.client.DisplayActions (Forge 1.12.2).
 */
public class DisplayActions {

    @Nullable private static String nearbyVillageName = null;
    @Nullable private static String nearbyVillageCulture = null;
    private static int notificationTimer = 0;
    @Nullable private static String notificationMessage = null;

    private static final int VILLAGE_PROXIMITY_RANGE = 128;
    private static final int NOTIFICATION_DURATION = 100;

    /**
     * Called periodically by ClientTickHandler to update which village the player is near.
     */
    public static void updateNearbyVillage(Minecraft mc) {
        if (mc.player == null) {
            nearbyVillageName = null;
            nearbyVillageCulture = null;
            return;
        }

        int px = (int) mc.player.getX();
        int pz = (int) mc.player.getZ();
        int closestDist = Integer.MAX_VALUE;
        VillageListClientEntry closest = null;

        for (VillageListClientEntry entry : ClientNetworkCache.villageListCache) {
            if (entry.pos != null) {
                int dx = entry.pos.x - px;
                int dz = entry.pos.z - pz;
                int dist = dx * dx + dz * dz;
                if (dist < closestDist) {
                    closestDist = dist;
                    closest = entry;
                }
            }
        }

        if (closest != null && closestDist <= VILLAGE_PROXIMITY_RANGE * VILLAGE_PROXIMITY_RANGE) {
            nearbyVillageName = closest.name;
            nearbyVillageCulture = closest.cultureKey;
        } else {
            nearbyVillageName = null;
            nearbyVillageCulture = null;
        }
    }

    /**
     * Called periodically by ClientTickHandler to tick notification timers.
     */
    public static void refreshHud(Minecraft mc) {
        if (notificationTimer > 0) {
            notificationTimer--;
            if (notificationTimer <= 0) {
                notificationMessage = null;
            }
        }
    }

    /**
     * Renders HUD overlays (village name, notifications) during RenderGuiLayerEvent.
     */
    public static void renderHudOverlay(GuiGraphics graphics, Minecraft mc) {
        if (mc.player == null || mc.getDebugOverlay().showDebugScreen()) return;

        int screenWidth = mc.getWindow().getGuiScaledWidth();

        // Village name overlay (top center)
        if (nearbyVillageName != null) {
            String display = nearbyVillageName;
            if (nearbyVillageCulture != null) {
                display += " (" + nearbyVillageCulture + ")";
            }
            int textWidth = mc.font.width(display);
            graphics.drawString(mc.font, display, (screenWidth - textWidth) / 2, 4,
                    0xFFFFFF, true);
        }

        // Notification overlay (below village name)
        if (notificationMessage != null && notificationTimer > 0) {
            int textWidth = mc.font.width(notificationMessage);
            int alpha = Math.min(255, notificationTimer * 5);
            int color = (alpha << 24) | 0xFFFF00;
            graphics.drawString(mc.font, notificationMessage,
                    (screenWidth - textWidth) / 2, 18, color, true);
        }
    }

    /**
     * Shows a brief notification message on the HUD.
     */
    public static void showNotification(String message) {
        notificationMessage = message;
        notificationTimer = NOTIFICATION_DURATION;
    }

    /**
     * Shows an action bar message (below crosshair).
     */
    public static void showActionBarMessage(Minecraft mc, String message) {
        if (mc.player != null) {
            mc.player.displayClientMessage(Component.literal(message), true);
        }
    }

    @Nullable
    public static String getNearbyVillageName() { return nearbyVillageName; }

    @Nullable
    public static String getNearbyVillageCulture() { return nearbyVillageCulture; }
}
