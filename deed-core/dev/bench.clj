(ns bench
  (:import
   java.io.ByteArrayOutputStream)
  (:require
   [jsonista.core :as json]
   [taoensso.nippy :as nippy]
   [clojure.java.io :as io]
   [deed.core :as deed])
  (:use criterium.core))

(deftype StressRecord [x])

(deftype StressType [x])

;; Stolen from nippy
(def STRESS-DATA
  {:nil                   nil
   :true                  true
   :false                 false
   :false-boxed (Boolean. false)

   :char      \ಬ
   :str-short "ಬಾ ಇಲ್ಲಿ ಸಂಭವಿಸ"
   :str-long  (reduce str (range 1024))
   :kw        :keyword
   :kw-ns     ::keyword
   :sym       'foo
   :sym-ns    'foo/bar
   :kw-long   (keyword (reduce str "_" (range 128)) (reduce str "_" (range 128)))
   :sym-long  (symbol  (reduce str "_" (range 128)) (reduce str "_" (range 128)))

   :byte      (byte   16)
   :short     (short  42)
   :integer   (int    3)
   :long      (long   3)
   :float     (float  3.1415926535897932384626433832795)
   :double    (double 3.1415926535897932384626433832795)
   :bigdec    (bigdec 3.1415926535897932384626433832795)
   :bigint    (bigint  31415926535897932384626433832795)
   :ratio     22/7

   :list      (list 1 2 3 4 5 (list 6 7 8 (list 9 10 (list) ())))
   :vector    [1 2 3 4 5 [6 7 8 [9 10 [[]]]]]
   :subvec    (subvec [1 2 3 4 5 6 7 8] 2 8)
   :map       {:a 1 :b 2 :c 3 :d {:e 4 :f {:g 5 :h 6 :i 7 :j {{} {}}}}}
   :map-entry (clojure.lang.MapEntry/create "key" "val")
   :set       #{1 2 3 4 5 #{6 7 8 #{9 10 #{#{}}}}}
   :meta      (with-meta {:a :A} {:metakey :metaval})
   :nested    [#{{1 [:a :b] 2 [:c :d] 3 [:e :f]} [#{{[] ()}}] #{:a :b}}
               #{{1 [:a :b] 2 [:c :d] 3 [:e :f]} [#{{[] ()}}] #{:a :b}}
               [1 [1 2 [1 2 3 [1 2 3 4 [1 2 3 4 5 "ಬಾ ಇಲ್ಲಿ ಸಂಭವಿಸ"] {} #{} [] ()]]]]]

   :regex          #"^(https?:)?//(www\?|\?)?"
   :sorted-set     (sorted-set 1 2 3 4 5)
   :sorted-map     (sorted-map :b 2 :a 1 :d 4 :c 3)
   :lazy-seq-empty (map identity ())
   :lazy-seq       (repeatedly 64 #(do nil))
   :queue          (into clojure.lang.PersistentQueue/EMPTY [:a :b :c :d :e :f :g])
   :queue-empty          clojure.lang.PersistentQueue/EMPTY

   :uuid       (java.util.UUID. 7232453380187312026 -7067939076204274491)
   :uri        (java.net.URI. "https://clojure.org")
   :defrecord  (StressRecord. "data")
   :deftype    (StressType.   "data")
   :bytes      (byte-array   [(byte 1) (byte 2) (byte 3)])
   :objects    (object-array [1 "two" {:data "data"}])

   :util-date (java.util.Date. 1577884455500)
   :sql-date  (java.sql.Date.  1577884455500)
   :instant   (java.time.Instant/parse "2020-01-01T13:14:15.50Z")
   :duration  (java.time.Duration/ofSeconds 100 100)
   :period    (java.time.Period/of 1 1 1)

   :throwable (Throwable. "Msg")
   :exception (Exception. "Msg")
   :ex-info   (ex-info    "Msg" {:data "data"})

   :many-longs    (vec (repeatedly 512         #(rand-nth (range 10))))
   :many-doubles  (vec (repeatedly 512 #(double (rand-nth (range 10)))))
   :many-strings  (vec (repeatedly 512         #(rand-nth ["foo" "bar" "baz" "qux"])))
   :many-keywords (vec (repeatedly 512
                                   #(keyword
                                     (rand-nth ["foo" "bar" "baz" "qux" nil])
                                     (rand-nth ["foo" "bar" "baz" "qux"    ]))))})

(comment
  [(=      (stress-data {:comparable? true}) (stress-data {:comparable? true}))
   (let [d (stress-data {:comparable? true})] (= (thaw (freeze d)) d))])

(def DATA
  (vec
   (for [x (range 1000000)]
     x))

  #_
  (vec
   (for [_ (range 10000000)]
     {:foo 1}
     ))

  #_
  (vec
   (for [_ (range 1000)]
     (vec
      (for [x (range 1000)]
        {:foo x
         :bar x}))))

  #_
  (vec
   (for [_ (range 999999)]
     {:foo 1
      :bar 2})))

(comment

  ;;
  ;; encode
  ;;
  (quick-bench
      (do (deed/encode-to-bytes STRESS-DATA) nil))

  (quick-bench
      (nippy/freeze STRESS-DATA {:compressor nil
                                 :encryptor nil}))

  ;;
  ;; Decode
  ;;
  (quick-bench (deed/decode-from deed-bytes))

  (quick-bench (nippy/thaw nippy-bytes))





  (def -m1 (new java.util.HashMap))
  (.put -m1 1 2)
  (.put -m1 3 4)
  (.put -m1 "aaa" -m1)


  ;; time
  (time
   (deed/with-encoder [e (new ByteArrayOutputStream 0xFFFF)]
     (deed/encode e DATA)))

  ;; mem
  (quick-bench
      (do (deed/encode-to-bytes STRESS-DATA) nil))

  ;; json mem
  (quick-bench
      (json/write-value (new ByteArrayOutputStream 0xFFFF) DATA))

  ;; file
  (quick-bench
      (deed/with-encoder [e (io/file "out.deed")]
        (deed/encode e DATA)))

  (quick-bench
      (deed/decode-from (io/file "out.deed")))

  ;; write
  (deed/with-encoder [e (new ByteArrayOutputStream 0xFFFF)]
    (quick-bench
        (deed/encode e DATA)))

  (time (do (nippy/freeze DATA) nil))

  (quick-bench
      (nippy/thaw-from-file (io/file "out.nippy")))

  (quick-bench
      (nippy/freeze-to-file (io/file "out.nippy") DATA))



  (quick-bench
      (nippy/freeze DATA {:compressor nil
                          :encryptor nil}))


  (quick-bench
      (deed/with-encoder [e (new ByteArrayOutputStream 0xFFFF)]
        (deed/encode e DATA)))

  (quick-bench
      (nippy/freeze DATA))

  (quick-bench
      (nippy/freeze-to-file "out.nippy" DATA))

  ;;


  )

;; nippy
;; Execution time mean : 64.530873 ms

;; BB + output stream
;; Execution time mean : 112.500529 ms multi-method
;; Execution time mean :  38.444453 ms switch

;; ObjectOutputStream
;; Execution time mean : 11.566030 ms

;; DataOutputStream
;; Execution time mean : 34.584300 ms

;; long switch/case
;; Execution time mean : 14.566030 ms

;; cascading switch case
;; Execution time mean : 13.762154 ms

;; BB + output stream
;; Execution time mean : 41.929175 ms

;; BB + Protocol
;; Execution time mean : 41.836865 ms

;; DataOutputStream + Protocol
;; Execution time mean : 38.514992 ms

;; DataOutputStream + Protocol more types
;; Execution time mean : 42.603235 ms

;; Nippy
;; Execution time mean : 64.002942 ms

;; Nippy file
;; Execution time mean : 75.161894 ms

;; Deed file
;; Execution time mean : 51.339890 ms

;; i5 nippy Execution time mean : 126.351367 ms
;; i5 deed Execution time mean : 65.736057 ms



;; deed enc
;; Evaluation count : 3378 in 6 samples of 563 calls.
;;              Execution time mean : 182.705886 µs
;;     Execution time std-deviation : 6.105809 µs
;;    Execution time lower quantile : 176.910199 µs ( 2.5%)
;;    Execution time upper quantile : 190.403639 µs (97.5%)
;;                    Overhead used : 5.359273 ns


;; nippy enc
;; Evaluation count : 2142 in 6 samples of 357 calls.
;;              Execution time mean : 291.348924 µs
;;     Execution time std-deviation : 5.820150 µs
;;    Execution time lower quantile : 282.747908 µs ( 2.5%)
;;    Execution time upper quantile : 297.646792 µs (97.5%)
;;                    Overhead used : 5.359273 ns

;; deed dec
;; Evaluation count : 2496 in 6 samples of 416 calls.
;;              Execution time mean : 237.042434 µs
;;     Execution time std-deviation : 6.558134 µs
;;    Execution time lower quantile : 230.504858 µs ( 2.5%)
;;    Execution time upper quantile : 246.314941 µs (97.5%)
;;                    Overhead used : 5.359273 ns

;; nippy dec
;; Evaluation count : 2070 in 6 samples of 345 calls.
;;              Execution time mean : 297.640234 µs
;;     Execution time std-deviation : 10.797824 µs
;;    Execution time lower quantile : 287.920006 µs ( 2.5%)
;;    Execution time upper quantile : 313.039736 µs (97.5%)
;;                    Overhead used : 5.359273 ns
