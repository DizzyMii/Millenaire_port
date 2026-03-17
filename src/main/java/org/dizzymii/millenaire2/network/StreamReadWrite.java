package org.dizzymii.millenaire2.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Stream read/write utilities for network serialisation.
 * Ported from org.millenaire.common.network.StreamReadWrite (Forge 1.12.2).
 * Adapted to NeoForge 1.21.1 FriendlyByteBuf.
 */
public final class StreamReadWrite {

    public static final int MAX_STR_LENGTH = 2048;

    private StreamReadWrite() {}

    // ========== Point ==========

    public static void writeNullablePoint(@Nullable Point p, FriendlyByteBuf buf) {
        buf.writeBoolean(p == null);
        if (p != null) {
            buf.writeInt(p.x);
            buf.writeInt(p.y);
            buf.writeInt(p.z);
        }
    }

    @Nullable
    public static Point readNullablePoint(FriendlyByteBuf buf) {
        boolean isNull = buf.readBoolean();
        if (isNull) return null;
        return new Point(buf.readInt(), buf.readInt(), buf.readInt());
    }

    // ========== String ==========

    public static void writeNullableString(@Nullable String s, FriendlyByteBuf buf) {
        buf.writeBoolean(s == null);
        if (s != null) {
            buf.writeUtf(s, MAX_STR_LENGTH);
        }
    }

    @Nullable
    public static String readNullableString(FriendlyByteBuf buf) {
        boolean isNull = buf.readBoolean();
        if (isNull) return null;
        return buf.readUtf(MAX_STR_LENGTH);
    }

    // ========== String List ==========

    public static void writeStringList(List<String> list, FriendlyByteBuf buf) {
        buf.writeInt(list.size());
        for (String s : list) {
            buf.writeUtf(s, MAX_STR_LENGTH);
        }
    }

    public static List<String> readStringList(FriendlyByteBuf buf) {
        int nb = buf.readInt();
        List<String> list = new ArrayList<>(nb);
        for (int i = 0; i < nb; i++) {
            list.add(buf.readUtf(MAX_STR_LENGTH));
        }
        return list;
    }

    // ========== Boolean List ==========

    public static void writeBooleanList(List<Boolean> list, FriendlyByteBuf buf) {
        buf.writeInt(list.size());
        for (Boolean b : list) {
            buf.writeBoolean(b);
        }
    }

    public static List<Boolean> readBooleanList(FriendlyByteBuf buf) {
        int nb = buf.readInt();
        List<Boolean> list = new ArrayList<>(nb);
        for (int i = 0; i < nb; i++) {
            list.add(buf.readBoolean());
        }
        return list;
    }

    // ========== UUID ==========

    public static void writeNullableUUID(@Nullable UUID uuid, FriendlyByteBuf buf) {
        buf.writeBoolean(uuid == null);
        if (uuid != null) {
            buf.writeUUID(uuid);
        }
    }

    @Nullable
    public static UUID readNullableUUID(FriendlyByteBuf buf) {
        boolean isNull = buf.readBoolean();
        if (isNull) return null;
        return buf.readUUID();
    }

    // ========== ResourceLocation ==========

    public static void writeNullableResourceLocation(@Nullable ResourceLocation rl, FriendlyByteBuf buf) {
        buf.writeBoolean(rl == null);
        if (rl != null) {
            buf.writeResourceLocation(rl);
        }
    }

    @Nullable
    public static ResourceLocation readNullableResourceLocation(FriendlyByteBuf buf) {
        boolean isNull = buf.readBoolean();
        if (isNull) return null;
        return buf.readResourceLocation();
    }

    // ========== Inventory (InvItem → count) ==========
    // TODO: Implement once InvItem serialisation is finalised (depends on item registry)

    public static void writeInventory(Map<?, Integer> inv, FriendlyByteBuf buf) {
        buf.writeInt(inv.size());
        // TODO: serialise each InvItem key + count
    }

    public static HashMap<String, Integer> readInventory(FriendlyByteBuf buf) {
        int nb = buf.readInt();
        HashMap<String, Integer> inv = new HashMap<>(nb);
        // TODO: deserialise each InvItem key + count
        return inv;
    }
}
