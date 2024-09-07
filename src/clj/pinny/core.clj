(ns pinny.core
  (:require
   [clojure.java.io :as io])
  (:import
   (java.util UUID)
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


  )


;;
;; Extension, encoding
;;

;; (defmethod -enc String [^Encoder encoder ^String value]
;;   (.encodeString encoder value))

;; (defmethod -enc Integer [^Encoder encoder ^Integer value]
;;   (.encodeInteger encoder value))

;; (defmethod -enc Long [^Encoder encoder ^Long value ]
;;   (.encodeLong encoder value))

;; (defmethod -enc Boolean [^Encoder encoder ^Boolean value ]
;;   (.encodeBoolean encoder value))

;; (defmethod -enc IPersistentVector [^Encoder encoder value]
;;   (.encodeCountable encoder OID/CLJ_VEC (count value) value))

;; (defmethod -enc IPersistentSet [^Encoder encoder value]
;;   (.encodeCountable encoder OID/CLJ_SET (count value) value))

;; (defmethod -enc IPersistentList [^Encoder encoder value]
;;   (.encodeCountable encoder OID/CLJ_SET (count value) value))

;; (defmethod -enc LazySeq [^Encoder encoder value]
;;   (.encodeUncountable encoder OID/CLJ_LAZY_SEQ value))


;;
;; API
;;

(defn encode-multi ^Long [^Encoder encoder coll]
  (.encodeMulti encoder coll))

(defn encode ^Long [^Encoder encoder x]
  (.encode encoder x))

(defmacro with-encoder [[bind dest] & body]
  `(with-open [input# (io/output-stream ~dest)
               ~bind (new Encoder -encode input#)]
     ~@body))

(defn eof? [x]
  (instance? EOF x))



;; (defmethod -dec OID/INT [_ decoder]
;;   (.readInt decoder))

;; (defmethod -dec OID/LONG [_ decoder]
;;   (.readLong decoder))


(comment

  (with-encoder [e (io/file "test.aaa")]
    (encode e 1)
    (encode e 2)
    (encode e 3))

  (with-encoder [e (io/file "test.aaa")]
    (encode-multi e [1 2 3]))

  )


;; (defmacro with-encoder [[bind dest] & body]
;;   `(with-open [input# (io/output-stream ~dest)
;;                ~bind (new Encoder input#)]
;;      ~@body))

;; (defn encode ^Encoder [^Encoder encoder x]
;;   (.encode encoder x)
;;   encoder)

;; (defn encode-multi ^Encoder [^Encoder encoder xs]
;;   (.encodeMulti encoder xs)
;;   encoder)

;; (defmacro with-decoder [[bind source] & body]
;;   `(with-open [input# (io/input-stream ~source)
;;                ~bind (new Decoder input#)]
;;      ~@body))

;; (defn decode [^Decoder decoder]
;;   (.decode decoder))

;; (defn decode-seq [^Decoder decoder]
;;   (iterator-seq decoder))

;; (defn decode-vec [^Decoder decoder]
;;   (into [] decoder))



(comment

  (with-encoder [enc (io/file "aaa.xxx")]
    (-> enc
        (encode 1)
        (encode 2)
        (encode-multi [1 2 3 3 4])
        (encode 3))
    )

  (with-decoder [dec (io/file "aaa.xxx")]
    (decode-vec dec)
    (decode dec)
    #_
    (doseq [x dec]
      (println x))
    #_
    [(decode dec)
     (decode dec)]
    )

  )
