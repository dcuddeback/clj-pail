(ns clj-pail.partitioner-test
  (:require [clj-pail.partitioner :as partitioner])
  (:use midje.sweet))


(facts "make-partition*"
  (fact "passes through to protocol implementation"
    (partitioner/make-partition* ..partitioner.. ..object..) => ..result..
    (provided
      (partitioner/make-partition ..partitioner.. ..object..) => ..result..)))


(facts "validate*"
  (fact "passes through to protocol implementation"
    (partitioner/validate* ..partitioner.. ..dirs..) => ..result..
    (provided
      (partitioner/validate ..partitioner.. ..dirs..) => ..result..)))


(facts "vertical-partition"
  (fact "returns a list of partitions"
    (partitioner/vertical-partition ..partitioner.. ..object..) => (just "foo" "bar")
    (provided
      (partitioner/make-partition ..partitioner.. ..object..) => ["foo" "bar"]))

  (fact "converts numbers to strings"
    (partitioner/vertical-partition ..partitioner.. ..object..) => (just "1")
    (provided
      (partitioner/make-partition ..partitioner.. ..object..) => [1]))

  (fact "ignores nils"
    (partitioner/vertical-partition ..partitioner.. ..object..) => (just "foo" "bar")
    (provided
      (partitioner/make-partition ..partitioner.. ..object..) => ["foo" nil "bar"]))

  (fact "converts nil to an empty sequence"
    (partitioner/vertical-partition ..partitioner.. ..object..) => empty?
    (partitioner/vertical-partition ..partitioner.. ..object..) => sequential?

    (against-background
      (partitioner/make-partition ..partitioner.. ..object..) => nil)))


(tabular "valid-partition?"
  (fact "converts arrays to Clojure seqs"
    (partitioner/valid-partition? ..partitioner.. (into-array String ?dirs)) => ..result..
    (provided
      (partitioner/validate ..partitioner.. ?seq) => [..result.. irrelevant]))

  ?dirs         ?seq
  []            (as-checker empty?)
  ["foo"]       (just ["foo"])
  ["bar" "42"]  (just ["bar" "42"]))


(facts "NullPartitioner"
  (fact "make-partition returns an empty sequence"
    (partitioner/make-partition (partitioner/null-partitioner) anything) => empty?
    (partitioner/make-partition (partitioner/null-partitioner) anything) => sequential?)

  (fact "vertical-partition returns an empty sequence"
    (partitioner/vertical-partition (partitioner/null-partitioner) anything) => empty?
    (partitioner/vertical-partition (partitioner/null-partitioner) anything) => sequential?)

  (tabular
    (facts
      (fact "validate returns true"
        (partitioner/validate (partitioner/null-partitioner) ?dirs) => (just true anything))

      (fact "validate returns the directories"
        (partitioner/validate (partitioner/null-partitioner) ?dirs) => (just anything ?dirs))

      (fact "valid-partition? always returns true"
        (partitioner/valid-partition? (partitioner/null-partitioner) ?dirs) => true))

    ?dirs
    nil
    []
    ["foo" "bar"]))
