package pinny;

import java.io.*;
import java.util.zip.GZIPOutputStream;

import clojure.lang.Atom;
import clojure.lang.LazySeq;
import clojure.lang.PersistentVector;
import clojure.core$vec;

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
