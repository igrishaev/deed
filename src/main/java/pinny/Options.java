package pinny;

public record Options(
        boolean useGzip
) {

    public static Options standard() {
        return new Builder().build();
    }

    @SuppressWarnings("unused")
    public static Builder builder() {
        return new Builder();
    }

    public final static class Builder {
        private boolean useGzip = Const.USE_GZIP;

        @SuppressWarnings("unused")
        public Builder useGzip(final boolean useGzip) {
            this.useGzip = useGzip;
            return this;
        }

        public Options build() {
            return new Options(
                    useGzip
            );
        }
    }


}
