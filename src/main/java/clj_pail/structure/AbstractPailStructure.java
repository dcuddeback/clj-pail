package clj_pail.structure;

import com.backtype.hadoop.pail.PailStructure;
import java.util.List;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.ClassNotFoundException;

/**
 * A PailStructure that can be serialized despite the serializability of its state.
 * AbstractPailStructure is the base class for all pail structures defined with the `gen-structure`
 * macro. The `gen-structure` macro creates pail structures from a Serializer and a
 * VerticalPartitioner. However, if either the serializer or the vertical partitioner can not be
 * serialized with Java serializations, then the entire PailStructure will fail to serialize. To
 * avoid the issue, the serializer and vertical partitioner can be treated as transient properties,
 * which can be reconstructed after deserialization.
 */
public abstract class AbstractPailStructure<T> implements PailStructure<T> {
  private transient Object serializer;
  private transient Object partitioner;

  /**
   * Creates an AbstractPailStructure with a Serializer and a VerticalPartitioner.
   */
  public AbstractPailStructure() {
    this.serializer = createSerializer();
    this.partitioner = createPartitioner();
  }

  /**
   * Returns the structure's Serializer.
   */
  public Object getSerializer() {
    return this.serializer;
  }

  /**
   * Returns the structure's VerticalPartitioner.
   */
  public Object getPartitioner() {
    return this.partitioner;
  }

  /**
   * Creates a Serializer.
   */
  protected abstract Object createSerializer();

  /**
   * Creates a VerticalPartitioner.
   */
  protected abstract Object createPartitioner();

  /**
   * Reads the object from a serialized stream. The serializer and vertical partitioner will be
   * restored after deserialization.
   */
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    this.serializer = createSerializer();
    this.partitioner = createPartitioner();
  }
}
