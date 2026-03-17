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
            // Read building identification
            Point pos = readOptionalPoint(r);
            String name = r.hasRemaining() ? r.readString() : "";
            String cultureKey = r.hasRemaining() ? r.readString() : "";
            boolean isTownhall = r.hasRemaining() && r.readBoolean();
            MillLog.minor("ClientPacketHandler", "Building sync: " + name + " at " + pos
                    + " culture=" + cultureKey + " townhall=" + isTownhall);
            // Client-side building cache will be updated when client village tracking is implemented
        } catch (Exception e) {
            MillLog.error("ClientPacketHandler", "Error handling building sync", e);
        } finally {
            r.release();
        }
    }

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
            String questId = r.readString();
            int stepIndex = r.hasRemaining() ? r.readInt() : 0;
            MillLog.minor("ClientPacketHandler", "Quest instance sync: " + questId + " step=" + stepIndex);
            // Quest GUI will display current quest state from this data
        } catch (Exception e) {
            MillLog.error("ClientPacketHandler", "Error handling quest instance", e);
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
