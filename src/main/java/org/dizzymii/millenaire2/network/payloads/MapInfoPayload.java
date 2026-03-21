package org.dizzymii.millenaire2.network.payloads;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;

/**
 * S2C payload for map info (village markers for minimap).
 */
public record MapInfoPayload(int villageCount) implements CustomPacketPayload {

    public static final Type<MapInfoPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "s2c_map_info"));

    public static final StreamCodec<FriendlyByteBuf, MapInfoPayload> STREAM_CODEC =
            StreamCodec.of(MapInfoPayload::encode, MapInfoPayload::decode);

    private static void encode(FriendlyByteBuf buf, MapInfoPayload p) {
        buf.writeVarInt(p.villageCount);
    }

    private static MapInfoPayload decode(FriendlyByteBuf buf) {
        return new MapInfoPayload(buf.readVarInt());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
