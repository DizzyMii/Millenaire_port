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
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.millenaire.client.book.BookManager;
import org.millenaire.client.book.TextLine;
import org.millenaire.client.gui.DisplayActions;
import org.millenaire.client.gui.text.GuiText;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.world.UserProfile;

public class GuiNewVillage
extends GuiText {
    private List<VillageType> possibleVillages = new ArrayList<VillageType>();
    private final Point pos;
    private final EntityPlayer player;
    ResourceLocation background = new ResourceLocation("millenaire", "textures/gui/panel.png");

    public GuiNewVillage(EntityPlayer player, Point p) {
        this.pos = p;
        this.player = player;
        this.bookManager = new BookManager(204, 220, 190, 195, new GuiText.FontRendererGUIWrapper(this));
    }

    @Override
    protected void func_146284_a(GuiButton guibutton) throws IOException {
        if (guibutton instanceof GuiText.MillGuiButton) {
            if (!guibutton.field_146124_l) {
                return;
            }
            VillageType village = this.possibleVillages.get(guibutton.field_146127_k);
            this.closeWindow();
            if (village.customCentre == null) {
                ClientSender.newVillageCreation(this.player, this.pos, village.culture.key, village.key);
            } else {
                DisplayActions.displayNewCustomBuildingGUI(this.player, this.pos, village);
            }
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
    public ResourceLocation getPNGPath() {
        return this.background;
    }

    @Override
    public void initData() {
        ArrayList<TextLine> text = new ArrayList<TextLine>();
        text.add(new TextLine(LanguageUtilities.string("ui.selectavillage"), "\u00a71"));
        text.add(new TextLine(false));
        text.add(new TextLine(LanguageUtilities.string("ui.leadershipstatus") + ":"));
        text.add(new TextLine());
        boolean notleader = false;
        UserProfile profile = Mill.proxy.getClientProfile();
        for (Culture culture : Culture.ListCultures) {
            if (profile.isTagSet("culturecontrol_" + culture.key)) {
                text.add(new TextLine(LanguageUtilities.string("ui.leaderin", culture.getAdjectiveTranslated()), new GuiText.GuiButtonReference(culture)));
                continue;
            }
            text.add(new TextLine(LanguageUtilities.string("ui.notleaderin", culture.getAdjectiveTranslated()), new GuiText.GuiButtonReference(culture)));
            notleader = true;
        }
        if (notleader) {
            text.add(new TextLine());
            text.add(new TextLine(LanguageUtilities.string("ui.leaderinstruction")));
        }
        text.add(new TextLine());
        this.possibleVillages = VillageType.spawnableVillages(this.player);
        for (int i = 0; i < this.possibleVillages.size(); ++i) {
            text.add(new TextLine(new GuiText.MillGuiButton(this.possibleVillages.get((int)i).name, i, this.possibleVillages.get((int)i).culture.getIcon(), this.possibleVillages.get(i).getIcon())));
            String extraInfo = this.possibleVillages.get((int)i).culture.getAdjectiveTranslated();
            String nameTranslated = this.possibleVillages.get(i).getNameTranslated();
            if (nameTranslated != null) {
                extraInfo = extraInfo + ", " + nameTranslated;
            }
            text.add(new TextLine("(" + extraInfo + ")"));
            text.add(new TextLine());
        }
        ArrayList<List<TextLine>> pages = new ArrayList<List<TextLine>>();
        pages.add(text);
        this.textBook = this.bookManager.convertAndAdjustLines(pages);
    }
}

