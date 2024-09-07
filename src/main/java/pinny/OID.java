package pinny;

public class OID {

    public static final short NULL            = 0x0000; //
    public static final short SERIALIZABLE    = 0x0001;

    public static final short INT             = 0x0010; //
    public static final short INT_ZERO        = 0x0011;
    public static final short INT_ONE         = 0x0012;
    public static final short INT_MINUS_ONE   = 0x0013;

    public static final short SHORT           = 0x0020; //
    public static final short SHORT_ZERO      = 0x0021;
    public static final short SHORT_ONE       = 0x0022;
    public static final short SHORT_MINUS_ONE = 0x0023;

    public static final short LONG            = 0x0030; //
    public static final short LONG_ZERO       = 0x0031;
    public static final short LONG_ONE        = 0x0032;
    public static final short LONG_MINUS_ONE  = 0x0033;

//    INPUT_STREAM
//    READER

//    ARR_BYTE
//    ARR_INT
//    ARR_SHORT
//    ARR_BOOL
//    ARR_FLOAT
//    ARR_DOUBLE
//    ARR_OBJ
//    ARR_LONG

 public static final short REGEX  = 1;               //
//    CLJ_SORTED_SET
//    CLJ_SORTED_MAP
//    URI
//    URL
//    EXCEPTION
//    THROWABLE
//    EX_INFO


    public static final short BOOL            = 0x0040; //
    public static final short BOOL_TRUE       = 0x0041;
    public static final short BOOL_FALSE      = 0x0042;

    public static final short STRING          = 0x0050; //
    public static final short CHAR            = 0x0050;

    public static final short CLJ_VEC         = 0x0060; //
    public static final short CLJ_VEC_EMPTY   = 0x0061;

    public static final short CLJ_ATOM   = 0x0061; //
    public static final short CLJ_REF   = 0x0061; //
    public static final short CLJ_FUTURE   = 0x0061;

    public static final short CLJ_SET         = 0x0070;
    public static final short CLJ_SET_EMPTY   = 0x0071;

    public static final short CLJ_LAZY_SEQ    = 0x0080;
    public static final short CLJ_SEQ         = 0x0081;
    public static final short CLJ_LIST        = 0x0082;
    public static final short CLJ_QUEUE       = 0x0083;

    public static final short CLJ_MAP         = 0x0090;
    public static final short CLJ_MAP_EMPTY   = 0x0091;

    public static final short JVM_MAP         = 0x00a0;
    public static final short JVM_MAP_EMPTY   = 0x00a1;

    public static final short UUID            = 0x00b0; //

    public static final short JVM_LIST        = 0x00c0;

    public static final short CLJ_KEYWORD     = 0x00d0; //
    public static final short CLJ_SYMBOL      = 0x00d1; //

    public static final short DT_DATE         = 0x00e2; //
    public static final short DT_SQL_DATE     = 0x00e2;
    public static final short DT_LOCAL_DATE   = 0x00e0;  //
    public static final short DT_LOCAL_TIME   = 0x00e1;  //
    public static final short DT_LOCAL_DATETIME   = 0x00e1;
    public static final short DT_OFFSET_DATETIME   = 0x00e1;
    public static final short DT_OFFSET_TIME   = 0x00e1;
    public static final short DT_DURATION   = 0x00e1;
    public static final short DT_ZONED_DATETIME   = 0x00e1;
    public static final short DT_ZONE_ID       = 0x00e1;
    public static final short DT_INSTANT      = 0x00e3;   //

    public static final short BYTE            = 0x00f0; //
    public static final short BYTE_ZERO       = 0x00f1;
    public static final short BYTE_ONE        = 0x00f2;
    public static final short BYTE_MINUS_ONE  = 0x00f2;

    public static final short FLOAT           = 0x0100; //
    public static final short FLOAT_ZERO      = 0x0101;
    public static final short FLOAT_ONE       = 0x0102;
    public static final short FLOAT_MINUS_ONE = 0x0103;

    public static final short DOUBLE           = 0x0104; //
    public static final short DOUBLE_ZERO      = 0x0105;
    public static final short DOUBLE_ONE       = 0x0106;
    public static final short DOUBLE_MINUS_ONE = 0x0107;

    public static final short JVM_BIG_DEC           = 0x0108; //
    public static final short JVM_BIG_DEC_ZERO      = 0x0109;
    public static final short JVM_BIG_DEC_ONE       = 0x010a;
    public static final short JVM_BIG_DEC_MINUS_ONE = 0x010b;

    public static final short JVM_BIG_INT           = 0x0108; //
    public static final short JVM_BIG_INT_ZERO      = 0x0109;
    public static final short JVM_BIG_INT_ONE       = 0x010a;
    public static final short JVM_BIG_INT_MINUS_ONE = 0x010b;

    public static final short CLJ_BIG_INT           = 0x0108; //
    public static final short CLJ_BIG_INT_ZERO      = 0x0109;
    public static final short CLJ_BIG_INT_ONE       = 0x010a;
    public static final short CLJ_BIG_INT_MINUS_ONE = 0x010b;

    public static final short CLJ_RATIO           = 0x0108; //

}
