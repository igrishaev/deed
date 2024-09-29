(ns deed.vectorz-test
  (:require
   [deed.core :as d]
   [deed.vectorz :as vz]
   [clojure.test :refer [is deftest]])
  (:import
   (mikera.vectorz Vectorz
                   Vector
                   Vector1
                   Vector2
                   Vector3
                   Vector4)))

(deftest test-vectorz-1
  (let [a (Vectorz/create (double-array [1]))
        buf (d/encode-to-bytes a)
        b (d/decode-from buf)]
    (is (instance? Vector1 b))
    (is (= a b))))

(deftest test-vectorz-2
  (let [a (Vectorz/create (double-array [1 2]))
        buf (d/encode-to-bytes a)
        b (d/decode-from buf)]
    (is (instance? Vector2 b))
    (is (= a b))))

(deftest test-vectorz-3
  (let [a (Vectorz/create (double-array [1 2 3]))
        buf (d/encode-to-bytes a)
        b (d/decode-from buf)]
    (is (instance? Vector3 b))
    (is (= a b))))

(deftest test-vectorz-4
  (let [a (Vectorz/create (double-array [1 2 3 4]))
        buf (d/encode-to-bytes a)
        b (d/decode-from buf)]
    (is (instance? Vector4 b))
    (is (= a b))))

(deftest test-vectorz
  (let [a (Vectorz/create (double-array [1 2 3 4 5]))
        buf (d/encode-to-bytes a)
        b (d/decode-from buf)]
    (is (instance? Vector b))
    (is (= a b))))
