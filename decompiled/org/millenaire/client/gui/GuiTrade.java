/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.gui.GuiLabel
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.client.util.ITooltipFlag$TooltipFlags
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.ClickType
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
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.lwjgl.opengl.GL11;
import org.millenaire.client.gui.text.GuiText;
import org.millenaire.client.gui.text.GuiTravelBook;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.ui.ContainerTrade;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.village.Building;
import org.millenaire.common.world.UserProfile;

public class GuiTrade
extends GuiContainer {
    private static final int DONATION_BUTTON_Y = 122;
    private static final int DONATION_BUTTON_X = 8;
    private Building building;
    private MillVillager merchant;
    private final EntityPlayer player;
    private final UserProfile profile;
    private int sellingRow = 0;
    private int buyingRow = 0;
    private final ContainerTrade container;
    private final Method drawSlotInventory;
    private final Method drawItemStackInventory;
    ResourceLocation background = new ResourceLocation("millenaire", "textures/gui/trade.png");

    public GuiTrade(EntityPlayer player, Building building) {
        super((Container)new ContainerTrade(player, building));
        this.drawSlotInventory = MillCommonUtilities.getDrawSlotInventoryMethod(this);
        this.drawItemStackInventory = MillCommonUtilities.getDrawItemStackInventoryMethod(this);
        this.container = (ContainerTrade)this.field_147002_h;
        this.building = building;
        this.player = player;
        this.profile = building.mw.getProfile(player);
        this.field_147000_g = 222;
        this.field_146999_f = 248;
        this.updateRows(false, 0, 0);
        this.updateRows(true, 0, 0);
    }

    public GuiTrade(EntityPlayer player, MillVillager merchant) {
        super((Container)new ContainerTrade(player, merchant));
        this.drawSlotInventory = MillCommonUtilities.getDrawSlotInventoryMethod(this);
        this.drawItemStackInventory = MillCommonUtilities.getDrawItemStackInventoryMethod(this);
        this.container = (ContainerTrade)this.field_147002_h;
        this.merchant = merchant;
        this.player = player;
        this.profile = merchant.mw.getProfile(player);
        this.field_147000_g = 222;
        this.field_146999_f = 248;
        this.updateRows(false, 0, 0);
        this.updateRows(true, 0, 0);
    }

    protected void func_146284_a(GuiButton button) throws IOException {
        if (button instanceof GuiText.MillGuiButton) {
            Culture culture = this.building != null ? this.building.culture : this.merchant.getCulture();
            GuiTravelBook guiTravelBook = new GuiTravelBook((EntityPlayer)Minecraft.func_71410_x().field_71439_g);
            guiTravelBook.setCallingScreen((GuiScreen)this);
            guiTravelBook.jumpToDetails(culture, GuiText.GuiButtonReference.RefType.CULTURE, null, false);
            Minecraft.func_71410_x().func_147108_a((GuiScreen)guiTravelBook);
            return;
        }
        super.func_146284_a(button);
    }

    protected void func_146976_a(float f, int i, int j) {
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.field_146297_k.func_110434_K().func_110577_a(this.background);
        int x = (this.field_146294_l - this.field_146999_f) / 2;
        int y = (this.field_146295_m - this.field_147000_g) / 2;
        this.func_73729_b(x, y, 0, 0, this.field_146999_f, this.field_147000_g);
        if (this.sellingRow == 0) {
            this.func_73729_b(x + 216, y + 68, 5, 5, 11, 7);
        }
        if (this.buyingRow == 0) {
            this.func_73729_b(x + 216, y + 122, 5, 5, 11, 7);
        }
        if (this.sellingRow >= this.container.nbRowSelling - 2) {
            this.func_73729_b(x + 230, y + 68, 5, 5, 11, 7);
        }
        if (this.buyingRow >= this.container.nbRowBuying - 2) {
            this.func_73729_b(x + 230, y + 122, 5, 5, 11, 7);
        }
        if (!this.profile.donationActivated) {
            this.func_73729_b(x + 8, y + 122, 0, 238, 16, 16);
            this.func_73729_b(x + 8 + 16, y + 122, 16, 222, 16, 16);
        } else {
            this.func_73729_b(x + 8, y + 122, 0, 222, 16, 16);
            this.func_73729_b(x + 8 + 16, y + 122, 16, 238, 16, 16);
        }
    }

    protected void func_146979_b(int x, int y) {
        if (this.building != null) {
            this.field_146289_q.func_78276_b(this.building.getNativeBuildingName(), 8, 6, 0x404040);
            this.field_146289_q.func_78276_b(LanguageUtilities.string("ui.wesell") + ":", 8, 22, 0x404040);
            this.field_146289_q.func_78276_b(LanguageUtilities.string("ui.webuy") + ":", 8, 76, 0x404040);
        } else {
            this.field_146289_q.func_78276_b(this.merchant.func_70005_c_() + ": " + this.merchant.getNativeOccupationName(), 8, 6, 0x404040);
            this.field_146289_q.func_78276_b(LanguageUtilities.string("ui.isell") + ":", 8, 22, 0x404040);
        }
        this.field_146289_q.func_78276_b(LanguageUtilities.string("ui.inventory"), 44, this.field_147000_g - 96 + 2, 0x404040);
    }

    public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
        int i;
        this.func_146276_q_();
        this.func_146976_a(partialTicks, mouseX, mouseY);
        GlStateManager.func_179101_C();
        RenderHelper.func_74518_a();
        GlStateManager.func_179140_f();
        GlStateManager.func_179097_i();
        for (i = 0; i < this.field_146292_n.size(); ++i) {
            ((GuiButton)this.field_146292_n.get(i)).func_191745_a(this.field_146297_k, mouseX, mouseY, partialTicks);
        }
        for (int j = 0; j < this.field_146293_o.size(); ++j) {
            ((GuiLabel)this.field_146293_o.get(j)).func_146159_a(this.field_146297_k, mouseX, mouseY);
        }
        i = this.field_147003_i;
        int j = this.field_147009_r;
        RenderHelper.func_74520_c();
        GlStateManager.func_179094_E();
        GlStateManager.func_179109_b((float)i, (float)j, (float)0.0f);
        GlStateManager.func_179131_c((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.func_179091_B();
        Slot hoveredSlot = null;
        String currentProblemString = null;
        int k = 240;
        int l = 240;
        OpenGlHelper.func_77475_a((int)OpenGlHelper.field_77476_b, (float)240.0f, (float)240.0f);
        GlStateManager.func_179131_c((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        for (int i1 = 0; i1 < this.field_147002_h.field_75151_b.size(); ++i1) {
            ContainerTrade.MerchantSlot tslot;
            int l1;
            int j1;
            Slot slot = (Slot)this.field_147002_h.field_75151_b.get(i1);
            if (slot.func_111238_b()) {
                try {
                    this.drawSlotInventory.invoke((Object)this, slot);
                }
                catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    MillLog.printException(e);
                }
            }
            String problem = null;
            if (slot instanceof ContainerTrade.TradeSlot) {
                ContainerTrade.TradeSlot tslot2 = (ContainerTrade.TradeSlot)slot;
                problem = tslot2.isProblem();
                if (problem != null) {
                    GlStateManager.func_179140_f();
                    GlStateManager.func_179097_i();
                    j1 = slot.field_75223_e;
                    l1 = slot.field_75221_f;
                    this.func_73733_a(j1, l1, j1 + 16, l1 + 16, Integer.MIN_VALUE, Integer.MIN_VALUE);
                    GlStateManager.func_179145_e();
                    GlStateManager.func_179126_j();
                }
            } else if (slot instanceof ContainerTrade.MerchantSlot && (problem = (tslot = (ContainerTrade.MerchantSlot)slot).isProblem()) != null) {
                GlStateManager.func_179140_f();
                GlStateManager.func_179097_i();
                j1 = slot.field_75223_e;
                l1 = slot.field_75221_f;
                this.func_73733_a(j1, l1, j1 + 16, l1 + 16, Integer.MIN_VALUE, Integer.MIN_VALUE);
                GlStateManager.func_179145_e();
                GlStateManager.func_179126_j();
            }
            if (!this.getIsMouseOverSlot(slot, mouseX, mouseY)) continue;
            hoveredSlot = slot;
            currentProblemString = problem;
            GlStateManager.func_179140_f();
            GlStateManager.func_179097_i();
            int j12 = slot.field_75223_e;
            int k1 = slot.field_75221_f;
            GlStateManager.func_179135_a((boolean)true, (boolean)true, (boolean)true, (boolean)false);
            this.func_73733_a(j12, k1, j12 + 16, k1 + 16, -2130706433, -2130706433);
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
                this.drawItemStackInventory.invoke((Object)this, inventoryplayer.func_70445_o(), i - 240 - 8, j - 240 - 8, currentProblemString);
            }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                MillLog.printException(e);
            }
        }
        GlStateManager.func_179121_F();
        GlStateManager.func_179145_e();
        GlStateManager.func_179126_j();
        RenderHelper.func_74519_b();
        if (inventoryplayer.func_70445_o().func_77973_b() == Items.field_190931_a && hoveredSlot != null && hoveredSlot.func_75216_d()) {
            Slot tslot;
            if (hoveredSlot instanceof ContainerTrade.TradeSlot) {
                tslot = (ContainerTrade.TradeSlot)hoveredSlot;
                int price = 0;
                String priceText = null;
                int priceColour = 0;
                price = tslot.sellingSlot ? tslot.good.getCalculatedSellingPrice(this.building, this.player) : tslot.good.getCalculatedBuyingPrice(this.building, this.player);
                priceText = MillCommonUtilities.getShortPrice(price);
                priceColour = MillCommonUtilities.getPriceColourMC(price);
                ItemStack itemstack = hoveredSlot.func_75211_c();
                try {
                    List list = itemstack.func_82840_a((EntityPlayer)this.field_146297_k.field_71439_g, (ITooltipFlag)(this.field_146297_k.field_71474_y.field_82882_x ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL));
                    if (((ContainerTrade.TradeSlot)hoveredSlot).sellingSlot || !this.profile.donationActivated) {
                        list.add("\u00a7" + Integer.toHexString(priceColour) + priceText);
                        list.add(LanguageUtilities.string("ui.repgain", "" + price));
                    } else {
                        list.add("\u00a76" + LanguageUtilities.string("ui.donatinggoods"));
                        list.add(LanguageUtilities.string("ui.repgain", "" + price * 4));
                    }
                    if (currentProblemString != null) {
                        list.add("\u00a74" + currentProblemString);
                    }
                    this.renderToolTipCustom(itemstack, mouseX, mouseY, list);
                }
                catch (Exception e) {
                    MillLog.printException("Exception when rendering tooltip for stack: " + itemstack, e);
                }
            } else if (hoveredSlot instanceof ContainerTrade.MerchantSlot) {
                tslot = (ContainerTrade.MerchantSlot)hoveredSlot;
                String price = MillCommonUtilities.getShortPrice(tslot.good.getCalculatedSellingPrice(this.merchant));
                int priceColour = MillCommonUtilities.getPriceColourMC(tslot.good.getCalculatedSellingPrice(this.merchant));
                ItemStack itemstack = hoveredSlot.func_75211_c();
                List list = itemstack.func_82840_a((EntityPlayer)this.field_146297_k.field_71439_g, (ITooltipFlag)(this.field_146297_k.field_71474_y.field_82882_x ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL));
                list.add("\u00a7" + Integer.toHexString(priceColour) + price);
                if (currentProblemString != null) {
                    list.add("\u00a74" + currentProblemString);
                }
                this.renderToolTipCustom(itemstack, mouseX, mouseY, list);
            } else {
                ItemStack itemstack = hoveredSlot.func_75211_c();
                this.func_146285_a(itemstack, mouseX, mouseY);
            }
        }
        int startx = (this.field_146294_l - this.field_146999_f) / 2;
        int starty = (this.field_146295_m - this.field_147000_g) / 2;
        int dx = mouseX - startx;
        int dy = mouseY - starty;
        if (dy >= 122 && dy <= 138) {
            if (dx >= 8 && dx <= 24) {
                String toolTip = LanguageUtilities.string("ui.trade_buying");
                int stringlength = this.field_146289_q.func_78256_a(toolTip);
                this.func_73733_a(mouseX + 5, mouseY - 3, mouseX + stringlength + 10, mouseY + 8 + 3, -1073741824, -1073741824);
                this.field_146289_q.func_78276_b(toolTip, mouseX + 8, mouseY, 0xFFFFFF);
            } else if (dx >= 24 && dx <= 40) {
                String toolTip = LanguageUtilities.string("ui.trade_donation");
                int stringlength = this.field_146289_q.func_78256_a(toolTip);
                this.func_73733_a(mouseX + 5, mouseY - 3, mouseX + stringlength + 10, mouseY + 8 + 3, -1073741824, -1073741824);
                this.field_146289_q.func_78276_b(toolTip, mouseX + 8, mouseY, 0xFFFFFF);
            }
        }
        this.func_191948_b(mouseX, mouseY);
    }

    private boolean getIsMouseOverSlot(Slot slot, int i, int j) {
        int k = (this.field_146294_l - this.field_146999_f) / 2;
        int l = (this.field_146295_m - this.field_147000_g) / 2;
        return (i -= k) >= slot.field_75223_e - 1 && i < slot.field_75223_e + 16 + 1 && (j -= l) >= slot.field_75221_f - 1 && j < slot.field_75221_f + 16 + 1;
    }

    protected void func_184098_a(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        if (slotIn != null && !(slotIn instanceof ContainerTrade.TradeSlot) && !(slotIn instanceof ContainerTrade.MerchantSlot)) {
            return;
        }
        if (slotIn == null || slotIn.func_75211_c() == null) {
            return;
        }
        super.func_184098_a(slotIn, slotId, mouseButton, type);
    }

    public void func_73866_w_() {
        super.func_73866_w_();
        if (this.building != null) {
            int xStart = (this.field_146294_l - this.field_146999_f) / 2;
            int yStart = (this.field_146295_m - this.field_147000_g) / 2;
            this.field_146292_n.add(new GuiText.MillGuiButton(0, xStart + this.field_146999_f - 21, yStart + 5, 15, 20, "?"));
        }
    }

    protected void func_73864_a(int x, int y, int clickType) throws IOException {
        if (clickType == 0) {
            int startx = (this.field_146294_l - this.field_146999_f) / 2;
            int starty = (this.field_146295_m - this.field_147000_g) / 2;
            int dx = x - startx;
            int dy = y - starty;
            if (dy >= 68 && dy <= 74) {
                if (dx >= 216 && dx <= 226) {
                    if (this.sellingRow > 0) {
                        --this.sellingRow;
                        this.updateRows(true, 1, this.sellingRow);
                    }
                } else if (dx >= 230 && dx <= 240 && this.sellingRow < this.container.nbRowSelling - 2) {
                    ++this.sellingRow;
                    this.updateRows(true, -1, this.sellingRow);
                }
            } else if (dy >= 122 && dy <= 127) {
                if (dx >= 216 && dx <= 226) {
                    if (this.buyingRow > 0) {
                        --this.buyingRow;
                        this.updateRows(false, 1, this.buyingRow);
                    }
                } else if (dx >= 230 && dx <= 240 && this.buyingRow < this.container.nbRowBuying - 2) {
                    ++this.buyingRow;
                    this.updateRows(false, -1, this.buyingRow);
                }
            }
            if (dy >= 122 && dy <= 138) {
                if (dx >= 8 && dx <= 24) {
                    if (this.profile.donationActivated) {
                        this.profile.donationActivated = false;
                        ClientSender.playerToggleDonation(this.player, this.profile.donationActivated);
                    }
                } else if (dx >= 24 && dx <= 40 && !this.profile.donationActivated) {
                    this.profile.donationActivated = true;
                    ClientSender.playerToggleDonation(this.player, this.profile.donationActivated);
                }
            }
        }
        super.func_73864_a(x, y, clickType);
    }

    protected void renderToolTipCustom(ItemStack stack, int x, int y, List<String> customToolTip) {
        FontRenderer font = stack.func_77973_b().getFontRenderer(stack);
        GuiUtils.preItemToolTip((ItemStack)stack);
        this.drawHoveringText(customToolTip, x, y, font == null ? this.field_146289_q : font);
        GuiUtils.postItemToolTip();
    }

    private void updateRows(boolean selling, int change, int row) {
        int pos = 0;
        for (Object o : this.container.field_75151_b) {
            Slot slot = (Slot)o;
            if (slot instanceof ContainerTrade.TradeSlot) {
                ContainerTrade.TradeSlot tradeSlot = (ContainerTrade.TradeSlot)slot;
                if (tradeSlot.sellingSlot != selling) continue;
                tradeSlot.field_75221_f += 18 * change;
                if (pos / 13 < row || pos / 13 > row + 1) {
                    if (tradeSlot.field_75223_e > 0) {
                        tradeSlot.field_75223_e -= 1000;
                    }
                } else if (tradeSlot.field_75223_e < 0) {
                    tradeSlot.field_75223_e += 1000;
                }
                ++pos;
                continue;
            }
            if (!(slot instanceof ContainerTrade.MerchantSlot) || !selling) continue;
            ContainerTrade.MerchantSlot merchantSlot = (ContainerTrade.MerchantSlot)slot;
            merchantSlot.field_75221_f += 18 * change;
            if (pos / 13 < row || pos / 13 > row + 1) {
                if (merchantSlot.field_75223_e > 0) {
                    merchantSlot.field_75223_e -= 1000;
                }
            } else if (merchantSlot.field_75223_e < 0) {
                merchantSlot.field_75223_e += 1000;
            }
            ++pos;
        }
    }
}

