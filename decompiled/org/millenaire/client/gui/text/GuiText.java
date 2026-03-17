/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.gui.GuiTextField
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.client.renderer.RenderItem
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.client.util.ITooltipFlag$TooltipFlags
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.text.TextFormatting
 *  org.lwjgl.opengl.GL11
 */
package org.millenaire.client.gui.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;
import org.millenaire.client.book.BookManager;
import org.millenaire.client.book.TextBook;
import org.millenaire.client.book.TextLine;
import org.millenaire.client.gui.text.GuiTravelBook;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.culture.VillagerType;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillLog;

public abstract class GuiText
extends GuiScreen {
    private static final ResourceLocation ICONS_TEXTURE = new ResourceLocation("millenaire", "textures/gui/icons.png");
    public static final String WHITE = "<white>";
    public static final String YELLOW = "<yellow>";
    public static final String PINK = "<pink>";
    public static final String LIGHTRED = "<lightred>";
    public static final String CYAN = "<cyan>";
    public static final String LIGHTGREEN = "<lightgreen>";
    public static final String BLUE = "<blue>";
    public static final String DARKGREY = "<darkgrey>";
    public static final String LIGHTGREY = "<lightgrey>";
    public static final String ORANGE = "<orange>";
    public static final String PURPLE = "<purple>";
    public static final String DARKRED = "<darkred>";
    public static final String LIGHTBLUE = "<lightblue>";
    public static final String DARKGREEN = "<darkgreen>";
    public static final String DARKBLUE = "<darkblue>";
    public static final String BLACK = "<black>";
    private GuiScreen callingScreen = null;
    protected int pageNum = 0;
    protected TextBook textBook = null;
    protected BookManager bookManager = null;
    List<MillGuiTextField> textFields = new ArrayList<MillGuiTextField>();
    protected final RenderItem itemRenderer = Minecraft.func_71410_x().func_175599_af();

    protected void func_146284_a(GuiButton button) throws IOException {
        if (button instanceof GuiButtonReference) {
            GuiButtonReference refButton = (GuiButtonReference)button;
            GuiTravelBook guiTravelBook = new GuiTravelBook((EntityPlayer)Minecraft.func_71410_x().field_71439_g);
            guiTravelBook.setCallingScreen(this);
            guiTravelBook.jumpToDetails(refButton.culture, refButton.type, refButton.key, false);
            Minecraft.func_71410_x().func_147108_a((GuiScreen)guiTravelBook);
        }
    }

    public void buttonPagination() {
        int elementsId = 0;
        try {
            if (this.textBook == null) {
                return;
            }
            int xStart = (this.field_146294_l - this.getXSize()) / 2;
            int yStart = (this.field_146295_m - this.getYSize()) / 2;
            this.field_146292_n.clear();
            this.textFields.clear();
            int vpos = 6;
            if (this.pageNum < this.textBook.nbPages()) {
                for (int cp = 0; cp < this.getTextHeight() && cp < this.textBook.getPage(this.pageNum).getNbLines(); ++cp) {
                    TextLine line = this.textBook.getPage(this.pageNum).getLine(cp);
                    int totalButtonWidth = this.getLineSizeInPx() - 20;
                    if (line.buttons != null) {
                        if (line.buttons.length == 1) {
                            if (line.buttons[0] != null) {
                                line.buttons[0].field_146128_h = xStart + this.getXSize() / 2 - totalButtonWidth / 2;
                                line.buttons[0].func_175211_a(totalButtonWidth);
                            }
                        } else if (line.buttons.length == 2) {
                            int buttonWidth = totalButtonWidth / 2 - 5;
                            if (line.buttons[0] != null) {
                                line.buttons[0].field_146128_h = xStart + this.getXSize() / 2 - totalButtonWidth / 2;
                                line.buttons[0].func_175211_a(buttonWidth);
                            }
                            if (line.buttons[1] != null) {
                                line.buttons[1].field_146128_h = xStart + this.getXSize() / 2 + 5;
                                line.buttons[1].func_175211_a(buttonWidth);
                            }
                        } else if (line.buttons.length == 3) {
                            int buttonWidth = totalButtonWidth / 3 - 10;
                            if (line.buttons[0] != null) {
                                line.buttons[0].field_146128_h = xStart + this.getXSize() / 2 - totalButtonWidth / 2;
                                line.buttons[0].func_175211_a(buttonWidth);
                            }
                            if (line.buttons[1] != null) {
                                line.buttons[1].field_146128_h = xStart + this.getXSize() / 2 - totalButtonWidth / 2 + buttonWidth + 10;
                                line.buttons[1].func_175211_a(buttonWidth);
                            }
                            if (line.buttons[2] != null) {
                                line.buttons[2].field_146128_h = xStart + this.getXSize() / 2 - totalButtonWidth / 2 + buttonWidth * 2 + 20;
                                line.buttons[2].func_175211_a(buttonWidth);
                            }
                        }
                        for (int i = 0; i < line.buttons.length; ++i) {
                            if (line.buttons[i] == null) continue;
                            line.buttons[i].field_146129_i = yStart + vpos;
                            line.buttons[i].setHeight(20);
                            this.field_146292_n.add(line.buttons[i]);
                        }
                    } else if (line.referenceButton != null) {
                        line.referenceButton.func_175211_a(20);
                        line.referenceButton.setHeight(20);
                        line.referenceButton.field_146129_i = yStart + vpos;
                        line.referenceButton.field_146128_h = xStart + 6 + line.getLineMarginLeft();
                        this.field_146292_n.add(line.referenceButton);
                    } else if (line.textField != null) {
                        MillGuiTextField textField = new MillGuiTextField(elementsId++, this.field_146289_q, xStart + this.getXSize() / 2 + 40, yStart + vpos, 95, 20, line.textField.fieldKey);
                        textField.func_146180_a(line.textField.func_146179_b());
                        textField.func_146203_f(line.textField.func_146208_g());
                        textField.func_146193_g(-1);
                        line.textField = textField;
                        line.textField.func_146193_g(-1);
                        line.textField.func_146185_a(false);
                        this.textFields.add(textField);
                    }
                    if (line.columns != null) {
                        int lineSize = this.getLineSizeInPx() - line.getTextMarginLeft() - line.getLineMarginLeft() - line.getLineMarginRight();
                        int colSize = (lineSize - (line.columns.length - 1) * 10) / line.columns.length;
                        for (int col = 0; col < line.columns.length; ++col) {
                            TextLine column = line.columns[col];
                            int colXStart = col * (colSize + 10) + line.getLineMarginLeft();
                            if (column.referenceButton == null) continue;
                            column.referenceButton.func_175211_a(20);
                            column.referenceButton.setHeight(20);
                            column.referenceButton.field_146129_i = yStart + vpos;
                            column.referenceButton.field_146128_h = xStart + colXStart + 6 + column.getLineMarginLeft();
                            this.field_146292_n.add(column.referenceButton);
                        }
                    }
                    vpos += line.getLineHeight();
                }
            }
        }
        catch (Exception e) {
            MillLog.printException("Exception while doing button pagination in GUI " + (Object)((Object)this), e);
        }
    }

    protected void closeGui() {
        if (this.callingScreen != null) {
            Minecraft.func_71410_x().func_147108_a(this.callingScreen);
        } else {
            this.field_146297_k.func_147108_a(null);
            this.field_146297_k.func_71381_h();
        }
    }

    protected void closeWindow() {
        this.field_146297_k.func_147108_a(null);
        this.field_146297_k.func_71381_h();
    }

    protected abstract void customDrawBackground(int var1, int var2, float var3);

    protected abstract void customDrawScreen(int var1, int var2, float var3);

    public void decrementPage() {
        if (this.textBook == null) {
            return;
        }
        if (this.pageNum > 0) {
            --this.pageNum;
        }
        this.buttonPagination();
    }

    public boolean func_73868_f() {
        return false;
    }

    protected void drawHoveringText(List par1List, int par2, int par3, FontRenderer font) {
        if (!par1List.isEmpty()) {
            GL11.glDisable((int)32826);
            RenderHelper.func_74518_a();
            GL11.glDisable((int)2896);
            GL11.glDisable((int)2929);
            int k = 0;
            for (String s : par1List) {
                int l = font.func_78256_a(s);
                if (l <= k) continue;
                k = l;
            }
            int i1 = par2 + 12;
            int j1 = par3 - 12;
            int k1 = 8;
            if (par1List.size() > 1) {
                k1 += 2 + (par1List.size() - 1) * 10;
            }
            if (i1 + k > this.field_146294_l) {
                i1 -= 28 + k;
            }
            if (j1 + k1 + 6 > this.field_146295_m) {
                j1 = this.field_146295_m - k1 - 6;
            }
            this.field_73735_i = 300.0f;
            this.itemRenderer.field_77023_b = 300.0f;
            int l1 = -267386864;
            this.func_73733_a(i1 - 3, j1 - 4, i1 + k + 3, j1 - 3, -267386864, -267386864);
            this.func_73733_a(i1 - 3, j1 + k1 + 3, i1 + k + 3, j1 + k1 + 4, -267386864, -267386864);
            this.func_73733_a(i1 - 3, j1 - 3, i1 + k + 3, j1 + k1 + 3, -267386864, -267386864);
            this.func_73733_a(i1 - 4, j1 - 3, i1 - 3, j1 + k1 + 3, -267386864, -267386864);
            this.func_73733_a(i1 + k + 3, j1 - 3, i1 + k + 4, j1 + k1 + 3, -267386864, -267386864);
            int i2 = 0x505000FF;
            int j2 = 1344798847;
            this.func_73733_a(i1 - 3, j1 - 3 + 1, i1 - 3 + 1, j1 + k1 + 3 - 1, 0x505000FF, 1344798847);
            this.func_73733_a(i1 + k + 2, j1 - 3 + 1, i1 + k + 3, j1 + k1 + 3 - 1, 0x505000FF, 1344798847);
            this.func_73733_a(i1 - 3, j1 - 3, i1 + k + 3, j1 - 3 + 1, 0x505000FF, 0x505000FF);
            this.func_73733_a(i1 - 3, j1 + k1 + 2, i1 + k + 3, j1 + k1 + 3, 1344798847, 1344798847);
            for (int k2 = 0; k2 < par1List.size(); ++k2) {
                String s1 = (String)par1List.get(k2);
                font.func_175063_a(s1, (float)i1, (float)j1, -1);
                if (k2 == 0) {
                    j1 += 2;
                }
                j1 += 10;
            }
            this.field_73735_i = 0.0f;
            this.itemRenderer.field_77023_b = 0.0f;
            GL11.glEnable((int)2929);
            GL11.glEnable((int)32826);
        }
    }

    protected void drawItemStackTooltip(ItemStack par1ItemStack, int xPos, int yPos, boolean displayItemLegend, String extraLegend) {
        List<String> list;
        if (displayItemLegend) {
            list = par1ItemStack.func_82840_a((EntityPlayer)this.field_146297_k.field_71439_g, (ITooltipFlag)(this.field_146297_k.field_71474_y.field_82882_x ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL));
            for (int k = 0; k < list.size(); ++k) {
                if (k == 0) {
                    list.set(k, par1ItemStack.func_77953_t().field_77937_e + (String)list.get(k));
                    continue;
                }
                list.set(k, TextFormatting.GRAY + (String)list.get(k));
            }
        } else {
            list = new ArrayList<String>();
        }
        if (extraLegend != null) {
            list.addAll(BookManager.splitStringByLength(new FontRendererWrapped(this.field_146289_q), extraLegend, 150));
        }
        if (!list.isEmpty()) {
            FontRenderer font = par1ItemStack.func_77973_b().getFontRenderer(par1ItemStack);
            this.drawHoveringText(list, xPos, yPos, font == null ? this.field_146289_q : font);
        }
    }

    public void func_73863_a(int mouseX, int mouseY, float f) {
        try {
            if (this.textBook == null) {
                this.initData();
            }
            boolean hasSpecialIcon = false;
            ItemStack hoverIcon = null;
            String extraLegend = null;
            boolean displayItemLegend = true;
            GuiButtonReference hoverReferenceButton = null;
            this.func_146276_q_();
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            this.field_146297_k.func_110434_K().func_110577_a(this.getPNGPath());
            int xStart = (this.field_146294_l - this.getXSize()) / 2;
            int yStart = (this.field_146295_m - this.getYSize()) / 2;
            this.func_73729_b(xStart, yStart, 0, 0, this.getXSize(), this.getYSize());
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            this.customDrawBackground(mouseX, mouseY, f);
            GL11.glPushMatrix();
            GL11.glRotatef((float)180.0f, (float)1.0f, (float)0.0f, (float)0.0f);
            RenderHelper.func_74519_b();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslatef((float)xStart, (float)yStart, (float)0.0f);
            RenderHelper.func_74518_a();
            GL11.glDisable((int)2896);
            GL11.glDisable((int)2929);
            if (this.textBook != null) {
                TextLine line;
                int linePos;
                int vpos = 6;
                if (this.pageNum < this.textBook.nbPages()) {
                    if (this.textBook.getPage(this.pageNum) == null) {
                        MillLog.printException(new MillLog.MillenaireException("descText.get(pageNum)==null for pageNum: " + this.pageNum + " in GUI: " + (Object)((Object)this)));
                    }
                    for (linePos = 0; linePos < this.getTextHeight() && linePos < this.textBook.getPage(this.pageNum).getNbLines(); ++linePos) {
                        line = this.textBook.getPage(this.pageNum).getLine(linePos);
                        int textXstart = this.getTextXStart() + line.getTextMarginLeft() + line.getLineMarginLeft();
                        if (line.shadow) {
                            this.field_146289_q.func_175063_a(line.style + line.text, (float)textXstart, (float)(vpos + line.getTextMarginTop()), 0x101010);
                        } else {
                            this.field_146289_q.func_78276_b(line.style + line.text, textXstart, vpos + line.getTextMarginTop(), 0x101010);
                        }
                        if (line.columns != null) {
                            int lineSize = this.getLineSizeInPx() - line.getTextMarginLeft() - line.getLineMarginLeft() - line.getLineMarginRight();
                            int colSize = (lineSize - (line.columns.length - 1) * 10) / line.columns.length;
                            for (int col = 0; col < line.columns.length; ++col) {
                                TextLine column = line.columns[col];
                                int colXStart = this.getTextXStart() + col * (colSize + 10) + line.getLineMarginLeft();
                                textXstart = colXStart + column.getTextMarginLeft();
                                if (column.shadow) {
                                    this.field_146289_q.func_175063_a(column.style + column.text, (float)textXstart, (float)(vpos + column.getTextMarginTop()), 0x101010);
                                    continue;
                                }
                                this.field_146289_q.func_78276_b(column.style + column.text, textXstart, vpos + column.getTextMarginTop(), 0x101010);
                            }
                        }
                        vpos += line.getLineHeight();
                    }
                }
                this.field_146289_q.func_78276_b(this.pageNum + 1 + "/" + this.getNbPage(), this.getXSize() / 2 - 10, this.getYSize() - 10, 0x101010);
                vpos = 6;
                this.field_73735_i = 100.0f;
                this.itemRenderer.field_77023_b = 100.0f;
                RenderHelper.func_74520_c();
                GlStateManager.func_179140_f();
                GlStateManager.func_179091_B();
                GlStateManager.func_179142_g();
                GlStateManager.func_179145_e();
                this.field_146296_j.field_77023_b = 100.0f;
                if (this.pageNum < this.textBook.nbPages()) {
                    for (linePos = 0; linePos < this.getTextHeight() && linePos < this.textBook.getPage(this.pageNum).getNbLines(); ++linePos) {
                        line = this.textBook.getPage(this.pageNum).getLine(linePos);
                        if (line.icons != null) {
                            for (int ic = 0; ic < line.icons.size(); ++ic) {
                                ItemStack icon = line.icons.get(ic);
                                int xPosition = this.getTextXStart() + 18 * ic + line.getLineMarginLeft();
                                if (icon != null) {
                                    if (line.iconExtraLegends == null) {
                                        MillLog.error(null, "Null legends!");
                                    }
                                    GL11.glEnable((int)2929);
                                    this.itemRenderer.func_180450_b(icon, xPosition, vpos);
                                }
                                if (xStart + xPosition >= mouseX || yStart + vpos >= mouseY || xStart + xPosition + 16 <= mouseX || yStart + vpos + 16 <= mouseY) continue;
                                String legend = line.iconExtraLegends.get(ic);
                                hoverIcon = icon;
                                extraLegend = legend;
                                displayItemLegend = line.displayItemLegend();
                            }
                        }
                        if (line.columns != null) {
                            int lineSize = this.getLineSizeInPx() - line.getTextMarginLeft() - line.getLineMarginLeft() - line.getLineMarginRight();
                            int colSize = (lineSize - (line.columns.length - 1) * 10) / line.columns.length;
                            for (int col = 0; col < line.columns.length; ++col) {
                                TextLine column = line.columns[col];
                                int colXStart = this.getTextXStart() + col * (colSize + 10) + line.getLineMarginLeft();
                                if (column.icons == null) continue;
                                for (int ic = 0; ic < column.icons.size(); ++ic) {
                                    ItemStack icon = column.icons.get(ic);
                                    int iconXpos = colXStart + 18 * ic;
                                    if (icon != null) {
                                        if (column.iconExtraLegends == null) {
                                            MillLog.error(null, "Null legends!");
                                        }
                                        GL11.glEnable((int)2929);
                                        this.itemRenderer.func_180450_b(icon, iconXpos, vpos);
                                    }
                                    if (xStart + iconXpos >= mouseX || yStart + vpos >= mouseY || xStart + iconXpos + 16 <= mouseX || yStart + vpos + 16 <= mouseY) continue;
                                    String legend = column.iconExtraLegends.get(ic);
                                    hoverIcon = icon;
                                    extraLegend = legend;
                                    displayItemLegend = column.displayItemLegend();
                                }
                            }
                        }
                        vpos += line.getLineHeight();
                    }
                }
                for (GuiButton button : this.field_146292_n) {
                    if (button instanceof MillGuiButton) {
                        MillGuiButton millButton = (MillGuiButton)button;
                        if (millButton.itemStackIconLeft != null) {
                            GL11.glEnable((int)2929);
                            this.itemRenderer.func_180450_b(millButton.itemStackIconLeft, millButton.field_146128_h + 4 - xStart, millButton.field_146129_i + 2 - yStart);
                        }
                        if (millButton.itemStackIconRight != null) {
                            GL11.glEnable((int)2929);
                            this.itemRenderer.func_180450_b(millButton.itemStackIconRight, millButton.field_146128_h + millButton.field_146120_f - 4 - 16 - xStart, millButton.field_146129_i + 2 - yStart);
                        }
                        if (millButton.specialIconLeft == null && millButton.specialIconRight == null) continue;
                        hasSpecialIcon = true;
                        continue;
                    }
                    if (!(button instanceof GuiButtonReference)) continue;
                    GuiButtonReference refButton = (GuiButtonReference)button;
                    if (refButton.getIcon() != null) {
                        GL11.glEnable((int)2929);
                        this.itemRenderer.func_180450_b(refButton.getIcon(), refButton.field_146128_h + 2 - xStart, refButton.field_146129_i + 2 - yStart);
                    }
                    if (refButton.field_146128_h >= mouseX || refButton.field_146129_i >= mouseY || refButton.field_146128_h + refButton.field_146120_f <= mouseX || refButton.field_146129_i + refButton.field_146121_g <= mouseY) continue;
                    hoverReferenceButton = refButton;
                }
                GlStateManager.func_179140_f();
                this.customDrawScreen(mouseX, mouseY, f);
            }
            GL11.glPopMatrix();
            super.func_73863_a(mouseX, mouseY, f);
            if (hasSpecialIcon) {
                this.field_146297_k.func_110434_K().func_110577_a(ICONS_TEXTURE);
                for (GuiButton button : this.field_146292_n) {
                    if (!(button instanceof MillGuiButton)) continue;
                    MillGuiButton millButton = (MillGuiButton)button;
                    if (millButton.specialIconLeft == null) continue;
                    this.func_73729_b(millButton.field_146128_h + 4, millButton.field_146129_i + 2, millButton.specialIconLeft.xpos, millButton.specialIconLeft.ypos, 16, 16);
                }
            }
            GL11.glEnable((int)2896);
            GL11.glEnable((int)2929);
            GL11.glDisable((int)2896);
            for (MillGuiTextField textField : this.textFields) {
                textField.func_146194_f();
            }
            if (hoverIcon != null) {
                this.drawItemStackTooltip(hoverIcon, mouseX, mouseY, displayItemLegend, extraLegend);
            }
            if (hoverReferenceButton != null) {
                if (this instanceof GuiTravelBook) {
                    this.drawHoveringText(BookManager.splitStringByLength(new FontRendererWrapped(this.field_146289_q), hoverReferenceButton.getIconName(), 150), hoverReferenceButton.field_146128_h + 15, hoverReferenceButton.field_146129_i, this.field_146289_q);
                } else {
                    this.drawHoveringText(BookManager.splitStringByLength(new FontRendererWrapped(this.field_146289_q), hoverReferenceButton.getIconFullLegend(), 150), hoverReferenceButton.field_146128_h + 15, hoverReferenceButton.field_146129_i, this.field_146289_q);
                }
            }
            this.itemRenderer.field_77023_b = 0.0f;
            this.field_73735_i = 0.0f;
        }
        catch (Exception e) {
            MillLog.printException("Exception in drawScreen of GUI: " + (Object)((Object)this), e);
        }
    }

    public GuiScreen getCallingScreen() {
        return this.callingScreen;
    }

    private final int getLineSizeInPx() {
        return this.bookManager.getLineSizeInPx();
    }

    protected int getNbPage() {
        return this.textBook.nbPages();
    }

    public abstract ResourceLocation getPNGPath();

    public final int getTextHeight() {
        return this.bookManager.getTextHeight();
    }

    public final int getTextXStart() {
        return this.bookManager.getTextXStart();
    }

    public final int getXSize() {
        return this.bookManager.getXSize();
    }

    public final int getYSize() {
        return this.bookManager.getYSize();
    }

    protected void handleTextFieldPress(MillGuiTextField textField) {
    }

    public void incrementPage() {
        if (this.textBook == null) {
            return;
        }
        if (this.pageNum < this.getNbPage() - 1) {
            ++this.pageNum;
        }
        this.buttonPagination();
    }

    public abstract void initData();

    public void func_73866_w_() {
        super.func_73866_w_();
        this.initData();
        this.buttonPagination();
    }

    protected void func_73869_a(char c, int i) {
        boolean keyTyped = false;
        for (MillGuiTextField textField : this.textFields) {
            if (!textField.func_146201_a(c, i)) continue;
            keyTyped = true;
            this.handleTextFieldPress(textField);
        }
        if (!keyTyped && i == 1) {
            this.closeGui();
        }
    }

    protected void func_73864_a(int i, int j, int k) throws IOException {
        int xStart = (this.field_146294_l - this.getXSize()) / 2;
        int yStart = (this.field_146295_m - this.getYSize()) / 2;
        int ai = i - xStart;
        int aj = j - yStart;
        if (aj > this.getYSize() - 14 && aj < this.getYSize()) {
            if (ai > 0 && ai < 33) {
                this.decrementPage();
            } else if (ai > this.getXSize() - 33 && ai < this.getXSize()) {
                this.incrementPage();
            }
        }
        for (MillGuiTextField textField : this.textFields) {
            textField.func_146192_a(i, j, k);
        }
        super.func_73864_a(i, j, k);
    }

    public void func_146281_b() {
        super.func_146281_b();
    }

    public void setCallingScreen(GuiScreen callingScreen) {
        this.callingScreen = callingScreen;
    }

    public static enum SpecialIcon {
        PLUS(0, 0),
        MINUS(16, 0);

        public int xpos;
        public int ypos;

        private SpecialIcon(int xpos, int ypos) {
            this.xpos = xpos;
            this.ypos = ypos;
        }
    }

    public static class MillGuiTextField
    extends GuiTextField {
        public final String fieldKey;

        public MillGuiTextField(int id, FontRenderer par1FontRenderer, int x, int y, int par5Width, int par6Height, String fieldKey) {
            super(id, par1FontRenderer, x, y, par5Width, par6Height);
            this.fieldKey = fieldKey;
        }
    }

    public static class MillGuiButton
    extends GuiButton {
        public static final int HELPBUTTON = 2000;
        public static final int CHUNKBUTTON = 3000;
        public static final int CONFIGBUTTON = 4000;
        public static final int TRAVELBOOKBUTTON = 5000;
        public ItemStack itemStackIconLeft = null;
        public SpecialIcon specialIconLeft = null;
        public ItemStack itemStackIconRight = null;
        public SpecialIcon specialIconRight = null;

        public MillGuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String label) {
            super(buttonId, x, y, widthIn, heightIn, label);
        }

        public MillGuiButton(String label, int id) {
            super(id, 0, 0, 0, 0, label);
        }

        public MillGuiButton(String label, int id, ItemStack icon) {
            super(id, 0, 0, 0, 0, label);
            this.itemStackIconLeft = icon;
        }

        public MillGuiButton(String label, int id, ItemStack iconLeft, ItemStack iconRight) {
            super(id, 0, 0, 0, 0, label);
            this.itemStackIconLeft = iconLeft;
            this.itemStackIconRight = iconRight;
        }

        public MillGuiButton(String label, int id, SpecialIcon icon) {
            super(id, 0, 0, 0, 0, label);
            this.specialIconLeft = icon;
        }

        public int getHeight() {
            return this.field_146121_g;
        }

        public int getWidth() {
            return this.field_146120_f;
        }

        public void setHeight(int h) {
            this.field_146121_g = h;
        }
    }

    public static class GuiButtonReference
    extends GuiButton {
        public Culture culture;
        public RefType type;
        public String key;

        public GuiButtonReference(BuildingPlanSet planSet) {
            super(0, 0, 0, 0, 0, "");
            if (planSet == null) {
                MillLog.printException(new Exception("Tried creating a ref button to a null planSet."));
            } else {
                this.culture = planSet.culture;
            }
            this.type = RefType.BUILDING_DETAIL;
            this.key = planSet.key;
        }

        public GuiButtonReference(Culture culture) {
            super(0, 0, 0, 0, 0, "");
            this.culture = culture;
            this.type = RefType.CULTURE;
            this.key = null;
        }

        public GuiButtonReference(Culture culture, RefType type, String key) {
            super(0, 0, 0, 0, 0, "");
            this.culture = culture;
            this.type = type;
            this.key = key;
        }

        public GuiButtonReference(TradeGood tradeGood) {
            super(0, 0, 0, 0, 0, "");
            this.culture = tradeGood.culture;
            this.type = RefType.TRADE_GOOD_DETAIL;
            this.key = tradeGood.key;
        }

        public GuiButtonReference(VillagerType villagerType) {
            super(0, 0, 0, 0, 0, "");
            this.culture = villagerType.culture;
            this.type = RefType.VILLAGER_DETAIL;
            this.key = villagerType.key;
        }

        public GuiButtonReference(VillageType villageType) {
            super(0, 0, 0, 0, 0, "");
            if (villageType.lonebuilding) {
                this.culture = villageType.culture;
                this.type = RefType.BUILDING_DETAIL;
                this.key = villageType.centreBuilding.key;
            } else {
                this.culture = villageType.culture;
                this.type = RefType.VILLAGE_DETAIL;
                this.key = villageType.key;
            }
        }

        public ItemStack getIcon() {
            if (this.type == RefType.BUILDING_DETAIL) {
                return this.culture.getBuildingPlanSet(this.key).getIcon();
            }
            if (this.type == RefType.VILLAGER_DETAIL) {
                return this.culture.getVillagerType(this.key).getIcon();
            }
            if (this.type == RefType.VILLAGE_DETAIL) {
                return this.culture.getVillageType(this.key).getIcon();
            }
            if (this.type == RefType.TRADE_GOOD_DETAIL) {
                return this.culture.getTradeGood(this.key).getIcon();
            }
            if (this.type == RefType.CULTURE) {
                return this.culture.getIcon();
            }
            return null;
        }

        public String getIconFullLegend() {
            return LanguageUtilities.string("travelbook.reference_button", this.getIconName());
        }

        public String getIconFullLegendExport() {
            return LanguageUtilities.string("travelbook.reference_button_export", this.getIconNameTranslated());
        }

        public String getIconName() {
            if (this.type == RefType.BUILDING_DETAIL) {
                return this.culture.getBuildingPlanSet(this.key).getNameNative();
            }
            if (this.type == RefType.VILLAGER_DETAIL) {
                return this.culture.getVillagerType((String)this.key).name;
            }
            if (this.type == RefType.VILLAGE_DETAIL) {
                return this.culture.getVillageType((String)this.key).name;
            }
            if (this.type == RefType.TRADE_GOOD_DETAIL) {
                return this.culture.getTradeGood(this.key).getName();
            }
            if (this.type == RefType.CULTURE) {
                return this.culture.getAdjectiveTranslated();
            }
            return null;
        }

        public String getIconNameTranslated() {
            if (this.type == RefType.BUILDING_DETAIL) {
                return this.culture.getBuildingPlanSet(this.key).getNameNativeAndTranslated();
            }
            if (this.type == RefType.VILLAGER_DETAIL) {
                return this.culture.getVillagerType(this.key).getNameNativeAndTranslated();
            }
            if (this.type == RefType.VILLAGE_DETAIL) {
                return this.culture.getVillageType(this.key).getNameNativeAndTranslated();
            }
            if (this.type == RefType.TRADE_GOOD_DETAIL) {
                return this.culture.getTradeGood(this.key).getName();
            }
            if (this.type == RefType.CULTURE) {
                return this.culture.getAdjectiveTranslated();
            }
            return null;
        }

        public void setHeight(int h) {
            this.field_146121_g = h;
        }

        public static enum RefType {
            BUILDING_DETAIL,
            VILLAGER_DETAIL,
            VILLAGE_DETAIL,
            TRADE_GOOD_DETAIL,
            CULTURE;

        }
    }

    public static class FontRendererWrapped
    implements BookManager.IFontRendererWrapper {
        private final FontRenderer fontRenderer;

        public FontRendererWrapped(FontRenderer fontRenderer) {
            this.fontRenderer = fontRenderer;
        }

        @Override
        public int getStringWidth(String text) {
            return this.fontRenderer.func_78256_a(text);
        }

        @Override
        public boolean isAvailable() {
            return this.fontRenderer != null;
        }
    }

    public static class FontRendererGUIWrapper
    implements BookManager.IFontRendererWrapper {
        private final GuiText gui;

        public FontRendererGUIWrapper(GuiText gui) {
            this.gui = gui;
        }

        @Override
        public int getStringWidth(String text) {
            return this.gui.field_146289_q.func_78256_a(text);
        }

        @Override
        public boolean isAvailable() {
            return this.gui.field_146289_q != null;
        }
    }
}

