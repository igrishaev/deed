(ns pinny.test-core
  (:require
   [clojure.java.io :as io]
   [pinny.core :as p]
   [clojure.test :refer [is deftest testing]]))


(def FILE (io/file "test.pinny"))

(deftest test-general-ok

  (testing "short"
    (p/with-encoder [e FILE]
      (p/encode e (short 1)))
    (let [res
          (p/with-decoder [d FILE]
            (p/decode d))]
      (is (= 1 res))
      (is (instance? Short res))))

  (testing "int 1"
    (p/with-encoder [e FILE]
      (p/encode e (int 1)))
    (let [res
          (p/with-decoder [d FILE]
            (p/decode d))]
      (is (= 1 res))
      (is (instance? Integer res))))

  (testing "int -1"
    (p/with-encoder [e FILE]
      (p/encode e (int -1)))
    (let [res
          (p/with-decoder [d FILE]
            (p/decode d))]
      (is (= -1 res))
      (is (instance? Integer res))))

  (testing "int 0"
    (p/with-encoder [e FILE]
      (p/encode e (int 0)))
    (let [res
          (p/with-decoder [d FILE]
            (p/decode d))]
      (is (= 0 res))
      (is (instance? Integer res))))

  (testing "int other"
    (p/with-encoder [e FILE]
      (p/encode e (int 999)))
    (let [res
          (p/with-decoder [d FILE]
            (p/decode d))]
      (is (= 999 res))
      (is (instance? Integer res))))


  )
