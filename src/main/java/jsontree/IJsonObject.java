package jsontree;

import parser.InvalidJsonException;

/**
 * The abstract class IJsonObject is aimed at representing all kinds of valid objects
 * available in a JSON. The object is represented as {"key":"value"} in JSON. The main JSON
 * itself is an object. Its add method allows us to add a key value pair to current implementation
 * of Object.
 */
public abstract class IJsonObject extends JsonNode{

  /**
   * The method add() shall allow us to add a Key Value pair to current implementation of JSON.
   * This implementation should be created to handle key-conflicts.
   * The value can be any JsonNode implementation i.e. a String, an Array or an Object.
   * But the key shall always be a string.
   * @param key Always a String, can have different rules over it.
   * @param value Any JsonNode implementation i.e. JsonString, IJsonArray or IJsonObject
   */
  public void add (String key, JsonNode value) throws IllegalArgumentException {}
}
