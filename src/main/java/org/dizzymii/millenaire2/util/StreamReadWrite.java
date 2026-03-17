package org.dizzymii.millenaire2.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Network serialization helpers for reading/writing complex types to FriendlyByteBuf.
 * Ported from org.millenaire.common.network.StreamReadWrite.
 */
public class StreamReadWrite {

    public static void writeNullablePoint(Point point, FriendlyByteBuf buf) {
        if (point == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            point.writeToBuf(buf);
        }
    }

    public static Point readNullablePoint(FriendlyByteBuf buf) {
        if (buf.readBoolean()) {
            return Point.readFromBuf(buf);
        }
        return null;
    }

    public static void writeNullableString(String s, FriendlyByteBuf buf) {
        if (s == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            buf.writeUtf(s);
        }
    }

    public static String readNullableString(FriendlyByteBuf buf) {
        if (buf.readBoolean()) {
            return buf.readUtf();
        }
        return null;
    }

    public static void writeStringList(List<String> list, FriendlyByteBuf buf) {
        buf.writeInt(list.size());
        for (String s : list) {
            buf.writeUtf(s);
        }
    }

    public static List<String> readStringList(FriendlyByteBuf buf) {
        int size = buf.readInt();
        List<String> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(buf.readUtf());
        }
        return list;
    }

    public static void writeStringIntMap(Map<String, Integer> map, FriendlyByteBuf buf) {
        buf.writeInt(map.size());
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            buf.writeUtf(entry.getKey());
            buf.writeInt(entry.getValue());
        }
    }

    public static Map<String, Integer> readStringIntMap(FriendlyByteBuf buf) {
        int size = buf.readInt();
        Map<String, Integer> map = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            map.put(buf.readUtf(), buf.readInt());
        }
        return map;
    }

    public static void writeNullableCompoundTag(CompoundTag tag, FriendlyByteBuf buf) {
        if (tag == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            buf.writeNbt(tag);
        }
    }

    public static CompoundTag readNullableCompoundTag(FriendlyByteBuf buf) {
        if (buf.readBoolean()) {
            return buf.readNbt();
        }
        return null;
    }
}
