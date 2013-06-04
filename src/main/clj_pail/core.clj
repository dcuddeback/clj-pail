(ns clj-pail.core
  (:import (com.backtype.hadoop.pail PailSpec PailStructure)))


(defn ^PailSpec spec
  "Builds a PailSpec from a PailStructure."
  [^PailStructure structure]
  (PailSpec. structure))
