(ns deed.vectorz
  (:import
   (deed Encoder
         Decoder
         OID)
   (mikera.vectorz IVector
                   Vectorz
                   AVector))
  (:require
   [deed.core :as d]))

(set! *warn-on-reflection* true)


(d/expand-encode [OID/MIKERA_IVECTOR ;; TODO: AVECTOR
                  AVector
                  iv
                  ^Encoder encoder]
  (let [len (.length iv)]
    (.writeInt encoder len)
    (loop [i 0]
      (if (= i len)
        nil
        (let [x (.get iv i)]
          (.writeDouble encoder x)
          (recur (inc i)))))))


(d/expand-decode [OID/MIKERA_IVECTOR
                  ^Decoder decoder]
  (let [len (.readInteger decoder)
        res (Vectorz/newVector len)]
    (loop [i 0]
      (if (= i len)
        res
        (let [x (.readDouble decoder)]
          (.set res i x)
          (recur (inc i)))))))
