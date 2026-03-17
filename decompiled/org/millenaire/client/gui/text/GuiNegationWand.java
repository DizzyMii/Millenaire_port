/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.ResourceLocation
 */
package org.millenaire.client.gui.text;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.millenaire.client.book.BookManager;
import org.millenaire.client.book.TextBook;
import org.millenaire.client.book.TextPage;
import org.millenaire.client.gui.text.GuiText;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.village.Building;

public class GuiNegationWand
extends GuiText {
    private final Building th;
    private final EntityPlayer player;
    ResourceLocation background = new ResourceLocation("millenaire", "textures/gui/quest.png");

    public GuiNegationWand(EntityPlayer player, Building th) {
        this.th = th;
        this.player = player;
        this.bookManager = new BookManager(256, 220, 160, 240, new GuiText.FontRendererGUIWrapper(this));
    }

    @Override
    protected void func_146284_a(GuiButton guibutton) throws IOException {
        if (!guibutton.field_146124_l) {
            return;
        }
        if (guibutton instanceof GuiText.MillGuiButton) {
            if (guibutton.field_146127_k == 0) {
                ClientSender.negationWand(this.player, this.th);
            }
            this.field_146297_k.func_147108_a(null);
            this.field_146297_k.func_71381_h();
        }
        super.func_146284_a(guibutton);
    }

    @Override
    public void buttonPagination() {
        super.buttonPagination();
        int xStart = (this.field_146294_l - this.getXSize()) / 2;
        int yStart = (this.field_146295_m - this.getYSize()) / 2;
        this.field_146292_n.add(new GuiText.MillGuiButton(1, xStart + this.getXSize() / 2 - 100, yStart + this.getYSize() - 40, 95, 20, LanguageUtilities.string("negationwand.cancel")));
        this.field_146292_n.add(new GuiText.MillGuiButton(0, xStart + this.getXSize() / 2 + 5, yStart + this.getYSize() - 40, 95, 20, LanguageUtilities.string("negationwand.confirm")));
    }

    @Override
    protected void customDrawBackground(int i, int j, float f) {
    }

    @Override
    protected void customDrawScreen(int i, int j, float f) {
    }

    @Override
    public boolean func_73868_f() {
        return false;
    }

    @Override
    public ResourceLocation getPNGPath() {
        return this.background;
    }

    @Override
    public void initData() {
        this.textBook = new TextBook();
        TextPage page = new TextPage();
        page.addLine(LanguageUtilities.string("negationwand.confirmmessage", this.th.villageType.name));
        this.textBook.addPage(page);
        this.textBook = this.bookManager.adjustTextBookLineLength(this.textBook);
        this.pageNum = 0;
    }
}

