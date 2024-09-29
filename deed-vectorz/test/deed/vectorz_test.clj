(ns deed.vectorz-test
  (:require
   [deed.core :as d]
   [deed.vectorz :as vz]
   [clojure.test :refer [is deftest]])
  (:import
   (mikera.vectorz Vectorz
                   IVector ;; TODO: tests
                   AVector
                   Vector
                   Vector1
                   Vector2
                   Vector3
                   Vector4)))


(deftest test-vectorz-ok
  (let [a (Vectorz/create (double-array [1]))
        buf (d/encode-to-bytes a)
        b (d/decode-from buf)
        ]

    (is (= 1 (vec buf)))


    (is (instance? Iterable a))
    #_
    (is (instance? IVector b))

    ;; (is (= 1 a))

    #_
    (is (= 1 (-> b class .getName)))

    #_
    (is (= 1 b))

    )



  )
