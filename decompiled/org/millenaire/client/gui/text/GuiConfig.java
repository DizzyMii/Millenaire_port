/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.util.ResourceLocation
 */
package org.millenaire.client.gui.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.millenaire.client.book.BookManager;
import org.millenaire.client.book.TextBook;
import org.millenaire.client.book.TextLine;
import org.millenaire.client.gui.text.GuiText;
import org.millenaire.common.config.MillConfigParameter;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.utilities.LanguageUtilities;

public class GuiConfig
extends GuiText {
    int pageId = -1;
    ResourceLocation background = new ResourceLocation("millenaire", "textures/gui/config.png");

    public GuiConfig() {
        this.bookManager = new BookManager(256, 220, 190, 247, new GuiText.FontRendererGUIWrapper(this));
    }

    @Override
    protected void func_146284_a(GuiButton guibutton) throws IOException {
        if (guibutton instanceof ConfigButton) {
            ConfigButton configButton = (ConfigButton)guibutton;
            int valPos = -1;
            for (int i = 0; i < configButton.config.getPossibleVals().length; ++i) {
                Object o = configButton.config.getPossibleVals()[i];
                if (!o.equals(configButton.config.getValue())) continue;
                valPos = i;
            }
            if (++valPos >= configButton.config.getPossibleVals().length) {
                valPos = 0;
            }
            configButton.config.setValue(configButton.config.getPossibleVals()[valPos]);
            configButton.refreshLabel();
            MillConfigValues.writeConfigFile();
        } else if (guibutton instanceof ConfigPageButton) {
            ConfigPageButton configPageButton = (ConfigPageButton)guibutton;
            this.pageId = configPageButton.pageId;
            this.pageNum = 0;
            this.textBook = this.getData();
            this.buttonPagination();
        }
        super.func_146284_a(guibutton);
    }

    @Override
    protected void customDrawBackground(int i, int j, float f) {
    }

    @Override
    protected void customDrawScreen(int i, int j, float f) {
    }

    @Override
    public boolean func_73868_f() {
        return true;
    }

    private TextBook getData() {
        if (this.pageId == -1) {
            return this.getHomepageData();
        }
        return this.getPageData();
    }

    private TextBook getHomepageData() {
        ArrayList<List<TextLine>> pages = new ArrayList<List<TextLine>>();
        ArrayList<TextLine> text = new ArrayList<TextLine>();
        text.add(new TextLine("<darkblue>" + LanguageUtilities.string("config.pagetitle"), false));
        text.add(new TextLine("", false));
        for (int i = 0; i < MillConfigValues.configPages.size(); ++i) {
            text.add(new TextLine(new ConfigPageButton(i)));
        }
        pages.add(text);
        return this.bookManager.convertAndAdjustLines(pages);
    }

    private TextBook getPageData() {
        int buttonId = 0;
        ArrayList<List<TextLine>> pages = new ArrayList<List<TextLine>>();
        ArrayList<TextLine> text = new ArrayList<TextLine>();
        text.add(new TextLine("<darkblue>" + LanguageUtilities.string(MillConfigValues.configPageTitles.get(this.pageId)), false));
        text.add(new TextLine());
        if (MillConfigValues.configPageDesc.get(this.pageId) != null) {
            text.add(new TextLine(LanguageUtilities.string(MillConfigValues.configPageDesc.get(this.pageId)), false));
            text.add(new TextLine());
        }
        for (int j = 0; j < MillConfigValues.configPages.get(this.pageId).size(); ++j) {
            MillConfigParameter config = MillConfigValues.configPages.get(this.pageId).get(j);
            if (!config.displayConfig && (!config.displayConfigDev || !MillConfigValues.DEV)) continue;
            if (config.getDesc().length() > 0) {
                text.add(new TextLine(config.getDesc(), false));
            }
            if (config.hasTextField()) {
                GuiText.MillGuiTextField textField = new GuiText.MillGuiTextField(buttonId++, this.field_146289_q, 0, 0, 0, 0, config.key);
                textField.func_146180_a(config.getStringValue());
                textField.func_146203_f(config.strLimit);
                textField.func_146193_g(-1);
                text.add(new TextLine(config.getLabel() + ":", textField));
                text.add(new TextLine(false));
                text.add(new TextLine());
                continue;
            }
            text.add(new TextLine(new ConfigButton(config)));
        }
        pages.add(text);
        return this.bookManager.convertAndAdjustLines(pages);
    }

    @Override
    public ResourceLocation getPNGPath() {
        return this.background;
    }

    @Override
    protected void handleTextFieldPress(GuiText.MillGuiTextField textField) {
        if (MillConfigValues.configParameters.containsKey(textField.fieldKey)) {
            MillConfigParameter config = MillConfigValues.configParameters.get(textField.fieldKey);
            config.setValueFromString(textField.func_146179_b(), false);
            MillConfigValues.writeConfigFile();
        }
    }

    @Override
    public void initData() {
        this.textBook = this.getData();
    }

    @Override
    protected void func_73869_a(char c, int i) {
        if (i == 1) {
            if (this.pageId == -1) {
                this.field_146297_k.func_147108_a(null);
                this.field_146297_k.func_71381_h();
            } else {
                this.pageId = -1;
                this.pageNum = 0;
                this.textBook = this.getData();
                this.buttonPagination();
            }
        } else {
            super.func_73869_a(c, i);
        }
    }

    public void func_73876_c() {
    }

    public static class ConfigPageButton
    extends GuiText.MillGuiButton {
        public int pageId;

        public ConfigPageButton(int pageId) {
            super(0, 0, 0, 0, 0, LanguageUtilities.string(MillConfigValues.configPageTitles.get(pageId)));
            this.pageId = pageId;
        }
    }

    public static class ConfigButton
    extends GuiText.MillGuiButton {
        public MillConfigParameter config;

        public ConfigButton(MillConfigParameter config) {
            super(0, 0, 0, 0, 0, config.getLabel());
            this.config = config;
            this.refreshLabel();
        }

        public void refreshLabel() {
            this.field_146126_j = this.config.getLabel() + ": " + this.config.getStringValue();
        }
    }
}

