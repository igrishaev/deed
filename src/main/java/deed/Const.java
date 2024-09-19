package deed;

public class Const {
    public static short HEADER_GAP = 30;
    public static short HEADER_VERSION = 1;

    public static int OPT_IN_BUF_SIZE = 0xFFFF;
    public static int OPT_OUT_BUF_SIZE = 0xFFFF;

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

    public static int LEN_INT = 4;
    public static int LEN_OID = 2;
    public static int LEN_SHORT = 2;
    public static int LEN_LONG = 8;
    public static int LEN_DOUBLE = 8;
    public static int LEN_FLOAT = 4;
    public static int LEN_BYTE = 1;
    public static int LEN_CHAR = 2;
}