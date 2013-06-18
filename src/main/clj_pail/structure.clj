(ns clj-pail.structure
  "Utilities for defining Pail structures."
  (:require [clj-pail.serializer :as serializer]
            [clj-pail.partitioner :as partitioner]))

;; ## Generating Pail Structures

(defmacro gen-structure
"Generates a class that implements `PailStructure`. The `PailStructure`'s behavior can be
customized by providing a `Serializer` or `VerticalPartitioner`. The class will be named whatever
is provided for the `name` parameter. (It should be specified just as it would be `gen-class`.

The `Serializer` and `VerticalPartitioner` should be able to handle the object type specified by the
`:type` option.

### Options

`:type`: The object type that the `PailStructure` can serialize. (Defaults to a byte array.)

`:serializer`: An implementation of the `Serializer` protocol, which will be used to serialize and
deserialize object. (Defaults to `NullSerializer`.)

`:partitioner`: An implementation of the `VerticalPartitioner` protocol, which will be used to
vertically partition the data. (Defaults to `NullPartitioner`.)

`:prefix`: Used to specify the prefix for the generated methods, just like with `gen-class`. A
prefix should be used to avoid name collisions when generating more than one class in the same
namespace. (Defaults to `\"-\"`.)

Any namespace that uses `gen-structure` should be configured to be AOT-compiled."
  [the-name & {:keys [type serializer partitioner prefix]
               :or {type (class (byte-array 0))
                    serializer `(serializer/null-serializer)
                    partitioner `(partitioner/null-partitioner)
                    prefix "-"}}]
  `(do
     (gen-class
       :name ~the-name
       :implements [com.backtype.hadoop.pail.PailStructure java.io.Serializable]
       :state "state"
       :init "init"
       :prefix ~prefix
       :main false)

     (defn ~(symbol (str prefix "init")) []
       [[] nil])

     (defn ~(symbol (str prefix "getType")) [this#]
       ~type)

     (defn ~(symbol (str prefix "serialize")) [this# object#]
       (serializer/serialize ~serializer object#))

     (defn ~(symbol (str prefix "deserialize")) [this# buffer#]
       (serializer/deserialize ~serializer buffer#))

     (defn ~(symbol (str prefix "getTarget")) [this# object#]
       (partitioner/vertical-partition ~partitioner object#))

     (defn ~(symbol (str prefix "isValidTarget")) [this# dirs#]
       (partitioner/valid-partition? ~partitioner dirs#))))
