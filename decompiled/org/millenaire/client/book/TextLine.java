/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.text.TextFormatting
 */
package org.millenaire.client.book;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import org.millenaire.client.gui.text.GuiText;
import org.millenaire.common.utilities.MillLog;

public class TextLine {
    public static final String WHITE = "\u00a7f";
    public static final String YELLOW = "\u00a7e";
    public static final String PINK = "\u00a7d";
    public static final String LIGHTRED = "\u00a7c";
    public static final String CYAN = "\u00a7b";
    public static final String LIGHTGREEN = "\u00a7a";
    public static final String BLUE = "\u00a79";
    public static final String DARKGREY = "\u00a78";
    public static final String LIGHTGREY = "\u00a77";
    public static final String ORANGE = "\u00a76";
    public static final String PURPLE = "\u00a75";
    public static final String DARKRED = "\u00a74";
    public static final String LIGHTBLUE = "\u00a73";
    public static final String DARKGREEN = "\u00a72";
    public static final String DARKBLUE = "\u00a71";
    public static final String BLACK = "\u00a70";
    public static final String ITALIC = "" + TextFormatting.ITALIC;
    public static final String BOLD = "" + TextFormatting.BOLD;
    public static final String UNDERLINE = "" + TextFormatting.UNDERLINE;
    public static final int DEFAULT_LINE_HEIGHT = 10;
    public String style = "";
    public String text = "";
    public transient GuiText.MillGuiButton[] buttons = null;
    public transient GuiText.GuiButtonReference referenceButton = null;
    public transient GuiText.MillGuiTextField textField = null;
    public transient List<ItemStack> icons = null;
    public List<String> iconExtraLegends = null;
    public boolean canCutAfter = true;
    public boolean shadow = false;
    private int lineMarginLeft = 0;
    private int lineMarginRight = 0;
    private int textMarginLeft = 0;
    private int textMarginTop = 0;
    private int lineHeight = 10;
    public TextLine[] columns = null;
    private boolean displayItemLegend = true;
    public boolean exportTwoColumns = false;
    public String exportSpecialTag = null;

    public TextLine() {
    }

    public TextLine(boolean canCutAfter) {
        this.canCutAfter = canCutAfter;
    }

    public TextLine(List<ItemStack> icons, List<String> iconExtraLegends, String s, int marginLeft) {
        this.icons = icons;
        this.iconExtraLegends = iconExtraLegends;
        if (icons != null && iconExtraLegends == null) {
            MillLog.printException("iconExtraLegends is null but icons isn't.", new Exception());
        } else if (icons != null && iconExtraLegends != null && icons.size() != iconExtraLegends.size()) {
            MillLog.printException("iconExtraLegends has a size of " + iconExtraLegends.size() + " but icons has a size of " + icons.size(), new Exception());
        }
        this.text = s;
        this.canCutAfter = false;
        this.textMarginLeft = marginLeft;
    }

    public TextLine(GuiText.MillGuiButton b) {
        this.buttons = new GuiText.MillGuiButton[]{b};
    }

    public TextLine(GuiText.MillGuiButton b, GuiText.MillGuiButton b2) {
        this.buttons = new GuiText.MillGuiButton[]{b, b2};
    }

    public TextLine(GuiText.MillGuiButton b, GuiText.MillGuiButton b2, GuiText.MillGuiButton b3) {
        this.buttons = new GuiText.MillGuiButton[]{b, b2, b3};
    }

    public TextLine(GuiText.MillGuiTextField tf) {
        this.textField = tf;
    }

    public TextLine(String s) {
        if (s == null) {
            this.text = "";
        } else {
            this.text = s;
            this.interpretTags();
        }
    }

    public TextLine(String s, boolean canCutAfter) {
        if (s == null) {
            this.text = "";
        } else {
            this.text = s;
            this.interpretTags();
        }
        this.canCutAfter = canCutAfter;
    }

    public TextLine(String s, GuiText.GuiButtonReference referenceButton) {
        this.text = s;
        this.referenceButton = referenceButton;
    }

    public TextLine(String s, ItemStack icon, boolean displayItemLegend) {
        this(s, "", icon, displayItemLegend);
    }

    public TextLine(String s, ItemStack icon, String iconLegend, boolean displayItemLegend) {
        this(s, "", icon, iconLegend, displayItemLegend);
    }

    public TextLine(String s, GuiText.MillGuiTextField tf) {
        this.textField = tf;
        if (s == null) {
            this.text = "";
        } else {
            this.text = s;
            this.interpretTags();
        }
    }

    public TextLine(String s, String colour) {
        this.style = colour;
        if (s == null) {
            this.text = "";
        } else {
            this.text = s;
            this.interpretTags();
        }
    }

    public TextLine(String s, String style, boolean canCutAfter) {
        this(s, canCutAfter);
        this.style = style;
    }

    public TextLine(String s, String style, GuiText.GuiButtonReference guiButtonReference) {
        this.text = s;
        this.style = style;
        this.referenceButton = guiButtonReference;
    }

    public TextLine(String text, String colour, ItemStack icon, boolean displayItemLegend) {
        this(text, colour, icon, null, displayItemLegend);
    }

    public TextLine(String text, String colour, ItemStack icon, String iconLegend, boolean displayItemLegend) {
        ArrayList<ItemStack> icons = new ArrayList<ItemStack>();
        icons.add(icon);
        ArrayList<String> legends = new ArrayList<String>();
        legends.add(iconLegend);
        this.icons = icons;
        this.iconExtraLegends = legends;
        this.text = text;
        this.style = colour;
        this.displayItemLegend = displayItemLegend;
    }

    public TextLine(String s, TextLine model, int lnpos) {
        if (model.icons != null && lnpos % 2 == 0) {
            int lnicon = lnpos / 2;
            this.icons = new ArrayList<ItemStack>();
            this.iconExtraLegends = new ArrayList<String>();
            for (int i = lnicon * 4; i < model.icons.size() && i < (lnicon + 1) * 4; ++i) {
                this.icons.add(model.icons.get(i));
                this.iconExtraLegends.add(model.iconExtraLegends.get(i));
            }
        }
        if (s == null) {
            this.text = "";
        } else {
            this.text = s;
            this.interpretTags();
        }
        this.canCutAfter = model.canCutAfter;
        this.shadow = model.shadow;
        this.textMarginLeft = model.textMarginLeft;
        this.textMarginTop = model.textMarginTop;
        this.lineMarginLeft = model.lineMarginLeft;
        this.lineMarginRight = model.lineMarginRight;
        this.lineHeight = model.lineHeight;
        this.style = model.style;
        this.columns = model.columns;
        this.displayItemLegend = model.displayItemLegend;
        this.exportTwoColumns = model.exportTwoColumns;
        this.exportSpecialTag = model.exportSpecialTag;
    }

    public TextLine(TextLine ... columns) {
        this.columns = columns;
        for (TextLine col : columns) {
            if (col.canCutAfter) continue;
            this.canCutAfter = false;
        }
    }

    public boolean displayItemLegend() {
        return this.displayItemLegend;
    }

    public boolean empty() {
        return (this.text == null || this.text.length() == 0) && this.buttons == null && this.textField == null && this.columns == null && this.referenceButton == null;
    }

    public int getLineHeight() {
        if (this.columns != null) {
            int max = 0;
            for (TextLine column : this.columns) {
                if (column.getLineHeight() <= max) continue;
                max = column.getLineHeight();
            }
            return max;
        }
        if (this.lineHeight == 10) {
            if (this.icons != null) {
                return 18;
            }
            if (this.buttons != null || this.referenceButton != null) {
                return 22;
            }
        }
        return this.lineHeight;
    }

    public int getLineMarginLeft() {
        return this.lineMarginLeft;
    }

    public int getLineMarginRight() {
        return this.lineMarginRight;
    }

    public int getTextMarginLeft() {
        if (this.icons != null && this.textMarginLeft == 0) {
            return this.icons.size() * 18;
        }
        if (this.referenceButton != null && this.textMarginLeft == 0) {
            return 20;
        }
        return this.textMarginLeft;
    }

    public int getTextMarginTop() {
        if (this.icons != null && this.textMarginTop == 0) {
            return 4;
        }
        if (this.referenceButton != null && this.textMarginTop == 0) {
            return 6;
        }
        return this.textMarginTop;
    }

    private void interpretTags() {
        if (this.text.startsWith("<shadow>")) {
            this.shadow = true;
            this.text = this.text.replaceAll("<shadow>", "");
        }
        this.text = this.text.replaceAll("<black>", BLACK);
        this.text = this.text.replaceAll("<darkblue>", DARKBLUE);
        this.text = this.text.replaceAll("<darkgreen>", DARKGREEN);
        this.text = this.text.replaceAll("<lightblue>", LIGHTBLUE);
        this.text = this.text.replaceAll("<darkred>", DARKRED);
        this.text = this.text.replaceAll("<purple>", PURPLE);
        this.text = this.text.replaceAll("<orange>", ORANGE);
        this.text = this.text.replaceAll("<lightgrey>", LIGHTGREY);
        this.text = this.text.replaceAll("<darkgrey>", DARKGREY);
        this.text = this.text.replaceAll("<blue>", BLUE);
        this.text = this.text.replaceAll("<lightgreen>", LIGHTGREEN);
        this.text = this.text.replaceAll("<cyan>", CYAN);
        this.text = this.text.replaceAll("<lightred>", LIGHTRED);
        this.text = this.text.replaceAll("<pink>", PINK);
        this.text = this.text.replaceAll("<yellow>", YELLOW);
        this.text = this.text.replaceAll("<white>", WHITE);
    }

    public void setLineMarginLeft(int lineMarginLeft) {
        this.lineMarginLeft = lineMarginLeft;
    }

    public void setLineMarginRight(int lineMarginRight) {
        this.lineMarginRight = lineMarginRight;
    }
}

