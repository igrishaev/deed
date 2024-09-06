package pinny;

import clojure.lang.LazySeq;
import clojure.lang.PersistentHashSet;
import clojure.lang.PersistentVector;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public final class Encoder implements AutoCloseable {

    private final ObjectOutputStream objectOutputStream;
    // private final byte[] buf;
    // private final ByteBuffer bb;

    public Encoder(final OutputStream outputStream) {
        this(outputStream, Options.standard());
    }

    public Encoder(final OutputStream outputStream, final Options options) {
        // buf = new byte[8];
        // bb = ByteBuffer.wrap(buf);
        OutputStream destination = outputStream;
        if (options.useGzip()) {
            try {
                destination = new GZIPOutputStream(destination);
            } catch (IOException e) {
                throw Error.error(e, "could not open Gzip output stream");
            }
        }
        destination = new BufferedOutputStream(destination, Const.OUT_BUF_SIZE);
        try {
            objectOutputStream = new ObjectOutputStream(destination);
        } catch (IOException e) {
            throw Error.error(e, "could not open ObjectOutputStream");
        }
    }

//    private int writeBuf(final int len) {
//        try {
//            outputStream.write(buf, 0, len);
//        } catch (IOException e) {
//            throw Error.error(e, "cannot write buffer, len: %s", len);
//        }
//        return len;
//    }

    public int writeInt(final int i) {
        try {
            objectOutputStream.writeInt(i);
            return 4;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int writeOID(final short oid) {
        try {
            objectOutputStream.writeShort(oid);
            return 2;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int writeShort(final short s) {
        try {
            objectOutputStream.writeShort(s);
            return 2;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int writeLong(final long l) {
        try {
            objectOutputStream.writeLong(l);
            return 8;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int writeFloat(final float f) {
        try {
            objectOutputStream.writeFloat(f);
            return 4;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int writeDouble(final double d) {
        try {
            objectOutputStream.writeDouble(d);
            return 8;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int writeBytes(final byte[] bytes) {
        try {
            objectOutputStream.write(bytes);
            return 8;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int writeBoolean(final boolean bool) {
        try {
            objectOutputStream.writeBoolean(bool);
            return 1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public long encodeString(final String s) {
        final byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        long sum = writeOID(OID.STRING);
        sum += writeInt(bytes.length);
        writeBytes(bytes);
        sum += bytes.length;
        return sum;
    }

    public long encodeCountable(final short oid, final int len, final Iterable<?> iterable) {
        long sum = writeInt(oid) + writeInt(len);
        for (Object x : iterable) {
            sum += encode(x);
        }
        return sum + writeInt(0);
    }

    public long encodeMulti(final Iterable<Object> xs) {
        long sum = 0;
        for (Object x: xs) {
            sum += encode(x);
        }
        return sum;
    }

    public long encode(final Object x) {
        if (x instanceof Integer i) {
            return encodeInteger(i);
        } else if (x instanceof Long l) {
            return encodeLong(l);
        } else if (x instanceof Short s) {
            return encodeShort(s);
        } else if (x instanceof PersistentVector v) {
            return encodeCountable(OID.CLJ_VEC, v.count(), v);
        } else if (x instanceof PersistentHashSet s) {
            return encodeCountable(OID.CLJ_SET, s.count(), s);
        } else if (x instanceof Map<?,?> m) {
            return encodeCountable(OID.JVM_MAP, m.size(), m.entrySet());
        } else if (x instanceof LazySeq lz) {
            return encodeUncountable(OID.CLJ_LAZY_SEQ, lz);
        } else if (x instanceof String s) {
            return encodeString(s);
        } else {
            throw Error.error("unsupported type: %s %s", x.getClass(), x);
        }
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
            objectOutputStream.flush();
        } catch (IOException e) {
            throw Error.error(e, "could not flush the stream");
        }
    }

    @Override
    public void close() {
        try {
            objectOutputStream.close();
        } catch (IOException e) {
            throw Error.error(e, "could not close the stream");
        }
    }
}
