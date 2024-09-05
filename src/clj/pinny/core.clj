(ns pinny.core
  (:require
   [clojure.java.io :as io])
  (:import
   (pinny Encoder Decoder EOF)))

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
