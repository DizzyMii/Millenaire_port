package org.dizzymii.millenaire2.network.payloads;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;

/**
 * S2C payload for full villager data sync.
 */
public record VillagerSyncPayload(
        int entityId, long villagerId,
        String firstName, String familyName, int gender,
        String cultureKey, String vtypeKey, String goalKey,
        boolean isRaider, boolean aggressiveStance,
        float posX, float posY, float posZ,
        boolean usingBow, boolean usingHandToHand,
        String speechKey, int speechVariant, long speechStarted,
        @Nullable Point housePoint, @Nullable Point townHallPoint
) implements CustomPacketPayload {

    public static final Type<VillagerSyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "s2c_villager_sync"));

    public static final StreamCodec<FriendlyByteBuf, VillagerSyncPayload> STREAM_CODEC =
            StreamCodec.of(VillagerSyncPayload::encode, VillagerSyncPayload::decode);

    private static void encode(FriendlyByteBuf buf, VillagerSyncPayload p) {
        buf.writeVarInt(p.entityId);
        buf.writeLong(p.villagerId);
        buf.writeUtf(p.firstName);
        buf.writeUtf(p.familyName);
        buf.writeVarInt(p.gender);
        buf.writeUtf(p.cultureKey);
        buf.writeUtf(p.vtypeKey);
        buf.writeUtf(p.goalKey);
        buf.writeBoolean(p.isRaider);
        buf.writeBoolean(p.aggressiveStance);
        buf.writeFloat(p.posX);
        buf.writeFloat(p.posY);
        buf.writeFloat(p.posZ);
        buf.writeBoolean(p.usingBow);
        buf.writeBoolean(p.usingHandToHand);
        buf.writeUtf(p.speechKey);
        buf.writeVarInt(p.speechVariant);
        buf.writeLong(p.speechStarted);
        BuildingSyncPayload.writeOptionalPoint(buf, p.housePoint);
        BuildingSyncPayload.writeOptionalPoint(buf, p.townHallPoint);
    }

    private static VillagerSyncPayload decode(FriendlyByteBuf buf) {
        return new VillagerSyncPayload(
                buf.readVarInt(), buf.readLong(),
                buf.readUtf(), buf.readUtf(), buf.readVarInt(),
                buf.readUtf(), buf.readUtf(), buf.readUtf(),
                buf.readBoolean(), buf.readBoolean(),
                buf.readFloat(), buf.readFloat(), buf.readFloat(),
                buf.readBoolean(), buf.readBoolean(),
                buf.readUtf(), buf.readVarInt(), buf.readLong(),
                BuildingSyncPayload.readOptionalPoint(buf),
                BuildingSyncPayload.readOptionalPoint(buf)
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
