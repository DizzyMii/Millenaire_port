package org.dizzymii.millenaire2.network.payloads;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;

import java.util.ArrayList;
import java.util.List;

/**
 * S2C payload for trade goods data sent before opening the trade GUI.
 */
public record TradeDataPayload(int villagerEntityId, String villagerName,
                                int deniers, int reputation,
                                List<Entry> goods) implements CustomPacketPayload {

    public record Entry(String itemId, int itemCount, int buyPrice, int sellPrice,
                        int adjustedBuy, int adjustedSell) {}

    public static final Type<TradeDataPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "s2c_trade_data"));

    public static final StreamCodec<FriendlyByteBuf, TradeDataPayload> STREAM_CODEC =
            StreamCodec.of(TradeDataPayload::encode, TradeDataPayload::decode);

    private static void encode(FriendlyByteBuf buf, TradeDataPayload p) {
        buf.writeVarInt(p.villagerEntityId);
        buf.writeUtf(p.villagerName);
        buf.writeVarInt(p.deniers);
        buf.writeVarInt(p.reputation);
        buf.writeVarInt(p.goods.size());
        for (Entry e : p.goods) {
            buf.writeUtf(e.itemId);
            buf.writeVarInt(e.itemCount);
            buf.writeVarInt(e.buyPrice);
            buf.writeVarInt(e.sellPrice);
            buf.writeVarInt(e.adjustedBuy);
            buf.writeVarInt(e.adjustedSell);
        }
    }

    private static TradeDataPayload decode(FriendlyByteBuf buf) {
        int villagerEntityId = buf.readVarInt();
        String villagerName = buf.readUtf();
        int deniers = buf.readVarInt();
        int reputation = buf.readVarInt();
        int count = buf.readVarInt();
        List<Entry> goods = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            goods.add(new Entry(
                    buf.readUtf(), buf.readVarInt(),
                    buf.readVarInt(), buf.readVarInt(),
                    buf.readVarInt(), buf.readVarInt()));
        }
        return new TradeDataPayload(villagerEntityId, villagerName, deniers, reputation, goods);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
