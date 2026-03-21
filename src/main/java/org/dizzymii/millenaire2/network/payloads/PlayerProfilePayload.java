package org.dizzymii.millenaire2.network.payloads;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;

/**
 * S2C payload for player profile sync (reputation, language levels, etc.).
 * Uses byte[] for the profile data blob until profile encoding is fully typed.
 */
public record PlayerProfilePayload(int updateType, byte[] data) implements CustomPacketPayload {

    public static final Type<PlayerProfilePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "s2c_profile"));

    public static final StreamCodec<FriendlyByteBuf, PlayerProfilePayload> STREAM_CODEC =
            StreamCodec.of(PlayerProfilePayload::encode, PlayerProfilePayload::decode);

    private static void encode(FriendlyByteBuf buf, PlayerProfilePayload p) {
        buf.writeVarInt(p.updateType);
        buf.writeByteArray(p.data);
    }

    private static PlayerProfilePayload decode(FriendlyByteBuf buf) {
        return new PlayerProfilePayload(buf.readVarInt(), buf.readByteArray());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
