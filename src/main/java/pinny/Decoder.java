package pinny;

import clojure.lang.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public final class Decoder implements Iterable<Object>, AutoCloseable {

    final private DataInputStream dataInputStream;
    final private BufferedInputStream bufferedInputStream;
    final private MultiFn mmDecode;
    final private EOF EOF;

    public Decoder(final MultiFn mmDecode, final InputStream inputStream) {
        this(mmDecode, inputStream, Options.standard());
    }

    private void readHeader() {
        readShort();
    }

    public Decoder(final MultiFn mmDecode, final InputStream inputStream, final Options options) {
        EOF = new EOF();
        this.mmDecode = mmDecode;
        InputStream source = inputStream;
        if (options.useGzip()) {
            try {
                source = new GZIPInputStream(inputStream);
            } catch (IOException e) {
                throw Err.error(e, "could not open a Gzip input stream");
            }
        }
        bufferedInputStream = new BufferedInputStream(source, Const.IN_BUF_SIZE);
        dataInputStream = new DataInputStream(bufferedInputStream);
        readHeader();
    }

    public short readShort() {
        try {
            return dataInputStream.readShort();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long readLong() {
        try {
            return dataInputStream.readLong();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int readInteger() {
        try {
            return dataInputStream.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public double readDouble() {
        try {
            return dataInputStream.readDouble();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public float readFloat() {
        try {
            return dataInputStream.readFloat();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Atom readAtom() {
        final Object content = decode();
        return new Atom(content);
    }

    public Ref readRef() {
        final Object content = decode();
        return new Ref(content);
    }

    public String readString() {
        final int len = readInteger();
        final byte[] buf = new byte[len];
        try {
            dataInputStream.readFully(buf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new String(buf, StandardCharsets.UTF_8);
    }

    public Keyword readKeyword() {
        final String payload = readString();
        return Keyword.intern(payload);
    }

    public Symbol readSymbol() {
        final String payload = readString();
        return Symbol.intern(payload);
    }

    public byte[] readBytes() {
        final int size = readInteger();
        final byte[] buf = new byte[size];
        try {
            dataInputStream.readFully(buf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return buf;
    }

    public BigInteger readBigInteger() {
        final byte[] buf = readBytes();
        return new BigInteger(buf);
    }

    public Ratio readRatio() {
        final BigInteger numerator = readBigInteger();
        final BigInteger denominator = readBigInteger();
        return new Ratio(numerator, denominator);
    }

    public IPersistentMap readClojureMap() {
        final int len = readInteger();
        ITransientMap m = PersistentArrayMap.EMPTY.asTransient();
        for (int i = 0; i < len; i++) {
            final Object key = decode();
            final Object val = decode();
            m = m.assoc(key, val);
        }
        return m.persistent();
    }

    public IPersistentCollection readClojureVector() {
        final int len = readInteger();
        ITransientCollection v = PersistentVector.EMPTY.asTransient();
        for (int i = 0; i < len; i++) {
            final Object x = decode();
            v = v.conj(x);
        }
        return v.persistent();
    }

    public Map<?,?> readJavaMap() {
        final int len = readInteger();
        HashMap<Object, Object> m = new HashMap<>(len);
        for (int i = 0; i < len; i++) {
            final Object key = decode();
            final Object val = decode();
            m.put(key, val);
        }
        return m;
    }

    public Pattern readRegex() {
        final String payload = readString();
        return Pattern.compile(payload);
    }

    public URL readURL() {
        final String payload = readString();
        try {
            //noinspection deprecation
            return new URL(payload);
        } catch (MalformedURLException e) {
            throw Err.error(e, "couldn't parse URL: %s", payload);
        }
    }

    public URI readURI() {
        final String payload = readString();
        return URI.create(payload);
    }

    public char readCharacter() {
        try {
            return dataInputStream.readChar();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public UUID readUUID() {
        final long hi = readLong();
        final long lo = readLong();
        return new UUID(hi, lo);
    }

    public Object[] readObjectArray() {
        final int len = readInteger();
        final Object[] array = new Object[len];
        for (int i = 0; i < len; i++) {
            final Object x = decode();
            array[i] = x;
        }
        return array;
    }

    public Date readUtilDate(){
        final long time = readLong();
        return new Date(time);
    }

    public Instant readInstant() {
        final long secs = readLong();
        final int nanos = readInteger();
        return Instant.ofEpochSecond(secs, nanos);
    }

    public LocalTime readLocalTime() {
        final long nanos = readLong();
        return LocalTime.ofNanoOfDay(nanos);
    }

    public LocalDate readLocalDate() {
        final long days = readLong();
        return LocalDate.ofEpochDay(days);
    }

    public Object decode() {
        int r;

        bufferedInputStream.mark(1);
        try {
            r = bufferedInputStream.read();
            bufferedInputStream.reset();
        } catch (IOException e) {
            throw Err.error(e, "could not read() from the input stream");
        }

        if (r == -1) {
            return EOF;
        }

        final short oid = readShort();

        return switch (oid) {
            case OID.DT_LOCAL_DATE -> readLocalDate();
            case OID.DT_LOCAL_TIME -> readLocalTime();
            case OID.UTIL_DATE -> readUtilDate();
            case OID.DT_INSTANT -> readInstant();
            case OID.CLJ_VEC_EMPTY -> PersistentVector.EMPTY;
            case OID.CLJ_VEC -> readClojureVector();
            case OID.DOUBLE -> readDouble();
            case OID.DOUBLE_ONE -> (double)1;
            case OID.DOUBLE_MINUS_ONE -> (double)-1;
            case OID.DOUBLE_ZERO -> (double)0;
            case OID.REGEX -> readRegex();
            case OID.NULL -> null;
            case OID.BOOL_FALSE -> false;
            case OID.BOOL_TRUE -> true;
            case OID.SHORT_ONE -> (short)1;
            case OID.SHORT_MINUS_ONE -> (short)-1;
            case OID.SHORT_ZERO -> (short)0;
            case OID.SHORT -> readShort();
            case OID.INT -> readInteger();
            case OID.INT_ONE -> 1;
            case OID.INT_ZERO -> 0;
            case OID.INT_MINUS_ONE -> -1;
            case OID.LONG -> readLong();
            case OID.LONG_ONE -> (long)1;
            case OID.LONG_MINUS_ONE -> (long)-1;
            case OID.LONG_ZERO -> (long)0;
            case OID.FLOAT -> readFloat();
            case OID.FLOAT_ONE -> (float)1;
            case OID.FLOAT_MINUS_ONE -> (float)-1;
            case OID.FLOAT_ZERO -> (float)0;
            case OID.CLJ_ATOM -> readAtom();
            case OID.CLJ_REF -> readRef();
            case OID.STRING -> readString();
            case OID.STRING_EMPTY -> "";
            case OID.CHAR -> readCharacter();
            case OID.URL -> readURL();
            case OID.URI -> readURI();
            case OID.UUID -> readUUID();
            case OID.CLJ_KEYWORD -> readKeyword();
            case OID.CLJ_SYMBOL -> readSymbol();
            case OID.CLJ_RATIO -> readRatio();
            case OID.JVM_BIG_INT -> readBigInteger();
            case OID.CLJ_MAP -> readClojureMap();
            case OID.CLJ_MAP_EMPTY -> PersistentArrayMap.EMPTY;
            case OID.JVM_MAP -> readJavaMap();
            case OID.ARR_BYTE -> readBytes();
            case OID.ARR_OBJ -> readObjectArray();
            default -> mmDecode.invoke(oid, this);
        };
    }

    @SuppressWarnings("unused")
    public ISeq decodeSeq() {
        return RT.chunkIteratorSeq(this.iterator());
    }

    @Override
    public Iterator<Object> iterator() {
        return new Iterator<>() {

            private Object next;

            @Override
            public boolean hasNext() {
                next = decode();
                return next != EOF;
            }

            @Override
            public Object next() {
                return next;
            }
        };
    }

    @Override
    public void close() {
        try {
            dataInputStream.close();
        } catch (IOException e) {
            throw Err.error(e, "could not close the stream");
        }
    }
}
