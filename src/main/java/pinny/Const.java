package pinny;

public class Const {
    public static short HEADER_LEN = 32;
    public static int OPT_IN_BUF_SIZE = 0xFFFF;
    public static int OPT_OUT_BUF_SIZE = 0xFFFF;
    public static short HEADER_VERSION = 1;

    public static boolean OPT_USE_GZIP = false;
    public static long OPT_FUTURE_GET_TIMEOUT_MS = 5000;
    public static int OPT_OBJECT_CHUNK_SIZE = 0xFF;
    public static int OPT_BYTE_CHUNK_SIZE = 0xFFFF;

    public static byte BYTE_MINUS_ONE = -1;
    public static byte BYTE_ZERO = 0;
    public static byte BYTE_ONE = 1;
    public static double DOUBLE_ONE = 1;
    public static double DOUBLE_MINUS_ONE = -1;
    public static double DOUBLE_ZERO = 0;
}
