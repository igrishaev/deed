package pinny;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import clojure.lang.*;

public final class EncoderOld implements AutoCloseable {

    private final OutputStream outputStream;
    private final byte[] buf;
    private final ByteBuffer bb;

    public EncoderOld(final OutputStream outputStream) {
        this(outputStream, Options.standard());
    }

    public EncoderOld(final OutputStream outputStream, final Options options) {
        buf = new byte[8];
        bb = ByteBuffer.wrap(buf);
        OutputStream destination = outputStream;
        if (options.useGzip()) {
            try {
                destination = new GZIPOutputStream(destination);
            } catch (IOException e) {
                throw Error.error(e, "could not open Gzip output stream");
            }
        }
        this.outputStream = new BufferedOutputStream(destination, Const.OUT_BUF_SIZE);
    }

    private int writeBuf(final int len) {
        try {
            outputStream.write(buf, 0, len);
        } catch (IOException e) {
            throw Error.error(e, "cannot write buffer, len: %s", len);
        }
        return len;
    }

    private int writeInt(final int i) {
        bb.putInt(0, i);
        return writeBuf(Const.INT_SIZE);
    }

    private int writeOID(final short oid) {
        bb.putShort(0, oid);
        return writeBuf(Const.OID_SIZE);
    }

    private int writeShort(final short s) {
        bb.putShort(0, s);
        return writeBuf(Const.SHORT_SIZE);
    }

    private int writeLong(final long l) {
        bb.putLong(0, l);
        return writeBuf(Const.LONG_SIZE);
    }

    @SuppressWarnings("unused")
    private int writeFloat(final float f) {
        bb.putFloat(0, f);
        return writeBuf(Const.FLOAT_SIZE);
    }

    @SuppressWarnings("unused")
    private int writeDouble(final double d) {
        bb.putDouble(0, d);
        return writeBuf(Const.DOUBLE_SIZE);
    }

    @SuppressWarnings("unused")
    private int writeBoolean(final boolean bool) {
        final byte b = bool ? (byte)1 : (byte)0;
        bb.put(b);
        return writeBuf(Const.BYTE_SIZE);
    }

    public long encodeInteger(final Integer i) {
        return writeOID(OID.INT) + writeInt(i);
    }

    public long encodeLong(final Long l) {
        return writeOID(OID.LONG) + writeLong(l);
    }

    public long encodeShort(final Short s) {
        return writeOID(OID.INT) + writeShort(s);
    }

    private long encodeChunk(final Object[] chunk) {
        long sum = writeInt(chunk.length);
        for (Object x: chunk) {
            sum += encode(x);
        }
        return sum;
    }

    private long encodeChunk(final Object[] chunk, final int pos) {
        long sum = writeInt(pos);
        for (int i = 0; i < pos; i++) {
            sum += encode(chunk[i]);
        }
        return sum;
    }

    private long encodeCountable(final int oid, final int len, final Iterable<?> iterable) {
        long sum = writeInt(oid) + writeInt(len);
        for (Object x: iterable) {
            sum += encode(x);
        }
        return sum + writeInt(0);
    }

    private long encodeString(final String s) {
        final byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        long sum = writeOID(OID.STRING);
        sum += writeInt(bytes.length);
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            throw Error.error(e, "count not write bytes, string: %s", s);
        }
        sum += bytes.length;
        return sum;
    }

    private long encodeLazySeq(final Iterable<?> iterable) {
        long sum = writeOID(OID.CLJ_LAZY_SEQ);
        final int limit = Const.OBJ_CHUNK_SIZE;
        final Object[] chunk = new Object[limit];
        int pos = 0;
        for (Object x: iterable) {
            chunk[pos] = x;
            pos++;
            if (pos == limit) {
                pos = 0;
                sum += encodeChunk(chunk);
            }
        }
        if (pos > 0) {
            sum += encodeChunk(chunk, pos);
        }
        return sum + writeInt(0);
    }

    public long encode(final Object x) {
        if (x instanceof Integer i) {
            return encodeInteger(i);
        } else if (x instanceof Long l) {
            return encodeLong(l);
        } else if (x instanceof Short s) {
            return encodeShort(s);
        } else if (x instanceof PersistentVector v) {
            return encodeCountable(OID.CLJ_VECTOR, v.count(), v);
        } else if (x instanceof PersistentHashSet s) {
            return encodeCountable(OID.CLJ_SET, s.count(), s);
        } else if (x instanceof Map<?,?> m) {
            return encodeCountable(OID.JVM_MAP, m.size(), m.entrySet());
        } else if (x instanceof LazySeq lz) {
            return encodeLazySeq(lz);
        } else if (x instanceof String s) {
            return encodeString(s);
        } else {
            throw Error.error("unsupported type: %s %s", x.getClass(), x);
        }
    }

    @SuppressWarnings("unused")
    public void encodeMulti(final Iterable<Object> xs) {
        for (Object x: xs) {
            encode(x);
        }
    }

    @SuppressWarnings("unused")
    public void flush() {
        try {
            outputStream.flush();
        } catch (IOException e) {
            throw Error.error(e, "could not flush the stream");
        }
    }

    @Override
    public void close() {
        try {
            outputStream.close();
        } catch (IOException e) {
            throw Error.error(e, "could not close the stream");
        }
    }
}
