package org.dizzymii.millenaire2.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * GUI screen for locked chests (village storage).
 * Shows that the chest belongs to a village and cannot be opened
 * unless the player has sufficient reputation.
 * Ported from org.millenaire.client.gui.GuiLockedChest (Forge 1.12.2).
 */
public class GuiLockedChest extends Screen {

    private static final int BG_WIDTH = 176;
    private static final int BG_HEIGHT = 90;

    private final String villageName;
    private final boolean canOpen;

    public GuiLockedChest(String villageName, boolean canOpen) {
        super(Component.literal("Locked Chest"));
        this.villageName = villageName;
        this.canOpen = canOpen;
    }

    public GuiLockedChest() { this("Unknown Village", false); }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        int x = (width - BG_WIDTH) / 2;
        int y = (height - BG_HEIGHT) / 2;

        graphics.fill(x, y, x + BG_WIDTH, y + BG_HEIGHT, 0xFFC6A96C);
        graphics.fill(x + 2, y + 2, x + BG_WIDTH - 2, y + BG_HEIGHT - 2, 0xFFF5E6C8);

        graphics.drawString(font, "Village Chest", x + 8, y + 8, 0x1A0D00, false);
        graphics.drawString(font, "Belongs to: " + villageName, x + 8, y + 24, 0x3F2A14, false);

        if (canOpen) {
            graphics.drawString(font, "You may access this chest.", x + 8, y + 44, 0x006600, false);
        } else {
            graphics.drawString(font, "This chest is locked.", x + 8, y + 44, 0x660000, false);
            graphics.drawString(font, "Increase your reputation to unlock.", x + 8, y + 58, 0x660000, false);
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
