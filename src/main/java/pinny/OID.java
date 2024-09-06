package pinny;

public class OID {

    public static final short NIL             = 0x0000;
    public static final short SERIALIZABLE    = 0x0001;

    public static final short INT             = 0x0010;
    public static final short INT_ZERO        = 0x0011;
    public static final short INT_ONE         = 0x0012;
    public static final short INT_MINUS_ONE   = 0x0013;

    public static final short SHORT           = 0x0020;
    public static final short SHORT_ZERO      = 0x0021;
    public static final short SHORT_ONE       = 0x0022;
    public static final short SHORT_MINUS_ONE = 0x0023;

    public static final short LONG            = 0x0030;
    public static final short LONG_ZERO       = 0x0031;
    public static final short LONG_ONE        = 0x0032;
    public static final short LONG_MINUS_ONE  = 0x0033;

    public static final short BOOL            = 0x0040;
    public static final short BOOL_TRUE       = 0x0041;
    public static final short BOOL_FALSE      = 0x0042;

    public static final short STRING          = 0x0050;

    public static final short CLJ_VEC         = 0x0060;
    public static final short CLJ_VEC_EMPTY   = 0x0061;

    public static final short CLJ_SET         = 0x0070;
    public static final short CLJ_SET_EMPTY   = 0x0071;

    public static final short CLJ_LAZY_SEQ    = 0x0080;

    public static final short CLJ_MAP         = 0x0090;
    public static final short CLJ_MAP_EMPTY   = 0x0091;

    public static final short JVM_MAP         = 0x00a0;
    public static final short JVM_MAP_EMPTY   = 0x00a1;

    public static final short UUID            = 0x00b0;

}
