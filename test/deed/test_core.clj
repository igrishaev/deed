(ns deed.test-core
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
   (java.util.stream Stream)
   (java.util Date
              List
              ArrayList
              Vector
              Iterator
              NoSuchElementException
              HashMap)
   (java.io IOException
            InputStream
            File)
   (java.net URL
             URI)
   (java.util.concurrent ArrayBlockingQueue)
   (clojure.lang Atom
                 ExceptionInfo
                 RT
                 PersistentQueue
                 Ref
                 BigInt
                 Ratio)
   (java.util.regex Pattern)
   (java.io ByteArrayOutputStream))
  (:require
   [clojure.string :as str]
   [clojure.java.io :as io]
   [deed.core :as d]
   [clojure.test :refer [is deftest testing]]))

(defn enc-dec [x]
  (let [out (new ByteArrayOutputStream 0xFF)]
    (d/with-encoder [e out]
      (d/encode e x))
    (let [in (-> out
                 .toByteArray
                 io/input-stream)]
      (d/with-decoder [d in]
        (d/decode d)))))

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

(d/handle-record 999 Bar)

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

  (testing "java list"
    (let [a (List/of 1 2 3)
          b (enc-dec a)]
      (is (= a b))
      (is (= "java.util.ArrayList" (-> b class .getName))))
    (let [a (List/of)
          b (enc-dec a)]
      (is (= a b))
      (is (instance? List b))))

  (testing "java vector"
    (let [a (doto (new Vector)
              (.add 1)
              (.add 2)
              (.add 3))
          b (enc-dec a)]
      (is (= a b))
      (is (instance? Vector b)))
    (let [a (new Vector)
          b (enc-dec a)]
      (is (= a b))
      (is (instance? Vector b))))

  (testing "java iterable"
    (let [a (doto (new ArrayBlockingQueue 8)
              (.add 1)
              (.add 2)
              (.add 3))
          b (enc-dec a)]
      (is (= (vec a) (vec b)))
      (is (instance? ArrayList b))))

  (testing "java iterator"
    (let [a (RT/iter [1 2 3])
          b (enc-dec a)]
      (is (instance? Iterator b))
      (is (= [1 2 3]
             (-> b iterator-seq vec)))))

  (testing "java stream"
    (let [a (doto (new ArrayList)
              (.add 1)
              (.add 2)
              (.add 3))
          b (enc-dec (.stream a))]
      (is (= [1 2 3] (-> ^Stream b .toArray vec)))
      (is (instance? Stream b))))

  (testing "java map entry"
    (let [a (first
             (doto (new HashMap)
               (.put 1 2)
               (.put 3 4)))
          b (enc-dec a)]
      (is (= "MEntry<1, 2>" (str b)))))

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

  #_
  (testing "unknown record"
    (let [r1 (new Foo 1 2 3)
          r2 (enc-dec r1)]
      (is (= "clojure.lang.PersistentArrayMap" (-> r2 class .getName)))
      (is (= {:c 3, :a 1, :b 2} r2))))

  (testing "known record"
    (let [r1 (new Bar "a" "b")
          r2 (enc-dec r1)]
      (is (= "deed.test_core.Bar" (-> r2 class .getName)))
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

(deftest test-throwable-exception

  (let [a (try
            (/ 0 0)
            (catch Exception e
              e))
        b (enc-dec a)
        m (Throwable->map b)
        c (ex-cause b)]

    (is (nil? c))
    (is (instance? Exception b))
    (is (= "Divide by zero" (ex-message b)))

    (is (= '{:via
             [{:type java.lang.Exception
               :message "Divide by zero"
               :at nil}]
             :cause "Divide by zero"}
           (-> m
               (dissoc :trace)
               (assoc-in [:via 0 :at] nil))))

    (is (= '[clojure.lang.Numbers divide "Numbers.java"]
           (-> m :trace first (subvec 0 3))))))

(deftest test-throwable-cause

  (let [a (try
            (/ 0 0)
            (catch Exception e
              (new Exception "math problem" e)))
        b (enc-dec a)
        c (ex-cause b)

        m1 (Throwable->map b)
        m2 (Throwable->map c)]

    (is (instance? Throwable b))
    (is (= "math problem" (ex-message b)))

    (is (= "Divide by zero" (ex-message c)))
    (is (nil? (ex-cause c)))

    (is (= 2 (-> m1 :via count)))
    (is (= 1 (-> m2 :via count)))))

(deftest test-throwable-suppressed

  (let [su (new Exception "4")
        e3 (new Exception "3")
        e2 (new Exception "2" e3)
        e1 (new Exception "1" e2)

        _ (.addSuppressed e3 su)

        b (enc-dec e1)

        root (-> b ex-cause ex-cause)

        sups
        (.getSuppressed root)]

    (is (= 1 (count sups)))
    (is (= "4" (-> sups
                   first
                   (ex-message))))
    (is (= "3" (ex-message root)))))

(deftest test-ex-info
  (let [a (ex-info "a" {:a 1})
        b (enc-dec a)
        m (Throwable->map b)]
    (is (instance? ExceptionInfo b))
    (is (= {:a 1} (ex-data b)))))

(deftest test-ex-info-nested

  (let [a (ex-info "a" {:a 1}
                   (ex-info "b" {:b 2}
                            (ex-info "c" {:c 3})))
        b (enc-dec a)
        m (Throwable->map b)]

    (is (= '[{:type clojure.lang.ExceptionInfo, :message "a", :data {:a 1}}
             {:type clojure.lang.ExceptionInfo, :message "b", :data {:b 2}}
             {:type clojure.lang.ExceptionInfo, :message "c", :data {:c 3}}]
           (->> m
                :via
                (mapv (fn [row]
                        (dissoc row :at))))))))

(deftest test-ex-io-nested

  (let [a (new IOException "io"
               (new Exception "ex"
                    (ex-info "info" {:foo 1}
                             (new IOException "root"))))
        b (enc-dec a)
        m (Throwable->map b)]

    (is (= '[{:type java.io.IOException, :message "io"}
             {:type java.lang.Exception, :message "ex"}
             {:type clojure.lang.ExceptionInfo, :message "info", :data {:foo 1}}
             {:type java.io.IOException, :message "root"}]
           (->> m
                :via
                (mapv (fn [row]
                        (dissoc row :at))))))))

(deftest test-ex-NPE

  (let [a (try
            (/ 1 nil)
            (catch NullPointerException e
              e))
        b (enc-dec a)]

    (is (instance? NullPointerException b))
    (is (= "Cannot invoke \"Object.getClass()\" because \"x\" is null"
           (ex-message b)))))

;; TODO check unsupported throwable

(deftest test-input-stream-ok
  (let [a (-> "hello" .getBytes (io/input-stream))
        b (enc-dec a)]
    (is (instance? InputStream b))
    (is (= "hello" (slurp b)))))

(deftest test-input-stream-large
  (let [s (str/join (repeat 3000 "abcabcabcabcabcabcabcabcabcabcabcabc"))
        a (-> s .getBytes (io/input-stream))
        b (enc-dec a)]
    (is (instance? InputStream b))
    (let [string (slurp b)]
      (is (str/starts-with? string "abc"))
      (is (= 108000 (count string))))))

(defn get-temp-file
  (^File []
   (get-temp-file "tmp" ".tmp"))
  (^File [prefix suffix]
   (File/createTempFile prefix suffix)))

(deftest test-version
  (let [file (get-temp-file "test" ".dump")]
    (with-open [e (d/encoder file)]
      (d/encode e 1))
    (with-open [d (d/decoder file)]
      (is (= 1 (d/version d))))))

(deftest test-enc-dec-seq
  (let [file (get-temp-file "test" ".dump")]
    (with-open [e (d/encoder file)]
      (d/encode-seq e (for [x [1 2 3]]
                        (* x x))))
    (with-open [d (d/decoder file)]
      (let [x1 (d/decode d)
            x2 (d/decode d)
            x3 (d/decode d)
            x4 (d/decode d)
            x5 (d/decode d)]
        (is (= 1 x1))
        (is (= 4 x2))
        (is (= 9 x3))
        (is (d/eof? x4))
        (is (d/eof? x5))))))

(deftest test-decode-iteration
  (let [file (get-temp-file "test" ".dump")
        data [nil 1 nil 2 nil 3 nil]]
    (with-open [e (d/encoder file)]
      (d/encode-seq e (for [x data]
                        x)))

    (with-open [d (d/decoder file)]
      (is (= data (vec d))))

    (with-open [d (d/decoder file)]
      (is (= data (for [x d]
                    x))))

    (with-open [d (d/decoder file)]
      (is (= '("" "1" "" "2" "" "3" "") (map str d))))

    (with-open [d (d/decoder file)]
      (is (= data (seq d))))

    (with-open [d (d/decoder file)]
      (is (= data (d/decode-seq d))))

    (with-open [d (d/decoder file)]
      (let [i (RT/iter d)]
        (is (.hasNext i))
        (is (.hasNext i))
        (is (= nil (.next i)))
        (is (.hasNext i))
        (is (.hasNext i))
        (is (.hasNext i))
        (is (.hasNext i))
        (is (.hasNext i))
        (is (= 1 (.next i)))
        (is (= nil (.next i)))
        (is (= 2 (.next i)))
        (is (= nil (.next i)))
        (is (= 3 (.next i)))
        (is (.hasNext i))
        (is (.hasNext i))
        (is (.hasNext i))
        (is (= nil (.next i)))
        (is (false? (.hasNext i)))
        (is (false? (.hasNext i)))
        (is (false? (.hasNext i)))
        (is (false? (.hasNext i)))
        (try
          (.next i)
          (is false "must have been an error")
          (catch NoSuchElementException e
            (is (= "decode iterator has reached the end"
                   (ex-message e)))))))))


(deftest test-encode-hi-level-api

  (let [file (get-temp-file "test" ".dump")]
    (d/encode-to 1 file)
    (d/with-decoder [d file]
      (is (= [1] (vec d)))))

  (let [file (get-temp-file "test" ".dump")]
    (is (= 3 (d/encode-seq-to [1 2 3] file)))
    (d/with-decoder [d file]
      (is (= [1 2 3] (vec d))))))


(deftest test-decode-hi-level-api

  (let [file (get-temp-file "test" ".dump")]

    (d/with-encoder [e file]
      (d/encode e 1)
      (d/encode e 2)
      (d/encode e 3))

    (is (= 1 (d/decode-from file)))
    (is (= [1 2 3] (d/decode-seq-from file)))))
