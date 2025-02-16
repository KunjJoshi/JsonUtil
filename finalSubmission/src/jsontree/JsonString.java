package jsontree;

public class JsonString extends JsonNode {
  private String value;

  public JsonString(String value) {
    this.value = value;
  }

  @Override
  public String prettyPrint() {
    return "\"" + value + "\"";
  }

  @Override
  protected String getStoredElements() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof JsonString)) {
      return false;
    }

    JsonString receivedObj = (JsonString) o;
    String objValue = receivedObj.getStoredElements();
    return this.value.equals(objValue);
  }

  @Override
  public int hashCode() {
    return this.value.hashCode();
  }
}
