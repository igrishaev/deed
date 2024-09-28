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


(deftest test-base64-bytes

  (let [buf
        (b64/encode-to-base64-bytes 42)

        res
        (b64/decode-from-base64-bytes buf)]

    (is (= 42 res))

    (is (= [65, 65, 69, 65, 65, 81, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65,
            65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65,
            65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 77, 65, 65, 65,
            65, 65, 65, 65, 65, 65, 67, 111, 61]
           (vec buf)))))


(deftest test-base64-bytes-seq
  (let [buf
        (b64/encode-seq-to-base64-bytes [1 2 3])
        res
        (b64/decode-seq-from-base64-bytes buf)]
    (is (= [1 2 3] res))))


(deftest test-base64-string
  (let [string
        (b64/encode-to-base64-string 42)
        res
        (b64/decode-from-base64-string string)]
    (is (= 42 res))
    (is (= "AAEAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAAAAAAACo="
           string))))

(deftest test-base64-seq-string
  (let [string
        (b64/encode-seq-to-base64-string [:foo 1 nil])
        res
        (b64/decode-seq-from-base64-string string)]
    (is (= [:foo 1 nil] res))
    (is (= "AAEAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABKAAAAA2ZvbwAOAAA="
           string))))
