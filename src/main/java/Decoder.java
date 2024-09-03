import clojure.lang.Atom;
import clojure.lang.Keyword;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public final class Decoder {

    final private ObjectInputStream inputStream;

    public Decoder(final InputStream source) throws IOException {
        this.inputStream = new ObjectInputStream(source);
    }

    private void error(final Throwable e) {
        throw new RuntimeException("aaa", e);
    }

    private Integer readInt() {
        try {
            return inputStream.readInt();
        } catch (IOException e) {
            error(e);
            return 0;
        }
    }

    private String readString() {
        final int len = readInt();
        final byte[] buf = new byte[len];
        try {
            inputStream.readFully(buf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new String(buf, StandardCharsets.UTF_8);
    }

    private Boolean readBoolean() {
        try {
            return inputStream.readBoolean();
        } catch (IOException e) {
            error(e);
            return false;
        }
    }

    public Object decode() {
        final int tag = readInt();
        return switch (tag) {

            case OID.BOOL -> readBoolean();
            case OID.INT -> readInt();

            case OID.STRING -> readString();

            case OID.ARRAY_LIST -> {
                final ArrayList<Object> result = new ArrayList<>();
                final int size = readInt();
                for (int i = 0; i < size; i++) {
                    Object item = decode();
                    result.add(item);
                }
                yield result;
            }

            case OID.ATOM -> {
                final Object x = decode();
                yield new Atom(x);
            }

            case OID.KEYWORD -> Keyword.intern(readString());

            default -> throw new IllegalStateException("Unexpected value: " + tag);
        };
    }

}
