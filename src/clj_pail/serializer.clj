(ns clj-pail.serializer)

(defprotocol Serializer
  "A protocol for serializing objects."

  (serialize [this object]
    "Serializes an object to a byte array.")

  (deserialize [this buffer]
    "Deserializes a byte array into an object."))


(defrecord ^{:doc "A serializer that does no serialization. It expects byte arrays and returns them
                  unmanipulated."}
  NullSerializer []
  Serializer
  (serialize [this buffer] buffer)
  (deserialize [this buffer] buffer))

(defn null-serializer
  "Returns a NullSerializer."
  []
  (NullSerializer.))
