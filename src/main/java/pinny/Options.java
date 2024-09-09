package pinny;

public record Options(
        boolean useGzip,
        boolean allowSerializable,
        long futureGetTimeoutMs
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
        private boolean allowSerializable = Const.OPT_ALLOW_SERIALIZABLE;
        private long futureGetTimeoutMs = Const.OPT_FUTURE_GET_TIMEOUT_MS;

        @SuppressWarnings("unused")
        public Builder useGzip(final boolean useGzip) {
            this.useGzip = useGzip;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder allowSerializable(final boolean allowSerializable) {
            this.allowSerializable = allowSerializable;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder futureGetTimeoutMs(final long futureGetTimeoutMs) {
            this.futureGetTimeoutMs = futureGetTimeoutMs;
            return this;
        }

        public Options build() {
            return new Options(
                    useGzip,
                    allowSerializable,
                    futureGetTimeoutMs
            );
        }
    }


}
