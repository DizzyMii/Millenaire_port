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
import org.millenaire.client.book.TextBook;
import org.millenaire.client.book.TextLine;
import org.millenaire.client.gui.text.GuiText;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.quest.QuestInstance;
import org.millenaire.common.quest.QuestStep;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.world.UserProfile;

public class GuiQuest
extends GuiText {
    private final MillVillager villager;
    private final EntityPlayer player;
    private boolean showOk = false;
    private int type;
    private boolean firstStep;
    ResourceLocation background = new ResourceLocation("millenaire", "textures/gui/quest.png");

    public GuiQuest(EntityPlayer player, MillVillager villager) {
        this.villager = villager;
        this.player = player;
        this.bookManager = new BookManager(256, 220, 160, 240, new GuiText.FontRendererGUIWrapper(this));
    }

    @Override
    protected void func_146284_a(GuiButton guibutton) throws IOException {
        if (!guibutton.field_146124_l) {
            return;
        }
        if (!(guibutton instanceof GuiText.GuiButtonReference)) {
            UserProfile profile = Mill.proxy.getClientProfile();
            QuestInstance qi = profile.villagersInQuests.get(this.villager.getVillagerId());
            boolean questActionHandled = false;
            if (qi != null) {
                if (guibutton.field_146127_k == 0) {
                    boolean firstStep = qi.currentStep == 0;
                    String res = qi.completeStep(this.player, this.villager);
                    ClientSender.questCompleteStep(this.player, this.villager);
                    this.initStatus(1, res, firstStep);
                    questActionHandled = true;
                } else if (guibutton.field_146127_k == 1) {
                    boolean firstStep = qi.currentStep == 0;
                    String res = qi.refuseQuest(this.player, this.villager);
                    ClientSender.questRefuse(this.player, this.villager);
                    this.initStatus(2, res, firstStep);
                    questActionHandled = true;
                }
            }
            if (!questActionHandled) {
                this.field_146297_k.func_147108_a(null);
                this.field_146297_k.func_71381_h();
                ClientSender.villagerInteractSpecial(this.player, this.villager);
            }
        }
        super.func_146284_a(guibutton);
    }

    @Override
    public void buttonPagination() {
        super.buttonPagination();
        int xStart = (this.field_146294_l - this.getXSize()) / 2;
        int yStart = (this.field_146295_m - this.getYSize()) / 2;
        if (this.type == 0) {
            if (this.firstStep) {
                if (this.showOk) {
                    this.field_146292_n.add(new GuiButton(1, xStart + this.getXSize() / 2 - 100, yStart + this.getYSize() - 40, 95, 20, LanguageUtilities.string("quest.refuse")));
                    this.field_146292_n.add(new GuiButton(0, xStart + this.getXSize() / 2 + 5, yStart + this.getYSize() - 40, 95, 20, LanguageUtilities.string("quest.accept")));
                } else {
                    this.field_146292_n.add(new GuiButton(1, xStart + this.getXSize() / 2 - 100, yStart + this.getYSize() - 40, 95, 20, LanguageUtilities.string("quest.refuse")));
                    this.field_146292_n.add(new GuiButton(2, xStart + this.getXSize() / 2 + 5, yStart + this.getYSize() - 40, 95, 20, LanguageUtilities.string("quest.close")));
                }
            } else if (this.showOk) {
                this.field_146292_n.add(new GuiButton(0, xStart + this.getXSize() / 2 - 100, yStart + this.getYSize() - 40, LanguageUtilities.string("quest.continue")));
            } else {
                this.field_146292_n.add(new GuiButton(2, xStart + this.getXSize() / 2 - 100, yStart + this.getYSize() - 40, LanguageUtilities.string("quest.close")));
            }
        } else {
            this.field_146292_n.add(new GuiButton(2, xStart + this.getXSize() / 2 - 100, yStart + this.getYSize() - 40, LanguageUtilities.string("quest.close")));
        }
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

    private TextBook getData(int type, String baseText) {
        ArrayList<TextLine> text = new ArrayList<TextLine>();
        String game = "";
        if (this.villager.getGameOccupationName(this.player.func_70005_c_()).length() > 0) {
            game = " (" + this.villager.getGameOccupationName(this.player.func_70005_c_()) + ")";
        }
        text.add(new TextLine(this.villager.func_70005_c_() + ", " + this.villager.getNativeOccupationName() + game, "\u00a71", new GuiText.GuiButtonReference(this.villager.vtype)));
        text.add(new TextLine());
        text.add(new TextLine(baseText.replaceAll("\\$name", this.player.func_70005_c_())));
        UserProfile profile = Mill.proxy.getClientProfile();
        if (type == 0) {
            QuestStep step = profile.villagersInQuests.get(this.villager.getVillagerId()).getCurrentStep();
            String error = step.lackingConditions(this.player);
            if (error != null) {
                text.add(new TextLine());
                text.add(new TextLine(error));
                this.showOk = false;
            } else {
                this.showOk = true;
            }
        }
        ArrayList<List<TextLine>> ftext = new ArrayList<List<TextLine>>();
        ftext.add(text);
        return this.bookManager.convertAndAdjustLines(ftext);
    }

    @Override
    public ResourceLocation getPNGPath() {
        return this.background;
    }

    @Override
    public void initData() {
        UserProfile profile = Mill.proxy.getClientProfile();
        String baseText = profile.villagersInQuests.get(this.villager.getVillagerId()).getDescription(profile);
        boolean firstStep = profile.villagersInQuests.get((Object)Long.valueOf((long)this.villager.getVillagerId())).currentStep == 0;
        this.initStatus(0, baseText, firstStep);
    }

    private void initStatus(int type, String baseText, boolean firstStep) {
        this.pageNum = 0;
        this.type = type;
        this.firstStep = firstStep;
        this.textBook = this.getData(type, baseText);
        this.buttonPagination();
    }

    @Override
    protected void func_73869_a(char c, int i) {
        if (i == 1) {
            this.field_146297_k.func_147108_a(null);
            this.field_146297_k.func_71381_h();
            ClientSender.villagerInteractSpecial(this.player, this.villager);
        }
    }
}

