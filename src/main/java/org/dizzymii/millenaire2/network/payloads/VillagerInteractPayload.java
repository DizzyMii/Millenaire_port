package org.dizzymii.millenaire2.network.payloads;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;

/**
 * C2S payload for requesting interaction with a villager entity.
 */
public record VillagerInteractPayload(int entityId) implements CustomPacketPayload {

    public static final Type<VillagerInteractPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "c2s_villager_interact"));

    public static final StreamCodec<FriendlyByteBuf, VillagerInteractPayload> STREAM_CODEC =
            StreamCodec.of(VillagerInteractPayload::encode, VillagerInteractPayload::decode);

    private static void encode(FriendlyByteBuf buf, VillagerInteractPayload p) {
        buf.writeVarInt(p.entityId);
    }

    private static VillagerInteractPayload decode(FriendlyByteBuf buf) {
        return new VillagerInteractPayload(buf.readVarInt());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
