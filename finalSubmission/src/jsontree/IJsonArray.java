package jsontree;

/**
 * An Abstract Class that determines kinds of arrays that can exist in Json Objects.
 * An array can only be values. The add method allows to add a type of Json Node
 * to the current implementation of array.
 */

public abstract class IJsonArray extends JsonNode {

  /**
   * The add method is aimed at allowing to add a JsonNode object to the
   * current implementation of array. The JsonNode could be anything: could
   * be a string, an object or an array itself
   * @param value JsonNode Object, Array or String.
   */
  public void add(JsonNode value) {}
}
