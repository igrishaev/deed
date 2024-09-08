(ns pinny.core
  (:require
   [clojure.java.io :as io])
  (:import
   (java.net URL URI)
   (java.sql Time Timestamp)
   (java.util.regex Pattern)
   (java.time LocalDate
              LocalTime
              Instant)
   (java.util UUID
              Date)
   (java.math BigInteger
              BigDecimal)
   (clojure.lang IPersistentVector
                 PersistentVector
                 IPersistentSet
                 IPersistentList
                 Keyword
                 Symbol
                 Ratio
                 Atom
                 BigInt
                 Ref
                 LazySeq)
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
    (throw (Err/error "don't know how to encode: %s %s" (type this) this)))

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
  ;; String
  ;;

  String
  (-encode [this ^Encoder encoder]
    (.encodeString encoder this))

  ;; Character
  ;; (-encode [this ^Encoder encoder]
  ;;   (.encodeCharacter encoder this))

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

  Keyword
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

  ;;
  ;; Clojure collections
  ;;

  PersistentVector
  (-encode [this ^Encoder encoder]
    (.encodeCountable encoder OID/CLJ_VEC (count this) this))

  ;;
  ;; Date & time
  ;;

  Timestamp
  (-encode [this ^Encoder encoder]
    (.encodeTimestamp encoder this))

  Time
  (-encode [this ^Encoder encoder]
    (.encodeTime encoder this))

  Date
  (-encode [this ^Encoder encoder]
    (.encodeDate encoder this))

  LocalDate
  (-encode [this ^Encoder encoder]
    (.encodeLocalDate encoder this))

  LocalTime
  (-encode [this ^Encoder encoder]
    (.encodeLocalTime encoder this))

  Instant
  (-encode [this ^Encoder encoder]
    (.encodeInstant encoder this)))

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




(comment

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
