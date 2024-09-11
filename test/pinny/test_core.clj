(ns pinny.test-core
  (:import
   (java.math BigInteger
              BigDecimal)
   (java.time LocalDate
              LocalDateTime
              OffsetDateTime
              OffsetTime
              ZonedDateTime
              LocalTime
              Instant
              Duration
              Period
              ZoneId)
   (java.sql Time
             Timestamp)
   (java.util Date
              HashMap)
   (java.net URL
             URI)
   (clojure.lang Atom
                 PersistentQueue
                 Ref
                 BigInt
                 Ratio)
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

(defn ZoneId? [x]
  (instance? ZoneId x))

(defn BigInteger? [x]
  (instance? BigInteger x))

(defrecord Foo [a b c])

(defrecord Bar [x y])

(p/handle-record 999 Bar)

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

  (testing "int array"
    (let [a (int-array [1 2 3])
          b (enc-dec a)]
      (is (= "[I" (-> b class .getName)))
      (is (= [1 2 3] (vec b)))))

  (testing "long array"
    (let [a (long-array [1 2 3])
          b (enc-dec a)]
      (is (= "[J" (-> b class .getName)))
      (is (= [1 2 3] (vec b)))))

  (testing "short array"
    (let [a (short-array [1 2 3])
          b (enc-dec a)]
      (is (= "[S" (-> b class .getName)))
      (is (= [1 2 3] (vec b)))))

  (testing "char array"
    (let [a (char-array [\a \b \c])
          b (enc-dec a)]
      (is (= "[C" (-> b class .getName)))
      (is (= [\a \b \c] (vec b)))))

  (testing "bool array"
    (let [a (boolean-array [true false true])
          b (enc-dec a)]
      (is (= "[Z" (-> b class .getName)))
      (is (= [true false true] (vec b)))))

  (testing "float array"
    (let [a (float-array [1.1 2.2 3.3])
          b (enc-dec a)]
      (is (= "[F" (-> b class .getName)))
      (is (= ["1.1" "2.2" "3.3"] (mapv str b)))))

  (testing "double array"
    (let [a (double-array [1.1 2.2 3.3])
          b (enc-dec a)]
      (is (= "[D" (-> b class .getName)))
      (is (= ["1.1" "2.2" "3.3"] (mapv str b)))))

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

  (testing "sql Time"
    (let [a (new java.sql.Time 1725992698456)
          b (enc-dec a)]
      (is (instance? java.sql.Time b))
      (is (= a b))))

  (testing "sql Time"
    (let [a (new java.sql.Date 1725992698456)
          b (enc-dec a)]
      (is (instance? java.sql.Date b))
      (is (= a b))))

  (testing "sql Timestamp"
    (let [a (new java.sql.Timestamp 1725992698456)
          b (enc-dec a)]
      (is (instance? java.sql.Timestamp b))
      (is (= a b))))

  (testing "Instant"
    (let [a (Instant/now)
          b (enc-dec a)]
      (is (Instant? b))
      (is (= a b))))

  (testing "Duration"
    (let [a (Duration/ofSeconds 123456789 123456789)
          b (enc-dec a)]
      (is (= a b))
      (is (= "PT34293H33M9.123456789S" (str b)))))

  (testing "Period"
    (let [a (Period/of 2022 11 9)
          b (enc-dec a)]
      (is (= a b))
      (is (= "P2022Y11M9D" (str b)))))

  (testing "ZoneId"
    (let [a (ZoneId/of "Asia/Dhaka")
          b (enc-dec a)]
      (is (= a b))
      (is (ZoneId? b))))

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

  (testing "LocalDateTime"
    (let [a (LocalDateTime/now)
          b (enc-dec a)]
      (is (instance? LocalDateTime b))
      (is (= a b))))

  (testing "OffsetDateTime"
    (let [a (OffsetDateTime/now)
          b (enc-dec a)]
      (is (instance? OffsetDateTime b))
      (is (= a b))))

  (testing "ZonedDateTime"
    (let [a (ZonedDateTime/now)
          b (enc-dec a)]
      (is (instance? ZonedDateTime b))
      (is (= a b))))

  (testing "OffsetTime"
    (let [a (OffsetTime/now)
          b (enc-dec a)]
      (is (instance? OffsetTime b))
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

  (testing "java map"
    (let [a (doto (new HashMap)
              (.put 1 2)
              (.put 3 4))
          b (enc-dec a)]
      (is (= {1 2 3 4} b))
      (is (instance? HashMap b)))
    (let [a (new HashMap)
          b (enc-dec a)]
      (is (= {} b))
      (is (instance? HashMap b))))

  (testing "java map entry"
    (let [a (first
             (doto (new HashMap)
               (.put 1 2)
               (.put 3 4)))
          b (enc-dec a)]
      (is (= "Map.Entry<1, 2>" (str b)))))

  (testing "clojure map entry"
    (let [a (first {:foo 1})
          b (enc-dec a)]
      (is (= a b))
      (is (= "clojure.lang.MapEntry" (-> b class .getName))))
    (let [a (seq {:foo 1 :bar 2})
          b (enc-dec a)]
      (is (= [[:foo 1] [:bar 2]] (vec b)))
      (is (= {:foo 1 :bar 2}
             (into {} b)))))

  (testing "clojure vector"
    (let [a [1 2 3]
          b (enc-dec a)]
      (is (= a b))
      (is (vector? b)))
    (let [a []
          b (enc-dec a)]
      (is (= a b))
      (is (vector? b))))

  (testing "clojure list"
    (let [a (list 1 2 3)
          b (enc-dec a)]
      (is (= a b))
      (is (= [1 2 3] b))
      (is (vector? b)))
    (let [a (list)
          b (enc-dec a)]
      (is (= a b))
      (is (= () b))
      (is (list? b))))

  (testing "clojure set"
    (is (= #{1 2 3} (enc-dec #{1 2 3})))
    (let [x #{1 2 3}]
      (is (= #{#{1 2 3}} (enc-dec #{x}))))
    (is (= #{} (enc-dec #{}))))

  (testing "clojure sorted set"
    (let [a (sorted-set 5 4 3 2 1)
          b (enc-dec a)]
      (is (= "clojure.lang.PersistentTreeSet" (-> b class .getName)))
      (is (= a b)))
    (let [a (sorted-set)
          b (enc-dec a)]
      (is (= "clojure.lang.PersistentTreeSet" (-> b class .getName)))
      (is (= a b))))

  (testing "clojure seq"
    (let [a (seq [1 2 3])
          b (enc-dec a)]
      (is (seq? b))
      (is (= a b)))
    (let [a (seq (set [1 2 3]))
          b (enc-dec a)]
      (is (seq? b))
      (is (= a b)))
    (let [a (seq [])
          b (enc-dec a)]
      (is (nil? b))
      (is (= a b))))

  (testing "clojure queue"
    (let [a PersistentQueue/EMPTY
          b (enc-dec a)]
      (is (= a b))
      (is (instance? PersistentQueue b)))
    (let [a (-> PersistentQueue/EMPTY
                (conj 1)
                (conj 2)
                (conj 3))
          b (enc-dec a)]
      (is (= [1 2 3] a b))
      (is (instance? PersistentQueue b))))

  (testing "lazy seq"
    (testing "map"
      (let [a (map inc [1 2 3])
            b (enc-dec a)]
        (is (seq? b))
        (is (= a b))))

    (testing "for"
      (let [a (for [x [1 2 3]]
                {x "hello"})
            b (enc-dec a)]
        (is (seq? b))
        (is (= '({1 "hello"} {2 "hello"} {3 "hello"}) b))))

    (testing "repeat"
      (let [a (repeat 42 0)
            b (enc-dec a)]
        (is (seq? b))
        (is (= 42 (count b)))
        (is (= #{0} (set b)))))

    (testing "->> combo"
      (let [a (->> (range 999)
                   (filter int?)
                   (filter even?)
                   (map inc)
                   (map str)
                   (take 16))
            b (enc-dec a)]
        (is (seq? b))
        (is (= '("1" "3" "5" "7" "9" "11" "13" "15" "17" "19" "21" "23" "25" "27" "29" "31") b))))

    (testing "repeat huge"
      (let [a (repeat 0xFFFF :kek)
            b (enc-dec a)]
        (is (seq? b))
        (is (= 0xFFFF (count b)))
        (is (= #{:kek} (set b))))))

  (testing "clojure sorted map"
    (let [a (sorted-map 1 2 3 4)
          b (enc-dec a)]
      (is (= "clojure.lang.PersistentTreeMap" (-> b class .getName)))
      (is (= a b)))
    (let [a (sorted-map)
          b (enc-dec a)]
      (is (= "clojure.lang.PersistentTreeMap" (-> b class .getName)))
      (is (= a b))))

  (testing "unknown record"
    (let [r1 (new Foo 1 2 3)
          r2 (enc-dec r1)]
      (is (= "clojure.lang.PersistentArrayMap" (-> r2 class .getName)))
      (is (= {:c 3, :a 1, :b 2} r2))))

  (testing "known record"
    (let [r1 (new Bar "a" "b")
          r2 (enc-dec r1)]
      (is (= "pinny.test_core.Bar" (-> r2 class .getName)))
      (is (= r1 r2))))

  (testing "biginteger"
    (let [b1 (new BigInteger "-123456")
          b2 (enc-dec b1)]
      (is (BigInteger? b2))
      (is (= b1 b2))))

  (testing "bigdecimal"
    (let [b1 (new BigDecimal "-123.456")
          b2 (enc-dec b1)]
      (is (instance? BigDecimal b2))
      (is (= b1 b2))))

  (testing "bigint"
    (let [b1 (bigint "-123456")
          b2 (enc-dec b1)]
      (is (instance? BigInt b2))
      (is (= b1 b2))))

  (testing "byte"
    (let [b1 (byte -1)
          b2 (enc-dec b1)]
      (is (instance? Byte b2))
      (is (= b1 b2)))
    (let [b1 (byte 0)
          b2 (enc-dec b1)]
      (is (instance? Byte b2))
      (is (= b1 b2)))
    (let [b1 (byte 1)
          b2 (enc-dec b1)]
      (is (instance? Byte b2))
      (is (= b1 b2)))
    (let [b1 (byte 99)
          b2 (enc-dec b1)]
      (is (instance? Byte b2))
      (is (= b1 b2))))

  ;; unknown type
  ;; custom deftype



  )

(deftest test-future-cases

  (testing "simple"
    (let [f1 (future 42)
          f2 (enc-dec f1)]
      (is (future? f2))
      (is (= 42 @f2))))

  (testing "nested"
    (let [f1 (future (future (future :lol)))
          f2 (enc-dec f1)]
      (is (future? f2))
      (is (-> f2 deref deref deref (= :lol)))))

  (testing "failed"
    (let [f1 (future (/ 0 0))]
      (try
        (enc-dec f1)
        (is false)
        (catch Exception e
          (is (= "future has failed: Divide by zero"
                 (ex-message e)))))))

  ;; TODO: timeout options

  (testing "timeout"
    (let [f1 (future
               (Thread/sleep 6000))]

      (try
        (enc-dec f1)
        (is false)
        (catch Exception e
          (is (= "future deref timeout (ms): 5000"
                 (ex-message e))))))))
