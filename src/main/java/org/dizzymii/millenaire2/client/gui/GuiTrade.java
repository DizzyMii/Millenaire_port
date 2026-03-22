package org.dizzymii.millenaire2.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.dizzymii.millenaire2.network.ClientPacketHandler;
import org.dizzymii.millenaire2.network.ClientPacketSender;
import org.dizzymii.millenaire2.network.MillPacketIds;
import org.dizzymii.millenaire2.network.PacketDataHelper;
import org.dizzymii.millenaire2.network.handler.ClientTradePacketHandler;

import java.util.List;

/**
 * GUI screen for trading with villagers.
 * Displays buy/sell items, prices, and the player's reputation/currency.
 * Ported from org.millenaire.client.gui.GuiTrade (Forge 1.12.2).
 */
public class GuiTrade extends Screen {

    private static final int BG_WIDTH = 220;
    private static final int BG_HEIGHT = 200;
    private static final int MAX_VISIBLE = 6;

    private int guiLeft;
    private int guiTop;
    private int selectedIndex = -1;
    private int scrollOffset = 0;

    public GuiTrade() {
        super(Component.literal("Trade"));
    }

    @Override
    protected void init() {
        super.init();
        guiLeft = (width - BG_WIDTH) / 2;
        guiTop = (height - BG_HEIGHT) / 2;

        addRenderableWidget(Button.builder(Component.literal("Buy"), btn -> executeBuy())
                .bounds(guiLeft + 8, guiTop + BG_HEIGHT - 28, 50, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Sell"), btn -> executeSell())
                .bounds(guiLeft + 62, guiTop + BG_HEIGHT - 28, 50, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Close"), btn -> onClose())
                .bounds(guiLeft + BG_WIDTH - 58, guiTop + BG_HEIGHT - 28, 50, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);

        // Background
        graphics.fill(guiLeft, guiTop, guiLeft + BG_WIDTH, guiTop + BG_HEIGHT, 0xFFC6A96C);
        graphics.fill(guiLeft + 2, guiTop + 2, guiLeft + BG_WIDTH - 2, guiTop + BG_HEIGHT - 2, 0xFFF5E6C8);

        String vName = ClientTradePacketHandler.cachedVillagerName;
        int deniers = ClientTradePacketHandler.cachedDeniers;
        int rep = ClientTradePacketHandler.cachedReputation;

        // Title
        graphics.drawString(font, "Trading with " + vName, guiLeft + 8, guiTop + 6, 0x3F2A14, false);

        // Deniers + reputation
        graphics.drawString(font, "Deniers: " + deniers + "  Rep: " + rep, guiLeft + 8, guiTop + 18, 0x3F2A14, false);

        // Trade goods list header
        graphics.drawString(font, "Item", guiLeft + 8, guiTop + 34, 0x1A0D00, false);
        graphics.drawString(font, "Buy", guiLeft + 130, guiTop + 34, 0x1A0D00, false);
        graphics.drawString(font, "Sell", guiLeft + 170, guiTop + 34, 0x1A0D00, false);

        // Separator
        graphics.fill(guiLeft + 6, guiTop + 44, guiLeft + BG_WIDTH - 6, guiTop + 45, 0xFF3F2A14);

        // Trade goods list
        List<ClientTradePacketHandler.TradeGoodClientEntry> goods = ClientTradePacketHandler.tradeGoodsCache;
        if (goods.isEmpty()) {
            graphics.drawString(font, "No goods available", guiLeft + 8, guiTop + 50, 0x666666, false);
        } else {
            int y = guiTop + 48;
            int end = Math.min(scrollOffset + MAX_VISIBLE, goods.size());
            for (int i = scrollOffset; i < end; i++) {
                ClientTradePacketHandler.TradeGoodClientEntry g = goods.get(i);
                int rowColor = (i == selectedIndex) ? 0x40FFD700 : 0x00000000;
                if (rowColor != 0) {
                    graphics.fill(guiLeft + 6, y, guiLeft + BG_WIDTH - 6, y + 14, rowColor);
                }

                // Item name (extract from resource ID)
                String displayName = g.itemId;
                int lastColon = displayName.lastIndexOf(':');
                if (lastColon >= 0) displayName = displayName.substring(lastColon + 1);
                if (displayName.length() > 18) displayName = displayName.substring(0, 18);

                graphics.drawString(font, displayName, guiLeft + 8, y + 2, 0x3F2A14, false);
                graphics.drawString(font, g.adjustedBuy > 0 ? String.valueOf(g.adjustedBuy) : "-",
                        guiLeft + 130, y + 2, 0x3F2A14, false);
                graphics.drawString(font, g.adjustedSell > 0 ? String.valueOf(g.adjustedSell) : "-",
                        guiLeft + 170, y + 2, 0x3F2A14, false);
                y += 16;
            }
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Check if click is in the trade goods list area
        List<ClientTradePacketHandler.TradeGoodClientEntry> goods = ClientTradePacketHandler.tradeGoodsCache;
        int listTop = guiTop + 48;
        int listLeft = guiLeft + 6;
        int listRight = guiLeft + BG_WIDTH - 6;
        if (mouseX >= listLeft && mouseX <= listRight && mouseY >= listTop) {
            int row = (int) ((mouseY - listTop) / 16);
            int index = scrollOffset + row;
            if (index >= 0 && index < goods.size()) {
                selectedIndex = index;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int maxScroll = Math.max(0, ClientTradePacketHandler.tradeGoodsCache.size() - MAX_VISIBLE);
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - (int) scrollY));
        return true;
    }

    private void executeBuy() {
        if (selectedIndex < 0 || selectedIndex >= ClientTradePacketHandler.tradeGoodsCache.size()) return;
        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeInt(ClientTradePacketHandler.cachedVillagerEntityId);
        w.writeInt(selectedIndex);
        ClientPacketSender.sendGuiAction(MillPacketIds.GUIACTION_TRADE_BUY, w);
    }

    private void executeSell() {
        if (selectedIndex < 0 || selectedIndex >= ClientTradePacketHandler.tradeGoodsCache.size()) return;
        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeInt(ClientTradePacketHandler.cachedVillagerEntityId);
        w.writeInt(selectedIndex);
        ClientPacketSender.sendGuiAction(MillPacketIds.GUIACTION_TRADE_SELL, w);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
