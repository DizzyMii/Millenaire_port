package org.dizzymii.millenaire2.network.payloads;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;

/**
 * C2S payload for GUI actions (trade, hire, quest, building project, etc.).
 * Carries an actionId sub-type and raw data until individual sub-actions
 * are decomposed in Phase 3.2.
 */
public record GuiActionPayload(int actionId, byte[] data) implements CustomPacketPayload {

    public static final Type<GuiActionPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "c2s_gui_action"));

    public static final StreamCodec<FriendlyByteBuf, GuiActionPayload> STREAM_CODEC =
            StreamCodec.of(GuiActionPayload::encode, GuiActionPayload::decode);

    private static void encode(FriendlyByteBuf buf, GuiActionPayload p) {
        buf.writeVarInt(p.actionId);
        buf.writeByteArray(p.data);
    }

    private static GuiActionPayload decode(FriendlyByteBuf buf) {
        return new GuiActionPayload(buf.readVarInt(), buf.readByteArray());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
