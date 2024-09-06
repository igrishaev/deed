(ns pinny.core
  (:require
   [clojure.java.io :as io])
  (:import
   (clojure.lang IPersistentVector
                 IPersistentSet
                 IPersistentList
                 LazySeq)
   (pinny Encoder Decoder EOF OID)))


(defmulti -enc
  (fn [encoder value]
    (class value)))

(defmulti -dec
  (fn [decoder oid]
    oid))

(defn -encode-countable [^Encoder encoder oid len coll]
  (let [sum
        (+ (.writeOID encoder oid)
           (.writeInt encoder len))

        sum
        (reduce
         (fn [acc item]
           (+ acc (-enc encoder item)))
         sum
         coll)]

    (+ sum (.writeInt encoder 0))))

(defn -encode-chunk
  ([encoder ^objects chunk]
   (-encode-chunk encoder chunk (alength chunk)))

  ([^Encoder encoder ^objects chunk pos]
   (loop [i 0
          sum 0]
     (if (< i pos)
       (let [obj (aget chunk i)
             len (-enc encoder obj)]
         (reduce (unchecked-inc-int i)
                 (+ sum len)))
       sum))))

(defn -encode-uncountable [^Encoder encoder oid coll]
  (let [sum
        (.writeOID encoder oid)

        limit
        0xFF

        chunk
        (object-array limit)

        iter
        (clojure.lang.RT/iter coll)]

    (loop [pos 0
           sum 0]
      (if (.hasNext iter)
        (let [obj (.next iter)]
          (aset chunk pos obj)

          )

        1
        sum))



    #_
    (+ sum (.writeInt encoder 0))))


;;
;; Extension, encoding
;;

(defmethod -enc String [^Encoder encoder ^String value]
  (.encodeString encoder value))

(defmethod -enc Integer [^Encoder encoder ^Integer value]
  (.encodeInteger encoder value))

(defmethod -enc Long [^Encoder encoder ^Long value ]
  (.encodeLong encoder value))

#_
(defmethod -enc Boolean [^Encoder encoder ^Boolean value ]
  (.encodeBoolean encoder value))

#_
(defmethod -enc IPersistentVector [^Encoder encoder value]
  (-encode-countable encoder OID/CLJ_VEC (count value) value))

(defmethod -enc IPersistentSet [^Encoder encoder value]
  (-encode-countable encoder OID/CLJ_SET (count value) value))

(defmethod -enc IPersistentList [^Encoder encoder value]
  (-encode-countable encoder OID/CLJ_SET (count value) value))

(defmethod -enc LazySeq [^Encoder encoder value]
  (-encode-uncountable encoder OID/CLJ_LAZY_SEQ value))


;;
;; API
;;

(defn encode-multi ^Long [^Encoder encoder coll]
  (reduce
   (fn [acc item]
     (+ acc (-enc encoder item)))
   0
   coll))

(defn encode ^Long [^Encoder encoder coll]
  (-enc encoder coll))

(defmacro with-encoder [[bind dest] & body]
  `(with-open [input# (io/output-stream ~dest)
               ~bind (new Encoder input#)]
     ~@body))

(defn eof? [x]
  (instance? EOF x))



;; (defmethod -dec OID/INT [_ decoder]
;;   (.readInt decoder))

;; (defmethod -dec OID/LONG [_ decoder]
;;   (.readLong decoder))

#_
(defmacro with-encoder2 [[bind dest] & body]
  `(with-open [input# (io/output-stream ~dest)
               ~bind (new Encoder input#)]
     ~@body))


(comment

  (with-encoder2 [e (io/file "test.aaa")]
    (-enc e 1)
    (-enc e 2)
    (-enc e 3))

  (with-encoder2 [e (io/file "test.aaa")]
    (-encode-multi e [1 2 3]))

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
