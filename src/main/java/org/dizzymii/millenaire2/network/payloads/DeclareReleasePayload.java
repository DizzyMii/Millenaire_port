package org.dizzymii.millenaire2.network.payloads;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;

/**
 * C2S payload declaring the client's mod release version to the server.
 */
public record DeclareReleasePayload(String releaseNumber) implements CustomPacketPayload {

    public static final Type<DeclareReleasePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "c2s_declare_release"));

    public static final StreamCodec<FriendlyByteBuf, DeclareReleasePayload> STREAM_CODEC =
            StreamCodec.of(DeclareReleasePayload::encode, DeclareReleasePayload::decode);

    private static void encode(FriendlyByteBuf buf, DeclareReleasePayload p) {
        buf.writeUtf(p.releaseNumber);
    }

    private static DeclareReleasePayload decode(FriendlyByteBuf buf) {
        return new DeclareReleasePayload(buf.readUtf());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
