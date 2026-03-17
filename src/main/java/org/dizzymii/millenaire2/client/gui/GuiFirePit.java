package org.dizzymii.millenaire2.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.menu.FirePitMenu;

/**
 * GUI screen for the fire pit block (cooking interface).
 * Renders the fire pit container with input/output/fuel slots.
 * Ported from org.millenaire.client.gui.GuiFirePit (Forge 1.12.2).
 */
public class GuiFirePit extends AbstractContainerScreen<FirePitMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            Millenaire2.MODID, "textures/gui/fire_pit.png");

    public GuiFirePit(FirePitMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Render burn progress (flame icon)
        if (menu.isBurning()) {
            int burnProgress = menu.getBurnProgress();
            graphics.blit(TEXTURE, x + 56, y + 36 + 12 - burnProgress,
                    176, 12 - burnProgress, 14, burnProgress + 1);
        }

        // Render cook progress (arrow)
        int cookProgress = menu.getCookProgress();
        graphics.blit(TEXTURE, x + 79, y + 34, 176, 14, cookProgress + 1, 16);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
