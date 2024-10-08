package deed;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class IOTool {

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

    @SuppressWarnings("unused")
    public static GZIPInputStream wrapGZIPInputStream (final InputStream in) {
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

    @SuppressWarnings("unused")
    public static GZIPOutputStream wrapGZIPOutputStream (final OutputStream out) {
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
}
