package deed;

public record Options(
        boolean useGzip,
        long futureGetTimeoutMs,
        int objectChunkSize,
        int byteChunkSize,
        int bufInputSize,
        int bufOutputSize,
        int uncountableMaxItems,
        boolean encodeUnsupported
) {

    public static Options standard() {
        return new Builder().build();
    }

    @SuppressWarnings("unused")
    public static Builder builder() {
        return new Builder();
    }

    public final static class Builder {

        private boolean useGzip = Const.OPT_USE_GZIP;
        private long futureGetTimeoutMs = Const.OPT_FUTURE_GET_TIMEOUT_MS;
        private int objectChunkSize = Const.OPT_OBJECT_CHUNK_SIZE;
        private int byteChunkSize = Const.OPT_BYTE_CHUNK_SIZE;
        private int bufInputSize = Const.OPT_IN_BUF_SIZE;
        private int bufOutputSize = Const.OPT_OUT_BUF_SIZE;
        private int uncountableMaxItems = Const.OPT_UNCOUNTABLE_MAX_ITEMS;
        private boolean encodeUnsupported = Const.OPT_ENCODE_UNSUPPORTED;

        @SuppressWarnings("unused")
        public Builder useGzip(final boolean useGzip) {
            this.useGzip = useGzip;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder futureGetTimeoutMs(final long futureGetTimeoutMs) {
            this.futureGetTimeoutMs = futureGetTimeoutMs;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder objectChunkSize(final int objectChunkSize) {
            this.objectChunkSize = objectChunkSize;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder byteChunkSize(final int byteChunkSize) {
            this.byteChunkSize = byteChunkSize;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder bufInputSize(final int bufInputSize) {
            this.bufInputSize = bufInputSize;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder bufOutputSize(final int bufOutputSize) {
            this.bufOutputSize = bufOutputSize;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder uncountableMaxItems(final int uncountableMaxItems) {
            this.uncountableMaxItems = uncountableMaxItems;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder encodeUnsupported(final boolean encodeUnsupported) {
            this.encodeUnsupported = encodeUnsupported;
            return this;
        }

        public Options build() {
            return new Options(
                    useGzip,
                    futureGetTimeoutMs,
                    objectChunkSize,
                    byteChunkSize,
                    bufInputSize,
                    bufOutputSize,
                    uncountableMaxItems,
                    encodeUnsupported
            );
        }
    }


}
