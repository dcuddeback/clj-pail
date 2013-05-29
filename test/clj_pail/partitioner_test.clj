(ns clj-pail.partitioner-test
  (:require [clj-pail.partitioner :as partitioner])
  (:use midje.sweet))


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


(facts "valid-partition?"
  (fact "returns true if the partition is valid"
    (partitioner/valid-partition? ..partitioner.. ..dirs..) => true
    (provided
      (partitioner/validate ..partitioner.. ..dirs..) => [true irrelevant]))

  (fact "returns false if the partition is invalid"
    (partitioner/valid-partition? ..partitioner.. ..dirs..) => false
    (provided
      (partitioner/validate ..partitioner.. ..dirs..) => [false irrelevant]))

  (tabular
    (fact "ignores the remaining directories"
      (partitioner/valid-partition? ..partitioner.. ..dirs..) => ..result..
      (provided
        (partitioner/validate ..partitioner.. ..dirs..) => [..result.. ?dirs]))

    ?dirs
    nil
    []
    ["foo" "bar"]))
