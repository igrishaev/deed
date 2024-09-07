package pinny;

import clojure.lang.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public final class Encoder implements AutoCloseable {

    private final OutputStream outputStream;
    private final byte[] buf;
    private final ByteBuffer bb;
    private final Options options;
    private final IFn protoEncode;

    public Encoder(final IFn protoEncode, final OutputStream outputStream) {
        this(protoEncode, outputStream, Options.standard());
    }

    public Encoder(final IFn protoEncode, final OutputStream outputStream, final Options options) {
        this.protoEncode = protoEncode;
        this.options = options;
        this.buf = new byte[8];
        this.bb = ByteBuffer.wrap(this.buf);
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

    public boolean useGzip() {
        return options.useGzip();
    }

    private void sendBuf(final int len) {
        try {
            outputStream.write(buf, 0, len);
        } catch (IOException e) {
            throw Error.error(e, "could not send bytes, len: %s", len);
        }
    }

    public void writeInt(final int i) {
        bb.putInt(0, i);
        sendBuf(4);
    }

    public void writeOID(final short oid) {
        bb.putShort(0, oid);
        sendBuf(2);
    }

    public void writeShort(final short s) {
        bb.putShort(0, s);
        sendBuf(2);
    }

    public void writeLong(final long l) {
        bb.putLong(0, l);
        sendBuf(8);
    }

    @SuppressWarnings("unused")
    public void writeFloat(final float f) {
        bb.putFloat(0, f);
        sendBuf(4);
    }

    @SuppressWarnings("unused")
    public void writeDouble(final double d) {
        bb.putDouble(0, d);
        sendBuf(8);
    }

    public void writeBytes(final byte[] bytes) {
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            throw Error.error(e, "could not send bytes, length: %s", bytes.length);
        }
    }

    public void writeBoolean(final boolean bool) {
        final byte b = bool ? (byte)1 : (byte)2;
        try {
            outputStream.write(b);
        } catch (IOException e) {
            throw Error.error(e, "could not send boolean");
        }
    }

    public void writeString(final String s) {
        final byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        writeInt(bytes.length);
        writeBytes(bytes);
    }

    // todo: OPTIMIZE true/false
    @SuppressWarnings("unused")
    public void encodeBoolean(final boolean b) {
        writeOID(OID.BOOL);
        writeBoolean(b);
    }

    public void encodeInteger(final Integer i) {
        switch (i) {
            case -1 -> writeOID(OID.INT_MINUS_ONE);
            case 0 -> writeOID(OID.INT_ZERO);
            case 1 -> writeOID(OID.INT_ONE);
            default -> {
                writeOID(OID.INT);
                writeInt(i);
            }
        }
    }

    public void encodeLong(final Long l) {
        writeOID(OID.LONG);
        writeLong(l);
    }

    public void encodeShort(final Short s) {
        writeOID(OID.SHORT);
        writeShort(s);
    }

    private void encodeChunk(final Object[] chunk) {
        writeInt(chunk.length);
        for (Object x: chunk) {
            encode(x);
        }
    }

    private void encodeChunk(final Object[] chunk, final int pos) {
        writeInt(pos);
        for (int i = 0; i < pos; i++) {
            encode(chunk[i]);
        }
    }

    public void encodeString(final String s) {
        writeOID(OID.STRING);
        writeString(s);
    }

    public void encodeCountable(final short oid, final int len, final Iterable<?> iterable) {
        writeOID(oid);
        writeInt(len);
        for (Object x : iterable) {
            encode(x);
        }
    }

    public void encodeUUID(final UUID u) {
        writeOID(OID.UUID);
        writeLong(u.getMostSignificantBits());
        writeLong(u.getLeastSignificantBits());
    }

    @SuppressWarnings("unused")
    public void encodeMulti(final Iterable<?> xs) {
        for (Object x: xs) {
            encode(x);
        }
    }

    public void encodeKeyword(final Keyword kw) {
        writeOID(OID.CLJ_KEYWORD);
        writeString(kw.toString().substring(1));
    }

    public void encodeSymbol(final Symbol s) {
        writeOID(OID.CLJ_SYMBOL);
        writeString(s.toString());
    }

    public void encodeLocalDate(final LocalDate ld) {
        writeOID(OID.DT_LOCAL_DATE);
        final long days = ld.getLong(ChronoField.EPOCH_DAY);
        writeLong(days);
    }

    public void encodeLocalTime(final LocalTime lt) {
        writeOID(OID.DT_LOCAL_TIME);
        final long nanos = lt.getLong(ChronoField.NANO_OF_DAY);
        writeLong(nanos);
    }

    public void encodeInstant(final Instant i) {
        encodeAsInstant(OID.DT_INSTANT, i);
    }

    public void encodeAsInstant(final short oid, final Instant i) {
        writeOID(oid);
        final long secs = i.getLong(ChronoField.INSTANT_SECONDS);
        final long nanos = i.getLong(ChronoField.NANO_OF_SECOND);
        writeLong(secs);
        writeLong(nanos);
    }

    public void encodeDate(final Date d) {
        encodeAsInstant(OID.DT_DATE, d.toInstant());
    }

    public void encodeMapEntry(final Map.Entry<?,?> me) {
        writeOID(OID.JVM_MAP_EMPTY);
        encode(me.getKey());
        encode(me.getValue());
    }

    public boolean encodeNumber(final Number n) {
        if (n instanceof Long l) {
            encodeLong(l);
            return true;
        }
        if (n instanceof Integer i) {
            encodeInteger(i);
            return true;
        }
        if (n instanceof Short s) {
            encodeShort(s);
            return true;
        }
        return false;
    }

    public boolean encodeTemporal(final Temporal t) {
        if (t instanceof LocalDate ld) {
            encodeLocalDate(ld);
            return true;
        }
        if (t instanceof Instant i) {
            encodeInstant(i);
            return true;
        }
        if (t instanceof LocalTime lt) {
            encodeLocalTime(lt);
            return true;
        }
        return false;

    }

    public boolean encodeStandard(final Object x) {
        if (x instanceof Number n) {
            return encodeNumber(n);
        }
        if (x instanceof Keyword kw) {
            encodeKeyword(kw);
            return true;
        }
        if (x instanceof Symbol s) {
            encodeSymbol(s);
            return true;
        }
        if (x instanceof APersistentVector v) {
            encodeCountable(OID.CLJ_VEC, v.count(), v);
            return true;
        }
        if (x instanceof APersistentMap m) {
            encodeCountable(OID.CLJ_MAP, m.size(), m);
            return true;
        }
        if (x instanceof Map.Entry<?,?> me) {
            encodeMapEntry(me);
            return true;
        }
        if (x instanceof APersistentSet s) {
            encodeCountable(OID.CLJ_SET, s.count(), s);
            return true;
        }
        if (x instanceof PersistentList l) {
            encodeCountable(OID.CLJ_LIST, l.count(), l);
            return true;
        }
        if (x instanceof LazySeq lz) {
            encodeUncountable(OID.CLJ_LAZY_SEQ, lz);
            return true;
        }
        if (x instanceof Map<?,?> m) {
            encodeCountable(OID.JVM_MAP, m.size(), m.entrySet());
            return true;
        }
        if (x instanceof List<?> l) {
            encodeCountable(OID.JVM_LIST, l.size(), l);
            return true;
        }
        if (x instanceof UUID u) {
            encodeUUID(u);
            return true;
        }
        if (x instanceof String s) {
            encodeString(s);
            return true;
        }
        if (x instanceof Temporal t) {
            return encodeTemporal(t);
        }
        if (x instanceof Date d) {
            encodeDate(d);
            return true;
        }
        return false;
    }

    public void encode(final Object x) {
        protoEncode.invoke(x, this);
//        if (encodeStandard(x)) {
//            return;
//        }
//        throw Error.error("unsupported type: %s %s", x.getClass(), x);
    }

    public void encodeUncountable(final short oid, final Iterable<?> iterable) {
        writeOID(oid);
        final int limit = Const.OBJ_CHUNK_SIZE;
        final Object[] chunk = new Object[limit];
        int pos = 0;
        for (Object x: iterable) {
            chunk[pos] = x;
            pos++;
            if (pos == limit) {
                pos = 0;
                encodeChunk(chunk);
            }
        }
        if (pos > 0) {
            encodeChunk(chunk, pos);
        }
        writeInt(0);
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
