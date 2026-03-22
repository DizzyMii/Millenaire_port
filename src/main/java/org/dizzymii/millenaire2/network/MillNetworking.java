package org.dizzymii.millenaire2.network;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.dizzymii.millenaire2.network.payloads.*;

/**
 * Registers all networking payloads for the mod.
 * Called from the mod event bus via RegisterPayloadHandlersEvent.
 */
public final class MillNetworking {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final String PROTOCOL_VERSION = "2";

    private MillNetworking() {}

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);

        // ========== Server → Client ==========

        registrar.playToClient(
                VillagerSyncPayload.TYPE,
                VillagerSyncPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> ClientPacketHandler.handleVillagerSync(payload))
        );

        registrar.playToClient(
                VillagerSpeechPayload.TYPE,
                VillagerSpeechPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> ClientPacketHandler.handleVillagerSpeech(payload))
        );

        registrar.playToClient(
                BuildingSyncPayload.TYPE,
                BuildingSyncPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> ClientPacketHandler.handleBuildingSync(payload))
        );

        registrar.playToClient(
                LockedChestPayload.TYPE,
                LockedChestPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> ClientPacketHandler.handleLockedChest(payload))
        );

        registrar.playToClient(
                MapInfoPayload.TYPE,
                MapInfoPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> ClientPacketHandler.handleMapInfo(payload))
        );

        registrar.playToClient(
                VillageListPayload.TYPE,
                VillageListPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> ClientPacketHandler.handleVillageList(payload))
        );

        registrar.playToClient(
                TradeDataPayload.TYPE,
                TradeDataPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> ClientPacketHandler.handleTradeData(payload))
        );

        registrar.playToClient(
                TranslatedChatPayload.TYPE,
                TranslatedChatPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> ClientPacketHandler.handleTranslatedChat(payload))
        );

        registrar.playToClient(
                PlayerProfilePayload.TYPE,
                PlayerProfilePayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> ClientPacketHandler.handleProfile(payload))
        );

        registrar.playToClient(
                QuestInstancePayload.TYPE,
                QuestInstancePayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> ClientPacketHandler.handleQuestInstance(payload))
        );

        registrar.playToClient(
                OpenGuiPayload.TYPE,
                OpenGuiPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> ClientPacketHandler.handleOpenGui(payload))
        );

        // ========== Client → Server ==========

        registrar.playToServer(
                GuiActionPayload.TYPE,
                GuiActionPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> ServerPacketHandler.handleGuiAction(payload, context))
        );

        registrar.playToServer(
                VillagerInteractPayload.TYPE,
                VillagerInteractPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> ServerPacketHandler.handleVillagerInteract(payload, context))
        );

        registrar.playToServer(
                VillageListRequestPayload.TYPE,
                VillageListRequestPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> ServerPacketHandler.handleVillageListRequest(context))
        );

        registrar.playToServer(
                MapInfoRequestPayload.TYPE,
                MapInfoRequestPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> ServerPacketHandler.handleMapInfoRequest(context))
        );

        registrar.playToServer(
                DeclareReleasePayload.TYPE,
                DeclareReleasePayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> ServerPacketHandler.handleDeclareRelease(payload, context))
        );

        registrar.playToServer(
                AvailableContentPayload.TYPE,
                AvailableContentPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> ServerPacketHandler.handleAvailableContent(payload, context))
        );

        registrar.playToServer(
                DevCommandPayload.TYPE,
                DevCommandPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> ServerPacketHandler.handleDevCommand(payload, context))
        );

        LOGGER.info("Networking payloads registered (" + PROTOCOL_VERSION + ").");
    }
}
