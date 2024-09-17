package pinny;

import java.nio.ByteBuffer;

public record Header(
        short version,        // 2
        boolean useGzip       // 1
) {

    public static Header of(final Options options) {
        return new Header(
                options.verson(),
                options.useGzip()
        );
    }

    public byte[] toByteArray() {
        final ByteBuffer buf = ByteBuffer.allocate(Const.HEADER_LEN);
        buf.putShort(version);
        buf.put(useGzip ? (byte) 1 : 0);
        return buf.array();
    }

    public static Header fromByteArray(final byte[] bytes) {
        final ByteBuffer bb = ByteBuffer.wrap(bytes);
        final short version = bb.getShort();
        final boolean useGzip = switch (bb.get()) {
            case 0: yield false;
            case 1: yield true;
            default: throw Err.error("aaa");
        };
        return new Header(version, useGzip);
    }

}
