# clj-pail

[![Build Status](https://travis-ci.org/dcuddeback/clj-pail.png?branch=master)](https://travis-ci.org/dcuddeback/clj-pail)

A Clojure interface for [Pail](https://github.com/nathanmarz/dfs-datastores) that allows for separate definitions of serialization, vertical partitioning, and `PailStructure`.

It breaks up the [`PailStructure` interface](https://github.com/nathanmarz/dfs-datastores/blob/develop/dfs-datastores/src/main/java/com/backtype/hadoop/pail/PailStructure.java) into two separate Clojure protocols (`Serializer` and `VerticalPartitioner`) so that serialization and partitioning can be defined separately. Then it defines a macro (`gen-structure`) to generate a class that implements `PailStructure` by composing the two protocols.

## Usage

Add `clj-pail` to your project's dependencies. If you're using Leiningen, your `project.clj` should look something like this:

~~~clojure
(defproject ...
  :dependencies [[clj-pail "0.1.1"]])
~~~

### Defining a `PailStructure`

You can generate classes that implement the `PailStructure` interface with the [`gen-structure` macro](src/main/clj_pail/structure.clj) from the `clj-pail.structure` namespace. The `PailStructure` interface is used by Pail to serialize, deserialize, and keep organized your data.

~~~clojure
(ns ...
  (:require [clj-pail.structure :as s]))

(s/gen-structure com.example.pail.DefaultPailStructure)

~~~

`gen-structure` uses `gen-class` under the hood. So any namespace that uses `gen-structure` needs to be AOT-compiled. In Leiningen, add your namespace to the `:aot` key in `project.clj`:

~~~clojure
(defproject ...
  :aot [myproj.ns.that.uses.clj-pail.structure])
~~~

#### Options

By default, a `PailStructure` class generated with `gen-structure` will do nothing. It will be defined to handle `byte[]`; serialization and deserialization will do nothing (because your data is already a `byte` array); and no vertical partitioning will be defined.

These behaviors can be specified with options to `gen-structure`:

~~~clojure
(s/gen-structure com.example.pail.CustomPailStructure
                 :type java.util.Date
                 :serializer (CustomDateSerializer. (java.text.DateFormat/getDateTimeInstance))
                 :partitioner (DailyDatePartitioner.)
                 :prefix "date-")
~~~

This example defines a `PailStructure` which serializes `Date` objects with a custom serializer and partitioner (explained below). `:prefix` can be set (as in `gen-class`) to avoid name collisions when defining multiple structures in the same namespace.

### Serialization

Serialization is defined by extending the [`clj-pail.serializer.Serializer`](src/main/clj_pail/serializer.clj) protocol.

~~~clojure
(ns ...
  (:require [clj-pail.serializer :as s]))

; For illustrative purposes only. Please don't serialize your dates this way.
(defrecord CustomDateSerializer [#^java.text.DateFormat format]
  s/Serializer
  (serialize [this date]
    (.. (:format this)
      (format date)
      (getBytes)))

  (deserialize [this buffer]
    (.parse (:format this)
            (String. buffer))))
~~~

### Vertical Partitioning

Vertical partitioning is defined by extending the [`clj-pail.partitioner.VerticalPartitioner`](src/main/clj_pail/partitioner.clj) protocol.

~~~clojure
(ns ...
  (:require [clj-pail.partitioner :as p]))

; For illustrative purposes only. This would be a very inefficient way to partition dates.
(defrecord DailyDatePartitioner []
  p/VerticalPartitioner
  (make-partition [this date]
    (vector ; must return a list
      (quot (.getTime date)
            (* 24 60 60 1000))))

  (validate [this dirs]
    (try
      (do
        (Long/parseLong (first dirs))
        [true (rest dirs)])
      (catch NumberFormatException e
        [false (rest dirs)]))))
~~~

## License

Copyright © 2013 David Cuddeback

Distributed under the [MIT License](LICENSE).
