(ns clj-pail.core-test
  (:require [clj-pail.core :as pail]
            [midje.sweet :refer :all])
  (:import (com.backtype.hadoop.pail PailSpec PailStructure)))


(defchecker instance-of
  [expected]
  (checker [actual]
    (instance? expected actual)))


(facts "spec"
  (let [structure (proxy [PailStructure] [])]
    (fact "creates a PailSpec from a PailStructure"
      (pail/spec structure) => (instance-of PailSpec)
      (.getStructure (pail/spec structure)) => structure)))
