(ns clj-pail.partitioner)

(defprotocol VerticalPartitioner
  "A protocol for vertically partitioning a PailStructure. Partitioners can be composed to build
  complex vertical partitioning schemes out of individual partitioners."

  (make-partition [this object]
    "Determines the vertical partition for an object. `make-partition` should return a list of objects
    specifying the partition. It is not `make-partition`'s responsibility to convert the objects to
    strings. Their `toString` method will be called to form a valid vertical partition.")

  (validate [this dirs]
    "Validates whether the directories form a valid vertical partition. `validate` should consume as
    many items from `dirs` as it needs in order to validate its partitioning scheme. It should
    return a boolean indicating whether its partition is valid and the rest of `dirs` that were not
    used for validation. The remaining `dirs` can be checked by a higher-level partitioner that will
    know what partitioning scheme comes next.

    For example, to validate that a partition is one of \"foo\" or \"bar\", a partitioner would
    check the first item in `dirs` and return the tail:

        (validate [this dirs]
          [(#{\"foo\" \"bar\"} (first dirs))  ; validate first directory is \"foo\" or \"bar\"
           (rest dirs)])                      ; return remaining directories"))


(defn vertical-partition
  "Returns the vertical partitions for an object as a list of strings."
  [partitioner object]
  (->> (make-partition partitioner object)
    (filter identity)
    (map str)))

(defn valid-partition?
  "Validates whether the directories specify a valid vertical partition."
  [partitioner dirs]
  ; It would be nice to check some properties of `dirs`, but Pail sometimes appends extra diretories
  ; to the partitions, which means that `dirs` won't necessarily be empty.
  (first (validate partitioner dirs)))


(defrecord ^{:doc "A vertical partitioner that places all objects in the root directory. In other
                  words, it does not vertically partition the data."}
  NullPartitioner []
  VerticalPartitioner
  (make-partition [_ _] [])
  (validate [_ dirs] [true dirs]))

(defn null-partitioner
  "Returns a NullPartitioner."
  []
  (NullPartitioner.))