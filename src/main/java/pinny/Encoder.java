package pinny;

import java.io.*;
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
                throw new RuntimeException("could not open Gzip output stream", e);
            }
        }
        try {
            final BufferedOutputStream buf = new BufferedOutputStream(destination, Const.OUT_BUF_SIZE);
            objectOutputStream = new ObjectOutputStream(buf);
        } catch (IOException e) {
            throw new RuntimeException("could not open object output stream", e);
        }
    }

    public void encode(final Object x) {
        try {
            objectOutputStream.writeObject(x);
        } catch (IOException e) {
            throw new RuntimeException("could not write an object", e);
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
            throw new RuntimeException("could not flush the stream", e);
        }
    }

    @Override
    public void close() {
        try {
            objectOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
