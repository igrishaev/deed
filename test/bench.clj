(ns bench
  (:import
   java.io.ByteArrayOutputStream)
  (:require
   [taoensso.nippy :as nippy]
   [clojure.java.io :as io]
   [pinny.core :as pinny])
  (:use criterium.core))

(def DATA
  (into [] (range 999999)))

(defn aaa []

  (pinny/with-encoder [e (new ByteArrayOutputStream 0xFFFFFF)]
    (quick-bench
        (pinny/encode e DATA)))

  (quick-bench
      (nippy/freeze DATA))


  )
