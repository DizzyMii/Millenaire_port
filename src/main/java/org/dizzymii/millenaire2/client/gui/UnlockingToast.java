package org.dizzymii.millenaire2.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;

/**
 * Toast notification for unlocking achievements/village features.
 * Uses 1.21.1 Toast system to display a popup when the player
 * unlocks a new building, trade good, or advancement.
 * Ported from org.millenaire.client.gui.UnlockingToast (Forge 1.12.2).
 */
public class UnlockingToast implements Toast {

    private final Component title;
    private final Component description;
    private long firstDrawTime = -1;
    private boolean needsUpdate = true;
    private static final long DISPLAY_TIME_MS = 5000;

    public UnlockingToast(Component title, Component description) {
        this.title = title;
        this.description = description;
    }

    @Override
    public Visibility render(GuiGraphics graphics, ToastComponent toastComponent, long timeSinceLastVisible) {
        if (needsUpdate) {
            firstDrawTime = timeSinceLastVisible;
            needsUpdate = false;
        }

        // Draw background
        graphics.fill(0, 0, width(), height(), 0xDD000000);
        graphics.fill(1, 1, width() - 1, height() - 1, 0xDD442200);

        // Draw text
        graphics.drawString(toastComponent.getMinecraft().font, title, 8, 7, 0xFFFF00, false);
        graphics.drawString(toastComponent.getMinecraft().font, description, 8, 18, 0xFFFFFF, false);

        return timeSinceLastVisible - firstDrawTime >= DISPLAY_TIME_MS
                ? Visibility.HIDE : Visibility.SHOW;
    }

    /**
     * Shows an unlocking toast to the current client player.
     */
    public static void show(String titleText, String descText) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getToasts() != null) {
            mc.getToasts().addToast(new UnlockingToast(
                    Component.literal(titleText),
                    Component.literal(descText)
            ));
        }
    }
}
