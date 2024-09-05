package pinny;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

public final class Decoder implements Iterable<Object>, AutoCloseable {

    final private BufferedInputStream bufferedInputStream;
    final private ObjectInputStream objectInputStream;
    final private EOF EOF;

    public Decoder(final InputStream inputStream) {
        this(inputStream, Options.standard());
    }

    public Decoder(final InputStream inputStream, final Options options) {
        InputStream source = inputStream;
        if (options.useGzip()) {
            try {
                source = new GZIPInputStream(inputStream);
            } catch (IOException e) {
                throw new RuntimeException("could not open a Gzip input stream", e);
            }
        }
        try {
            bufferedInputStream = new BufferedInputStream(source, Const.IN_BUF_SIZE);
            objectInputStream = new ObjectInputStream(bufferedInputStream);
        } catch (IOException e) {
            throw new RuntimeException("could not open object input stream", e);
        }
        EOF = new EOF();
    }

    public Object decode() {
        int r;

        bufferedInputStream.mark(1);
        try {
            r = bufferedInputStream.read();
            bufferedInputStream.reset();
        } catch (IOException e) {
            throw new RuntimeException("could not read() from the input stream", e);
        }

        if (r == -1) {
            return EOF;
        }

        try {
            return objectInputStream.readObject();
        } catch (IOException e) {
            throw new RuntimeException("could not read object from the stream", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found", e);
        }
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
            objectInputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("could not close the stream", e);
        }
    }
}
