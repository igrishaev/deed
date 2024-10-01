# Deed

A fast, zero-deps binary encoding and decoding library for Clojure.

## About

[vectorz]: https://github.com/mikera/vectorz

Deed is a library to dump any value into a byte array and read it back. It
supports plenty of types out from the box: Java primitives, most of the Clojure
types, Java collections, date and time, and so on. It supports even such tricky
types as atoms, refs, and input streams. The full list of supported types is
shown in the corresponding section below.

Deed can be extended for custom types with ease. There is a contrib package that
extends encoding and decoding logic for vectors from the the well-known
[mikera/vectorz][vectorz] library.

Deed is written in pure Java and thus is pretty fast (see the "Benchmarks"
section). It's about 20% faster than Nippy. It doesn't rely on built-in Java
`Serializable` interface for security reasons.

Provides convenient API for reading the frozen data lazily one by one.

## Motivation

Obviously you would ask why doing this if we have Nippy? This is what I had in
mind while working on Deed:

1. The library must be **absolutely free** from dependencies. This is true for
   the `deed-core` package: it's written in pure Java with no dependencies at
   all. By adding it into a project, you won't blow up you uberjar, nor you will
   have troubles with building a native image with GraalVM.

2. Any part of Deed that requires 3rd-party stuff must be a sub-library. Thus,
   you have precise control of what you use and what you don't

3. Unlike Nippy, Deed never falls back to native Java serialization. There is
   just no such an option. Thus, you cannot be attacked by reading a forged
   binary dump.

4. Deed is simple: it blindly works with input- and output byte streams having
   no idea what's behind them. It doesn't take compression or encryption into
   account -- yet there are utilities for corresponding types of streams.

5. The library provides API which personally I consider more convenient than
   Nippi's. Namely, it allows to lazily iterate by a series of encoded data
   instead of reading the whole dump at once.

6. Why not using popular and cross-platform formats like JSON, Message Pack, or
   YAML? Well, because of poor types support. JSON has only primitive types and
   collections, and nothing else. Extending it with custom types is always a
   pain. At the same time, I want my decoded data be as close to the origin data
   as possible, say, `LocalDateTime` be an instance of `LocalDateTime` but not a
   string nor `java.util.Date`. Sometimes, preserving metadata is crucial. To
   haldle all of these cases, there is only one solution: make your own library.

## Installation & Requirements

Deed requires Java version at least 16 to run. Tested with Clojure 1.9.0.

**The core module** with basic encode and decode capabilities:

~~~clojure
;; lein
[com.github.igrishaev/deed-core "0.1.0-SNAPSHOT"]

;; deps
com.github.igrishaev/deed-core {:mvn/version "0.1.0-SNAPSHOT"}
~~~

**Base64 module** do encode and decode from/into base64 on the fly:

~~~clojure
;; lein
[com.github.igrishaev/deed-base64 "0.1.0-SNAPSHOT"]

;; deps
com.github.igrishaev/deed-base64 {:mvn/version "0.1.0-SNAPSHOT"}
~~~

**Vectorz module** wich extends Deed with a number of `Vector*` classes from the
[mikera/vectorz][vectorz] library:

~~~clojure
;; lein
[com.github.igrishaev/deed-vectorz "0.1.0-SNAPSHOT"]

;; deps
com.github.igrishaev/deed-vectorz {:mvn/version "0.1.0-SNAPSHOT"}
~~~

## Quick Demo

This is a very brief example of how to dump the data into a file. Prepare the
namespace with imports:

~~~clojure
(ns demo
  (:require
   [clojure.java.io :as io]
   [deed.core :as deed]))
~~~

Declare the file and the data:

~~~clojure
(def file
  (io/file "dump.deed"))

(def data
  {:number 1
   :string "hello"
   :bool true
   :nil nil
   :symbol 'hello/test
   :simple-kw :test
   :complex-kw :foo/bar
   :vector [1 2 :test nil 42 "hello"]
   :map {:test {:bar {'dunno {"lol" [:kek]}}}}
   :set #{:a :b :c}
   :atom (atom {:abc 42})})
~~~

Pass them into the `encode-to` function as follows:

~~~clojure
(deed/encode-to data file)
~~~

And this is it! If you examine the file with any kind of hex editor, you'll see
a binary payload:

~~~clojure
xxd /path/to/dump.deed

00000000: 0001 0001 0000 0000 0000 0000 0000 0000  ................
00000010: 0000 0000 0000 0000 0000 0000 0000 0000  ................
00000020: 0000 003b 0000 000b 004a 0000 0006 6e75  ...;.....J....nu
00000030: 6d62 6572 000e 004a 0000 0006 7379 6d62  mber...J....symb
00000040: 6f6c 004b 0000 000a 6865 6c6c 6f2f 7465  ol.K....hello/te
00000050: 7374 004a 0000 000a 636f 6d70 6c65 782d  st.J....complex-
00000060: 6b77 004a 0000 0007 666f 6f2f 6261 7200  kw.J....foo/bar.
00000070: 4a00 0000 0673 7472 696e 6700 2b00 0000  J....string.+...
00000080: 0568 656c 6c6f 004a 0000 0006 7665 6374  .hello.J....vect
00000090: 6f72 002e 0000 0006 000e 000c 0000 0000  or..............
000000a0: 0000 0002 004a 0000 0004 7465 7374 0000  .....J....test..
000000b0: 000c 0000 0000 0000 002a 002b 0000 0005  .........*.+....
000000c0: 6865 6c6c 6f00 4a00 0000 036e 696c 0000  hello.J....nil..
000000d0: 004a 0000 0004 626f 6f6c 0029 004a 0000  .J....bool.).J..
000000e0: 0003 7365 7400 3300 0000 0300 4a00 0000  ..set.3.....J...
000000f0: 0163 004a 0000 0001 6200 4a00 0000 0161  .c.J....b.J....a
00000100: 004a 0000 0009 7369 6d70 6c65 2d6b 7700  .J....simple-kw.
00000110: 4a00 0000 0474 6573 7400 4a00 0000 0461  J....test.J....a
00000120: 746f 6d00 3000 3b00 0000 0100 4a00 0000  tom.0.;.....J...
00000130: 0361 6263 000c 0000 0000 0000 002a 004a  .abc.........*.J
00000140: 0000 0003 6d61 7000 3b00 0000 0100 4a00  ....map.;.....J.
00000150: 0000 0474 6573 7400 3b00 0000 0100 4a00  ...test.;.....J.
00000160: 0000 0362 6172 003b 0000 0001 004b 0000  ...bar.;.....K..
00000170: 0005 6475 6e6e 6f00 3b00 0000 0100 2b00  ..dunno.;.....+.
00000180: 0000 036c 6f6c 002e 0000 0001 004a 0000  ...lol.......J..
00000190: 0003 6b65 6b                             ..kek
~~~

Now that you have a .deed file, read it back using the `decode-from` function:

~~~clojure
(def data-back
  (deed/decode-from file))
~~~

Print the `data-back` to ensure it keeps the origin types:

~~~clojure
{:number 1,
 :symbol hello/test,
 :complex-kw :foo/bar,
 :string "hello",
 :vector [1 2 :test nil 42 "hello"],
 :nil nil,
 :bool true,
 :set #{:c :b :a},
 :simple-kw :test,
 :atom #<Atom@75f6dd87: {:abc 42}>,
 :map {:test {:bar {dunno {"lol" [:kek]}}}}}
~~~

Compare them to ensure we didn't loose anything. Since atoms cannot be compared,
we `dissoc` them from both sides:

~~~clojure
(= (dissoc data :atom) (dissoc data-back :atom))
;; true
~~~

Deed supports plenty of built-in Clojure and Java types. The next section
describes which API it provides to manage the data.

## API

### Simple Encode and Decode

The `encode-to` function accepts two parameters: a value and an output. The
value is an instance of any [supported type](#supported-types) (a map, a vector,
etc).

The output is anything that can be coerced to the output stream using the
`io/output-stream` function. It could be a file, another output stream, a byte
array, or similar. If you pass a string, it's treated as a name of a file which
will be created.

Examples:

~~~clojure
(deed/encode-to {:foo 123} "test.deed")

(deed/encode-to {:foo 123} (io/file "test2.deed"))

(deed/encode-to {:foo 123} (-> "test3.deed"
                               io/file
                               io/output-stream))
~~~

All three invocations dump the same map `{:foo 123}` into different files.

To read the data back, invoke the `decode-from` function. It accepts anything
that can be coerced into an input stream using the `io/input-stream` function
internally. It might be a file, another stream, a byte array, or a name of a
file.

~~~clojure
(deed/decode-from "test.deed")
;; {:foo 123}

(deed/decode-from (io/file "test2.deed"))
;; {:foo 123}

(deed/decode-from (-> "test3.deed"
                      io/file
                      io/input-stream))
;; {:foo 123}
~~~

### Encoding to Memory

The functions below rely on external IO resources. If you want to dump the data
into memory, use the `encode-to-bytes` function. It returns a byte array without
any IO interaction:

~~~clojure
(def buf
  (deed/encode-to-bytes {:test 123}))

(println (vec buf))

[0 1 0 1 0 0 ... 59 0 0 0 1 0 74 0 0 0 4 116 101 115 116 0 12 0 0 0 0 0 0 0 123]
~~~

There is no an opposite `decode-from-bytes` function because a byte array is
already a data source for the `decode-from` function. Just pass the result into
it:

~~~clojure
(deed/decode-from buf)
;; {:test 123}
~~~

### Sequential Encoding and Decoding

Often, we dump vast collections to explore them afterwards. Say, you're going to
write 10M of database rows into a file to find broken rows with a script.

The functions above encode and decode a single value. That's OK for primitive
types and maps but not good for vast collections. For example, if you encode a
vector of 10M items into a file and read it back, you'll get the same vector of
10M items back. Most likely you don't need all of these read at once: you would
better to iterate on them one by one.

This is the case that `encode-seq-to` and `decode-seq-from` functions cover. The
first function accepts a collection of items and writes them sequentially. It's
not a vector any longer but a series of items written one after another. The
`encode-seq-to` invocation returns the number of items written:

~~~clojure
(deed/encode-seq-to [1 2 3] "test.deed")
;; 3
~~~

Instead of vector, there might be a lazy sequence, or anything that can be
iterated.

If you read the dump using `decode-from`, you'll get the first item only:

~~~clojure
(deed/decode-from "test.deed")
1
~~~

To read all of them, use `decode-seq-from`:

~~~clojure
(deed/decode-seq-from "test.deed")
[1 2 3]
~~~

What is the point to use sequential encoding? Because with a special API, you
can read them lazily one by one.

The `with-decoder` macro takes two parameters: the binding symbol and the
input. Internally, it creates a `Decoder` instance and binds it to the first
symbol. The `decode-seq` function returns a lazy sequence of items from a
decoder:

~~~clojure
(deed/with-decoder [d "test.deed"]
  (doseq [item (deed/decode-seq d)]
    (println item)))
;; 1
;; 2
;; 3
~~~

The form above might be also written using the `with-open` macro. Since the
`Decoder` object implements `AutoCloseable` interface, it handles the `.close`
method which in turn closes the origin stream.

~~~clojure
(with-open [d (deed/decoder "test.deed")]
  (doseq [item (deed/decode-seq d)]
    (println item)))
~~~

In fact, you don't even need to pass the decoder object into `decode-seq`
because it implements the `Iterable` Java interface. Just iterate over the
decoder:

~~~clojure
(deed/with-decoder [d "test.deed"]
  (mapv inc d))
;; [2 3 3]

(deed/with-decoder [d "test.deed"]
  (doseq [item d]
    (println "item is" item)))
;; item is 1
;; item is 2
;; item is 3
~~~

Pay attention that items are read lazily so you won't saturate memory.

### Low-Level API

Deed provides low-level API for conditional or imperative encoding. The `encode`
function writes a value into an instance of the `Encoder` class. You can use it
in a cycle with some condition:

~~~clojure
(deed/with-encoder [e "test.deed"]
  (doseq [x (range 1 32)]
    (when (even? x)
      (deed/encode e x))))
~~~

The `decode` function reads an object from the `Decoder` instance. When the end
of the stream is met, you'll get a special `EOF` object that you can check with
the `eof?` preficate:

~~~clojure
(deed/with-decoder [d "test.deed"]
  (loop [i 0]
    (let [item (deed/decode d)]
      (if (deed/eof? item)
        (println "EOF")
        (do
          (println "item" i item)
          (recur (inc i)))))))

;; item 0 2
;; item 1 4
;; item 2 6
;; item 3 8
;; item 4 10
;; item 5 12
;; item 6 14
;; item 7 16
;; item 8 18
;; item 9 20
;; item 10 22
;; item 11 24
;; item 12 26
;; item 13 28
;; item 14 30
;; EOF
~~~

The low-level might be useful when you need precise control on what you're
encoding and decoding.

### API Options

Most of the functions accept an optional map of parameters. Here is a list of
ones available at the moment:

| Name                     | Default           | Meaning                                                                              |
|--------------------------|-------------------|--------------------------------------------------------------------------------------|
| `:deref-timeout-ms`      | 5000              | The number of milliseconds to wait when derefing futures.                            |
| `:object-chunk-size`     | 0xFF              | The number of object chunk when encoding uncountable collections (e.g. lazy).        |
| `:byte-chunk-size`       | 0xFFFF            | The number of byte chunk when encoding input streams.                                |
| `:uncountable-max-items` | Integer.MAX_VALUE | The max number of items to encode when encoding uncountable collections (e.g. lazy). |
| `:encode-unsupported?`   |                   |                                                                                      |
| `:io-temp-file?`         |                   |                                                                                      |
| `:save-meta?`            |                   |                                                                                      |
| `:append?`               |                   |                                                                                      |
|                          |                   |                                                                                      |

## GZip

## Handle Unsupported Types

## Supported Types

## Custom Types

## Contrib

## Binary Format

## Benchmarks
