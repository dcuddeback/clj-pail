(ns clj-pail.fakes.structure
  (:require [clj-pail.structure :as structure]
            [clj-pail.serializer :as s]
            [clj-pail.partitioner :as p])
  (:use midje.open-protocols))


(defrecord-openly FakeSerializer []
  s/Serializer
  (s/serialize [this object] :fake)
  (s/deserialize [this buffer] :fake))


(defrecord-openly FakePartitioner []
  p/VerticalPartitioner
  (p/make-partition [this object] :fake)
  (p/validate [this dirs] :fake))


(structure/gen-structure clj_pail.fakes.structure.DefaultPailStructure)


(structure/gen-structure clj_pail.fakes.structure.FakePailStructure
                         :type Object
                         :prefix "fake-"
                         :serializer (FakeSerializer.)
                         :partitioner (FakePartitioner.))
