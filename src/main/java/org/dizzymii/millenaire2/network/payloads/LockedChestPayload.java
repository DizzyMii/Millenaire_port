package org.dizzymii.millenaire2.network.payloads;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;

/**
 * S2C payload for locked chest data.
 */
public record LockedChestPayload(int chestEntityId) implements CustomPacketPayload {

    public static final Type<LockedChestPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "s2c_locked_chest"));

    public static final StreamCodec<FriendlyByteBuf, LockedChestPayload> STREAM_CODEC =
            StreamCodec.of(LockedChestPayload::encode, LockedChestPayload::decode);

    private static void encode(FriendlyByteBuf buf, LockedChestPayload p) {
        buf.writeVarInt(p.chestEntityId);
    }

    private static LockedChestPayload decode(FriendlyByteBuf buf) {
        return new LockedChestPayload(buf.readVarInt());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
