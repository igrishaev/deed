(ns pinny.test-core
  (:import
   java.io.ByteArrayOutputStream)
  (:require
   [clojure.java.io :as io]
   [pinny.core :as p]
   [clojure.test :refer [is deftest testing]]))


(def FILE (io/file "test.pinny"))

(defn enc-dec [x]
  (let [out (new ByteArrayOutputStream 0xFF)]
    (p/with-encoder [e out]
      (p/encode e x))
    (let [in (-> out
                 .toByteArray
                 io/input-stream)]
      (p/with-decoder [d in]
        (p/decode d)))))

(defn Short? [x]
  (instance? Short x))

(defn Integer? [x]
  (instance? Integer x))

(defn Long? [x]
  (instance? Long x))

(deftest test-general-ok

  (testing "short"

    (is (= -1 (enc-dec (short -1))))
    (is (Short? (enc-dec (short -1))))

    (is (= 0 (enc-dec (short 0))))
    (is (Short? (enc-dec (short 0))))

    (is (= 1 (enc-dec (short 1))))
    (is (Short? (enc-dec (short 1))))

    (is (= 123 (enc-dec (short 123))))
    (is (Short? (enc-dec (short 123)))))

  (testing "int"

    (is (= -1 (enc-dec (int -1))))
    (is (Integer? (enc-dec (int -1))))

    (is (= 0 (enc-dec (int 0))))
    (is (Integer? (enc-dec (int 0))))

    (is (= 1 (enc-dec (int 1))))
    (is (Integer? (enc-dec (int 1))))

    (is (= 123 (enc-dec (int 123))))
    (is (Integer? (enc-dec (int 123)))))

  #_
  (testing "long"

    (is (= -1 (enc-dec -1)))
    (is (Long? (enc-dec -1)))

    (is (= 0 (enc-dec 0)))
    (is (Long? (enc-dec 0)))

    (is (= 1 (enc-dec 1)))
    (is (Long? (enc-dec 1)))

    (is (= 123 (enc-dec 123)))
    (is (Long? (enc-dec 123))))

  (testing "keyword"
    (is (= :abc (enc-dec :abc)))
    (is (= :aaa/bbb (enc-dec :aaa/bbb)))
    (is (= (keyword "") (enc-dec (keyword "")))))

  (testing "symbol"
    (is (= 'abc (enc-dec 'abc)))
    (is (= 'aaa/bbb (enc-dec 'aaa/bbb)))
    (is (= (symbol "") (enc-dec (symbol "")))))

  (testing "clojure map"
    (is (= 1 (enc-dec {:aaa 1}))))



  )
