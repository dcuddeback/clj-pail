(ns clj-pail.core
  (:import (com.backtype.hadoop.pail Pail PailSpec PailStructure)))


(defn ^PailSpec spec
  "Builds a PailSpec from a PailStructure."
  [^PailStructure structure]
  (PailSpec. structure))


(defn ^Pail pail
  "Opens an existing Pail."
  ([path]
   (Pail. path))
  ([fs path]
   (Pail. fs path)))


(defn ^Pail create
  "Creates a Pail from a PailSpec at `path`."
  [^PailSpec spec path & {:keys [filesystem fail-on-exists]
                          :or {fail-on-exists true}}]
  (if filesystem
    (Pail/create filesystem path spec fail-on-exists)
    (Pail/create path spec fail-on-exists)))
