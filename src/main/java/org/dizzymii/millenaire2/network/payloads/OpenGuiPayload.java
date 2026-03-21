package org.dizzymii.millenaire2.network.payloads;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;

/**
 * S2C payload telling the client to open a specific GUI.
 */
public record OpenGuiPayload(int guiId, int entityId,
                              @Nullable Point villagePos) implements CustomPacketPayload {

    public static final Type<OpenGuiPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "s2c_open_gui"));

    public static final StreamCodec<FriendlyByteBuf, OpenGuiPayload> STREAM_CODEC =
            StreamCodec.of(OpenGuiPayload::encode, OpenGuiPayload::decode);

    private static void encode(FriendlyByteBuf buf, OpenGuiPayload p) {
        buf.writeVarInt(p.guiId);
        buf.writeVarInt(p.entityId);
        BuildingSyncPayload.writeOptionalPoint(buf, p.villagePos);
    }

    private static OpenGuiPayload decode(FriendlyByteBuf buf) {
        return new OpenGuiPayload(
                buf.readVarInt(), buf.readVarInt(), BuildingSyncPayload.readOptionalPoint(buf));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
