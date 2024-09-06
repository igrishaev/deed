(ns pinny.core
  (:require
   [clojure.java.io :as io])
  (:import
   (clojure.lang IPersistentVector
                 IPersistentSet
                 )
   (pinny Encoder Decoder EOF OID
          Encoder2

          )))


(defmulti -enc
  (fn [encoder value]
    (class value)))

(defmulti -dec
  (fn [decoder oid]
    oid))

(defn -encode-countable [^Encoder2 encoder oid len coll]
  (reduce
   (fn [acc item]
     (+ acc (-enc encoder item)))
   (.writeOID encoder oid)
   coll))

(defn -encode-multi [^Encoder2 encoder coll]
  (reduce
   (fn [acc item]
     (+ acc (-enc encoder item)))
   0
   coll))

(defmethod -enc String [^Encoder2 encoder ^String value]
  (.encodeString encoder value))

(defmethod -enc Integer [^Encoder2 encoder ^Integer value]
  (.encodeInteger encoder value))

(defmethod -enc Long [^Encoder2 encoder ^Long value ]
  (.encodeLong encoder value))

(defmethod -enc IPersistentVector [^Encoder2 encoder value]
  (-encode-countable encoder OID/CLJ_VECTOR (count value) value))

(defmethod -enc IPersistentSet [^Encoder2 encoder value]
  (-encode-countable encoder 123 (count value) value))


;; (defmethod -dec OID/INT [_ decoder]
;;   (.readInt decoder))

;; (defmethod -dec OID/LONG [_ decoder]
;;   (.readLong decoder))


(defmacro with-encoder2 [[bind dest] & body]
  `(with-open [input# (io/output-stream ~dest)
               ~bind (new Encoder2 input#)]
     ~@body))


(comment

  (with-encoder2 [e (io/file "test.aaa")]
    (-enc e 1)
    (-enc e 2)
    (-enc e 3))

  (with-encoder2 [e (io/file "test.aaa")]
    (-encode-multi e [1 2 3]))

  )


(defmacro with-encoder [[bind dest] & body]
  `(with-open [input# (io/output-stream ~dest)
               ~bind (new Encoder input#)]
     ~@body))

(defn encode ^Encoder [^Encoder encoder x]
  (.encode encoder x)
  encoder)

(defn encode-multi ^Encoder [^Encoder encoder xs]
  (.encodeMulti encoder xs)
  encoder)

(defmacro with-decoder [[bind source] & body]
  `(with-open [input# (io/input-stream ~source)
               ~bind (new Decoder input#)]
     ~@body))

(defn decode [^Decoder decoder]
  (.decode decoder))

(defn decode-seq [^Decoder decoder]
  (iterator-seq decoder))

(defn decode-vec [^Decoder decoder]
  (into [] decoder))

(defn eof? [x]
  (instance? EOF x))

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
