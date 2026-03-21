package org.dizzymii.millenaire2.network.payloads;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;

/**
 * S2C payload for quest instance data (offer or progress update).
 */
public record QuestInstancePayload(String questKey, int stepIndex, int totalSteps,
                                    String stepDescription, String stepLabel,
                                    int rewardMoney, int rewardRep,
                                    int villagerEntityId, boolean isOffer) implements CustomPacketPayload {

    public static final Type<QuestInstancePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "s2c_quest_instance"));

    public static final StreamCodec<FriendlyByteBuf, QuestInstancePayload> STREAM_CODEC =
            StreamCodec.of(QuestInstancePayload::encode, QuestInstancePayload::decode);

    private static void encode(FriendlyByteBuf buf, QuestInstancePayload p) {
        buf.writeUtf(p.questKey);
        buf.writeVarInt(p.stepIndex);
        buf.writeVarInt(p.totalSteps);
        buf.writeUtf(p.stepDescription);
        buf.writeUtf(p.stepLabel);
        buf.writeVarInt(p.rewardMoney);
        buf.writeVarInt(p.rewardRep);
        buf.writeVarInt(p.villagerEntityId);
        buf.writeBoolean(p.isOffer);
    }

    private static QuestInstancePayload decode(FriendlyByteBuf buf) {
        return new QuestInstancePayload(
                buf.readUtf(), buf.readVarInt(), buf.readVarInt(),
                buf.readUtf(), buf.readUtf(),
                buf.readVarInt(), buf.readVarInt(),
                buf.readVarInt(), buf.readBoolean());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
