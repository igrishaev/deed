package pinny;

import clojure.lang.LazySeq;
import clojure.lang.PersistentHashSet;
import clojure.lang.PersistentVector;

import java.io.*;
import java.util.UUID;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public final class Encoder implements AutoCloseable {

    private final ObjectOutputStream objectOutputStream;

    public Encoder(final OutputStream outputStream) {
        this(outputStream, Options.standard());
    }

    public Encoder(final OutputStream outputStream, final Options options) {
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

    public void writeInt(final int i) {
        try {
            objectOutputStream.writeInt(i);
        } catch (IOException e) {
            throw Error.error(e, "cannot write int: %s", i);
        }
    }

    public void writeOID(final short oid) {
        try {
            objectOutputStream.writeShort(oid);
        } catch (IOException e) {
            throw Error.error(e, "cannot write OID: 0x%04X", oid);
        }
    }

    public void writeShort(final short s) {
        try {
            objectOutputStream.writeShort(s);
        } catch (IOException e) {
            throw Error.error(e, "cannot write short: %s", s);
        }
    }

    public void writeLong(final long l) {
        try {
            objectOutputStream.writeLong(l);
        } catch (IOException e) {
            throw Error.error(e, "cannot write long: %s", l);
        }
    }

    @SuppressWarnings("unused")
    public void writeFloat(final float f) {
        try {
            objectOutputStream.writeFloat(f);
        } catch (IOException e) {
            throw Error.error(e, "cannot write float: %s", f);
        }
    }

    @SuppressWarnings("unused")
    public void writeDouble(final double d) {
        try {
            objectOutputStream.writeDouble(d);
        } catch (IOException e) {
            throw Error.error(e, "cannot write double: %s", d);
        }
    }

    public void writeBytes(final byte[] bytes) {
        try {
            objectOutputStream.write(bytes);
        } catch (IOException e) {
            throw Error.error(e, "cannot write bytes, length: %s", bytes.length);
        }
    }

    public void writeBoolean(final boolean bool) {
        try {
            objectOutputStream.writeBoolean(bool);
        } catch (IOException e) {
            throw Error.error(e, "cannot write boolean: %s", bool);
        }
    }

    // todo: OPTIMIZE true/false
    @SuppressWarnings("unused")
    public void encodeBoolean(final boolean b) {
        writeOID(OID.BOOL);
        writeBoolean(b);
    }

    // todo: optimize -1, 0, 1
    public void encodeInteger(final Integer i) {
        writeOID(OID.INT);
        writeInt(i);
    }

    public void encodeLong(final Long l) {
        writeOID(OID.LONG);
        writeLong(l);
    }

    public void encodeShort(final Short s) {
        writeOID(OID.INT);
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
        final byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        writeOID(OID.STRING);
        writeInt(bytes.length);
        writeBytes(bytes);
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

    public void encodeSerializable(final Object x) {
        writeOID(OID.SERIALIZABLE);
        try {
            objectOutputStream.writeObject(x);
        } catch (IOException e) {
            throw Error.error(e, "cannot serialize object: %s %s", x.getClass(), x);
        }
    }

    public void encode(final Object x) {
        if (x instanceof Integer i) {
            encodeInteger(i);
        } else if (x instanceof Long l) {
            encodeLong(l);
        } else if (x instanceof Short s) {
            encodeShort(s);
        } else if (x instanceof PersistentVector v) {
            encodeCountable(OID.CLJ_VEC, v.count(), v);
        } else if (x instanceof PersistentHashSet s) {
            encodeCountable(OID.CLJ_SET, s.count(), s);
        } else if (x instanceof Map<?,?> m) {
            encodeCountable(OID.JVM_MAP, m.size(), m.entrySet());
        } else if (x instanceof LazySeq lz) {
            encodeUncountable(OID.CLJ_LAZY_SEQ, lz);
        } else if (x instanceof UUID u) {
            encodeUUID(u);
        } else if (x instanceof String s) {
            encodeString(s);
        } else if (x instanceof Serializable) {
            encodeSerializable(x);
        } else {
            throw Error.error("unsupported type: %s %s", x.getClass(), x);
        }
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
