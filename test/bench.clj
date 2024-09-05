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

(defn aaa []

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
