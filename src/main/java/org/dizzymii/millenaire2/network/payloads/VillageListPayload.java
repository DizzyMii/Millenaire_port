package org.dizzymii.millenaire2.network.payloads;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * S2C payload for the village list sent to a player.
 */
public record VillageListPayload(List<Entry> entries) implements CustomPacketPayload {

    public record Entry(@Nullable Point pos, String cultureKey, String name,
                        int distance, boolean isLoneBuilding) {}

    public static final Type<VillageListPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "s2c_village_list"));

    public static final StreamCodec<FriendlyByteBuf, VillageListPayload> STREAM_CODEC =
            StreamCodec.of(VillageListPayload::encode, VillageListPayload::decode);

    private static void encode(FriendlyByteBuf buf, VillageListPayload p) {
        buf.writeVarInt(p.entries.size());
        for (Entry e : p.entries) {
            BuildingSyncPayload.writeOptionalPoint(buf, e.pos);
            buf.writeUtf(e.cultureKey);
            buf.writeUtf(e.name);
            buf.writeVarInt(e.distance);
            buf.writeBoolean(e.isLoneBuilding);
        }
    }

    private static VillageListPayload decode(FriendlyByteBuf buf) {
        int count = buf.readVarInt();
        List<Entry> entries = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            entries.add(new Entry(
                    BuildingSyncPayload.readOptionalPoint(buf),
                    buf.readUtf(), buf.readUtf(),
                    buf.readVarInt(), buf.readBoolean()));
        }
        return new VillageListPayload(entries);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
