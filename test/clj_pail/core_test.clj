(ns clj-pail.core-test
  (:require [clj-pail.core :as pail]
            [clojure.java.shell :as shell])
  (:use midje.sweet)
  (:import (com.backtype.hadoop.pail PailSpec PailStructure DefaultPailStructure)
           (org.apache.hadoop.fs FileSystem Path)
           (org.apache.hadoop.conf Configuration)))


(defchecker instance-of
  [expected]
  (checker [actual]
    (instance? expected actual)))

(defchecker array-eq
  [expected]
  (checker [actual]
           (java.util.Arrays/equals expected actual)))


(facts "spec"
  (let [structure (proxy [PailStructure] [])]
    (fact "creates a PailSpec from a PailStructure"
      (pail/spec structure) => (instance-of PailSpec )
      (.getStructure (pail/spec structure)) => structure)))


(let [tmp-path "tmp/test"]
  (with-state-changes [(before :contents (shell/sh "rm" "-rf" tmp-path))
                       (after :contents (shell/sh "rm" "-rf" tmp-path))]

    (facts "with-snapshot"
      (let [pail-path (str tmp-path "/pail")
            snap-path (str tmp-path "/snapshot")
            pail (-> (DefaultPailStructure.)
                   (pail/spec)
                   (pail/create pail-path :fail-on-exists false))

            before-1 (byte-array 0)
            before-2 (byte-array (map byte (range 10)))
            during-1 (byte-array 8)
            during-2 (byte-array (map byte (range 8)))]

        (with-state-changes [(before :contents (with-open [writer (.openWrite pail)]
                                              (doto writer
                                                (.writeObject before-1)
                                                (.writeObject before-2))))]

          (let [snapshot-objects (pail/with-snapshot pail [snapshot (.snapshot pail snap-path)]
                                   (with-open [writer (.openWrite pail)]
                                     (doto writer
                                       (.writeObject during-1)
                                       (.writeObject during-2)))
                                   (pail/object-seq snapshot))
                pail-objects (pail/object-seq pail)]

            (fact "snapshot sees data written before snapshot"
              snapshot-objects => (contains (array-eq before-1))
              snapshot-objects => (contains (array-eq before-2)))

            (fact "snapshot does not see data written during snapshot"
              snapshot-objects =not=> (contains (array-eq during-1))
              snapshot-objects =not=> (contains (array-eq during-2)))

            (fact "original pail does not contain data written before snapshot"
              pail-objects =not=> (contains (array-eq before-1))
              pail-objects =not=> (contains (array-eq before-2)))

            (fact "original pail contains data written during snapshot"
              pail-objects => (contains (array-eq during-1))
              pail-objects => (contains (array-eq during-2)))))))))
