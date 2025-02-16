package jsontree;

import static org.junit.Assert.assertEquals;

import jsontree.IJsonArray;
import jsontree.IJsonObject;
import jsontree.JsonArray;
import jsontree.JsonNode;
import jsontree.JsonObject;
import jsontree.JsonString;
import org.junit.Test;
import parser.InvalidJsonException;

public class JsonObjectTest {

  IJsonObject root;

  @Test
  public void test() {
    root = new JsonObject();
    IJsonObject root2;
    try {
      root.add("name", new JsonString("cs5010"));
      IJsonArray array = new JsonArray();
      array.add(new JsonString("cs5010"));
      array.add(new JsonString("cy5010"));
      root.add("array", array);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(e);
    }
    System.out.println(root.prettyPrint());
  }
}