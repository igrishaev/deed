package deed;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class IOTool {

    public static BufferedInputStream wrapBuf (final InputStream in, final int bufSize) {
        if (in instanceof BufferedInputStream b) {
            return b;
        } else {
            return new BufferedInputStream(in, bufSize);
        }
    }

    public static BufferedOutputStream wrapBuf (final OutputStream out, final int bufSize) {
        if (out instanceof BufferedOutputStream b) {
            return b;
        } else {
            return new BufferedOutputStream(out, bufSize);
        }
    }

    public static GZIPOutputStream wrapGzip (final OutputStream out) {
        if (out instanceof GZIPOutputStream gz) {
            return gz;
        } else {
            try {
                return new GZIPOutputStream(out);
            } catch (IOException e) {
                throw Err.error(e, "could not create GZIPOutputStream");
            }

        }
    }

    public static GZIPInputStream wrapGzip (final InputStream in) {
        if (in instanceof GZIPInputStream gz) {
            return gz;
        } else {
            try {
                return new GZIPInputStream(in);
            } catch (IOException e) {
                throw Err.error(e, "could not create GZIPInputStream");
            }
        }
    }


}
