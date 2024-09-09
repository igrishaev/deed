package pinny;

import clojure.lang.*;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.List;
import java.net.URL;
import java.net.URI;
import java.util.UUID;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

public final class Encoder implements AutoCloseable {

    private final DataOutputStream dataOutputStream;
    private final Options options;
    private final IFn protoEncode;

    public Encoder(final IFn protoEncode, final OutputStream outputStream) {
        this(protoEncode, outputStream, Options.standard());
    }

    private void initHeader() {
        writeShort(Const.VERSION);
    }

    public Encoder(final IFn protoEncode, final OutputStream outputStream, final Options options) {
        this.protoEncode = protoEncode;
        this.options = options;
        OutputStream destination = outputStream;
        destination = new BufferedOutputStream(destination, Const.OUT_BUF_SIZE);
        if (options.useGzip()) {
            try {
                destination = new GZIPOutputStream(destination);
            } catch (IOException e) {
                throw Err.error(e, "could not open Gzip output stream");
            }
        }
        dataOutputStream = new DataOutputStream(destination);
        initHeader();
    }

    @SuppressWarnings("unused")
    public boolean useGzip() {
        return options.useGzip();
    }

    public void writeInt(final int i) {
        try {
            dataOutputStream.writeInt(i);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeOID(final short oid) {
        try {
            dataOutputStream.writeShort(oid);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeShort(final short s) {
        try {
            dataOutputStream.writeShort(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeLong(final long l) {
        try {
            dataOutputStream.writeLong(l);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unused")
    public void writeFloat(final float f) {
        try {
            dataOutputStream.writeFloat(f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unused")
    public void writeDouble(final double d) {
        try {
            dataOutputStream.writeDouble(d);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unused")
    public void writeByte(final byte b) {
        try {
            dataOutputStream.write(b);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeBytes(final byte[] bytes) {
        try {
            dataOutputStream.writeInt(bytes.length);
            dataOutputStream.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeString(final String s) {
        final byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        writeBytes(bytes);
    }

    public void writeBigInteger(final BigInteger bi) {
        final byte[] bytes = bi.toByteArray();
        writeBytes(bytes);
    }

    @SuppressWarnings("unused")
    public void encodeBoolean(final boolean b) {
        if (b) {
            writeOID(OID.BOOL_TRUE);
        } else {
            writeOID(OID.BOOL_FALSE);
        }
    }

    @SuppressWarnings("unused")
    public void encodeInteger(final int i) {
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

    @SuppressWarnings("unused")
    public void encodeLong(final long l) {
        if (l == -1) {
            writeOID(OID.LONG_MINUS_ONE);
        } else if (l == 0) {
            writeOID(OID.LONG_ZERO);
        } else if (l == 1) {
            writeOID(OID.LONG_ONE);
        } else {
            writeOID(OID.LONG);
            writeLong(l);
        }
    }

    @SuppressWarnings("unused")
    public void encodeFloat(final float f) {
        if (f == (float)-1) {
            writeOID(OID.FLOAT_MINUS_ONE);
        } else if (f == (float)0) {
            writeOID(OID.FLOAT_ZERO);
        } else if (f == (float)1) {
            writeOID(OID.FLOAT_ONE);
        } else {
            writeOID(OID.FLOAT);
            writeFloat(f);
        }
    }

    @SuppressWarnings("unused")
    public void encodeDouble(final double d) {
        if (d == (double)-1) {
            writeOID(OID.DOUBLE_MINUS_ONE);
        } else if (d == (double)0) {
            writeOID(OID.DOUBLE_ZERO);
        } else if (d == (double)1) {
            writeOID(OID.DOUBLE_ONE);
        } else {
            writeOID(OID.DOUBLE);
            writeDouble(d);
        }
    }

    @SuppressWarnings("unused")
    public void encodeByte(final byte b) {
        writeOID(OID.BYTE);
        writeByte(b);
    }

    @SuppressWarnings("unused")
    public void encodeShort(final Short s) {
        switch (s) {
            case -1 -> writeOID(OID.SHORT_MINUS_ONE);
            case 0 -> writeOID(OID.SHORT_ZERO);
            case 1 -> writeOID(OID.SHORT_ONE);
            default -> {
                writeOID(OID.SHORT);
                writeShort(s);
            }
        }
    }

    private void encodeChunk(final Object[] chunk) {
        writeInt(chunk.length);
        for (final Object x: chunk) {
            encode(x);
        }
    }

    private void encodeChunk(final Object[] chunk, final int pos) {
        writeInt(pos);
        for (int i = 0; i < pos; i++) {
            encode(chunk[i]);
        }
    }

    @SuppressWarnings("unused")
    public void encodeString(final String s) {
        if (s.isEmpty()) {
            writeOID(OID.STRING_EMPTY);
        } else {
            encodeAsString(OID.STRING, s);
        }
    }

    public void encodeAsString(final short oid, final String s) {
        writeOID(oid);
        writeString(s);
    }

    public void encodeAsDeref(final short oid, final IDeref deref) {
        writeOID(oid);
        final Object content = deref.deref();
        encode(content);
    }

    @SuppressWarnings("unused")
    public void encodeAtom(final Atom a) {
        encodeAsDeref(OID.CLJ_ATOM, a);
    }

    @SuppressWarnings("unused")
    public void encodeRef(final Ref r) {
        encodeAsDeref(OID.CLJ_REF, r);
    }

    @SuppressWarnings("unused")
    public void encodeCharacter(final char c) {
        writeOID(OID.CHAR);
        try {
            dataOutputStream.writeChar(c);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unused")
    public void encodeObjectArray(final Object[] array) {
        writeOID(OID.ARR_OBJ);
        writeInt(array.length);
        for (final Object x: array) {
            encode(x);
        }
    }

    public void encodeCountable(final short oid, final int len, final Iterable<?> iterable) {
        writeOID(oid);
        writeInt(len);
        for (final Object x : iterable) {
            encode(x);
        }
    }

    @SuppressWarnings("unused")
    public void encodeUUID(final UUID u) {
        writeOID(OID.UUID);
        writeLong(u.getMostSignificantBits());
        writeLong(u.getLeastSignificantBits());
    }

    @SuppressWarnings("unused")
    public void encodeMulti(final Iterable<?> xs) {
        for (final Object x: xs) {
            encode(x);
        }
    }

    @SuppressWarnings("unused")
    public void encodeKeyword(final Keyword kw) {
        encodeAsString(OID.CLJ_KEYWORD, kw.toString().substring(1));
    }

    @SuppressWarnings("unused")
    public void encodeSymbol(final Symbol s) {
        encodeAsString(OID.CLJ_SYMBOL, s.toString());
    }

    @SuppressWarnings("unused")
    public void encodeBigInteger(final BigInteger bi) {
        writeOID(OID.JVM_BIG_INT);
        writeBigInteger(bi);
    }

    @SuppressWarnings("unused")
    public void encodeRatio(final Ratio r) {
        writeOID(OID.CLJ_RATIO);
        writeBigInteger(r.numerator);
        writeBigInteger(r.denominator);
    }

    @SuppressWarnings("unused")
    public void encodeBigInt(final BigInt bi) {
        writeOID(OID.CLJ_BIG_INT);
        writeLong(bi.lpart);
        writeBigInteger(bi.bipart);
    }

    @SuppressWarnings("unused")
    public void encodeBigDecimal(final BigDecimal bd) {
        writeOID(OID.JVM_BIG_DEC);
        writeLong(bd.scale());
        writeBigInteger(bd.unscaledValue());
    }

    @SuppressWarnings("unused")
    public void encodeNULL() {
        writeOID(OID.NULL);
    }

    @SuppressWarnings("unused")
    public void encodeURL(final URL url) {
        encodeAsString(OID.URL, url.toString());
    }

    @SuppressWarnings("unused")
    public void encodeURI(final URI uri) {
        encodeAsString(OID.URI, uri.toString());
    }

    @SuppressWarnings("unused")
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
        final long secs = i.getEpochSecond();
        final int nanos = i.getNano();
        writeLong(secs);
        writeInt(nanos);
    }

    @SuppressWarnings("unused")
    public void encodeDate(final Date d) {
        writeOID(OID.UTIL_DATE);
        final long time = d.getTime();
        writeLong(time);
    }

    @SuppressWarnings("unused")
    public void encodeTime(final Time t) {
        writeOID(OID.SQL_TIME);
        final long time = t.getTime();
        writeLong(time);
    }

    @SuppressWarnings("unused")
    public void encodeTimestamp(final Timestamp ts) {
        writeOID(OID.SQL_TIMESTAMP);
        final long time = ts.getTime();
        writeLong(time);
    }

    public void encodeAsMap(final short oid, final Map<?,?> m) {
        writeOID(oid);
        writeInt(m.size());
        for (final Map.Entry<?,?> e: m.entrySet()) {
            encode(e.getKey());
            encode(e.getValue());
        }
    }

    @SuppressWarnings("unused")
    public void encodeMap(final Map<?,?> m) {
        encodeAsMap(OID.JVM_MAP, m);
    }

    @SuppressWarnings("unused")
    public void encodeAPersistentMap(final APersistentMap m) {
        if (m.isEmpty()) {
            writeOID(OID.CLJ_MAP_EMPTY);
        } else {
            encodeAsMap(OID.CLJ_MAP, m);
        }
    }

    @SuppressWarnings("unused")
    public void encodeAPersistentVector(final APersistentVector v) {
        if (v.isEmpty()) {
            writeOID(OID.CLJ_VEC_EMPTY);
        } else {
            encodeCountable(OID.CLJ_VEC, v.count(), v);
        }
    }

    @SuppressWarnings("unused")
    public void encodeJavaMapEntry(final Map.Entry<?,?> me) {
        writeOID(OID.JVM_MAP_ENTRY);
        encode(me.getKey());
        encode(me.getValue());
    }

    @SuppressWarnings("unused")
    public void encodeClojureMapEntry(final MapEntry me) {
        writeOID(OID.CLJ_MAP_ENTRY);
        encode(me.key());
        encode(me.val());
    }

    @SuppressWarnings("unused")
    public void encodeByteArray(final byte[] array) {
        writeOID(OID.ARR_BYTE);
        writeBytes(array);
    }

    @SuppressWarnings("unused")
    public void encodePattern(final Pattern p) {
        encodeAsString(OID.REGEX, p.toString());
    }

    // TODO: drop it
    public boolean encodeTemporal(final Temporal t) {


        if (t instanceof LocalTime lt) {
            encodeLocalTime(lt);
            return true;
        }
        return false;

    }

    // TODO: drop it
    public boolean encodeStandard(final Object x) {



        if (x instanceof Map.Entry<?,?> me) {
            encodeJavaMapEntry(me);
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
        if (x instanceof List<?> l) {
            encodeCountable(OID.JVM_LIST, l.size(), l);
            return true;
        }
        if (x instanceof Temporal t) {
            return encodeTemporal(t);
        }

        return false;
    }

    public void encode(final Object x) {
        protoEncode.invoke(x, this);
    }

    public void encodeUncountable(final short oid, final Iterable<?> iterable) {
        writeOID(oid);
        final int limit = Const.OBJ_CHUNK_SIZE;
        final Object[] chunk = new Object[limit];
        int pos = 0;
        for (final Object x: iterable) {
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
            dataOutputStream.flush();
        } catch (IOException e) {
            throw Err.error(e, "could not flush the stream");
        }
    }

    @Override
    public void close() {
        try {
            dataOutputStream.close();
        } catch (IOException e) {
            throw Err.error(e, "could not close the stream");
        }
    }
}
