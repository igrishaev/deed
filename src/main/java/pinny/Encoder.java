package pinny;

import java.io.*;
import java.util.Iterator;
import java.util.zip.GZIPOutputStream;

import clojure.lang.Atom;
import clojure.lang.IPersistentVector;
import clojure.lang.RT;

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
        try {
            final BufferedOutputStream buf = new BufferedOutputStream(destination, Const.OUT_BUF_SIZE);
            objectOutputStream = new ObjectOutputStream(buf);
        } catch (IOException e) {
            throw Error.error(e, "could not open object output stream");
        }
    }

    private int writeInt(final int i) {
        try {
            objectOutputStream.writeInt(i);
        } catch (IOException e) {
            throw Error.error(e, "could not write integer %s", i);
        }
        return 4;
    }

    private int writeShort(final short s) {
        try {
            objectOutputStream.writeShort(s);
        } catch (IOException e) {
            throw Error.error(e, "could not write short %s", s);
        }
        return 2;
    }

    public int encode2(final Object x) {
        if (x instanceof Integer i) {
            return writeInt(OID.INT) + writeInt(i);
        } else if (x instanceof Short s) {
            return writeInt(OID.SHORT) + writeShort(s);
        } else if (x instanceof IPersistentVector v) {
            int sum = writeInt(OID.CLJ_VECTOR);
            final Iterator<?> iter = RT.iter(v);
            while (iter.hasNext()) {
                sum += encode2(iter.next());
            }
            return sum;
        } else {
            throw Error.error("unsupported type: %s", x);
        }
    }


    public void encode(final Object x) {
//        if (x instanceof LazySeq lz) {
//            encode(core$vec.invokeStatic(lz));
//        } else
        if (x instanceof Atom a) {
            encode(a.deref());
        } else {
            try {
                objectOutputStream.writeObject(x);
            } catch (IOException e) {
                throw Error.error(e, "could not write an object: %s", x);
            }
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
