package jsontree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsonArray extends IJsonArray {

  private List<JsonNode> elements;

  public JsonArray() {
    elements = new ArrayList<>();
  }

  @Override
  public void add(JsonNode value) {
    elements.add(value);
  }

  @Override
  public String prettyPrint() {
    StringBuilder sb = new StringBuilder("\n[");
    Iterator<JsonNode> iterator = elements.iterator();
    while (iterator.hasNext()) {
      JsonNode node = iterator.next();
      String formattedValue = (node instanceof JsonString) ? node.prettyPrint() : node.prettyPrint().replace("\n", "\n  ");
      sb.append("\n  ").append(formattedValue);
      if (iterator.hasNext()) {
        sb.append(",");
      }
    }
    sb.append("\n]");
    return sb.toString();
  }

  @Override
  protected List<JsonNode> getStoredElements() {
    return elements;
  }

  private boolean twoArrayListsEqual(List<JsonNode> array1, List<JsonNode> array2) {
    if (!(array1 instanceof ArrayList) && !(array2 instanceof ArrayList)) {
      return false;
    }

    if (array1.size() != array2.size()) {
      return false;
    }

    for (int i = 0; i < array1.size(); i++) {
      if (!array1.get(i).equals(array2.get(i))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof JsonArray)) {
      return false;
    }

    JsonArray jsonArray = (JsonArray) o;
    List<JsonNode> receivedElements = jsonArray.getStoredElements();
    return twoArrayListsEqual(elements, receivedElements);
  }

  @Override
  public int hashCode() {
    StringBuilder sb = new StringBuilder("");
    for (int i = 0; i < elements.size(); i++) {
      JsonNode element = elements.get(i);
      int elementHashCode = element.hashCode();
      sb.append(elementHashCode);
    }
    return sb.toString().hashCode();
  }


}
