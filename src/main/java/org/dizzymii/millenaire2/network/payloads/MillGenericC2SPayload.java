package org.dizzymii.millenaire2.network.payloads;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;

/**
 * Generic client-to-server payload carrying a packet type ID and raw byte data.
 * Mirrors the original mod's single-channel approach where all C2S packets
 * were distinguished by an integer type field.
 *
 * Individual packet types will be split into dedicated payloads in later phases
 * as their handlers are implemented. This generic payload allows incremental porting.
 */
public record MillGenericC2SPayload(int packetType, int subType, byte[] data) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MillGenericC2SPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "c2s_generic"));

    public static final StreamCodec<ByteBuf, MillGenericC2SPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, MillGenericC2SPayload::packetType,
            ByteBufCodecs.VAR_INT, MillGenericC2SPayload::subType,
            ByteBufCodecs.BYTE_ARRAY, MillGenericC2SPayload::data,
            MillGenericC2SPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
