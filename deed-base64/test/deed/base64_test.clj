(ns deed.base64-test
  (:import
   (java.io ByteArrayOutputStream
            ByteArrayInputStream
            File))
  (:require
   [deed.core :as d]
   [deed.base64 :as b64]
   [clojure.test :refer [is deftest]]))


(defn get-temp-file
  (^File []
   (get-temp-file "tmp" ".tmp"))
  (^File [prefix suffix]
   (File/createTempFile prefix suffix)))


(deftest test-base-64-ok
  (let [file
        (get-temp-file)

        _
        (d/encode-to 99
                     (b64/base64-output-stream file))

        res
        (d/decode-from (b64/base64-input-stream file))]

    (is (= "AAEAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAAAAAAAGM="
           (slurp file)))
    (is (= 99 res))))
