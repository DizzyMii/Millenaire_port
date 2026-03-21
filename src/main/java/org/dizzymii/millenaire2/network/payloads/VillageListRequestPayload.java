package org.dizzymii.millenaire2.network.payloads;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;

/**
 * C2S payload requesting the village list from the server. No data fields.
 */
public record VillageListRequestPayload() implements CustomPacketPayload {

    public static final Type<VillageListRequestPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "c2s_village_list_request"));

    public static final StreamCodec<FriendlyByteBuf, VillageListRequestPayload> STREAM_CODEC =
            StreamCodec.of(VillageListRequestPayload::encode, VillageListRequestPayload::decode);

    private static void encode(FriendlyByteBuf buf, VillageListRequestPayload p) {
        // no data
    }

    private static VillageListRequestPayload decode(FriendlyByteBuf buf) {
        return new VillageListRequestPayload();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
