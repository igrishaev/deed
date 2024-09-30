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

## Installation & Requirements

Deed requires at least Java 16 to run. Tested with Clojure 1.9.0.

## Quick Demo

## API

## Supported Types

## Extending with Custom Types

## Contrib

## Benchmarks
