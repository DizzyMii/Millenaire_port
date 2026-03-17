/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.util.ResourceLocation
 */
package org.millenaire.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.entity.TileEntityFirePit;
import org.millenaire.common.ui.firepit.ContainerFirePit;

public class GuiFirePit
extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("millenaire", "textures/gui/firepit.png");
    private static final int[][] ARROWS = new int[][]{{77, 22, 23, 31, 8}, {71, 28, 37, 14, 16}, {77, 42, 23, 31, 8}};
    private static final int[] FIRE = new int[]{81, 54};
    private final EntityPlayer player;
    private final TileEntityFirePit firePit;

    public GuiFirePit(EntityPlayer player, TileEntityFirePit firePit) {
        super((Container)new ContainerFirePit(player, firePit));
        this.player = player;
        this.firePit = firePit;
        this.field_146999_f = 176;
        this.field_147000_g = 175;
    }

    protected void func_146976_a(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.func_179131_c((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.field_146297_k.func_110434_K().func_110577_a(TEXTURE);
        int x = (this.field_146294_l - this.field_146999_f) / 2;
        int y = (this.field_146295_m - this.field_147000_g) / 2;
        this.func_73729_b(x, y, 0, 0, this.field_146999_f, this.field_147000_g);
        if (this.firePit.getBurnTime() > 0) {
            int burn = this.getBurnLeftScaled(13);
            this.func_73729_b(x + FIRE[0], y + FIRE[1] + 12 - burn, this.field_146999_f, 12 - burn, 14, burn + 1);
        }
        for (int i = 0; i < 3; ++i) {
            int[] data = ARROWS[i];
            int arrowX = data[0];
            int arrowY = data[1];
            int arrowLen = data[2];
            int arrowTexY = data[3];
            int arrowHeight = data[4];
            int progress = this.getCookProgressScaled(i, arrowLen);
            this.func_73729_b(x + arrowX, y + arrowY, this.field_146999_f, arrowTexY, progress, arrowHeight);
        }
    }

    protected void func_146979_b(int mouseX, int mouseY) {
        this.field_146289_q.func_78276_b(MillBlocks.FIRE_PIT.getName(), 8, 6, 0x404040);
        this.field_146289_q.func_78276_b(this.player.field_71071_by.func_145748_c_().func_150260_c(), 8, this.field_147000_g - 96 + 2, 0x404040);
    }

    public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
        this.func_146276_q_();
        super.func_73863_a(mouseX, mouseY, partialTicks);
        this.func_191948_b(mouseX, mouseY);
    }

    private int getBurnLeftScaled(int pixels) {
        int time = this.firePit.getTotalBurnTime();
        if (time == 0) {
            time = 200;
        }
        return this.firePit.getBurnTime() * pixels / time;
    }

    private int getCookProgressScaled(int idx, int pixels) {
        int cook = this.firePit.getCookTime(idx);
        return cook != 0 ? cook * pixels / 200 : 0;
    }
}

