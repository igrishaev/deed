package pinny;

import clojure.lang.MultiFn;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

public final class Encoder implements AutoCloseable {

    private final OutputStream outputStream;
    private final byte[] buf;
    private final ByteBuffer bb;
    private final MultiFn encode;

    public Encoder(final MultiFn encode, final OutputStream outputStream) {
        this(encode, outputStream, Options.standard());
    }

    public Encoder(final MultiFn encode, final OutputStream outputStream, final Options options) {
        this.encode = encode;
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

    public int writeInt(final int i) {
        bb.putInt(0, i);
        return writeBuf(Const.INT_SIZE);
    }

    public int writeOID(final short oid) {
        bb.putShort(0, oid);
        return writeBuf(Const.OID_SIZE);
    }

    public int writeShort(final short s) {
        bb.putShort(0, s);
        return writeBuf(Const.SHORT_SIZE);
    }

    public int writeLong(final long l) {
        bb.putLong(0, l);
        return writeBuf(Const.LONG_SIZE);
    }

    public int writeFloat(final float f) {
        bb.putFloat(0, f);
        return writeBuf(Const.FLOAT_SIZE);
    }

    public int writeDouble(final double d) {
        bb.putDouble(0, d);
        return writeBuf(Const.DOUBLE_SIZE);
    }

    public int writeBoolean(final boolean bool) {
        final byte b = bool ? (byte)1 : (byte)0;
        bb.put(b);
        return writeBuf(Const.BYTE_SIZE);
    }

    // todo: OPTIMIZE true/false
    public long encodeBoolean(final boolean b) {
        return writeOID(OID.BOOL) + writeBoolean(b);
    }

    // todo: optimize -1, 0, 1
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
            sum += (long) encode.invoke(this, x);
        }
        return sum;
    }

    private long encodeChunk(final Object[] chunk, final int pos) {
        long sum = writeInt(pos);
        for (int i = 0; i < pos; i++) {
            sum += (long) encode.invoke(this, chunk[i]);
        }
        return sum;
    }

    public long encodeString(final String s) {
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

    public long encodeCountable(final short oid, final int len, final Iterable<?> iterable) {
        long sum = writeInt(oid) + writeInt(len);
        for (Object x : iterable) {
            sum += (long) encode.invoke(this, x);
        }
        return sum + writeInt(0);
    }

    public long encodeMulti(final Iterable<Object> xs) {
        long sum = 0;
        for (Object x: xs) {
            sum += (long) encode.invoke(this, x);
        }
        return sum;
    }

    public long encode(final Object x) {
        return (long) encode.invoke(this, x);
    }

    public long encodeUncountable(final short oid, final Iterable<?> iterable) {
        long sum = writeOID(oid);
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
