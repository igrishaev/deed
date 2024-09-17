package pinny;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class IOTool {

    public static BufferedInputStream wrapBufferedInputStream (final InputStream in, final int bufSize) {
        if (in instanceof BufferedInputStream b) {
            return b;
        } else {
            return new BufferedInputStream(in, bufSize);
        }
    }

    public static BufferedOutputStream wrapBufferedOutputStream (final OutputStream out, final int bufSize) {
        if (out instanceof BufferedOutputStream b) {
            return b;
        } else {
            return new BufferedOutputStream(out, bufSize);
        }
    }

    public static GZIPOutputStream wrapGZIPOutputStream(final OutputStream out) {
        if (out instanceof GZIPOutputStream gz) {
            return gz;
        } else {
            try {
                return new GZIPOutputStream(out);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public static GZIPInputStream wrapGZIPInputStream(final InputStream in) {
        if (in instanceof GZIPInputStream gz) {
            return gz;
        } else {
            try {
                return new GZIPInputStream(in);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
