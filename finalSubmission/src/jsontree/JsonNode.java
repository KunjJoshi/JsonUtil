package jsontree;

/**
 * A node in tree hierarchy is represented by this class.
 * All kinds of nodes i.e. JsonString, JsonArray or JsonObject are of type JsonNode.
 */
public abstract class JsonNode {
  public abstract String prettyPrint();
  protected abstract Object getStoredElements();
}


