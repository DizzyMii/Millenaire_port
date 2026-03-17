/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.toasts.GuiToast
 *  net.minecraft.client.gui.toasts.IToast
 *  net.minecraft.client.gui.toasts.IToast$Visibility
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.ItemStack
 */
package org.millenaire.client.gui;

import java.util.List;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.culture.VillagerType;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillLog;

public class UnlockingToast
implements IToast {
    private static final long SINGLE_ITEM_DURATION = 2000L;
    private static final long MULTI_ITEMS_DURATION = 1000L;
    private final BuildingPlanSet planSet;
    private final VillageType villageType;
    private final VillagerType villagerType;
    private final TradeGood tradeGood;
    private final List<TradeGood> tradeGoods;
    private final int nbUnlocked;
    private final int nbTotal;
    private long firstDrawTime;
    private boolean firstPassDone = false;

    public UnlockingToast(BuildingPlanSet planSet, int nbUnlocked, int nbTotal) {
        this.planSet = planSet;
        this.villageType = null;
        this.villagerType = null;
        this.tradeGood = null;
        this.tradeGoods = null;
        this.nbUnlocked = nbUnlocked;
        this.nbTotal = nbTotal;
    }

    public UnlockingToast(List<TradeGood> tradeGoods, int nbUnlocked, int nbTotal) {
        this.tradeGoods = tradeGoods;
        this.villageType = null;
        this.villagerType = null;
        this.tradeGood = null;
        this.planSet = null;
        this.nbUnlocked = nbUnlocked;
        this.nbTotal = nbTotal;
    }

    public UnlockingToast(TradeGood tradeGood, int nbUnlocked, int nbTotal) {
        this.tradeGood = tradeGood;
        this.villageType = null;
        this.villagerType = null;
        this.planSet = null;
        this.tradeGoods = null;
        this.nbUnlocked = nbUnlocked;
        this.nbTotal = nbTotal;
    }

    public UnlockingToast(VillagerType villagerType, int nbUnlocked, int nbTotal) {
        this.villagerType = villagerType;
        this.villageType = null;
        this.planSet = null;
        this.tradeGood = null;
        this.tradeGoods = null;
        this.nbUnlocked = nbUnlocked;
        this.nbTotal = nbTotal;
    }

    public UnlockingToast(VillageType villageType, int nbUnlocked, int nbTotal) {
        this.villageType = villageType;
        this.planSet = null;
        this.villagerType = null;
        this.tradeGood = null;
        this.tradeGoods = null;
        this.nbUnlocked = nbUnlocked;
        this.nbTotal = nbTotal;
    }

    public IToast.Visibility func_193653_a(GuiToast toastGui, long delta) {
        if (!this.firstPassDone) {
            this.firstDrawTime = delta;
            this.firstPassDone = true;
        }
        String title = null;
        String text = null;
        ItemStack icon = null;
        try {
            int pos;
            if (this.planSet != null) {
                title = this.planSet.getNameNative();
                text = LanguageUtilities.string("travelbook.unlockedbuilding", this.planSet.culture.getAdjectiveTranslated(), "" + this.nbUnlocked, "" + this.nbTotal);
                icon = this.planSet.getIcon();
            } else if (this.villageType != null) {
                title = this.villageType.name;
                text = LanguageUtilities.string("travelbook.unlockedvillage", this.villageType.culture.getAdjectiveTranslated(), "" + this.nbUnlocked, "" + this.nbTotal);
                icon = this.villageType.getIcon();
            } else if (this.villagerType != null) {
                title = this.villagerType.name;
                text = LanguageUtilities.string("travelbook.unlockedvillager", this.villagerType.culture.getAdjectiveTranslated(), "" + this.nbUnlocked, "" + this.nbTotal);
                icon = this.villagerType.getIcon();
            } else if (this.tradeGood != null) {
                title = this.tradeGood.getName();
                text = LanguageUtilities.string("travelbook.unlockedtradegood", this.tradeGood.culture.getAdjectiveTranslated(), "" + this.nbUnlocked, "" + this.nbTotal);
                icon = this.tradeGood.getIcon();
            } else if (this.tradeGoods != null && (pos = (int)((delta - this.firstDrawTime) / 1000L)) >= 0 && pos < this.tradeGoods.size()) {
                TradeGood tradeGood = this.tradeGoods.get(pos);
                title = tradeGood.getName();
                text = LanguageUtilities.string("travelbook.unlockedtradegood", tradeGood.culture.getAdjectiveTranslated(), "" + this.nbUnlocked, "" + this.nbTotal);
                icon = tradeGood.getIcon();
            }
            toastGui.func_192989_b().func_110434_K().func_110577_a(field_193654_a);
            GlStateManager.func_179124_c((float)1.0f, (float)1.0f, (float)1.0f);
            toastGui.func_73729_b(0, 0, 0, 32, 160, 32);
            toastGui.func_192989_b().field_71466_p.func_78276_b(title, 30, 7, -11534256);
            toastGui.func_192989_b().field_71466_p.func_78276_b(text, 30, 18, -16777216);
            RenderHelper.func_74520_c();
            if (icon != null) {
                toastGui.func_192989_b().func_175599_af().func_184391_a((EntityLivingBase)null, icon, 8, 8);
            }
        }
        catch (Exception e) {
            MillLog.printException(this.toString(), e);
            return IToast.Visibility.HIDE;
        }
        return delta - this.firstDrawTime >= this.getDisplayDuration() ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
    }

    private long getDisplayDuration() {
        if (this.tradeGoods != null) {
            return 1000L * (long)this.tradeGoods.size();
        }
        return 2000L;
    }

    public String toString() {
        if (this.planSet != null) {
            return "Toast:" + this.planSet;
        }
        if (this.villageType != null) {
            return "Toast:" + this.villageType;
        }
        if (this.villagerType != null) {
            return "Toast:" + this.villagerType;
        }
        if (this.tradeGood != null) {
            return "Toast:" + this.tradeGood;
        }
        if (this.tradeGoods != null) {
            return "Toast:" + this.tradeGoods;
        }
        return "Toast:no data";
    }
}

