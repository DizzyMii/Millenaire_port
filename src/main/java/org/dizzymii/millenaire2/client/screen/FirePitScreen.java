package org.dizzymii.millenaire2.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.menu.FirePitMenu;

/**
 * Client-side GUI screen for the Fire Pit.
 * Uses a furnace-like layout with burn and cook progress indicators.
 */
public class FirePitScreen extends AbstractContainerScreen<FirePitMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "textures/gui/firepit.png");

    public FirePitScreen(FirePitMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // Burn progress (flame icon)
        if (this.menu.isBurning()) {
            int burnProgress = this.menu.getBurnProgress();
            guiGraphics.blit(TEXTURE, x + 56, y + 36 + 12 - burnProgress, 176, 12 - burnProgress, 14, burnProgress + 1);
        }

        // Cook progress (arrow)
        int cookProgress = this.menu.getCookProgress();
        guiGraphics.blit(TEXTURE, x + 79, y + 34, 176, 14, cookProgress + 1, 16);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
