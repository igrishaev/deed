import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import clojure.lang.Atom;
import clojure.lang.Keyword;

public final class Encoder {

    private final ObjectOutputStream outputStream;

    private int error(final Throwable e, final Object x) {
        throw new RuntimeException("hello", e);
    }

    private int writeInt(final Integer i) {
        try {
            outputStream.writeInt(i);
            return 4;
        } catch (IOException e) {
            return error(e, i);
        }
    }

    private int writeShort(final Short s) {
        try {
            outputStream.writeShort(s);
            return 2;
        } catch (IOException e) {
            return error(e, s);
        }
    }

    private int writeBool(final Boolean b) {
        try {
            outputStream.writeBoolean(b);
            return 1;
        } catch (IOException e) {
            return error(e, b);
        }
    }

    private int writeLong(final Long l) {
        try {
            outputStream.writeLong(l);
            return 8;
        } catch (IOException e) {
            return error(e, l);
        }
    }

    private int writeFloat(final Float f) {
        try {
            outputStream.writeFloat(f);
            return 4;
        } catch (IOException e) {
            return error(e, f);
        }
    }

    private int writeDouble(final Double d) {
        try {
            outputStream.writeDouble(d);
            return 8;
        } catch (IOException e) {
            return error(e, d);
        }
    }

    private int writeChar(final Character c) {
        try {
            outputStream.writeChar(c);
            return 2;
        } catch (IOException e) {
            return error(e, c);
        }
    }

    private int writeString(final String s) {
        final byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            return error(e, s);
        }
        return bytes.length;
    }

    public Encoder(final OutputStream source) {
        try {
            this.outputStream = new ObjectOutputStream(source);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void flush() {
        try {
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int encode(final Object x) {
        if (x instanceof Boolean b) {
            return writeInt(OID.BOOL) + writeBool(b);
        } else if (x instanceof Integer i) {
            return writeInt(OID.INT) + writeInt(i);
        } else if (x instanceof String s) {
            return writeInt(OID.STRING) + writeInt(s.length()) + writeString(s);
        } else if (x instanceof Float f) {
            return writeInt(OID.FLOAT) + writeFloat(f);
        } else if (x instanceof Double d) {
            return writeInt(OID.DOUBLE) + writeDouble(d);
        } else if (x instanceof Long l) {
            return writeInt(OID.LONG) + writeLong(l);
        } else if (x instanceof ArrayList<?> al) {
            int sum = 0;
            sum += writeInt(OID.ARRAY_LIST);
            sum += writeInt(al.size());
            for (Object item: al) {
                sum += encode(item);
            }
            return sum;
        } else if (x instanceof HashMap<?,?> hm) {
            int sum = 0;
            sum += writeInt(OID.HASH_MAP);
            sum += writeInt(hm.size());
            for (Map.Entry<?,?> me: hm.entrySet()) {
                sum += encode(me.getKey());
                sum += encode(me.getValue());
            }
            return sum;

        } else if (x instanceof Keyword kw) {
            return writeInt(OID.KEYWORD) + writeString(kw.toString().substring(1));
        } else if (x instanceof Atom a) {
            return writeInt(OID.ATOM) + encode(a.deref());
        } else {
            throw new RuntimeException("unknown type: " + x.toString());
        }

    }






}
