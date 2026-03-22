package org.dizzymii.millenaire2.network.handler;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.item.TradeGood;
import org.dizzymii.millenaire2.network.MillPacketIds;
import org.dizzymii.millenaire2.network.PacketDataHelper;
import org.dizzymii.millenaire2.util.MillCommonUtilities;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.world.MillWorldData;
import org.dizzymii.millenaire2.world.UserProfile;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Handles trade buy/sell GUI actions from the client.
 */
public final class TradePacketHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    private TradePacketHandler() {}

    public static void handle(int actionId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;

        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            int entityId = r.readInt();
            int goodIndex = r.readInt();

            Entity entity = player.level().getEntity(entityId);
            if (!(entity instanceof MillVillager villager)) {
                LOGGER.warn("Trade: villager entity " + entityId + " not found");
                return;
            }

            Building building = villager.getHomeBuilding();
            if (building == null) building = villager.getTownHallBuilding();
            if (building == null) {
                LOGGER.warn("Trade: no building for villager " + villager.getFirstName());
                return;
            }

            List<TradeGood> goods = building.getTradeGoods();
            if (goodIndex < 0 || goodIndex >= goods.size()) {
                LOGGER.warn("Trade: invalid good index " + goodIndex);
                return;
            }

            TradeGood good = goods.get(goodIndex);

            MillWorldData mw = MillWorldData.get(player.serverLevel());
            if (mw == null) return;
            UserProfile profile = mw.getOrCreateProfile(player.getUUID(), player.getName().getString());

            Point villagePos = building.getTownHallPos() != null ? building.getTownHallPos() : building.getPos();
            int reputation = villagePos != null ? profile.getVillageReputation(villagePos) : 0;

            if (actionId == MillPacketIds.GUIACTION_TRADE_BUY) {
                executeBuy(player, profile, good, reputation, villagePos, mw);
            } else {
                executeSell(player, profile, good, reputation, villagePos, mw);
            }
        } catch (Exception e) {
            LOGGER.error("Error handling trade action", e);
        } finally {
            r.release();
        }
    }

    private static void executeBuy(ServerPlayer player, UserProfile profile, TradeGood good,
                                    int reputation, @Nullable Point villagePos, MillWorldData mw) {
        int price = good.getAdjustedBuyPrice(reputation);
        if (price <= 0 || good.item.isEmpty()) return;

        if (profile.deniers < price) {
            player.sendSystemMessage(MillCommonUtilities.chatError("Not enough deniers! Need " + price + ", have " + profile.deniers));
            return;
        }

        if (!player.getInventory().add(good.item.copy())) {
            player.sendSystemMessage(MillCommonUtilities.chatError("Inventory full!"));
            return;
        }

        profile.deniers -= price;
        if (villagePos != null) {
            profile.adjustVillageReputation(villagePos, 1);
        }
        mw.setDirty();

        player.sendSystemMessage(MillCommonUtilities.chatMsg("Bought " + good.item.getHoverName().getString() + " for " + price + " deniers"));
        LOGGER.debug("Trade buy: " + player.getName().getString()
                + " bought " + good.item.getHoverName().getString() + " for " + price + "d");
    }

    private static void executeSell(ServerPlayer player, UserProfile profile, TradeGood good,
                                     int reputation, @Nullable Point villagePos, MillWorldData mw) {
        int price = good.getAdjustedSellPrice(reputation);
        if (price <= 0 || good.item.isEmpty()) return;

        int slot = findItemSlot(player, good.item);
        if (slot < 0) {
            player.sendSystemMessage(MillCommonUtilities.chatError("You don't have that item!"));
            return;
        }

        ItemStack slotStack = player.getInventory().getItem(slot);
        slotStack.shrink(1);
        if (slotStack.isEmpty()) {
            player.getInventory().setItem(slot, ItemStack.EMPTY);
        }

        profile.deniers += price;
        if (villagePos != null) {
            profile.adjustVillageReputation(villagePos, 1);
        }
        mw.setDirty();

        player.sendSystemMessage(MillCommonUtilities.chatMsg("Sold " + good.item.getHoverName().getString() + " for " + price + " deniers"));
        LOGGER.debug("Trade sell: " + player.getName().getString()
                + " sold " + good.item.getHoverName().getString() + " for " + price + "d");
    }

    private static int findItemSlot(ServerPlayer player, ItemStack target) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (ItemStack.isSameItemSameComponents(stack, target) && !stack.isEmpty()) {
                return i;
            }
        }
        return -1;
    }
}
