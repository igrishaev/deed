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

    public static File tempFile() {
        try {
            return File.createTempFile("temp", ".tmp");
        } catch (IOException e) {
            throw Err.error(e, "cannot create a temp file");
        }
    }

    public static ByteArrayInputStream byteArrayInputStream(final ByteArrayOutputStream ba) {
        return new ByteArrayInputStream(ba.toByteArray());
    }

    public static FileInputStream fileInputStream(final File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw Err.error(e, "file %s not found", file);
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
