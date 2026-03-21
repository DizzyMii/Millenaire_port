package org.dizzymii.millenaire2.network.payloads;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;

/**
 * C2S payload for dev/debug commands. Carries a command sub-ID and optional data.
 */
public record DevCommandPayload(int commandId, byte[] data) implements CustomPacketPayload {

    public static final Type<DevCommandPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "c2s_dev_command"));

    public static final StreamCodec<FriendlyByteBuf, DevCommandPayload> STREAM_CODEC =
            StreamCodec.of(DevCommandPayload::encode, DevCommandPayload::decode);

    private static void encode(FriendlyByteBuf buf, DevCommandPayload p) {
        buf.writeVarInt(p.commandId);
        buf.writeByteArray(p.data);
    }

    private static DevCommandPayload decode(FriendlyByteBuf buf) {
        return new DevCommandPayload(buf.readVarInt(), buf.readByteArray());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
