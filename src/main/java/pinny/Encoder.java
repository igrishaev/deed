package pinny;

import clojure.lang.*;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.time.temporal.ChronoField;
import java.util.*;
import java.net.URL;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import java.util.stream.Stream;
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

    public void writeByte(final byte b) {
        try {
            dataOutputStream.write(b);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeCharacter(final char c) {
        try {
            dataOutputStream.writeChar(c);
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

    public void writeBoolean(final boolean b) {
        try {
            dataOutputStream.writeBoolean(b);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        switch (b) {
            case -1 -> writeOID(OID.BYTE_MINUS_ONE);
            case 0 -> writeOID(OID.BYTE_ZERO);
            case 1 -> writeOID(OID.BYTE_ONE);
            default -> {
                writeOID(OID.BYTE);
                writeByte(b);
            }
        }
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
        writeCharacter(c);
    }

    @SuppressWarnings("unused")
    public void encodeObjectArray(final Object[] array) {
        writeOID(OID.ARR_OBJ);
        writeInt(array.length);
        for (final Object x: array) {
            encode(x);
        }
    }

    @SuppressWarnings("unused")
    public void encodeIntArray(final int[] array) {
        writeOID(OID.ARR_INT);
        writeInt(array.length);
        for (final int i: array) {
            writeInt(i);
        }
    }

    @SuppressWarnings("unused")
    public void encodeShortArray(final short[] array) {
        writeOID(OID.ARR_SHORT);
        writeInt(array.length);
        for (final short s: array) {
            writeShort(s);
        }
    }

    @SuppressWarnings("unused")
    public void encodeBoolArray(final boolean[] array) {
        writeOID(OID.ARR_BOOL);
        writeInt(array.length);
        for (final boolean b: array) {
            writeBoolean(b);
        }
    }

    @SuppressWarnings("unused")
    public void encodeFloatArray(final float[] array) {
        writeOID(OID.ARR_FLOAT);
        writeInt(array.length);
        for (final float f: array) {
            writeFloat(f);
        }
    }

    @SuppressWarnings("unused")
    public void encodeDoubleArray(final double[] array) {
        writeOID(OID.ARR_DOUBLE);
        writeInt(array.length);
        for (final double d: array) {
            writeDouble(d);
        }
    }

    @SuppressWarnings("unused")
    public void encodeCharArray(final char[] array) {
        writeOID(OID.ARR_CHAR);
        writeInt(array.length);
        for (final char c: array) {
            writeCharacter(c);
        }
    }

    @SuppressWarnings("unused")
    public void encodeLongArray(final long[] array) {
        writeOID(OID.ARR_LONG);
        writeInt(array.length);
        for (final long l: array) {
            writeLong(l);
        }
    }

    @SuppressWarnings("unused")
    public void encodeFuture(final Future<?> f) {
        final long timeout = options.futureGetTimeoutMs();
        try {
            final Object x = f.get(timeout, TimeUnit.MILLISECONDS);
            writeOID(OID.FUTURE);
            encode(x);
        } catch (InterruptedException e) {
            throw Err.error(e, "future was interrupted");
        } catch (ExecutionException e) {
            throw Err.error(e, "future has failed: %s", e.getCause().getMessage());
        } catch (TimeoutException e) {
            throw Err.error(e, "future deref timeout (ms): %s", timeout);
        }
    }

    public void writeCountable(final int len, final Iterable<?> iterable) {
        writeInt(len);
        for (final Object x : iterable) {
            encode(x);
        }
    }

    public void encodeCountable(final short oid, final int len, final Iterable<?> iterable) {
        writeOID(oid);
        writeCountable(len, iterable);
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
        writeBigInteger(bi.toBigInteger());
    }

    @SuppressWarnings("unused")
    public void encodeBigDecimal(final BigDecimal bd) {
        writeOID(OID.JVM_BIG_DEC);
        writeInt(bd.scale());
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

    @SuppressWarnings("unused")
    public void encodeLocalDateTime(final LocalDateTime ldt) {
        writeOID(OID.DT_LOCAL_DATETIME);
        final long epoch = ldt.toEpochSecond(ZoneOffset.UTC);
        final int nanos = ldt.getNano();
        writeLong(epoch);
        writeInt(nanos);
    }

    @SuppressWarnings("unused")
    public void encodeOffsetDateTime(final OffsetDateTime odt) {
        writeOID(OID.DT_OFFSET_DATETIME);
        final LocalDateTime localDateTime = odt.toLocalDateTime();
        final ZoneOffset zoneOffset = odt.getOffset();
        final long epoch = localDateTime.toEpochSecond(ZoneOffset.UTC);
        final int nanos = localDateTime.getNano();
        final int offset = zoneOffset.getTotalSeconds();
        writeLong(epoch);
        writeInt(nanos);
        writeInt(offset);
    }

    @SuppressWarnings("unused")
    public void encodeZonedDateTime(final ZonedDateTime zdt) {
        writeOID(OID.DT_ZONED_DATETIME);
        final LocalDateTime localDateTime = zdt.toLocalDateTime();
        final ZoneOffset zoneOffset = zdt.getOffset();
        final long epoch = localDateTime.toEpochSecond(ZoneOffset.UTC);
        final int nanos = localDateTime.getNano();
        final String zoneId = zdt.getZone().getId();
        writeLong(epoch);
        writeInt(nanos);
        writeString(zoneId);
    }

    @SuppressWarnings("unused")
    public void encodeOffsetTime(final OffsetTime offsetTime) {
        writeOID(OID.DT_OFFSET_TIME);
        final long nanos = offsetTime.getLong(ChronoField.NANO_OF_DAY);
        final int offset = offsetTime.getOffset().getTotalSeconds();
        writeLong(nanos);
        writeInt(offset);
    }

    @SuppressWarnings("unused")
    public void encodeDuration(final Duration d) {
        writeOID(OID.DT_DURATION);
        final long seconds = d.getSeconds();
        final int nanos = d.getNano();
        writeLong(seconds);
        writeInt(nanos);
    }

    @SuppressWarnings("unused")
    public void encodePeriod(final Period p) {
        writeOID(OID.DT_PERIOD);
        final int years = p.getYears();
        final int month = p.getMonths();
        final int days = p.getDays();
        writeInt(years);
        writeInt(month);
        writeInt(days);
    }

    @SuppressWarnings("unused")
    public void encodeZoneId(final ZoneId zoneId) {
        encodeAsString(OID.DT_ZONE_ID, zoneId.getId());
    }

    @SuppressWarnings("unused")
    public void encodeLocalTime(final LocalTime lt) {
        writeOID(OID.DT_LOCAL_TIME);
        final long nanos = lt.getLong(ChronoField.NANO_OF_DAY);
        writeLong(nanos);
    }

    @SuppressWarnings("unused")
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
    public void encodeAsUtilDate(final short oid, final java.util.Date d) {
        writeOID(oid);
        final long time = d.getTime();
        writeLong(time);
    }

    @SuppressWarnings("unused")
    public void encodeUtilDate(final java.util.Date d) {
        encodeAsUtilDate(OID.UTIL_DATE, d);
    }

    @SuppressWarnings("unused")
    public void encodeSqlTime(final java.sql.Time t) {
        encodeAsUtilDate(OID.SQL_TIME, t);
    }

    @SuppressWarnings("unused")
    public void encodeSqlDate(final java.sql.Date d) {
        encodeAsUtilDate(OID.SQL_DATE, d);
    }

    @SuppressWarnings("unused")
    public void encodeSqlTimestamp(final java.sql.Timestamp ts) {
        encodeAsUtilDate(OID.SQL_TIMESTAMP, ts);
    }

    public void writeMap(final Map<?,?> m) {
        writeInt(m.size());
        for (final Map.Entry<?,?> e: m.entrySet()) {
            encode(e.getKey());
            encode(e.getValue());
        }
    }

    public void encodeAsMap(final short oid, final Map<?,?> m) {
        writeOID(oid);
        writeMap(m);
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

    @SuppressWarnings("unused")
    public void encodeRecord(final IRecord r) {
        final Map<?,?> m = (Map<?, ?>) r;
        encodeAsMap(OID.CLJ_RECORD, m);
    }

    @SuppressWarnings("unused")
    public void encodeAPersistentSet(final APersistentSet s) {
        if (s.isEmpty()) {
            writeOID(OID.CLJ_SET_EMPTY);
        } else {
            encodeCountable(OID.CLJ_SET, s.count(), s);
        }
    }

    @SuppressWarnings("unused")
    public void encodeSortedSet(final PersistentTreeSet s) {
        if (s.isEmpty()) {
            writeOID(OID.CLJ_SORTED_SET_EMPTY);
        } else {
            encodeCountable(OID.CLJ_SORTED_SET, s.count(), s);
        }
    }

    @SuppressWarnings("unused")
    public void encodeSortedMap(final PersistentTreeMap s) {
        if (s.isEmpty()) {
            writeOID(OID.CLJ_SORTED_MAP_EMPTY);
        } else {
            encodeAsMap(OID.CLJ_SORTED_MAP, s);
        }
    }

    @SuppressWarnings("unused")
    public void encodeLazySeq(final LazySeq lz) {
        encodeUncountable(OID.CLJ_LAZY_SEQ, lz.iterator());
    }

    @SuppressWarnings("unused")
    public void encodeClojureSeq(final ASeq seq) {
        encodeUncountable(OID.CLJ_SEQ, seq.iterator());
    }

    @SuppressWarnings("unused")
    public void encodeClojureList(final PersistentList l) {
        encodeCountable(OID.CLJ_LIST, l.count(), l);
    }

    @SuppressWarnings("unused")
    public void encodeClojureEmptyList(final Object ignored) {
        // A workaround for the private PersistentList.EmptyList class
        writeOID(OID.CLJ_LIST_EMPTY);
    }

    @SuppressWarnings("unused")
    public void encodeClojureQueue(final PersistentQueue q) {
        if (q.isEmpty()) {
            writeOID(OID.CLJ_QUEUE_EMPTY);
        } else {
            encodeCountable(OID.CLJ_QUEUE, q.count(), q);
        }
    }

    @SuppressWarnings("unused")
    public void encodeJavaList(final List<?> l) {
        if (l.isEmpty()) {
            writeOID(OID.JVM_LIST_EMPTY);
        } else {
            encodeCountable(OID.JVM_LIST, l.size(), l);
        }
    }

    public void writeStackTraceElement(final StackTraceElement element) {
        writeString(element.getClassName());
        writeString(element.getMethodName());
        final String fileName = element.getFileName();
        if (fileName == null) {
            writeString("");
        } else {
            writeString(element.getFileName());
        }
        writeInt(element.getLineNumber());
    }

    public void writeThrowable(final Throwable t) {
        final String message = t.getMessage();
        final StackTraceElement[] trace = t.getStackTrace();
        final Throwable cause = t.getCause();
        final Throwable[] suppressed = t.getSuppressed();

        if (message == null) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            writeString(message);
        }

        writeInt(trace.length);
        for (StackTraceElement element: trace) {
            writeStackTraceElement(element);
        }

        if (cause == null) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            encode(cause);
        }

        writeInt(suppressed.length);
        for (Throwable s: suppressed) {
            encode(s);
        }
    }

    @SuppressWarnings("unused")
    public void encodeThrowable(final Throwable t) {
        writeOID(OID.THROWABLE);
        writeThrowable(t);
    }

    public Map<?,?> getExData(final ExceptionInfo e) {
        final IPersistentMap data = e.getData();
        if (data instanceof Map<?,?> m) {
            return m;
        } else {
            throw Err.error("unsupported ex-data: %s %s", data.getClass(), data);
        }
    }

    @SuppressWarnings("unused")
    public void encodeExceptionInfo(final ExceptionInfo e) {
        writeOID(OID.EX_INFO);
        final Map<?,?> data = getExData(e);
        writeMap(data);
        writeThrowable(e);
    }

    @SuppressWarnings("unused")
    public void encodeJavaVector(final Vector<?> v) {
        if (v.isEmpty()) {
            writeOID(OID.JVM_VECTOR_EMPTY);
        } else {
            encodeCountable(OID.JVM_VECTOR, v.size(), v);
        }
    }

    @SuppressWarnings("unused")
    public void encodeJavaIterable(final Iterable<?> iterable) {
        encodeUncountable(OID.JVM_ITERABLE, iterable.iterator());
    }

    @SuppressWarnings("unused")
    public void encodeJavaIterator(final Iterator<?> iterator) {
        encodeUncountable(OID.JVM_ITERATOR, iterator);
    }

    @SuppressWarnings("unused")
    public void encodeJavaStream(final Stream<?> stream) {
        encodeUncountable(OID.JVM_STREAM, stream.iterator());
    }

    public void encode(final Object x) {
        protoEncode.invoke(x, this);
    }

    public void encodeUncountable(final short oid, final Iterator<?> iterator) {
        writeOID(oid);
        Object x;
        final int limit = Const.OBJ_CHUNK_SIZE;
        final Object[] chunk = new Object[limit];
        int pos = 0;
        while (iterator.hasNext()) {
            x = iterator.next();
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
