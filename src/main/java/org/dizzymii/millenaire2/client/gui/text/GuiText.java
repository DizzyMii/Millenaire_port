package org.dizzymii.millenaire2.client.gui.text;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for Millenaire text-based GUI screens (parchment-style).
 * Provides parchment background rendering, centered text layout, and
 * standard close/navigation buttons.
 * Ported from org.millenaire.client.gui.text.GuiText (Forge 1.12.2).
 */
public class GuiText extends Screen {

    protected static final ResourceLocation PARCHMENT_BG = ResourceLocation.fromNamespaceAndPath(
            Millenaire2.MODID, "textures/gui/parchment.png");

    protected static final int BG_WIDTH = 256;
    protected static final int BG_HEIGHT = 200;
    protected static final int TEXT_COLOR = 0x3F2A14;
    protected static final int HEADER_COLOR = 0x1A0D00;
    protected static final int MARGIN = 20;
    protected static final int LINE_HEIGHT = 12;

    protected int guiLeft;
    protected int guiTop;
    protected final List<String> lines = new ArrayList<>();

    protected GuiText(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        guiLeft = (width - BG_WIDTH) / 2;
        guiTop = (height - BG_HEIGHT) / 2;

        // Close button in bottom-right
        addRenderableWidget(Button.builder(Component.literal("Close"), btn -> onClose())
                .bounds(guiLeft + BG_WIDTH - 60 - MARGIN, guiTop + BG_HEIGHT - 30, 60, 20)
                .build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        renderParchment(graphics);
        renderTitle(graphics);
        renderContent(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    protected void renderParchment(GuiGraphics graphics) {
        graphics.blit(PARCHMENT_BG, guiLeft, guiTop, 0, 0, BG_WIDTH, BG_HEIGHT, BG_WIDTH, BG_HEIGHT);
    }

    protected void renderTitle(GuiGraphics graphics) {
        String titleStr = getTitle().getString();
        int titleWidth = font.width(titleStr);
        graphics.drawString(font, titleStr, guiLeft + (BG_WIDTH - titleWidth) / 2,
                guiTop + MARGIN, HEADER_COLOR, false);
    }

    protected void renderContent(GuiGraphics graphics) {
        int y = guiTop + MARGIN + LINE_HEIGHT + 4;
        for (String line : lines) {
            graphics.drawString(font, line, guiLeft + MARGIN, y, TEXT_COLOR, false);
            y += LINE_HEIGHT;
            if (y > guiTop + BG_HEIGHT - 40) break;
        }
    }

    protected void setLines(List<String> newLines) {
        lines.clear();
        lines.addAll(newLines);
    }

    protected void addLine(String line) {
        lines.add(line);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
