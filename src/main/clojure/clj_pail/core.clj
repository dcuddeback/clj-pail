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


(defn object-seq
  "Returns a sequence of objects read from the Pail."
  [^Pail pail]
  (iterator-seq (.iterator pail)))


(defmacro with-snapshot
  "Automatically deletes Pail snapshots after successfuly executing a block of code. The snapshots
  should be created with `Pail.snapshot()` and will only be deleted if the body finishes
  successfully. If the body throws an exception, the snapshot will not be deleted.

  This is intended to be used to safely delete data that was successfully processed while not
  deleting data that fails to process.

  The first argument should be the original Pail that the snapshots are derived from. If the body
  finishes correctly, this is the Pail from which the data will be deleted.

  The second argument is a vector of bindings. The bindings should specify snapshots that can be
  deleted from the original Pail with `Pail.deleteSnapshot()`.

  Example:

    (with-snapshot original-pail [snapshot-pail (.snapshot origin-pail \"/path/to/snapshot\")]
      ...)"
  [pail bindings & body]
  (cond
    (empty? bindings)
    `(do ~@body)

    (symbol? (first bindings))
    (let [[this-binding rest-bindings] (split-at 2 bindings)
          snapshot (first this-binding)]
      `(let ~(vec this-binding)
         (let [result# (with-snapshot ~pail ~rest-bindings ~@body)]
           (.deleteSnapshot ~pail ~snapshot)
           result#)))

    :else
    (throw (IllegalArgumentException. "with-snapshot only allows symbols in bindings"))))
