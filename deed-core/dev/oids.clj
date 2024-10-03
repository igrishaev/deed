(ns oids)

(def OIDS
  '[
    NULL                    "`null` (`nil`)"                    nil
    HEADER                  "`deed.Header`"                     "A leading object with metadata about encoding"
    UNSUPPORTED             "`deed.Unsupported`"                "A wrapper for unsupported objects"
    META                    nil                                 "Specifies an object with metadata"
    INT                     "int, java.lang.Integer"            nil
    INT_ZERO                nil                                 "A special OID for 0 int"
    INT_ONE                 nil                                 "A special OID for 1 int"
    INT_MINUS_ONE           nil                                 "A special OID for -1 int"
    SHORT                   "`short`, `java.lang.Short`"        nil
    SHORT_ZERO              nil                                 "A special OID for 0 short"
    SHORT_ONE               nil                                 "A special OID for 1 short"
    SHORT_MINUS_ONE         nil                                 "A special OID for -1 short"
    LONG                    "`long`, `java.lang.Long`"          nil
    LONG_ZERO               nil                                 nil
    LONG_ONE                nil                                 nil
    LONG_MINUS_ONE          nil                                 nil
    IO_INPUT_STREAM         "`java.io.InputStream`"             "When decoding, the bytes are put into a `ByteArrayInputStream`. It's also possible to put them into a temp file and obtain a `FileInputStream`"
    IO_READER               "-"                                 "Not implemented"
    IO_FILE                 "-"                                 "Not implemented"
    IO_BYTEBUFFER           "`java.nio.ByteBuffer`"             nil
    ARR_BYTE                "byte[]"                            nil
    ARR_INT                 "int[]"                             nil
    ARR_SHORT               "short[]"                           nil
    ARR_BOOL                "bool[]"                            nil
    ARR_FLOAT               "float[]"                           nil
    ARR_DOUBLE              "double[]"                          nil
    ARR_OBJ                 "Object[]"                          nil
    ARR_LONG                "long[]"                            nil
    ARR_CHAR                "char[]"                            nil
    REGEX                   "java.util.regex.Pattern"           nil
    CLJ_SORTED_SET          "clojure.lang.PersistentTreeSet"    "A sorted set usually created with `(sorted-set ...)`"
    CLJ_SORTED_SET_EMPTY    nil                                 "A special OID for an empty sorted set"
    CLJ_SORTED_MAP          "clojure.lang.PersistentTreeMap"    "A sorted map usually created with `(sorted-map ...)`"
    CLJ_SORTED_MAP_EMPTY    nil                                 "An empty sorted map"
    URI                     "java.net.URI"                      nil
    URL                     "java.net.URL"                      nil
    EXCEPTION               "java.lang.Exception"               "Keeps message, class name, stack trace, cause (recursively encoded), and all the suppressed exceptions"
    IO_EXCEPTION            nil                                 nil
    THROWABLE               nil                                 nil
    EX_INFO                 nil                                 nil
    EX_NPE                  nil                                 nil
    BOOL_TRUE               "boolean, java.lang.Boolean"        "True value only"
    BOOL_FALSE              "boolean, java.lang.Boolean"        "False value only"
    STRING                  "java.lang.String"                  "Stored as a number of bytes + bytes"
    STRING_EMPTY            nil                                 "A special OID indicating an empty string"
    CHAR                    "char, java.lang.Character"         nil
    CLJ_VEC                 "clojure.lang.APersistentVector"    "A standard Clojure vector"
    CLJ_VEC_EMPTY           nil                                 "A special OID indicating an empty vector"
    CLJ_ATOM                "clojure.lang.Atom"                 "Gets deref-ed when encoding"
    CLJ_REF                 "clojure.lang.Ref"                  "Gets deref-ed when encoding"
    FUTURE                  "`java.util.concurrent.Future`"     "Deed `.get`s the value using timeout from options. When time is up, an exception is throw. When decoded, it's returned as an instance of `deed.FutureWrapper`: a fake object that mimics a future."
    CLJ_SET                 "`clojure.lang.APersistentSet`"     "A standard Clojure immutable set"
    CLJ_SET_EMPTY           nil                                 "A special OID indicating an empty set"
    CLJ_LAZY_SEQ            "`clojure.lang.LazySeq`"            "Encode a lazy sequence produced with `map`, `for`, etc. Stored as a collection of chunks like `<chunk-len><items...>`. When decoding, read until the chunk of zero length is met."
    CLJ_SEQ                 nil                                 nil
    CLJ_LIST                nil                                 nil
    CLJ_LIST_EMPTY          nil                                 nil
    CLJ_QUEUE               nil                                 nil
    CLJ_QUEUE_EMPTY         nil                                 nil
    CLJ_MAP                 nil                                 nil
    CLJ_MAP_EMPTY           nil                                 nil
    CLJ_MAP_ENTRY           nil                                 nil
    CLJ_RECORD              nil                                 nil
    CLJ_TR_VEC              nil                                 nil
    JVM_MAP                 nil                                 nil
    JVM_MAP_ENTRY           nil                                 nil
    UUID                    nil                                 nil
    JVM_LIST                nil                                 nil
    JVM_LIST_EMPTY          nil                                 nil
    JVM_VECTOR              nil                                 nil
    JVM_VECTOR_EMPTY        nil                                 nil
    JVM_ITERABLE            nil                                 nil
    JVM_ITERATOR            nil                                 nil
    JVM_STREAM              nil                                 nil
    CLJ_KEYWORD             nil                                 nil
    CLJ_SYMBOL              nil                                 nil
    UTIL_DATE               nil                                 nil
    DT_LOCAL_DATE           nil                                 nil
    DT_LOCAL_TIME           nil                                 nil
    DT_LOCAL_DATETIME       nil                                 nil
    DT_OFFSET_DATETIME      nil                                 nil
    DT_OFFSET_TIME          nil                                 nil
    DT_DURATION             nil                                 nil
    DT_PERIOD               nil                                 nil
    DT_ZONED_DATETIME       nil                                 nil
    DT_ZONE_ID              nil                                 nil
    DT_INSTANT              nil                                 nil
    SQL_TIMESTAMP           nil                                 nil
    SQL_TIME                nil                                 nil
    SQL_DATE                nil                                 nil
    BYTE                    nil                                 nil
    BYTE_ZERO               nil                                 nil
    BYTE_ONE                nil                                 nil
    BYTE_MINUS_ONE          nil                                 nil
    FLOAT                   nil                                 nil
    FLOAT_ZERO              nil                                 nil
    FLOAT_ONE               nil                                 nil
    FLOAT_MINUS_ONE         nil                                 nil
    DOUBLE                  nil                                 nil
    DOUBLE_ZERO             nil                                 nil
    DOUBLE_ONE              nil                                 nil
    DOUBLE_MINUS_ONE        nil                                 nil
    JVM_BIG_DEC             nil                                 nil
    JVM_BIG_INT             nil                                 nil
    CLJ_BIG_INT             nil                                 nil
    CLJ_RATIO               nil                                 nil
    VECTORZ_AVECTOR         nil                                 nil
    ])


(defn enumerate [coll]
  (map-indexed vector coll))

(defn generateClass []

  (println "//")
  (println "// Generated! See the oids.clj file")
  (println "//")
  (println)
  (println "package deed;")
  (println)
  (println "public class OID {")
  (println)
  (doseq [[i [oid]] (enumerate (partition 3 OIDS))]
    (println (format "    public static final short %-25s = 0x%04X;"
                     oid i)))
  (println)
  (println "}")
  (println)
  )

(defn line [n]
  (apply str (repeat n "-")))

(defn generateTable []

  (println)
  (println (format "| %-6s | %-25s | %-30s | %-150s |" "OID" "TAG" "Class" "Comment"))
  (println (format "|%s|%s|%s|%s|" (line 8) (line 27) (line 32) (line 152)))
  (doseq [[i [oid Type doc]] (enumerate (partition 3 OIDS))]
    (println (format "| 0x%04X | %-25s | %-30s | %-150s |" i oid (str Type) (str doc))))
  (println)
  )
