package org.dizzymii.millenaire2.network.payloads;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;

/**
 * Generic server-to-client payload carrying a packet type ID and raw byte data.
 * Mirrors the original mod's single-channel approach where all S2C packets
 * were distinguished by an integer type field.
 *
 * Individual packet types will be split into dedicated payloads in later phases
 * as their handlers are implemented. This generic payload allows incremental porting.
 */
public record MillGenericS2CPayload(int packetType, int subType, byte[] data) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MillGenericS2CPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "s2c_generic"));

    public static final StreamCodec<ByteBuf, MillGenericS2CPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, MillGenericS2CPayload::packetType,
            ByteBufCodecs.VAR_INT, MillGenericS2CPayload::subType,
            ByteBufCodecs.BYTE_ARRAY, MillGenericS2CPayload::data,
            MillGenericS2CPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
