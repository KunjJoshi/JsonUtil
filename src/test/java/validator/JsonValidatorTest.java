package validator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import parser.InvalidJsonException;
import parser.JsonParser;

/**
 * A Testing Class to test the JSON Validator Class.
 */
public class JsonValidatorTest {

  JsonParser parser;


  @Test
  public void testConstructor() {
    parser = new JsonValidator();
    assertEquals("Status:Empty", parser.output());
  }

  @Test
  public void testEmptyString() {
    String json = "";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("", status);
    assertEquals("Status:Empty", parser.output());
  }

  @Test
  public void testEmptyObject() {
    String json = "{}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("", status);
    assertEquals("Status:Valid", parser.output());
  }

  @Test
  public void testSimpleObject() {
    String json = "{\"name\":\"Kunj\",\"age\":\"23\"}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("", status);
    assertEquals("Status:Valid", parser.output());
  }

  @Test
  public void objectAsValue() {
    String json = "{\"grades\":{\"CS5080\":\"A+\",\"CY5010\":\"A\"}}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("", status);
    assertEquals("Status:Valid", parser.output());
  }

  @Test
  public void arrayAsValue() {
    String json = "{\"students\":[\"Kunj\",\"Viraj\",\"Sans\"],\"professor\":\"Amit Sesh\"}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("", status);
    assertEquals("Status:Valid", parser.output());
  }

  @Test
  public void arrayNestedInObject() {
    String json = "{\"visited\":{\"countries\":[\"India\",\"USA\"]"
        + ",\"states\":[\"Mass\",\"Penn\",\"Cali\"],\"by\":\"Kunj\"}}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("", status);
    assertEquals("Status:Valid", parser.output());
  }

  @Test
  public void objectNestedInObject() {
    String json = "{\"address\":{\"kunj\":{\"street\":\"9 Chilcott Place\",\"city\""
        + ":\"Boston\",\"state\":\"MA\",\"zip\":\"02130\"}}}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("", status);
    assertEquals("Status:Valid", parser.output());
  }

  @Test
  public void arrayNestedInArray() {
    String json = "{\"key1\":[\"val1\",[\"val2\",\"val3\"],\"val4\"]}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("", status);
    assertEquals("Status:Valid", parser.output());
  }

  @Test
  public void objectNestedInArray() {
    String json = "{\"key1\":[\"val1\",{\"subkey1\":\"subval1\"},\"val2\"]}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("", status);
    assertEquals("Status:Valid", parser.output());
  }

  @Test
  public void spaceInKey() {
    String json = "{\"occupation of student\":\"Research Assistant\"}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        System.out.println(e.getMessage());
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void numberStartingKey() {
    String json = "{\"internships\":{\"1StopAI\":\"Data Science Internship\"}}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void ignorableWhitespace() {
    String json = "{\"name\" :    \"Kunj Joshi\"  , \"age\":\"23\"}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("", status);
    assertEquals("Status:Valid", parser.output());
  }

  @Test
  public void incompleteKeyString() {
    String json = "{\"name :\"Kunj Joshi\"}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void incompleteObject() {
    String json = "{\"grades\":{\"CS5080\":\"A+\" , \"CY5010\":\"A\"}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "";
      }
    }
    assertEquals("", status);
    assertEquals("Status:Incomplete", parser.output());
  }

  @Test
  public void incompleteArray() {
    String json = "{\"students\":[\"Kunj\",\"Viraj\",\"Sans\"}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void extraCurly() {
    String json = "{\"name\":\"Kunj\"}}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void extraSquare() {
    String json = "{\"students\":[\"Kunj\",\"Viraj\",\"Sans\"]]}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void misplacedColon() {
    String json = "{\"k1\"::\"v1\"}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void misplacedColonInArray() {
    String json = "{\"students\":[\"Kunj\",\"Viraj\":\"Sans\"]}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void misplacedComma() {
    String json = "{\"k1\":\"v1\" ,, \"k2\":\"v2\"}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void misplacedColonInObject() {
    String json = "{\"k1\":\"v1\":\"k2\":\"v2\"}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void misplacedCommaInObject() {
    String json = "{\"k1\",\"v1\",\"k2\":\"v2\"}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void unenclosedValues() {
    String json = "{name:\"Kunj\"}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void emptyKey() {
    String json = "{\"\":\"randomValue\"}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void invalidBeginning() {
    String json = "[\"k1\":\"v1\",\"k2\":\"v2\"}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void invalidArray() {
    String json = "{\"students\":\"Kunj\",\"Viraj\",\"Sans\"]}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void testMissingSeparator() {
    String json = "{\"name\"\"}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void testNoValues() {
    String json = "{\"key\":}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void doubleColons() {
    String json = "{\"name\":\"CS5010\",\"semester\"::}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void jsonCharsInValues() {
    String json = "{\"value\":\"{}[]:',.?;`~\"}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("", status);
    assertEquals("Status:Valid", parser.output());
  }

  @Test
  public void misplacedOpenObject() {
    String json = "{\"name\"{:\"Kunj\"}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void objectArray() {
    String json = "{\"names\":[{\"Kunj\":\"Boston\"}, {\"Dwireph\":\"NYC\"}]}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("", status);
    assertEquals("Status:Valid", parser.output());
  }

  @Test
  public void trailingWhiteSpace() {
    String json = "{\"name\":\"Kunj\"}       ";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("", status);
    assertEquals("Status:Valid", parser.output());
  }

  @Test
  public void beginningWhiteSpace() {
    String json = "    {\"name\":\"Kunj\"}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("", status);
    assertEquals("Status:Valid", parser.output());
  }

  @Test
  public void invalidJSONInput() {
    String json = "{{";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void testEmptyKeyCharByChar() {
    String json = "{\"\"";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void invalidBeginningWithDoubleQoutes() {
    String json = "\"name\"";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void noKeyCharByChar() {
    String json = "{:";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void noStartingArrayValueCharByChar() {
    String json = "{,";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void coveredKeyCharByChar() {
    String json = "{[";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void differentBrackets() {
    String json = "{]";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void coveredKey() {
    String json = "{[";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void firstSubarrayValueCharByChar() {
    String json = "{\"name\":[[";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("", status);
    assertEquals("Status:Incomplete", parser.output());
  }

  @Test
  public void subarrayEndingArray() {
    String json = "{\"name\":[\"Kunj\",[\"Viraj\",\"Sans\"]]";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("", status);
    assertEquals("Status:Incomplete", parser.output());
  }

  @Test
  public void spaceInKeyCharByChar() {
    String json = "{\"name ";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void numericStartingKeyCharByChar() {
    String json = "{\"1";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void unenclosedAlphabet() {
    String json = "{n";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void questionMarkAppearingInJSON() {
    String json = "{?";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("invalidated", status);
    assertEquals("Status:Invalid", parser.output());
  }

  @Test
  public void IncompleteObject() {
    String json = "{\"object\":{\"k1\":";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("", status);
    assertEquals("Status:Incomplete", parser.output());
  }

  @Test
  public void emptyValue() {
    String json = "{\"object\":\"\"}";
    String status = "";
    parser = new JsonValidator();
    for (char c : json.toCharArray()) {
      try {
        parser = parser.input(c);
      } catch (InvalidJsonException e) {
        status = "invalidated";
      }
    }
    assertEquals("", status);
    assertEquals("Status:Valid", parser.output());
  }
}