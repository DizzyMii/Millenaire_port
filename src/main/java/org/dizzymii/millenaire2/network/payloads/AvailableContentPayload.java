package org.dizzymii.millenaire2.network.payloads;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;

/**
 * C2S payload declaring the client's available content packs.
 */
public record AvailableContentPayload(int contentCount) implements CustomPacketPayload {

    public static final Type<AvailableContentPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "c2s_available_content"));

    public static final StreamCodec<FriendlyByteBuf, AvailableContentPayload> STREAM_CODEC =
            StreamCodec.of(AvailableContentPayload::encode, AvailableContentPayload::decode);

    private static void encode(FriendlyByteBuf buf, AvailableContentPayload p) {
        buf.writeVarInt(p.contentCount);
    }

    private static AvailableContentPayload decode(FriendlyByteBuf buf) {
        return new AvailableContentPayload(buf.readVarInt());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
