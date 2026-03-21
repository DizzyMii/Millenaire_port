package org.dizzymii.millenaire2.network.payloads;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;

/**
 * S2C payload for a culture-translated chat message.
 */
public record TranslatedChatPayload(String translationKey, String cultureKey,
                                     String[] args) implements CustomPacketPayload {

    public static final Type<TranslatedChatPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "s2c_translated_chat"));

    public static final StreamCodec<FriendlyByteBuf, TranslatedChatPayload> STREAM_CODEC =
            StreamCodec.of(TranslatedChatPayload::encode, TranslatedChatPayload::decode);

    private static void encode(FriendlyByteBuf buf, TranslatedChatPayload p) {
        buf.writeUtf(p.translationKey);
        buf.writeUtf(p.cultureKey);
        buf.writeVarInt(p.args.length);
        for (String arg : p.args) {
            buf.writeUtf(arg);
        }
    }

    private static TranslatedChatPayload decode(FriendlyByteBuf buf) {
        String translationKey = buf.readUtf();
        String cultureKey = buf.readUtf();
        int count = buf.readVarInt();
        String[] args = new String[count];
        for (int i = 0; i < count; i++) {
            args[i] = buf.readUtf();
        }
        return new TranslatedChatPayload(translationKey, cultureKey, args);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
