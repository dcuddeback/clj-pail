(ns clj-pail.serializer)

(defprotocol Serializer
  "A protocol for serializing objects."

  (serialize [this object]
    "Serializes an object to a byte array.")

  (deserialize [this buffer]
    "Deserializes a byte array into an object."))
