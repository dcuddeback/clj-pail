(ns clj-pail.structure-test
  (:require [clj-pail.fakes.structure :as fakes]
            [clj-pail.serializer :as serializer]
            [clj-pail.partitioner :as partitioner])
  (:import [com.backtype.hadoop.pail PailStructure]
           [clj_pail.partitioner NullPartitioner]
           [clj_pail.fakes.structure DefaultPailStructure FakePailStructure FakeSerializer FakePartitioner UnserializableStateStructure]
           [java.io ByteArrayOutputStream ByteArrayInputStream ObjectOutputStream ObjectInputStream NotSerializableException])
  (:use midje.sweet))


(defchecker instance-of [expected]
  (checker [actual]
    (instance? expected actual)))


(facts "gen-structure"
  (facts "default structure"
    (let [default-structure (DefaultPailStructure.)]

      (fact "implements PailStructure"
        default-structure => (instance-of PailStructure))

      (fact "getType returns byte array"
        (.getType default-structure) => (class (byte-array 0)))

      (tabular
        (facts "serialization"
          (let [buffer ?bytes]

            (fact "serializes with NullSerializer"
              (.serialize default-structure buffer) => buffer)

            (fact "deserializes with NullSerializer"
              (.deserialize default-structure buffer) => buffer)))

        ?bytes
        (byte-array 0)
        (byte-array 10))

      (tabular
        (facts "getTarget"
          (let [buffer ?bytes]
            (fact "partitions with NullPartitioner"
              (.getTarget default-structure buffer) => empty?
              (.getTarget default-structure buffer) => sequential?
              (provided
                (partitioner/vertical-partition (instance-of NullPartitioner) buffer) => []))))

        ?bytes
        (byte-array 0)
        (byte-array 10))

      (tabular
        (facts "isValidTarget"
          (let [dirs (into-array String ?dirs)]
            (fact "validates with NullPartitioner"
              (.isValidTarget default-structure dirs) => true
              (provided
                (partitioner/valid-partition? (instance-of NullPartitioner) dirs) => true))))

        ?dirs
        []
        ["foo" "bar"])))


  (facts "with options"
    (let [fake-structure (FakePailStructure.)]

      (fact "implements PailStructure"
        fake-structure => (instance-of PailStructure))

      (fact "getType returns Object"
        (.getType fake-structure) => Object)

      (tabular
        (facts "serialization"
          (let [object ?object, serialized ?serialized]

            (fact "serializes with provided serializer"
              (.serialize fake-structure object) => serialized
              (provided
                (serializer/serialize (instance-of FakeSerializer) object) => serialized))

            (fact "deserializes with provided serializer"
              (.deserialize fake-structure serialized) => object
              (provided
                (serializer/deserialize (instance-of FakeSerializer) serialized) => object))))

        ?object   ?serialized
        nil       (byte-array 0)
        {:foo 42} (byte-array 10))

      (tabular
        (fact "getTarget partitions with provided partitioner"
          (.getTarget fake-structure ?object) => ?partitions
          (provided
            (partitioner/vertical-partition (instance-of FakePartitioner) ?object) => ?partitions))

        ?object   ?partitions
        nil       []
        {:foo 42} ["foo" "42"])

      (tabular
        (facts "isValidTarget"
          (let [dirs (into-array String ?dirs)]
            (fact "validates with provided partitioner"
              (.isValidTarget fake-structure dirs) => ?result
              (provided
                (partitioner/valid-partition? (instance-of FakePartitioner) dirs) => ?result))))

        ?dirs         ?result
        []            false
        ["foo" "42"]  true)))


  (facts "with unserializable state"
    (let [fake-structure (UnserializableStateStructure.)
          bytes-out (ByteArrayOutputStream.)
          object-out (ObjectOutputStream. bytes-out)]

      (fact "should be serializable"
        ; NOTE: These checks depend on being run in this order
        (.writeObject object-out fake-structure) =not=> (throws NotSerializableException)

        (-> bytes-out
          (.toByteArray)
          (ByteArrayInputStream.)
          (ObjectInputStream.)
          (.readObject)) => (instance-of UnserializableStateStructure)

        (-> bytes-out
          (.toByteArray)
          (ByteArrayInputStream.)
          (ObjectInputStream.)
          (.readObject)
          (.getSerializer)) =not=> nil?

        (-> bytes-out
          (.toByteArray)
          (ByteArrayInputStream.)
          (ObjectInputStream.)
          (.readObject)
          (.getPartitioner)) =not=> nil?))))
