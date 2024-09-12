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
    BYTEBUFFER
    ARR_BYTE                              ;; +
    ARR_INT                               ;; +
    ARR_SHORT                             ;; +
    ARR_BOOL                              ;; +
    ARR_FLOAT                             ;; +
    ARR_DOUBLE                            ;; +
    ARR_OBJ                               ;; +
    ARR_LONG                              ;; +
    ARR_CHAR                              ;; +
    REGEX                                 ;; +
    CLJ_SORTED_SET                        ;; +
    CLJ_SORTED_SET_EMPTY                  ;; +
    CLJ_SORTED_MAP                        ;; +
    CLJ_SORTED_MAP_EMPTY                  ;; +
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
    CLJ_VEC                               ;; +
    CLJ_VEC_EMPTY                         ;; +
    CLJ_ATOM                              ;; +
    CLJ_REF                               ;; +
    FUTURE                                ;; +
    CLJ_SET                               ;; +
    CLJ_SET_EMPTY                         ;; +
    CLJ_LAZY_SEQ                          ;; +
    CLJ_SEQ                               ;; +
    CLJ_LIST                              ;; +
    CLJ_LIST_EMPTY                        ;; +
    CLJ_QUEUE                             ;; +
    CLJ_QUEUE_EMPTY                       ;; +
    CLJ_MAP                               ;; +
    CLJ_MAP_EMPTY                         ;; +
    CLJ_MAP_ENTRY                         ;; +
    CLJ_RECORD                            ;; +
    JVM_MAP                               ;; +
    JVM_MAP_ENTRY                         ;; +
    UUID                                  ;; +
    JVM_LIST                              ;; +
    JVM_LIST_EMPTY                        ;; +
    JVM_VECTOR                            ;;
    JVM_VECTOR_EMPTY                      ;;
    JVM_ITERABLE                          ;;
    JVM_ITERATOR                          ;;
    JVM_STREAM                            ;;
    CLJ_KEYWORD                           ;; +
    CLJ_SYMBOL                            ;; +
    UTIL_DATE                             ;; +
    DT_LOCAL_DATE                         ;; +
    DT_LOCAL_TIME                         ;; +
    DT_LOCAL_DATETIME                     ;; +
    DT_OFFSET_DATETIME                    ;; +
    DT_OFFSET_TIME                        ;; +
    DT_DURATION                           ;; +
    DT_PERIOD                             ;; +
    DT_ZONED_DATETIME                     ;; +
    DT_ZONE_ID                            ;; +
    DT_INSTANT                            ;; +
    SQL_TIMESTAMP                         ;; +
    SQL_TIME                              ;; +
    SQL_DATE                              ;; +
    BYTE                                  ;; +
    BYTE_ZERO                             ;; +
    BYTE_ONE                              ;; +
    BYTE_MINUS_ONE                        ;; +
    FLOAT                                 ;; +
    FLOAT_ZERO                            ;; +
    FLOAT_ONE                             ;; +
    FLOAT_MINUS_ONE                       ;; +
    DOUBLE                                ;; +
    DOUBLE_ZERO                           ;; +
    DOUBLE_ONE                            ;; +
    DOUBLE_MINUS_ONE                      ;; +
    JVM_BIG_DEC                           ;; +
    JVM_BIG_INT                           ;; +
    CLJ_BIG_INT                           ;; +
    CLJ_RATIO                             ;; +
    CLJ_TR_MAP
    CLJ_TR_SET
    CLJ_TR_VEC
    CLJ_TR_LIST
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
