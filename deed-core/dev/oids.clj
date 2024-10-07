(ns oids)

(def OIDS
  '[
    NULL                    "`null` (`nil`)"                           nil
    HEADER                  "`deed.Header`"                            "A leading object with general info about encoding"
    UNSUPPORTED             "`deed.Unsupported`"                       "A wrapper for unsupported objects"
    META                    nil                                        "Specifies an object with metadata"
    INT                     "`int`, `java.lang.Integer`"               nil
    INT_ZERO                nil                                        "A special OID for 0 int"
    INT_ONE                 nil                                        "A special OID for 1 int"
    INT_MINUS_ONE           nil                                        "A special OID for -1 int"
    SHORT                   "`short`, `java.lang.Short`"               nil
    SHORT_ZERO              nil                                        "A special OID for 0 short"
    SHORT_ONE               nil                                        "A special OID for 1 short"
    SHORT_MINUS_ONE         nil                                        "A special OID for -1 short"
    LONG                    "`long`, `java.lang.Long`"                 nil
    LONG_ZERO               nil                                        nil
    LONG_ONE                nil                                        nil
    LONG_MINUS_ONE          nil                                        nil
    IO_INPUT_STREAM         "`java.io.InputStream`"                    "When decoding, the bytes are put into a `ByteArrayInputStream`. It's also possible to put them into a temp file and obtain a `FileInputStream`"
    IO_READER               "-"                                        "Not implemented"
    IO_FILE                 "-"                                        "Not implemented"
    IO_BYTEBUFFER           "`java.nio.ByteBuffer`"                    nil
    ARR_BYTE                "`byte[]`"                                 nil
    ARR_INT                 "`int[]`"                                  nil
    ARR_SHORT               "`short[]`"                                nil
    ARR_BOOL                "`boolean[]`"                              nil
    ARR_FLOAT               "`float[]`"                                nil
    ARR_DOUBLE              "`double[]`"                               nil
    ARR_OBJ                 "`Object[]`"                               nil
    ARR_LONG                "`long[]`"                                 nil
    ARR_CHAR                "`char[]`"                                 nil
    REGEX                   "`java.util.regex.Pattern`"                nil
    CLJ_SORTED_SET          "`clojure.lang.PersistentTreeSet`"         "A sorted set usually created with `(sorted-set ...)`"
    CLJ_SORTED_SET_EMPTY    nil                                        "A special OID for an empty sorted set"
    CLJ_SORTED_MAP          "`clojure.lang.PersistentTreeMap`"         "A sorted map usually created with `(sorted-map ...)`"
    CLJ_SORTED_MAP_EMPTY    nil                                        "An empty sorted map"
    URI                     "`java.net.URI`"                           nil
    URL                     "`java.net.URL`"                           nil
    EXCEPTION               "`java.lang.Exception`"                    "Keeps message, class name, stack trace, cause (recursively encoded), and all the suppressed exceptions"
    IO_EXCEPTION            nil                                        nil
    THROWABLE               nil                                        nil
    EX_INFO                 nil                                        nil
    EX_NPE                  nil                                        nil
    BOOL_TRUE               "`boolean`, `java.lang.Boolean`"           "True value only"
    BOOL_FALSE              "`boolean`, `java.lang.Boolean`"           "False value only"
    STRING                  "`java.lang.String`"                       "Stored as a number of bytes + bytes"
    STRING_EMPTY            nil                                        "A special OID indicating an empty string"
    CHAR                    "`char`, `java.lang.Character`"            nil
    CLJ_VEC                 "`clojure.lang.APersistentVector`"         "A standard Clojure vector"
    CLJ_VEC_EMPTY           nil                                        "A special OID indicating an empty vector"
    CLJ_ATOM                "`clojure.lang.Atom`"                      "Gets deref-ed when encoding"
    CLJ_REF                 "`clojure.lang.Ref`"                       "Gets deref-ed when encoding"
    FUTURE                  "`java.util.concurrent.Future`"            "Deed `.get`s the value using timeout from options. When time is up, an exception is throw. When decoded, it's returned as an instance of `deed.FutureWrapper`: a fake object that mimics a future."
    CLJ_SET                 "`clojure.lang.APersistentSet`"            "A standard Clojure immutable set"
    CLJ_SET_EMPTY           nil                                        "A special OID indicating an empty set"
    CLJ_LAZY_SEQ            "`clojure.lang.LazySeq`"                   "Encode a lazy sequence produced with `map`, `for`, etc. Stored as a collection of chunks like `<chunk-len><items...>`. When decoding, read until the chunk of zero length is met."
    CLJ_SEQ                 "Type depends on the origin collection"    "Becomes a vector when decoding"
    CLJ_LIST                "`clojure.lang.PersistentList`"            nil
    CLJ_LIST_EMPTY          nil                                        "A special OID indicating an empty list"
    CLJ_QUEUE               "`clojure.lang.PersistentQueue`"           nil
    CLJ_QUEUE_EMPTY         nil                                        "A special OID indicating an empty queue"
    CLJ_MAP                 "`clojure.lang.APersistentMap`"            nil
    CLJ_MAP_EMPTY           "`clojure.lang.APersistentMap`"            "An empty Clojure map"
    CLJ_MAP_ENTRY           "`clojure.lang.MapEntry`"                  "A pair of key and value"
    CLJ_RECORD              "`clojure.lang.IRecord`"                   "An instance of `defrecord` object. When decoding, becomes an ordinary map. To preserve the origin type, use the `handle-record` macro (see below)."
    CLJ_TR_VEC              "`c.l.PersistentVector$TransientVector`"   "A transient Clojure vector."
    JVM_MAP                 "`java.util.Map`"                          "A Java map, usually an instance of `HashMap`."
    JVM_MAP_ENTRY           "`java.util.Map$Entry`"                    nil
    UUID                    "`java.util.UUID`"                         nil
    JVM_LIST                "`java.util.List`"                         "When decoding, becomes an instance of `ArrayList`."
    JVM_LIST_EMPTY          "`java.util.List`"                         "A stub for an empty list."
    JVM_VECTOR              "`java.util.Vector`"                       nil
    JVM_VECTOR_EMPTY        nil                                        "An empty Java vector."
    JVM_ITERABLE            "`java.lang.Iterable`"                     "Encoded as uncounted chunked sequence of objects"
    JVM_ITERATOR            nil                                        "When decoding, becomes an instance of `ArrayList`."
    JVM_STREAM              "`java.util.stream.Stream`"                nil
    CLJ_KEYWORD             "`clojure.lang.Keyword`"                   nil
    CLJ_SYMBOL              "`clojure.lang.Symbol`"                    nil
    UTIL_DATE               "`java.util.Date`"                         nil
    DT_LOCAL_DATE           "`java.time.LocalDate`"                    nil
    DT_LOCAL_TIME           "`java.time.LocalTime`"                    nil
    DT_LOCAL_DATETIME       "`java.time.LocalDateTime`"                nil
    DT_OFFSET_DATETIME      "`java.time.OffsetDateTime`"               nil
    DT_OFFSET_TIME          "`java.time.OffsetTime`"                   nil
    DT_DURATION             "`java.time.Duration`"                     nil
    DT_PERIOD               "`java.time.Period`"                       nil
    DT_ZONED_DATETIME       "`java.time.ZonedDateTime`"                nil
    DT_ZONE_ID              "`java.time.ZoneId`"                       nil
    DT_INSTANT              "`java.time.Instant`"                      nil
    SQL_TIMESTAMP           "`java.sql.Timestamp`"                     nil
    SQL_TIME                "`java.sql.Time`"                          nil
    SQL_DATE                "`java.sql.Date`"                          nil
    BYTE                    "`byte`, `java.lang.Byte`"                 nil
    BYTE_ZERO               nil                                        "A stub for byte 0"
    BYTE_ONE                nil                                        "A stub for byte 1"
    BYTE_MINUS_ONE          nil                                        "A stub for byte -1"
    FLOAT                   "`float`, `java.lang.Float`"               nil
    FLOAT_ZERO              nil                                        "A stub for float 0"
    FLOAT_ONE               nil                                        "A stub for float 1"
    FLOAT_MINUS_ONE         nil                                        "A stub for float -1"
    DOUBLE                  "`double`, `java.lang.Double`"             nil
    DOUBLE_ZERO             nil                                        "A stub for double 0"
    DOUBLE_ONE              nil                                        "A stub for double 1"
    DOUBLE_MINUS_ONE        nil                                        "A stub for double -1"
    JVM_BIG_DEC             "`java.math.BigDecimal`"                   nil
    JVM_BIG_INT             "`java.math.BigInteger`"                   nil
    CLJ_BIG_INT             "`clojure.lang.BigInt`"                    nil
    CLJ_RATIO               "`clojure.lang.Ratio`"                     nil
    VECTORZ_AVECTOR         "`mikera.vectorz.AVector`"                 "See the `deed-vectorz` package"
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
