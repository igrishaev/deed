(ns deed.base64
  (:import
   (org.apache.commons.codec.binary
    Base64OutputStream
    Base64InputStream)
   (org.apache.commons.codec CodecPolicy))
  (:require
   [clojure.java.io :as io]
   [deed.core :as d]))


(defn base64-input-stream [src]
  (-> src
      (io/input-stream)
      (Base64InputStream. false -1 (byte-array 0) CodecPolicy/STRICT)))


(defn base64-output-stream [dst]
  (-> dst
      (io/output-stream)
      (Base64OutputStream. true -1 (byte-array 0) CodecPolicy/STRICT)))
