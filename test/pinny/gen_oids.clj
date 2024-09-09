(ns pinny.gen-oids)

(def OIDS                                 ;; tested?
  '[
    NULL                                  ;; +
    SERIALIZABLE
    INT                                   ;; +
    INT_ZERO                              ;; +
    INT_ONE                               ;; +
    INT_MINUS_ONE                         ;; +
    SHORT                                 ;; +
    SHORT_ZERO                            ;; +
    SHORT_ONE                             ;; +
    SHORT_MINUS_ONE                       ;; +
    LONG                                  ;; +
    LONG_ZERO                             ;; +
    LONG_ONE                              ;; +
    LONG_MINUS_ONE                        ;; +
    IO_INPUT_STREAM
    IO_READER
    IO_FILE
    ARR_BYTE                              ;; +
    ARR_INT
    ARR_SHORT
    ARR_BOOL
    ARR_FLOAT
    ARR_DOUBLE
    ARR_OBJ                               ;; +
    ARR_LONG
    REGEX                                 ;; +
    CLJ_SORTED_SET
    CLJ_SORTED_MAP
    URI                                   ;; +
    URL                                   ;; +
    EXCEPTION
    THROWABLE
    EX_INFO
    BOOL_TRUE                             ;; +
    BOOL_FALSE                            ;; +
    STRING                                ;; +
    STRING_EMPTY                          ;; +
    CHAR                                  ;; +
    CLJ_VEC
    CLJ_VEC_EMPTY
    CLJ_ATOM                              ;; +
    CLJ_REF                               ;; +
    CLJ_FUTURE
    CLJ_SET
    CLJ_SET_EMPTY
    CLJ_LAZY_SEQ
    CLJ_SEQ
    CLJ_LIST
    CLJ_QUEUE
    CLJ_MAP            ;; +
    CLJ_MAP_EMPTY      ;; +
    JVM_MAP
    JVM_MAP_EMPTY
    UUID               ;; +
    JVM_LIST
    CLJ_KEYWORD        ;; +
    CLJ_SYMBOL         ;; +
    DT_DATE
    DT_LOCAL_DATE
    DT_LOCAL_TIME
    DT_LOCAL_DATETIME
    DT_OFFSET_DATETIME
    DT_OFFSET_TIME
    DT_DURATION
    DT_ZONED_DATETIME
    DT_ZONE_ID
    DT_INSTANT
    SQL_TIMESTAMP
    SQL_TIME
    BYTE
    BYTE_ZERO
    BYTE_ONE
    BYTE_MINUS_ONE
    FLOAT              ;; +
    FLOAT_ZERO         ;; +
    FLOAT_ONE          ;; +
    FLOAT_MINUS_ONE    ;; +
    DOUBLE             ;; +
    DOUBLE_ZERO        ;; +
    DOUBLE_ONE         ;; +
    DOUBLE_MINUS_ONE   ;; +
    JVM_BIG_DEC
    JVM_BIG_DEC_ZERO
    JVM_BIG_DEC_ONE
    JVM_BIG_DEC_MINUS_ONE
    JVM_BIG_INT
    JVM_BIG_INT_ZERO
    JVM_BIG_INT_ONE
    JVM_BIG_INT_MINUS_ONE
    CLJ_BIG_INT
    CLJ_BIG_INT_ZERO
    CLJ_BIG_INT_ONE
    CLJ_BIG_INT_MINUS_ONE
    CLJ_RATIO
    ])


(defn enumerate [coll]
  (map-indexed vector coll))

(defn generateClass []

  (println "package pinny;")
  (println)
  (println "public class OID {")
  (println)
  (doseq [[i oid] (enumerate OIDS)]
    (println (format "    public static final short %-25s = 0x%04X;"
                     oid i)))
  (println)
  (println "}")
  (println)


  )
