package org.dizzymii.millenaire2.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;

/**
 * Utility for encoding/decoding structured data into raw byte arrays
 * used by the generic payload system. Wraps Netty ByteBuf for convenience.
 *
 * Ported from the original mod's DataOutputStream/DataInputStream approach.
 */
public final class PacketDataHelper {

    private PacketDataHelper() {}

    // ========== Writer ==========

    public static class Writer {
        private final ByteBuf buf;

        public Writer() {
            this.buf = Unpooled.buffer();
        }

        public Writer writeInt(int value) {
            buf.writeInt(value);
            return this;
        }

        public Writer writeLong(long value) {
            buf.writeLong(value);
            return this;
        }

        public Writer writeFloat(float value) {
            buf.writeFloat(value);
            return this;
        }

        public Writer writeDouble(double value) {
            buf.writeDouble(value);
            return this;
        }

        public Writer writeBoolean(boolean value) {
            buf.writeBoolean(value);
            return this;
        }

        public Writer writeByte(int value) {
            buf.writeByte(value);
            return this;
        }

        public Writer writeShort(int value) {
            buf.writeShort(value);
            return this;
        }

        public Writer writeString(@Nullable String value) {
            if (value == null) {
                buf.writeShort(-1);
            } else {
                byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
                buf.writeShort(bytes.length);
                buf.writeBytes(bytes);
            }
            return this;
        }

        public Writer writeBlockPos(int x, int y, int z) {
            buf.writeInt(x);
            buf.writeInt(y);
            buf.writeInt(z);
            return this;
        }

        public byte[] toByteArray() {
            byte[] result = new byte[buf.readableBytes()];
            buf.readBytes(result);
            buf.release();
            return result;
        }
    }

    // ========== Reader ==========

    public static class Reader {
        private final ByteBuf buf;

        public Reader(byte[] data) {
            this.buf = Unpooled.wrappedBuffer(data);
        }

        public int readInt() {
            return buf.readInt();
        }

        public long readLong() {
            return buf.readLong();
        }

        public float readFloat() {
            return buf.readFloat();
        }

        public double readDouble() {
            return buf.readDouble();
        }

        public boolean readBoolean() {
            return buf.readBoolean();
        }

        public byte readByte() {
            return buf.readByte();
        }

        public short readShort() {
            return buf.readShort();
        }

        @Nullable
        public String readString() {
            short len = buf.readShort();
            if (len < 0) return null;
            byte[] bytes = new byte[len];
            buf.readBytes(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        }

        public int[] readBlockPos() {
            return new int[]{buf.readInt(), buf.readInt(), buf.readInt()};
        }

        public boolean hasRemaining() {
            return buf.readableBytes() > 0;
        }

        public void release() {
            buf.release();
        }
    }
}
