(ns deed.base64
  (:import
   (java.nio.charset StandardCharsets)
   (java.io ByteArrayOutputStream
            ByteArrayInputStream)
   (org.apache.commons.codec.binary
    Base64OutputStream
    Base64InputStream)
   (org.apache.commons.codec CodecPolicy))
  (:require
   [clojure.java.io :as io]
   [deed.core :as d]))


;;
;; Util
;;

(defn bytes->string ^String [^bytes buf]
  (new String buf StandardCharsets/UTF_8))

(defn string->bytes ^bytes [^String string]
  (.getBytes string StandardCharsets/UTF_8))


(def SEPARATOR
  (byte-array 0))

;;
;; Stream wrappers
;;

(defn base64-input-stream
  "
  Make an input stream that base64-encodes the payload.
  The `src` is anything that gets transformed into the
  `InputStream` type using the `io/input-stream` function.
  "
  ^Base64InputStream [src]
  (-> src
      (io/input-stream)
      (Base64InputStream. false -1 SEPARATOR CodecPolicy/STRICT)))


(defn base64-output-stream
  "
  Make in output stream that base64-decodes the payload.
  The `dst` is anything that gets transformed into the
  `OutputStream` type using the `io/output-stream` function.
  "
  ^Base64OutputStream [dst]
  (-> dst
      (io/output-stream)
      (Base64OutputStream. true -1 SEPARATOR CodecPolicy/STRICT)))

;;
;; Encode
;;

(defn encode-to-base64-bytes
  "
  Encode a single value into a base64-encoded byte array.
  "
  (^bytes [x]
   (encode-to-base64-bytes x nil))

  (^bytes [x options]
   (with-open [out (new ByteArrayOutputStream)
               b64 (base64-output-stream out)]
     (d/encode-to x b64 options)
     (.toByteArray out))))


(defn encode-seq-to-base64-bytes
  "
  Encode a collection of values into a base64-encoded byte array.
  "
  (^bytes [xs]
   (encode-seq-to-base64-bytes xs nil))

  (^bytes [xs options]
   (with-open [out (new ByteArrayOutputStream)
               b64 (base64-output-stream out)]
     (d/encode-seq-to xs b64 options)
     (.toByteArray out))))


(defn encode-to-base64-string
  "
  Encode a single value into a base64-encoded string.
  "
  (^bytes [x]
   (encode-to-base64-string x nil))

  (^bytes [x options]
   (-> x
       (encode-to-base64-bytes options)
       (bytes->string))))


(defn encode-seq-to-base64-string
  "
  Encode a collection of values into a base64-encoded string.
  "
  (^bytes [xs]
   (encode-seq-to-base64-string xs nil))

  (^bytes [xs options]
   (-> xs
       (encode-seq-to-base64-bytes options)
       (bytes->string))))

;;
;; Decode
;;

(defn decode-from-base64-bytes
  "
  Decode a single value from a base64-encoded byte array.
  "
  ([^bytes base64]
   (decode-from-base64-bytes base64 nil))

  ([^bytes base64 options]
   (with-open [in (base64-input-stream base64)]
     (d/decode-from in options))))


(defn decode-from-base64-string
  "
  Decode a single value from a base64-encoded string.
  "
  ([^String base64]
   (decode-from-base64-string base64 nil))

  ([^String base64 options]
   (with-open [in (-> base64
                      (string->bytes)
                      (base64-input-stream))]
     (d/decode-from in options))))


(defn decode-seq-from-base64-bytes
  "
  Decode a collection of values from a base64-encoded byte array.
  "
  ([^bytes base64]
   (decode-seq-from-base64-bytes base64 nil))

  ([^bytes base64 options]
   (with-open [in (base64-input-stream base64)]
     (d/decode-seq-from in options))))


(defn decode-seq-from-base64-string
  "
  Decode a collection of values from a base64-encoded string.
  "
  ([^String base64]
   (decode-seq-from-base64-string base64 nil))

  ([^String base64 options]
   (with-open [in (-> base64
                      (string->bytes)
                      (base64-input-stream))]
     (d/decode-seq-from in options))))
