(ns deed.vectorz
  (:import
   (deed Encoder
         Decoder
         OID
         VectorZ)
   (mikera.vectorz AVector))
  (:require
   [deed.core :as d]))

(set! *warn-on-reflection* true)


(extend-protocol d/IEncode
  AVector
  (-encode [this ^Encoder encoder]
    (VectorZ/encodeAVector encoder this)))


(defmethod d/-decode OID/VECTORZ_AVECTOR
  [_ ^Decoder decoder]
  (VectorZ/decodeAVector decoder))
