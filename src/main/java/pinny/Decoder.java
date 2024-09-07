package pinny;

import clojure.lang.*;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
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
            case OID.SHORT -> readShort();
            case OID.INT -> readInteger();
            case OID.INT_ONE -> 1;
            case OID.INT_ZERO -> 0;
            case OID.INT_MINUS_ONE -> -1;
            case OID.LONG -> readLong();
            case OID.FLOAT -> readFloat();
            case OID.CLJ_ATOM -> readAtom();
            case OID.STRING -> readString();
            case OID.CLJ_KEYWORD -> readKeyword();
            case OID.CLJ_SYMBOL -> readSymbol();
            case OID.CLJ_RATIO -> readRatio();
            case OID.JVM_BIG_INT -> readBigInteger();
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
