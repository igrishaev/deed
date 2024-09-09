(ns pinny.test-core
  (:import
   (java.time Instant
              LocalDate
              LocalTime)
   (java.util Date)
   (java.net URL URI)
   (clojure.lang Atom Ref Ratio)
   (java.util.regex Pattern)
   (java.io ByteArrayOutputStream))
  (:require
   [clojure.java.io :as io]
   [pinny.core :as p]
   [clojure.test :refer [is deftest testing]]))

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

(defn Float? [x]
  (instance? Float x))

(defn Double? [x]
  (instance? Double x))

(defn URL? [x]
  (instance? URL x))

(defn URI? [x]
  (instance? URI x))

(defn Ratio? [x]
  (instance? Ratio x))

(defn Date? [x]
  (instance? Date x))

(defn Instant? [x]
  (instance? Instant x))

(defn LocalDate? [x]
  (instance? LocalDate x))

(defn LocalTime? [x]
  (instance? LocalTime x))

(deftest test-general-ok

  (testing "nil"
    (is (nil? (enc-dec nil))))

  (testing "bool"
    (is (true? (enc-dec true)))
    (is (false? (enc-dec false))))

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

  (testing "long"

    (is (= -1 (enc-dec -1)))
    (is (Long? (enc-dec -1)))

    (is (= 0 (enc-dec 0)))
    (is (Long? (enc-dec 0)))

    (is (= 1 (enc-dec 1)))
    (is (Long? (enc-dec 1)))

    (is (= 123 (enc-dec 123)))
    (is (Long? (enc-dec 123))))

  (testing "float"
    (is (= -1.0 (enc-dec (float -1))))
    (is (Float? (enc-dec (float -1))))

    (is (= 0.0 (enc-dec (float 0))))
    (is (Float? (enc-dec (float 0))))

    (is (= 1.0 (enc-dec (float 1))))
    (is (Float? (enc-dec (float 1))))

    (is (= "10.1" (str (enc-dec (float 10.1)))))
    (is (Float? (enc-dec (float 10.1)))))

  (testing "double"
    (is (= -1.0 (enc-dec -1.0)))
    (is (= 0.0 (enc-dec 0.0)))
    (is (= 1.0 (enc-dec 1.0)))
    (is (= 123.123 (enc-dec 123.123)))
    (is (Double? (enc-dec -1.0))))

  (testing "byte array"
    (let [a (byte-array [1 2 3])
          b (enc-dec a)]
      (is (bytes? b))
      (is (= [1 2 3] (vec b)))))

  (testing "object array"
    (let [a (object-array [1 nil :kek {"aaa" 1.0}])
          b (enc-dec a)]
      (is (= "[Ljava.lang.Object;" (-> b class .getName)))
      (is (= [1 nil :kek {"aaa" 1.0}] (vec b)))))

  (testing "URL"
    (let [a (new URL "http://test.com/bar?query=clojure")
          b (enc-dec a)]
      (is (= a b))))

  (testing "URI"
    (let [a (new URI "http://test.com/bar?query=clojure")
          b (enc-dec a)]
      (is (= a b))))

  (testing "uuid"
    (let [uuid (random-uuid)]
      (is (= uuid (enc-dec uuid)))))

  (testing "string"
    (is (= "" (enc-dec "")))
    (is (= "\t" (enc-dec "\t")))
    (is (= "hello" (enc-dec "hello"))))

  (testing "char"
    (is (= \A (enc-dec \A)))
    (is (= \0 (enc-dec \0))))

  (testing "pattern"
    (let [regex #"(?i)foo\.bar[azAZ_-]"
          res (enc-dec regex)]
      (is (instance? Pattern res))
      (is (= (str regex) (str res)))))

  (testing "atom"
    (let [a (atom 42)
          b (enc-dec a)]
      (is (instance? Atom b))
      (is (= @a @b)))

    (let [a (atom (atom (atom :kek)))
          b (enc-dec a)]
      (is (instance? Atom b))
      (is (-> b deref deref deref (= :kek)))))

  (testing "ref"
    (let [a (ref 42)
          b (enc-dec a)]
      (is (instance? Ref b))
      (is (= 42 @b)))

    (let [a (ref (ref (ref :kek)))
          b (enc-dec a)]
      (is (instance? Ref b))
      (is (-> b deref deref deref (= :kek)))))

  (testing "keyword"
    (is (= :abc (enc-dec :abc)))
    (is (= :aaa/bbb (enc-dec :aaa/bbb)))
    (is (= (keyword "") (enc-dec (keyword "")))))

  (testing "symbol"
    (is (= 'abc (enc-dec 'abc)))
    (is (= 'aaa/bbb (enc-dec 'aaa/bbb)))
    (is (= (symbol "") (enc-dec (symbol "")))))

  (testing "util Date"
    (let [a (new Date)
          b (enc-dec a)]
      (is (Date? b))
      (is (= a b))))

  (testing "Instant"
    (let [a (Instant/now)
          b (enc-dec a)]
      (is (Instant? b))
      (is (= a b))))

  (testing "LocalDate"
    (let [a (LocalDate/now)
          b (enc-dec a)]
      (is (LocalDate? b))
      (is (= a b))))

  (testing "LocalTime"
    (let [a (LocalTime/now)
          b (enc-dec a)]
      (is (LocalTime? b))
      (is (= a b))))

  (testing "ratio"
    (let [a (/ 10 3)
          b (enc-dec a)]
      (is (Ratio? b))
      (is (= a b))))

  (testing "clojure map"
    (is (= {:aaa 1} (enc-dec {:aaa 1})))
    (is (= {} (enc-dec {})))
    (is (= {:foo {:bar {:baz true}}} (enc-dec {:foo {:bar {:baz true}}})))
    (is (= {{:id 1} "foo"} (enc-dec {{:id 1} "foo"}))))

  (testing "clojure vector"
    (is (= [1 2 3] (enc-dec [1 2 3])))
    (let [x [1 2 3]]
      (is (= [[1 2 3] [1 2 3] [1 2 3]] (enc-dec [x x x]))))
    (is (= [] (enc-dec []))))


  )
