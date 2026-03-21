package org.dizzymii.millenaire2.network.payloads;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;

/**
 * S2C payload for villager speech bubble data.
 */
public record VillagerSpeechPayload(int entityId, String speechKey, int variant,
                                     String cultureKey) implements CustomPacketPayload {

    public static final Type<VillagerSpeechPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "s2c_villager_speech"));

    public static final StreamCodec<FriendlyByteBuf, VillagerSpeechPayload> STREAM_CODEC =
            StreamCodec.of(VillagerSpeechPayload::encode, VillagerSpeechPayload::decode);

    private static void encode(FriendlyByteBuf buf, VillagerSpeechPayload p) {
        buf.writeVarInt(p.entityId);
        buf.writeUtf(p.speechKey);
        buf.writeVarInt(p.variant);
        buf.writeUtf(p.cultureKey);
    }

    private static VillagerSpeechPayload decode(FriendlyByteBuf buf) {
        return new VillagerSpeechPayload(
                buf.readVarInt(), buf.readUtf(), buf.readVarInt(), buf.readUtf());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
