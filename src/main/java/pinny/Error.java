package pinny;

public class Error {

    public static RuntimeException error(final Throwable e, final String message) {
        return new RuntimeException(message, e);
    }

    public static RuntimeException error(final Throwable e, final String template, final Object... args) {
        return new RuntimeException(String.format(template, args), e);
    }

    public static RuntimeException error(final String template, final Object... args) {
        return new RuntimeException(String.format(template, args));
    }

}
