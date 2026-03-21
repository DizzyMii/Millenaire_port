package org.dizzymii.millenaire2.network.payloads;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;

/**
 * S2C payload for building data sync.
 */
public record BuildingSyncPayload(@Nullable Point pos, String name, String cultureKey,
                                   boolean isTownhall) implements CustomPacketPayload {

    public static final Type<BuildingSyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "s2c_building_sync"));

    public static final StreamCodec<FriendlyByteBuf, BuildingSyncPayload> STREAM_CODEC =
            StreamCodec.of(BuildingSyncPayload::encode, BuildingSyncPayload::decode);

    private static void encode(FriendlyByteBuf buf, BuildingSyncPayload p) {
        writeOptionalPoint(buf, p.pos);
        buf.writeUtf(p.name);
        buf.writeUtf(p.cultureKey);
        buf.writeBoolean(p.isTownhall);
    }

    private static BuildingSyncPayload decode(FriendlyByteBuf buf) {
        return new BuildingSyncPayload(
                readOptionalPoint(buf), buf.readUtf(), buf.readUtf(), buf.readBoolean());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    // ========== Point helpers ==========

    static void writeOptionalPoint(FriendlyByteBuf buf, @Nullable Point p) {
        if (p != null) {
            buf.writeBoolean(true);
            buf.writeInt(p.x);
            buf.writeInt(p.y);
            buf.writeInt(p.z);
        } else {
            buf.writeBoolean(false);
        }
    }

    @Nullable
    static Point readOptionalPoint(FriendlyByteBuf buf) {
        if (buf.readBoolean()) {
            return new Point(buf.readInt(), buf.readInt(), buf.readInt());
        }
        return null;
    }
}
