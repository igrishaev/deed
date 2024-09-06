package pinny;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

public final class Encoder implements AutoCloseable {

    private final OutputStream outputStream;
    private final byte[] buf;
    private final ByteBuffer bb;

    public Encoder(final OutputStream outputStream) {
        this(outputStream, Options.standard());
    }

    public Encoder(final OutputStream outputStream, final Options options) {
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
