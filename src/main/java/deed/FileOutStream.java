package deed;

import java.io.*;

public class FileOutStream extends OutputStream {

    private final OutputStream outputStream;
    private final File file;

    private FileOutStream(final File file) {
        this.file = file;
        try {
            this.outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw Err.error(e, "file not found: %s", file.toString());
        }
    }

    @SuppressWarnings("unused")
    public static FileOutStream of(final File file) {
        return new FileOutStream(file);
    }

    public static FileOutStream ofTemp() {
        return new FileOutStream(IOTool.tempFile());
    }

    public File file() {
        return file;
    }

    @Override
    public void write(final int b) throws IOException {
        this.outputStream.write(b);
    }
}
