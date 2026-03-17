/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.client.util.ITooltipFlag$TooltipFlags
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.client.event.GuiContainerEvent$DrawForeground
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.client.config.GuiUtils
 *  net.minecraftforge.fml.common.eventhandler.Event
 *  org.lwjgl.opengl.GL11
 */
package org.millenaire.client.gui;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.lwjgl.opengl.GL11;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.ui.ContainerPuja;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.village.Building;

public class GuiPujas
extends GuiContainer {
    private static final ResourceLocation texturePujas = new ResourceLocation("millenaire", "textures/gui/pujas.png");
    private static final ResourceLocation textureSacrifices = new ResourceLocation("millenaire", "textures/gui/mayansacrifices.png");
    private final Building temple;
    private final EntityPlayer player;
    private final Method drawSlotInventory;
    private final Method drawItemStackInventory;

    public GuiPujas(EntityPlayer player, Building temple) {
        super((Container)new ContainerPuja(player, temple));
        this.field_147000_g = 188;
        this.temple = temple;
        this.player = player;
        if (MillConfigValues.LogPujas >= 3) {
            MillLog.debug((Object)this, "Opening shrine GUI");
        }
        this.drawSlotInventory = MillCommonUtilities.getDrawSlotInventoryMethod(this);
        this.drawItemStackInventory = MillCommonUtilities.getDrawItemStackInventoryMethod(this);
    }

    protected void func_146976_a(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        if (this.temple.pujas != null && this.temple.pujas.type == 1) {
            this.field_146297_k.func_110434_K().func_110577_a(textureSacrifices);
        } else {
            this.field_146297_k.func_110434_K().func_110577_a(texturePujas);
        }
        int x = (this.field_146294_l - this.field_146999_f) / 2;
        int y = (this.field_146295_m - this.field_147000_g) / 2;
        this.func_73729_b(x, y, 0, 0, this.field_146999_f, this.field_147000_g);
        if (this.temple.pujas != null) {
            int linePos = 0;
            int colPos = 0;
            for (int cp = 0; cp < this.temple.pujas.getTargets().size(); ++cp) {
                if (this.temple.pujas.currentTarget == this.temple.pujas.getTargets().get(cp)) {
                    this.func_73729_b(x + this.getTargetXStart() + colPos * this.getButtonWidth(), y + this.getTargetYStart() + this.getButtonHeight() * linePos, this.temple.pujas.getTargets().get((int)cp).startXact, this.temple.pujas.getTargets().get((int)cp).startYact, this.getButtonWidth(), this.getButtonHeight());
                } else {
                    this.func_73729_b(x + this.getTargetXStart() + colPos * this.getButtonWidth(), y + this.getTargetYStart() + this.getButtonHeight() * linePos, this.temple.pujas.getTargets().get((int)cp).startX, this.temple.pujas.getTargets().get((int)cp).startY, this.getButtonWidth(), this.getButtonHeight());
                }
                if (++colPos < this.getNbPerLines()) continue;
                colPos = 0;
                ++linePos;
            }
            int progress = this.temple.pujas.getPujaProgressScaled(13);
            this.func_73729_b(x + 27, y + 39 + 13 - progress, 176, 13 - progress, 15, progress);
            progress = this.temple.pujas.getOfferingProgressScaled(16);
            this.func_73729_b(x + 84, y + 63 + 16 - progress, 176, 47 - progress, 19, progress);
        }
    }

    protected void func_146979_b(int mouseX, int mouseY) {
        if (this.temple.pujas.type == 1) {
            this.field_146289_q.func_78276_b(LanguageUtilities.string("sacrifices.offering"), 8, 6, 0x404040);
            this.field_146289_q.func_78276_b(LanguageUtilities.string("sacrifices.panditfee"), 8, 75, 0x404040);
        } else {
            this.field_146289_q.func_78276_b(LanguageUtilities.string("pujas.offering"), 8, 6, 0x404040);
            this.field_146289_q.func_78276_b(LanguageUtilities.string("pujas.panditfee"), 8, 75, 0x404040);
        }
        this.field_146289_q.func_78276_b(I18n.func_135052_a((String)"container.inventory", (Object[])new Object[0]), 8, this.field_147000_g - 94 + 2, 0x404040);
    }

    public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
        int k;
        this.func_146276_q_();
        int i = this.field_147003_i;
        int j = this.field_147009_r;
        this.func_146976_a(partialTicks, mouseX, mouseY);
        GlStateManager.func_179101_C();
        RenderHelper.func_74518_a();
        GlStateManager.func_179140_f();
        GlStateManager.func_179097_i();
        try {
            for (k = 0; k < this.field_146292_n.size(); ++k) {
                GuiButton guibutton = (GuiButton)this.field_146292_n.get(k);
                guibutton.func_191745_a(this.field_146297_k, i, j, partialTicks);
            }
        }
        catch (Exception e) {
            MillLog.printException("Exception in button rendering: ", e);
        }
        RenderHelper.func_74520_c();
        GlStateManager.func_179094_E();
        GlStateManager.func_179109_b((float)i, (float)j, (float)0.0f);
        GlStateManager.func_179131_c((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.func_179091_B();
        k = 240;
        int l = 240;
        OpenGlHelper.func_77475_a((int)OpenGlHelper.field_77476_b, (float)240.0f, (float)240.0f);
        GlStateManager.func_179131_c((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        Slot hoveredSlot = null;
        for (int i1 = 0; i1 < this.field_147002_h.field_75151_b.size(); ++i1) {
            Slot slot1 = (Slot)this.field_147002_h.field_75151_b.get(i1);
            this.drawSlotInventory(slot1);
            if (!this.getIsMouseOverSlot(slot1, mouseX, mouseY)) continue;
            hoveredSlot = slot1;
            GlStateManager.func_179140_f();
            GlStateManager.func_179097_i();
            int j1 = hoveredSlot.field_75223_e;
            int k1 = hoveredSlot.field_75221_f;
            GlStateManager.func_179135_a((boolean)true, (boolean)true, (boolean)true, (boolean)false);
            this.func_73733_a(j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
            GlStateManager.func_179135_a((boolean)true, (boolean)true, (boolean)true, (boolean)true);
            GlStateManager.func_179145_e();
            GlStateManager.func_179126_j();
        }
        RenderHelper.func_74518_a();
        this.func_146979_b(mouseX, mouseY);
        RenderHelper.func_74520_c();
        MinecraftForge.EVENT_BUS.post((Event)new GuiContainerEvent.DrawForeground((GuiContainer)this, mouseX, mouseY));
        InventoryPlayer inventoryplayer = this.field_146297_k.field_71439_g.field_71071_by;
        if (inventoryplayer.func_70445_o() != null) {
            try {
                this.drawItemStackInventory.invoke((Object)this, inventoryplayer.func_70445_o(), i - 240 - 8, j - 240 - 8, null);
            }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                MillLog.printException(e);
            }
        }
        GlStateManager.func_179121_F();
        GlStateManager.func_179145_e();
        GlStateManager.func_179126_j();
        RenderHelper.func_74519_b();
        if (inventoryplayer.func_70445_o().func_190926_b() && hoveredSlot != null) {
            ArrayList<String> list = null;
            ItemStack itemstack = null;
            if (hoveredSlot.func_75216_d()) {
                itemstack = hoveredSlot.func_75211_c();
                list = itemstack.func_82840_a((EntityPlayer)this.field_146297_k.field_71439_g, (ITooltipFlag)(this.field_146297_k.field_71474_y.field_82882_x ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL));
                this.renderToolTipCustom(itemstack, mouseX, mouseY, list);
            } else if (hoveredSlot instanceof ContainerPuja.OfferingSlot) {
                list = new ArrayList<String>();
                list.add("\u00a76" + LanguageUtilities.string("pujas.offeringslot"));
                list.add("\u00a77" + LanguageUtilities.string("pujas.offeringslot2"));
                this.renderToolTipCustom(itemstack, mouseX, mouseY, list);
            } else if (hoveredSlot instanceof ContainerPuja.MoneySlot) {
                list = new ArrayList();
                list.add("\u00a76" + LanguageUtilities.string("pujas.moneyslot"));
                this.renderToolTipCustom(itemstack, mouseX, mouseY, list);
            } else if (hoveredSlot instanceof ContainerPuja.ToolSlot) {
                list = new ArrayList();
                list.add("\u00a76" + LanguageUtilities.string("pujas.toolslot"));
                this.renderToolTipCustom(itemstack, mouseX, mouseY, list);
            }
        }
        int startx = (this.field_146294_l - this.field_146999_f) / 2;
        int starty = (this.field_146295_m - this.field_147000_g) / 2;
        if (this.temple.pujas != null) {
            int linePos = 0;
            int colPos = 0;
            for (int cp = 0; cp < this.temple.pujas.getTargets().size(); ++cp) {
                if (mouseX > startx + this.getTargetXStart() + colPos * this.getButtonWidth() && mouseX < startx + this.getTargetXStart() + (colPos + 1) * this.getButtonWidth() && mouseY > starty + this.getTargetYStart() + this.getButtonHeight() * linePos && mouseY < starty + this.getTargetYStart() + this.getButtonHeight() * (linePos + 1)) {
                    String s = LanguageUtilities.string(this.temple.pujas.getTargets().get((int)cp).mouseOver);
                    int stringlength = this.field_146289_q.func_78256_a(s);
                    this.func_73733_a(mouseX + 5, mouseY - 3, mouseX + stringlength + 10, mouseY + 8 + 3, -1073741824, -1073741824);
                    this.field_146289_q.func_78276_b(s, mouseX + 8, mouseY, 0xF0F0F0);
                }
                if (++colPos < this.getNbPerLines()) continue;
                colPos = 0;
                ++linePos;
            }
        }
    }

    public void drawSlotInventory(Slot slot) {
        try {
            this.drawSlotInventory.invoke((Object)this, slot);
        }
        catch (Exception e) {
            MillLog.printException("Exception when trying to access drawSlotInventory", e);
        }
    }

    private int getButtonHeight() {
        if (this.temple.pujas == null) {
            return 0;
        }
        if (this.temple.pujas.type == 0) {
            return 17;
        }
        if (this.temple.pujas.type == 1) {
            return 20;
        }
        return 0;
    }

    private int getButtonWidth() {
        if (this.temple.pujas == null) {
            return 0;
        }
        if (this.temple.pujas.type == 0) {
            return 46;
        }
        if (this.temple.pujas.type == 1) {
            return 20;
        }
        return 0;
    }

    private boolean getIsMouseOverSlot(Slot slot, int i, int j) {
        int k = (this.field_146294_l - this.field_146999_f) / 2;
        int l = (this.field_146295_m - this.field_147000_g) / 2;
        return (i -= k) >= slot.field_75223_e - 1 && i < slot.field_75223_e + 16 + 1 && (j -= l) >= slot.field_75221_f - 1 && j < slot.field_75221_f + 16 + 1;
    }

    private int getNbPerLines() {
        if (this.temple.pujas == null) {
            return 1;
        }
        if (this.temple.pujas.type == 0) {
            return 1;
        }
        if (this.temple.pujas.type == 1) {
            return 3;
        }
        return 1;
    }

    private int getTargetXStart() {
        if (this.temple.pujas == null) {
            return 0;
        }
        if (this.temple.pujas.type == 0) {
            return 118;
        }
        if (this.temple.pujas.type == 1) {
            return 110;
        }
        return 0;
    }

    private int getTargetYStart() {
        if (this.temple.pujas == null) {
            return 0;
        }
        if (this.temple.pujas.type == 0) {
            return 22;
        }
        if (this.temple.pujas.type == 1) {
            return 22;
        }
        return 0;
    }

    protected void func_73864_a(int x, int y, int par3) throws IOException {
        super.func_73864_a(x, y, par3);
        int startx = (this.field_146294_l - this.field_146999_f) / 2;
        int starty = (this.field_146295_m - this.field_147000_g) / 2;
        if (this.temple.pujas != null) {
            int linePos = 0;
            int colPos = 0;
            for (int cp = 0; cp < this.temple.pujas.getTargets().size(); ++cp) {
                if (x > startx + this.getTargetXStart() + colPos * this.getButtonWidth() && x < startx + this.getTargetXStart() + (colPos + 1) * this.getButtonWidth() && y > starty + this.getTargetYStart() + this.getButtonHeight() * linePos && y < starty + this.getTargetYStart() + this.getButtonHeight() * (linePos + 1)) {
                    ClientSender.pujasChangeEnchantment(this.player, this.temple, cp);
                }
                if (++colPos < this.getNbPerLines()) continue;
                colPos = 0;
                ++linePos;
            }
        }
    }

    protected void renderToolTipCustom(ItemStack stack, int x, int y, List<String> customToolTip) {
        if (stack == null) {
            stack = ItemStack.field_190927_a;
        }
        FontRenderer font = stack.func_77973_b().getFontRenderer(stack);
        GuiUtils.preItemToolTip((ItemStack)stack);
        this.drawHoveringText(customToolTip, x, y, font == null ? this.field_146289_q : font);
        GuiUtils.postItemToolTip();
    }
}

