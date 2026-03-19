package org.dizzymii.millenaire2.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.network.payloads.MillGenericS2CPayload;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Client-side handler for server-to-client packets.
 * Dispatches incoming generic payloads to the appropriate handler based on packet type.
 */
public final class ClientPacketHandler {

    // Client-side cache of village list entries (for GUI display)
    public static final List<VillageListClientEntry> villageListCache = new ArrayList<>();

    // Client-side cache of trade data (populated by PACKET_SHOP before GUI opens)
    public static final List<TradeGoodClientEntry> tradeGoodsCache = new ArrayList<>();
    public static int cachedDeniers = 0;
    public static int cachedReputation = 0;
    public static String cachedVillagerName = "";
    public static int cachedVillagerEntityId = -1;

    // Client-side cache of quest data (populated by PACKET_QUESTINSTANCE)
    @Nullable public static QuestClientEntry cachedQuest = null;
    public static int cachedQuestVillagerEntityId = -1;

    private ClientPacketHandler() {}

    public static void handleGenericS2C(MillGenericS2CPayload payload) {
        int type = payload.packetType();
        int subType = payload.subType();

        switch (type) {
            case MillPacketIds.PACKET_BUILDING:
                handleBuildingSync(payload.data());
                break;
            case MillPacketIds.PACKET_VILLAGER:
                handleVillagerSync(payload.data());
                break;
            case MillPacketIds.PACKET_MILLCHEST:
                handleLockedChest(payload.data());
                break;
            case MillPacketIds.PACKET_MAPINFO:
                handleMapInfo(payload.data());
                break;
            case MillPacketIds.PACKET_VILLAGELIST:
                handleVillageList(payload.data());
                break;
            case MillPacketIds.PACKET_SHOP:
                handleShopData(payload.data());
                break;
            case MillPacketIds.PACKET_OPENGUI:
                handleOpenGui(payload.data());
                break;
            case MillPacketIds.PACKET_TRANSLATED_CHAT:
                handleTranslatedChat(payload.data());
                break;
            case MillPacketIds.PACKET_PROFILE:
                handleProfile(subType, payload.data());
                break;
            case MillPacketIds.PACKET_QUESTINSTANCE:
                handleQuestInstance(payload.data());
                break;
            case MillPacketIds.PACKET_VILLAGER_SENTENCE:
                handleVillagerSentence(payload.data());
                break;
            default:
                MillLog.warn("ClientPacketHandler", "Unknown S2C packet type: " + type + "/" + subType);
                break;
        }
    }

    // ========== Villager sync ==========

    private static void handleVillagerSync(byte[] data) {
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            int entityId = r.readInt();
            long villagerId = r.readLong();
            String firstName = r.readString();
            String familyName = r.readString();
            int gender = r.readInt();
            String cultureKey = r.readString();
            String vtypeKey = r.readString();
            String goalKey = r.readString();
            boolean isRaider = r.readBoolean();
            boolean aggressiveStance = r.readBoolean();
            float posX = r.readFloat();
            float posY = r.readFloat();
            float posZ = r.readFloat();
            boolean usingBow = r.readBoolean();
            boolean usingHandToHand = r.readBoolean();
            String speechKey = r.readString();
            int speechVariant = r.readInt();
            long speechStarted = r.readLong();
            Point housePoint = readOptionalPoint(r);
            Point townHallPoint = readOptionalPoint(r);

            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;

            Entity entity = mc.level.getEntity(entityId);
            if (entity instanceof MillVillager villager) {
                villager.setVillagerId(villagerId);
                if (firstName != null) villager.setFirstName(firstName);
                if (familyName != null) villager.setFamilyName(familyName);
                villager.setGender(gender);
                if (cultureKey != null) villager.setCultureKey(cultureKey);
                if (vtypeKey != null) villager.setVillagerTypeKey(vtypeKey);
                villager.goalKey = goalKey;
                villager.isRaider = isRaider;
                villager.aggressiveStance = aggressiveStance;
                villager.isUsingBow = usingBow;
                villager.isUsingHandToHand = usingHandToHand;
                villager.speech_key = speechKey;
                villager.speech_variant = speechVariant;
                villager.speech_started = speechStarted;
                villager.housePoint = housePoint;
                villager.townHallPoint = townHallPoint;
            }
        } catch (Exception e) {
            MillLog.error("ClientPacketHandler", "Error handling villager sync", e);
        } finally {
            r.release();
        }
    }

    // ========== Villager speech bubble ==========

    private static void handleVillagerSentence(byte[] data) {
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            int entityId = r.readInt();
            String speechKey = r.readString();
            int variant = r.readInt();
            String cultureKey = r.readString();

            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;

            Entity entity = mc.level.getEntity(entityId);
            if (entity instanceof MillVillager villager) {
                villager.speech_key = speechKey;
                villager.speech_variant = variant;
                villager.speech_started = mc.level.getGameTime();
            }
        } catch (Exception e) {
            MillLog.error("ClientPacketHandler", "Error handling villager sentence", e);
        } finally {
            r.release();
        }
    }

    // ========== Translated chat ==========

    private static void handleTranslatedChat(byte[] data) {
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            String translationKey = r.readString();
            String cultureKey = r.readString();
            int argCount = r.readInt();
            String[] args = new String[argCount];
            for (int i = 0; i < argCount; i++) {
                args[i] = r.readString();
            }

            // Display the message — for now, use the translation key directly
            // Full culture-aware translation will use LanguageUtilities
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && translationKey != null) {
                StringBuilder msg = new StringBuilder(translationKey);
                for (String arg : args) {
                    if (arg != null) msg.append(" ").append(arg);
                }
                mc.player.displayClientMessage(Component.literal(msg.toString()), false);
            }
        } catch (Exception e) {
            MillLog.error("ClientPacketHandler", "Error handling translated chat", e);
        } finally {
            r.release();
        }
    }

    // ========== Village list ==========

    private static void handleVillageList(byte[] data) {
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            villageListCache.clear();
            int count = r.readInt();
            for (int i = 0; i < count; i++) {
                Point pos = readOptionalPoint(r);
                String cultureKey = r.readString();
                String name = r.readString();
                int distance = r.readInt();
                boolean isLone = r.readBoolean();
                villageListCache.add(new VillageListClientEntry(pos, cultureKey, name, distance, isLone));
            }
            MillLog.minor("ClientPacketHandler", "Received village list with " + count + " entries.");
        } catch (Exception e) {
            MillLog.error("ClientPacketHandler", "Error handling village list", e);
        } finally {
            r.release();
        }
    }

    // ========== Profile sync ==========

    private static void handleProfile(int updateType, byte[] data) {
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            int readUpdateType = r.readInt();
            // Apply reputation/language data to client-side profile cache
            MillLog.minor("ClientPacketHandler", "Received profile update type: " + readUpdateType);
            // Profile data is logged; full client-side caching will use a mirror of UserProfile
        } catch (Exception e) {
            MillLog.error("ClientPacketHandler", "Error handling profile sync", e);
        } finally {
            r.release();
        }
    }

    // ========== Open GUI ==========

    private static void handleOpenGui(byte[] data) {
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            int guiId = r.readInt();
            int entityId = r.readInt();
            Point villagePos = readOptionalPoint(r);

            MillLog.minor("ClientPacketHandler", "Open GUI request: " + guiId + " entity=" + entityId);
            org.dizzymii.millenaire2.client.ClientGuiHandler.openGui(guiId);
        } catch (Exception e) {
            MillLog.error("ClientPacketHandler", "Error handling open GUI", e);
        } finally {
            r.release();
        }
    }

    // ========== Building sync ==========

    private static void handleBuildingSync(byte[] data) {
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            // Decode in the same order as ServerPacketSender.buildBuildingSyncPayload
            Point pos = null;
            if (r.readBoolean()) {
                pos = new Point(r.readInt(), r.readInt(), r.readInt());
            }
            String cultureKey = r.readString();
            String name = r.readString();
            String planSetKey = r.readString();
            int level = r.readInt();
            boolean isTownhall = r.readBoolean();
            boolean isInn = r.readBoolean();
            boolean isMarket = r.readBoolean();
            boolean underConstruction = r.readBoolean();
            boolean underAttack = r.readBoolean();
            int population = r.readInt();

            Point thPos = null;
            if (r.readBoolean()) {
                thPos = new Point(r.readInt(), r.readInt(), r.readInt());
            }

            // Update client-side building cache
            if (pos != null) {
                ClientBuildingEntry entry = new ClientBuildingEntry(
                        pos, cultureKey, name, planSetKey, level,
                        isTownhall, isInn, isMarket,
                        underConstruction, underAttack, population, thPos);
                clientBuildings.put(pos, entry);
            }

            MillLog.minor("ClientPacketHandler", "Building sync: " + name + " at " + pos
                    + " culture=" + cultureKey + " townhall=" + isTownhall + " pop=" + population);
        } catch (Exception e) {
            MillLog.error("ClientPacketHandler", "Error handling building sync", e);
        } finally {
            r.release();
        }
    }

    // ========== Client building cache ==========

    private static final java.util.concurrent.ConcurrentHashMap<Point, ClientBuildingEntry> clientBuildings =
            new java.util.concurrent.ConcurrentHashMap<>();

    public static ClientBuildingEntry getClientBuilding(Point pos) {
        return clientBuildings.get(pos);
    }

    public static java.util.Collection<ClientBuildingEntry> getAllClientBuildings() {
        return clientBuildings.values();
    }

    public static void clearClientBuildings() {
        clientBuildings.clear();
    }

    public record ClientBuildingEntry(
            Point pos, String cultureKey, String name, String planSetKey, int level,
            boolean isTownhall, boolean isInn, boolean isMarket,
            boolean underConstruction, boolean underAttack, int population,
            @javax.annotation.Nullable Point townHallPos
    ) {}

    // ========== Locked chest ==========

    private static void handleLockedChest(byte[] data) {
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            int chestEntityId = r.readInt();
            MillLog.minor("ClientPacketHandler", "Locked chest data for entity: " + chestEntityId);
            // Chest contents will be displayed in the locked chest GUI screen
        } catch (Exception e) {
            MillLog.error("ClientPacketHandler", "Error handling locked chest", e);
        } finally {
            r.release();
        }
    }

    // ========== Map info ==========

    private static void handleMapInfo(byte[] data) {
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            int villageCount = r.readInt();
            MillLog.minor("ClientPacketHandler", "Map info received: " + villageCount + " villages");
            // Village markers for minimap overlay will be cached here
        } catch (Exception e) {
            MillLog.error("ClientPacketHandler", "Error handling map info", e);
        } finally {
            r.release();
        }
    }

    // ========== Quest instance ==========

    private static void handleQuestInstance(byte[] data) {
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            String questKey = r.readString();
            int stepIndex = r.readInt();
            int totalSteps = r.readInt();
            String stepDescription = r.readString();
            String stepLabel = r.readString();
            int rewardMoney = r.readInt();
            int rewardRep = r.readInt();
            int villagerEntityId = r.readInt();
            boolean isOffer = r.readBoolean();

            cachedQuest = new QuestClientEntry(questKey, stepIndex, totalSteps,
                    stepDescription, stepLabel, rewardMoney, rewardRep, isOffer);
            cachedQuestVillagerEntityId = villagerEntityId;

            MillLog.minor("ClientPacketHandler", "Quest sync: " + questKey
                    + " step=" + stepIndex + "/" + totalSteps + " offer=" + isOffer);
        } catch (Exception e) {
            MillLog.error("ClientPacketHandler", "Error handling quest instance", e);
        } finally {
            r.release();
        }
    }

    // ========== Shop / trade data ==========

    private static void handleShopData(byte[] data) {
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            cachedVillagerEntityId = r.readInt();
            cachedVillagerName = r.readString();
            cachedDeniers = r.readInt();
            cachedReputation = r.readInt();
            int count = r.readInt();
            tradeGoodsCache.clear();
            for (int i = 0; i < count; i++) {
                String itemId = r.readString();
                int itemCount = r.readInt();
                int buyPrice = r.readInt();
                int sellPrice = r.readInt();
                int adjBuy = r.readInt();
                int adjSell = r.readInt();
                tradeGoodsCache.add(new TradeGoodClientEntry(i, itemId, itemCount, buyPrice, sellPrice, adjBuy, adjSell));
            }
            MillLog.minor("ClientPacketHandler", "Received trade data: " + count + " goods, " + cachedDeniers + " deniers");
        } catch (Exception e) {
            MillLog.error("ClientPacketHandler", "Error handling shop data", e);
        } finally {
            r.release();
        }
    }

    // ========== Helpers ==========

    @Nullable
    private static Point readOptionalPoint(PacketDataHelper.Reader r) {
        if (r.readBoolean()) {
            int x = r.readInt();
            int y = r.readInt();
            int z = r.readInt();
            return new Point(x, y, z);
        }
        return null;
    }

    // ========== Client data classes ==========

    public static class TradeGoodClientEntry {
        public final int index;
        public final String itemId;
        public final int itemCount;
        public final int buyPrice;
        public final int sellPrice;
        public final int adjustedBuy;
        public final int adjustedSell;

        public TradeGoodClientEntry(int index, String itemId, int itemCount,
                                     int buyPrice, int sellPrice, int adjBuy, int adjSell) {
            this.index = index;
            this.itemId = itemId;
            this.itemCount = itemCount;
            this.buyPrice = buyPrice;
            this.sellPrice = sellPrice;
            this.adjustedBuy = adjBuy;
            this.adjustedSell = adjSell;
        }
    }

    public static class QuestClientEntry {
        public final String questKey;
        public final int stepIndex;
        public final int totalSteps;
        public final String stepDescription;
        public final String stepLabel;
        public final int rewardMoney;
        public final int rewardReputation;
        public final boolean isOffer;

        public QuestClientEntry(String questKey, int stepIndex, int totalSteps,
                                 String stepDescription, String stepLabel,
                                 int rewardMoney, int rewardReputation, boolean isOffer) {
            this.questKey = questKey;
            this.stepIndex = stepIndex;
            this.totalSteps = totalSteps;
            this.stepDescription = stepDescription;
            this.stepLabel = stepLabel;
            this.rewardMoney = rewardMoney;
            this.rewardReputation = rewardReputation;
            this.isOffer = isOffer;
        }
    }

    public static class VillageListClientEntry {
        @Nullable public final Point pos;
        @Nullable public final String cultureKey;
        @Nullable public final String name;
        public final int distance;
        public final boolean isLoneBuilding;

        public VillageListClientEntry(@Nullable Point pos, @Nullable String cultureKey,
                                      @Nullable String name, int distance, boolean isLoneBuilding) {
            this.pos = pos;
            this.cultureKey = cultureKey;
            this.name = name;
            this.distance = distance;
            this.isLoneBuilding = isLoneBuilding;
        }
    }
}
