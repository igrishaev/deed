(ns pinny.core
  (:require
   [clojure.java.io :as io])
  (:import
   (java.util.concurrent Future)
   (java.io IOException
            InputStream)
   (clojure.lang IPersistentVector
                 APersistentVector
                 PersistentVector
                 IPersistentSet
                 APersistentSet
                 APersistentMap
                 PersistentList
                 ExceptionInfo
                 PersistentList$EmptyList
                 PersistentTreeSet
                 PersistentTreeMap
                 PersistentQueue
                 IRecord
                 MapEntry
                 ASeq
                 Keyword
                 Symbol
                 Ratio
                 Atom
                 BigInt
                 Ref
                 LazySeq)
   (java.util.stream Stream)
   (java.math BigInteger
              BigDecimal)
   (java.net URL
             URI)
   (java.time LocalDate
              LocalTime
              LocalDateTime
              OffsetDateTime
              ZonedDateTime
              OffsetTime
              Instant
              Duration
              Period
              ZoneId)
   (java.util UUID
              AbstractCollection
              Map
              Map$Entry
              List
              Vector
              Iterator
              Date)
   (java.util.regex Pattern)
   (pinny Encoder Decoder Err EOF OID)))

(set! *warn-on-reflection* true)

(defprotocol IEncode
  (-encode [this ^Encoder encoder]))

(extend-protocol IEncode

  ;;
  ;; Defaults
  ;;

  nil
  (-encode [this ^Encoder encoder]
    (.encodeNULL encoder))

  Object
  (-encode [this ^Encoder encoder]
    (throw (Err/error nil
                      "don't know how to encode: %s %s"
                      (into-array Object [(type this) this]))))

  ;;
  ;; Numbers
  ;;

  Byte
  (-encode [this ^Encoder encoder]
    (.encodeByte encoder this))

  Short
  (-encode [this ^Encoder encoder]
    (.encodeShort encoder this))

  Integer
  (-encode [this ^Encoder encoder]
    (.encodeInteger encoder this))

  Float
  (-encode [this ^Encoder encoder]
    (.encodeFloat encoder this))

  Double
  (-encode [this ^Encoder encoder]
    (.encodeDouble encoder this))

  Long
  (-encode [this ^Encoder encoder]
    (.encodeLong encoder this))

  BigInteger
  (-encode [this ^Encoder encoder]
    (.encodeBigInteger encoder this))

  BigDecimal
  (-encode [this ^Encoder encoder]
    (.encodeBigDecimal encoder this))

  BigInt
  (-encode [this ^Encoder encoder]
    (.encodeBigInt encoder this))

  Ratio
  (-encode [this ^Encoder encoder]
    (.encodeRatio encoder this))

  ;;
  ;; IO
  ;;

  InputStream
  (-encode [this ^Encoder encoder]
    (.encodeInputStream encoder this))

  ;;
  ;; String
  ;;

  String
  (-encode [this ^Encoder encoder]
    (.encodeString encoder this))

  Character
  (-encode [this ^Encoder encoder]
    (.encodeCharacter encoder this))

  ;;
  ;; Misc
  ;;

  Boolean
  (-encode [this ^Encoder encoder]
    (.encodeBoolean encoder this))

  UUID
  (-encode [this ^Encoder encoder]
    (.encodeUUID encoder this))

  Pattern
  (-encode [this ^Encoder encoder]
    (.encodePattern encoder this))

  ;;
  ;; Exceptions
  ;;

  Throwable
  (-encode [this ^Encoder encoder]
    (.encodeThrowable encoder this))

  Exception
  (-encode [this ^Encoder encoder]
    (.encodeException encoder this))

  ExceptionInfo
  (-encode [this ^Encoder encoder]
    (.encodeExceptionInfo encoder this))

  IOException
  (-encode [this ^Encoder encoder]
    (.encodeIOException encoder this))

  NullPointerException
  (-encode [this ^Encoder encoder]
    (.encodeNullPointerException encoder this))

  ;;
  ;; Net
  ;;

  URL
  (-encode [this ^Encoder encoder]
    (.encodeURL encoder this))

  URI
  (-encode [this ^Encoder encoder]
    (.encodeURI encoder this))

  ;;
  ;; Keyword/Symbol
  ;;

  Keyword
  (-encode [this ^Encoder encoder]
    (.encodeKeyword encoder this))

  Symbol
  (-encode [this ^Encoder encoder]
    (.encodeSymbol encoder this))

  ;;
  ;; Deref
  ;;

  Atom
  (-encode [this ^Encoder encoder]
    (.encodeAtom encoder this))

  Ref
  (-encode [this ^Encoder encoder]
    (.encodeRef encoder this))

  Future
  (-encode [this ^Encoder encoder]
    (.encodeFuture encoder this))

  ;;
  ;; Clojure collections
  ;;

  PersistentList
  (-encode [this ^Encoder encoder]
    (.encodeClojureList encoder this))

  PersistentList$EmptyList
  (-encode [this ^Encoder encoder]
    (.encodeClojureEmptyList encoder this))

  MapEntry
  (-encode [this ^Encoder encoder]
    (.encodeClojureMapEntry encoder this))

  PersistentQueue
  (-encode [this ^Encoder encoder]
    (.encodeClojureQueue encoder this))

  APersistentVector
  (-encode [this ^Encoder encoder]
    (.encodeAPersistentVector encoder this))

  APersistentMap
  (-encode [this ^Encoder encoder]
    (.encodeAPersistentMap encoder this))

  PersistentTreeSet
  (-encode [this ^Encoder encoder]
    (.encodeSortedSet encoder this))

  PersistentTreeMap
  (-encode [this ^Encoder encoder]
    (.encodeSortedMap encoder this))

  APersistentSet
  (-encode [this ^Encoder encoder]
    (.encodeAPersistentSet encoder this))

  ASeq
  (-encode [this ^Encoder encoder]
    (.encodeClojureSeq encoder this))

  LazySeq
  (-encode [this ^Encoder encoder]
    (.encodeLazySeq encoder this))

  ;;
  ;; Clojure records
  ;;

  IRecord
  (-encode [this ^Encoder encoder]
    (.encodeRecord encoder this))

  ;;
  ;; Java Collections
  ;;

  Stream
  (-encode [this ^Encoder encoder]
    (.encodeJavaStream encoder this))

  List
  (-encode [this ^Encoder encoder]
    (.encodeJavaList encoder this))

  Vector
  (-encode [this ^Encoder encoder]
    (.encodeJavaVector encoder this))

  Iterable
  (-encode [this ^Encoder encoder]
    (.encodeJavaIterable encoder this))

  Iterator
  (-encode [this ^Encoder encoder]
    (.encodeJavaIterator encoder this))

  Map
  (-encode [this ^Encoder encoder]
    (.encodeMap encoder this))

  Map$Entry
  (-encode [this ^Encoder encoder]
    (.encodeJavaMapEntry encoder this))

  ;;
  ;; SQL
  ;;

  java.sql.Date
  (-encode [this ^Encoder encoder]
    (.encodeSqlDate encoder this))

  java.sql.Timestamp
  (-encode [this ^Encoder encoder]
    (.encodeSqlTimestamp encoder this))

  java.sql.Time
  (-encode [this ^Encoder encoder]
    (.encodeSqlTime encoder this))

  ;;
  ;; Date & time
  ;;

  LocalDateTime
  (-encode [this ^Encoder encoder]
    (.encodeLocalDateTime encoder this))

  OffsetDateTime
  (-encode [this ^Encoder encoder]
    (.encodeOffsetDateTime encoder this))

  OffsetTime
  (-encode [this ^Encoder encoder]
    (.encodeOffsetTime encoder this))

  ZonedDateTime
  (-encode [this ^Encoder encoder]
    (.encodeZonedDateTime encoder this))

  java.util.Date
  (-encode [this ^Encoder encoder]
    (.encodeUtilDate encoder this))

  LocalDate
  (-encode [this ^Encoder encoder]
    (.encodeLocalDate encoder this))

  LocalTime
  (-encode [this ^Encoder encoder]
    (.encodeLocalTime encoder this))

  Instant
  (-encode [this ^Encoder encoder]
    (.encodeInstant encoder this))

  Duration
  (-encode [this ^Encoder encoder]
    (.encodeDuration encoder this))

  Period
  (-encode [this ^Encoder encoder]
    (.encodePeriod encoder this))

  ZoneId
  (-encode [this ^Encoder encoder]
    (.encodeZoneId encoder this)))




;;
;; Arrays (these forms don't work when merged with the previous form)
;;

(extend-protocol IEncode
  (Class/forName "[B") ;; byte
  (-encode [this ^Encoder encoder]
    (.encodeByteArray encoder this)))

(extend-protocol IEncode
  (Class/forName "[S") ;; short
  (-encode [this ^Encoder encoder]
    (.encodeShortArray encoder this)))

(extend-protocol IEncode
  (Class/forName "[I") ;; int
  (-encode [this ^Encoder encoder]
    (.encodeIntArray encoder this)))

(extend-protocol IEncode
  (Class/forName "[J") ;; long
  (-encode [this ^Encoder encoder]
    (.encodeLongArray encoder this)))

(extend-protocol IEncode
  (Class/forName "[F") ;; float
  (-encode [this ^Encoder encoder]
    (.encodeFloatArray encoder this)))

(extend-protocol IEncode
  (Class/forName "[D") ;; double
  (-encode [this ^Encoder encoder]
    (.encodeDoubleArray encoder this)))

(extend-protocol IEncode
  (Class/forName "[Z") ;; bool
  (-encode [this ^Encoder encoder]
    (.encodeBoolArray encoder this)))

(extend-protocol IEncode
  (Class/forName "[C") ;; char
  (-encode [this ^Encoder encoder]
    (.encodeCharArray encoder this)))

(extend-protocol IEncode
  (Class/forName "[Ljava.lang.Object;") ;; object
  (-encode [this ^Encoder encoder]
    (.encodeObjectArray encoder this)))


(defmulti -decode
  (fn [oid decoder]
    oid))

(defmethod -decode :default
  [oid decoder]
  (throw (ex-info "aaa" {})))


;;
;; API
;;

(defn encode-multi ^Long [^Encoder encoder coll]
  (.encodeMulti encoder coll))

(defn encode ^Long [^Encoder encoder x]
  (.encode encoder x))

(defmacro with-encoder [[bind dest] & body]
  `(with-open [output# (io/output-stream ~dest)
               ~bind (new Encoder -encode output#)]
     ~@body))


(defmacro with-decoder [[bind source] & body]
  `(with-open [input# (io/input-stream ~source)
               ~bind (new Decoder -decode input#)]
     ~@body))


(defn decode [^Decoder decoder]
  (.decode decoder))


(defn eof? [x]
  (instance? EOF x))


(defmacro expand-encode [[type encoder] & body]
  )

(defmacro expand-decode [[oid decoder] & body]
  )

#_
(expand-encode [clojure.foo.Test e]
  (sdfadsf fsdf)
  (sdfsdf sdf sf)
  (sdfsdfg 3))

#_
(expand-decode [0x1323 d]
  (sdfadsf fsdf)
  (sdfsdf sdf sf)
  (sdfsdfg 3))



(defmacro handle-record [OID RecordClass]
  (let [create
        (symbol (str RecordClass "/create"))]
    `(do

       (extend-protocol IEncode
         ~RecordClass
         (-encode [this# ^Encoder encoder#]
           (.encodeAsMap encoder# ~OID this#)))

       (defmethod -decode ~OID
         [_# ^Decoder decoder#]
         (~create (.readClojureMap decoder#)))

       nil)))


#_
(defrecord Foo [a b c])


#_
(handle-record 123 Foo)


(comment

  (require '[clojure.string :as str])

  (def ^String STRING
    (str/join (repeat 3000 "abcabcabcabcabcabcabcabcabcabcabcabc")))

  (with-encoder [e (io/file "test.aaa")]
    (encode e (-> (subs STRING 0 0xFFFF) .getBytes (io/input-stream))))

  (with-encoder [e (io/file "test.aaa")]
    (encode e (-> STRING .getBytes (io/input-stream))))

  (with-encoder [e (io/file "test.aaa")]
    (encode e (-> "hello" .getBytes (io/input-stream))))

  (with-encoder [e (io/file "test.aaa")]
    (encode e (try
                (/ 0 0)
                (catch Exception e
                  e))))

  (with-encoder [e (io/file "test.aaa")]
    (encode e (new Foo 1 2 3)))

  (with-decoder [dec (io/file "test.aaa")]
    (decode dec))



  (with-encoder [e (io/file "test.aaa")]
    (encode e {:aaa 1}))

  (with-encoder [e (io/file "test.aaa")]
    (encode e 1)
    (encode e 2)
    (encode e 3))

  (with-encoder [e (io/file "test.aaa")]
    (encode-multi e [1 2 3]))

  (with-decoder [dec (io/file "test.aaa")]

    [(decode dec)
     (decode dec)
     (decode dec)
     ])

  )
