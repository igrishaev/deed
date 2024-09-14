package pinny;

public record ThrowableInfo(
        String message,
        StackTraceElement[] trace,
        Throwable cause,
        Throwable[] suppressed
) {
}
