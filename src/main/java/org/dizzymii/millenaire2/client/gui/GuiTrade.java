package org.dizzymii.millenaire2.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;

/**
 * GUI screen for trading with villagers.
 * Displays buy/sell items, prices, and the player's reputation/currency.
 * Ported from org.millenaire.client.gui.GuiTrade (Forge 1.12.2).
 */
public class GuiTrade extends Screen {

    private static final ResourceLocation TRADE_BG = ResourceLocation.fromNamespaceAndPath(
            Millenaire2.MODID, "textures/gui/trade.png");
    private static final int BG_WIDTH = 176;
    private static final int BG_HEIGHT = 166;

    private int guiLeft;
    private int guiTop;

    private final String villagerName;
    private final int playerDeniers;

    public GuiTrade(String villagerName, int playerDeniers) {
        super(Component.literal("Trade"));
        this.villagerName = villagerName;
        this.playerDeniers = playerDeniers;
    }

    public GuiTrade() { this("Villager", 0); }

    @Override
    protected void init() {
        super.init();
        guiLeft = (width - BG_WIDTH) / 2;
        guiTop = (height - BG_HEIGHT) / 2;

        addRenderableWidget(Button.builder(Component.literal("Buy"), btn -> executeBuy())
                .bounds(guiLeft + 10, guiTop + BG_HEIGHT - 28, 50, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Sell"), btn -> executeSell())
                .bounds(guiLeft + 65, guiTop + BG_HEIGHT - 28, 50, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Close"), btn -> onClose())
                .bounds(guiLeft + BG_WIDTH - 60, guiTop + BG_HEIGHT - 28, 50, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        // Background texture (fallback to fill if texture missing)
        graphics.fill(guiLeft, guiTop, guiLeft + BG_WIDTH, guiTop + BG_HEIGHT, 0xFFC6A96C);
        graphics.fill(guiLeft + 2, guiTop + 2, guiLeft + BG_WIDTH - 2, guiTop + BG_HEIGHT - 2, 0xFFF5E6C8);

        // Title
        graphics.drawString(font, "Trading with " + villagerName, guiLeft + 8, guiTop + 6, 0x3F2A14, false);

        // Deniers
        graphics.drawString(font, "Your Deniers: " + playerDeniers, guiLeft + 8, guiTop + 20, 0x3F2A14, false);

        // Buy section
        graphics.drawString(font, "--- For Sale ---", guiLeft + 8, guiTop + 38, 0x1A0D00, false);
        graphics.drawString(font, "(Items populated by server)", guiLeft + 8, guiTop + 52, 0x666666, false);

        // Sell section
        graphics.drawString(font, "--- You Can Sell ---", guiLeft + 8, guiTop + 80, 0x1A0D00, false);
        graphics.drawString(font, "(Items populated by server)", guiLeft + 8, guiTop + 94, 0x666666, false);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void executeBuy() {
        // Send buy packet to server
    }

    private void executeSell() {
        // Send sell packet to server
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
