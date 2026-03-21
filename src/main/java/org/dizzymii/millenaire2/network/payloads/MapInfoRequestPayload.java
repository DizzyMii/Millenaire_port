package org.dizzymii.millenaire2.network.payloads;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;

/**
 * C2S payload requesting map info from the server. No data fields.
 */
public record MapInfoRequestPayload() implements CustomPacketPayload {

    public static final Type<MapInfoRequestPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "c2s_map_info_request"));

    public static final StreamCodec<FriendlyByteBuf, MapInfoRequestPayload> STREAM_CODEC =
            StreamCodec.of(MapInfoRequestPayload::encode, MapInfoRequestPayload::decode);

    private static void encode(FriendlyByteBuf buf, MapInfoRequestPayload p) {
        // no data
    }

    private static MapInfoRequestPayload decode(FriendlyByteBuf buf) {
        return new MapInfoRequestPayload();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
