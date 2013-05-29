(ns clj-pail.serializer-test
  (:require [clj-pail.serializer :as serializer])
  (:use midje.sweet))

(tabular
  (facts "NullSerializer"
    (let [buffer ?bytes]
      (fact "serializes byte array as itself"
        (serializer/serialize (serializer/null-serializer) buffer) => buffer)

      (fact "deserializes byte array as itself"
        (serializer/deserialize (serializer/null-serializer) buffer) => buffer)))

  ?bytes
  (byte-array 0)
  (byte-array 10)
  (byte-array (map byte (range 10)))
  (byte-array (map (comp byte
                         #(- % 128)
                         #(mod % 256))
                   (range 100000))))
