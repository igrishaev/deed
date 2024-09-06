(ns bench
  (:import
   java.io.ByteArrayOutputStream)
  (:require
   [taoensso.nippy :as nippy]
   [clojure.java.io :as io]
   [pinny.core :as pinny])
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

  (time
   (pinny/with-encoder [e (new ByteArrayOutputStream 0xFFFF)]
     (pinny/encode e DATA)))

  (quick-bench
      (pinny/with-encoder [e (new ByteArrayOutputStream 0xFFFF)]
        (pinny/encode e DATA)))

  (quick-bench
      (pinny/with-encoder [e (io/file "out.pinny")]
        (pinny/encode e DATA)))

  (time (do (nippy/freeze DATA) nil))

  (quick-bench
      (nippy/freeze DATA))

  (pinny/with-encoder [e (new ByteArrayOutputStream 0xFFFF)]
    (quick-bench
        (pinny/encode e DATA)))

  (quick-bench
      (pinny/with-encoder [e (new ByteArrayOutputStream 0xFFFF)]
        (pinny/encode e DATA)))

  (quick-bench
      (nippy/freeze DATA))

  (quick-bench
      (nippy/freeze-to-file "out.nippy" DATA))

  ;;


  )

;; BB + output stream
;; Execution time mean : 112.500529 ms multi-method
;; Execution time mean : 38.444453 ms  switch

;; ObjectOutputStream
;; Execution time mean : 11.566030 ms

;; DataOutputStream
;; Execution time mean : 34.584300 ms
