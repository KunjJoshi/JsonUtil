package jsontree;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import parser.InvalidJsonException;

public class JsonObject extends IJsonObject {

  private Map<String, ArrayList<JsonNode>> objectMap;
  private List<Map.Entry<String, JsonNode>> storedElements;

  public JsonObject() {
    objectMap = new LinkedHashMap<>();
    storedElements = new ArrayList<>();
  }

  @Override
  public void add(String key, JsonNode value) throws IllegalArgumentException{
    String regex = "^[A-Za-z][A-Za-z0-9]*$";
    if (!(Pattern.matches(regex, key))) {
      throw new IllegalArgumentException("Invalid Key: " + key);
    }
    objectMap.putIfAbsent(key, new ArrayList<>());
    objectMap.get(key).add(value);
    AbstractMap.SimpleEntry<String, JsonNode> entry = new AbstractMap.SimpleEntry<>(key, value);
    storedElements.add(entry);
  }


  @Override
  public String prettyPrint() {
    StringBuilder sb = new StringBuilder("{\n");
    Iterator<Map.Entry<String, JsonNode>> iterator = storedElements.iterator();
    while (iterator.hasNext()) {
      Map.Entry<String, JsonNode> entry = iterator.next();
      String key = entry.getKey();
      JsonNode node = entry.getValue();
      String formattedValue = (node instanceof JsonString)
          ? node.prettyPrint() : node.prettyPrint().replace("\n","\n  ");
      if (node instanceof JsonObject) {
        sb.append("  \"").append(key).append("\":").append("\n  ").append(formattedValue);
      } else {
        sb.append("  \"").append(key).append("\":").append(formattedValue);
      }
      if (iterator.hasNext()) {
        sb.append(",");
      }
      sb.append("\n");
    }
    sb.append("}");
    return sb.toString();
  }

  @Override
  protected Map<String, ArrayList<JsonNode>> getStoredElements() {
    return objectMap;
  }

  private int anyValueEqualToMyCurrentValue(JsonNode node, ArrayList<JsonNode> receivedValue) {
    for (int i = 0; i < receivedValue.size(); i++) {
      if (receivedValue.get(i).equals(node)) {
        return 1;
      }
    }
    return -1;
  }
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof JsonObject)) {
      return false;
    }

    JsonObject jsonObject = (JsonObject) o;
    Map<String, ArrayList<JsonNode>> receivedObject = jsonObject.getStoredElements();
    Set<String> receivedSet = receivedObject.keySet();

    for (Map.Entry<String, ArrayList<JsonNode>> entry : objectMap.entrySet()) {
      String key = entry.getKey();
      if (!receivedSet.contains(key)) {
        return false;
      }
      ArrayList<JsonNode> keyValue = entry.getValue();
      ArrayList<JsonNode> receivedValue = receivedObject.get(key);

      for (JsonNode node : keyValue) {
        int equalFlag = anyValueEqualToMyCurrentValue(node, receivedValue);
        if (equalFlag == -1) {
          return false;
        }
      }

    }
    return true;
  }

  private int cumulativeHashCode(ArrayList<JsonNode> keyValues) {
    int hashCode = 0;
    for (JsonNode node : keyValues) {
      hashCode += node.hashCode();
    }
    return hashCode;
  }
  @Override
  public int hashCode() {
    Map<Integer, Integer> hashMap = new HashMap<>();
    Set<String> keySet = objectMap.keySet();
    for (String key : keySet) {
      ArrayList<JsonNode> keyValue = objectMap.get(key);
      int cumulativeHashCode = cumulativeHashCode(keyValue);
      hashMap.put(key.hashCode(), cumulativeHashCode);
    }
    return hashMap.hashCode();
  }
}