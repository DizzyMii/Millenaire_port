/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.input.Keyboard
 */
package org.millenaire.client.gui.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.millenaire.client.book.BookManager;
import org.millenaire.client.book.TextBook;
import org.millenaire.client.book.TextLine;
import org.millenaire.client.forge.ClientProxy;
import org.millenaire.client.gui.text.GuiText;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;

public class GuiHire
extends GuiText {
    private static final int REPUTATION_NEEDED = 4096;
    public static final int BUTTON_CLOSE = 0;
    public static final int BUTTON_HIRE = 1;
    public static final int BUTTON_EXTEND = 2;
    public static final int BUTTON_RELEASE = 3;
    private final MillVillager villager;
    private final EntityPlayer player;
    ResourceLocation background = new ResourceLocation("millenaire", "textures/gui/quest.png");

    public GuiHire(EntityPlayer player, MillVillager villager) {
        this.villager = villager;
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
                this.field_146297_k.func_147108_a(null);
                this.field_146297_k.func_71381_h();
            } else if (guibutton.field_146127_k == 1) {
                ClientSender.hireHire(this.player, this.villager);
                this.refreshContent();
            } else if (guibutton.field_146127_k == 2) {
                ClientSender.hireExtend(this.player, this.villager);
                this.refreshContent();
            } else if (guibutton.field_146127_k == 3) {
                ClientSender.hireRelease(this.player, this.villager);
                this.refreshContent();
            }
        }
        super.func_146284_a(guibutton);
    }

    @Override
    public void buttonPagination() {
        super.buttonPagination();
        int xStart = (this.field_146294_l - this.getXSize()) / 2;
        int yStart = (this.field_146295_m - this.getYSize()) / 2;
        if (this.villager.hiredBy != null) {
            if (MillCommonUtilities.countMoney((IInventory)this.player.field_71071_by) >= this.villager.getHireCost(this.player)) {
                this.field_146292_n.add(new GuiText.MillGuiButton(2, xStart + this.getXSize() / 2 - 100, yStart + this.getYSize() - 40, 63, 20, LanguageUtilities.string("hire.extend")));
            }
            this.field_146292_n.add(new GuiText.MillGuiButton(3, xStart + this.getXSize() / 2 - 32, yStart + this.getYSize() - 40, 64, 20, LanguageUtilities.string("hire.release")));
            this.field_146292_n.add(new GuiText.MillGuiButton(0, xStart + this.getXSize() / 2 + 37, yStart + this.getYSize() - 40, 63, 20, LanguageUtilities.string("hire.close")));
        } else {
            if (this.villager.getTownHall().getReputation(this.player) >= 4096 && MillCommonUtilities.countMoney((IInventory)this.player.field_71071_by) >= this.villager.getHireCost(this.player)) {
                this.field_146292_n.add(new GuiText.MillGuiButton(1, xStart + this.getXSize() / 2 - 100, yStart + this.getYSize() - 40, 95, 20, LanguageUtilities.string("hire.hire")));
            }
            this.field_146292_n.add(new GuiText.MillGuiButton(0, xStart + this.getXSize() / 2 + 5, yStart + this.getYSize() - 40, 95, 20, LanguageUtilities.string("hire.close")));
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

    private TextBook getData() {
        ArrayList<TextLine> text = new ArrayList<TextLine>();
        text.add(new TextLine(this.villager.func_70005_c_() + ", " + this.villager.getNativeOccupationName(), "\u00a71", new GuiText.GuiButtonReference(this.villager.vtype)));
        text.add(new TextLine());
        if (this.villager.hiredBy != null) {
            text.add(new TextLine(LanguageUtilities.string("hire.hiredvillager", "" + Math.round((this.villager.hiredUntil - this.villager.field_70170_p.func_72820_D()) / 1000L), Keyboard.getKeyName((int)ClientProxy.KB_ESCORTS.func_151463_i()))));
        } else if (this.villager.getTownHall().getReputation(this.player) >= 4096) {
            text.add(new TextLine(LanguageUtilities.string("hire.hireablevillager")));
        } else {
            text.add(new TextLine(LanguageUtilities.string("hire.hireablevillagernoreputation")));
        }
        text.add(new TextLine());
        text.add(new TextLine(LanguageUtilities.string("hire.health") + ": " + (double)this.villager.func_110143_aJ() * 0.5 + "/" + (double)this.villager.func_110138_aP() * 0.5));
        text.add(new TextLine(LanguageUtilities.string("hire.strength") + ": " + this.villager.getAttackStrength()));
        text.add(new TextLine(LanguageUtilities.string("hire.cost") + ": " + MillCommonUtilities.getShortPrice(this.villager.getHireCost(this.player))));
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
        this.refreshContent();
    }

    private void refreshContent() {
        this.textBook = this.getData();
        this.buttonPagination();
    }
}

