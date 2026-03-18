package org.dizzymii.millenaire2.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * GUI screen for the pujas (Hindu prayer) interaction.
 * Allows the player to make an offering at an Indian temple for reputation.
 * Ported from org.millenaire.client.gui.GuiPujas (Forge 1.12.2).
 */
public class GuiPujas extends Screen {

    private static final int BG_WIDTH = 200;
    private static final int BG_HEIGHT = 120;

    public GuiPujas() { super(Component.literal("Pujas")); }

    @Override
    protected void init() {
        super.init();
        int x = (width - BG_WIDTH) / 2;
        int y = (height - BG_HEIGHT) / 2;

        addRenderableWidget(Button.builder(Component.literal("Make Offering"), btn -> makeOffering())
                .bounds(x + 10, y + BG_HEIGHT - 30, 100, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Close"), btn -> onClose())
                .bounds(x + BG_WIDTH - 60, y + BG_HEIGHT - 30, 50, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        int x = (width - BG_WIDTH) / 2;
        int y = (height - BG_HEIGHT) / 2;

        graphics.fill(x, y, x + BG_WIDTH, y + BG_HEIGHT, 0xFFC6A96C);
        graphics.fill(x + 2, y + 2, x + BG_WIDTH - 2, y + BG_HEIGHT - 2, 0xFFF5E6C8);

        graphics.drawString(font, "Pujas Ceremony", x + 8, y + 8, 0x1A0D00, false);
        graphics.drawString(font, "Make an offering to the gods", x + 8, y + 28, 0x3F2A14, false);
        graphics.drawString(font, "for the village's blessing.", x + 8, y + 42, 0x3F2A14, false);
        graphics.drawString(font, "Cost: 16 Deniers", x + 8, y + 62, 0x3F2A14, false);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void makeOffering() {
        // Send pujas offering packet to server
        onClose();
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
