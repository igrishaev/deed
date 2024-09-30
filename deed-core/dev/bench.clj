(ns bench
  (:import
   java.io.ByteArrayOutputStream)
  (:require
   [jsonista.core :as json]
   [taoensso.nippy :as nippy]
   [clojure.java.io :as io]
   [deed.core :as deed])
  (:use criterium.core))

(def DATA
  (vec
   (for [x (range 1000000)]
     x))

  #_
  (vec
   (for [_ (range 10000000)]
     {:foo 1}
     ))

  #_
  (vec
   (for [_ (range 1000)]
     (vec
      (for [x (range 1000)]
        {:foo x
         :bar x}))))

  #_
  (vec
   (for [_ (range 999999)]
     {:foo 1
      :bar 2})))

(comment

  (def -m1 (new java.util.HashMap))
  (.put -m1 1 2)
  (.put -m1 3 4)
  (.put -m1 "aaa" -m1)


  ;; time
  (time
   (deed/with-encoder [e (new ByteArrayOutputStream 0xFFFF)]
     (deed/encode e DATA)))

  ;; mem
  (quick-bench
      (do (deed/encode-to-bytes DATA) nil))

  ;; json mem
  (quick-bench
      (json/write-value (new ByteArrayOutputStream 0xFFFF) DATA))

  ;; file
  (quick-bench
      (deed/with-encoder [e (io/file "out.deed")]
        (deed/encode e DATA)))

  (quick-bench
      (deed/decode-from (io/file "out.deed")))

  ;; write
  (deed/with-encoder [e (new ByteArrayOutputStream 0xFFFF)]
    (quick-bench
        (deed/encode e DATA)))

  (time (do (nippy/freeze DATA) nil))

  (quick-bench
      (nippy/thaw-from-file (io/file "out.nippy")))

  (quick-bench
      (nippy/freeze-to-file (io/file "out.nippy") DATA))



  (quick-bench
      (nippy/freeze DATA {:compressor nil
                          :encryptor nil}))


  (quick-bench
      (deed/with-encoder [e (new ByteArrayOutputStream 0xFFFF)]
        (deed/encode e DATA)))

  (quick-bench
      (nippy/freeze DATA))

  (quick-bench
      (nippy/freeze-to-file "out.nippy" DATA))

  ;;


  )

;; nippy
;; Execution time mean : 64.530873 ms

;; BB + output stream
;; Execution time mean : 112.500529 ms multi-method
;; Execution time mean :  38.444453 ms switch

;; ObjectOutputStream
;; Execution time mean : 11.566030 ms

;; DataOutputStream
;; Execution time mean : 34.584300 ms

;; long switch/case
;; Execution time mean : 14.566030 ms

;; cascading switch case
;; Execution time mean : 13.762154 ms

;; BB + output stream
;; Execution time mean : 41.929175 ms

;; BB + Protocol
;; Execution time mean : 41.836865 ms

;; DataOutputStream + Protocol
;; Execution time mean : 38.514992 ms

;; DataOutputStream + Protocol more types
;; Execution time mean : 42.603235 ms

;; Nippy
;; Execution time mean : 64.002942 ms

;; Nippy file
;; Execution time mean : 75.161894 ms

;; Deed file
;; Execution time mean : 51.339890 ms

;; i5 nippy Execution time mean : 126.351367 ms
;; i5 deed Execution time mean : 65.736057 ms
