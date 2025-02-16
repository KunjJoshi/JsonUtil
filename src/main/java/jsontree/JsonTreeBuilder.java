package jsontree;

import java.util.ArrayList;
import java.util.List;
import parser.InvalidJsonException;
import parser.JsonParser;
import parser.STATUS;
import validator.JsonValidator;

public class JsonTreeBuilder implements JsonParser<JsonNode> {

  List<JsonNode> objectStack;
  List<String> keyStack;
  List<String> separatorStack;
  STATUS currForm;
  STATUS nextForm;
  STATUS prevForm;
  String currentStatus;
  String currentString;
  boolean error;
  JsonNode root;
  JsonParser<String> validator;

  public JsonTreeBuilder() {
    objectStack = new ArrayList<>();
    keyStack = new ArrayList<>();
    separatorStack = new ArrayList<>();
    currentStatus = "Status:Empty";
    currentString = "";
    error = false;
    currForm = STATUS.ITER;
    validator = new JsonValidator();
  }



  private void alphabetEncountered(char c) {
    try {
      validator = validator.input(c);
    } catch (InvalidJsonException e) {
      this.currentStatus = "Status:Invalid";
      error = true;
    }

    currentStatus = validator.output();
    if (!error) {
      currentString = currentString + c;
    }

  }


  private void numericCharacterEncountered(char c) {
    try{
      validator = validator.input(c);
    } catch (InvalidJsonException e) {
      this.currentStatus = "Status:Invalid";
      error = true;
    }

    currentStatus = validator.output();

    if (!error) {
      currentString = currentString + c;
    }
  }


  //Wrapper function for empty spaces
  private void whiteSpaceActions() {
    switch (currForm) {
      case KEY:
        break;

      case VALUE:
        currentString = currentString + " ";
        break;

      case ITER:
        break;

      default:
        //No Possible STATUS
        break;
    }
  }

  private void spaceEncountered() {
    try {
      validator = validator.input(' ');
    } catch (InvalidJsonException e) {
      error = true;
      currentStatus = "Status:Invalid";
    }

    currentStatus = validator.output();
    if (!error) {
      whiteSpaceActions();
    }
  }

  private void objectAsAValue() {
    JsonNode newObj = new JsonObject();
    objectStack.add(newObj);
    nextForm = STATUS.KEY;
    currForm = STATUS.ITER;
  }

  private void objectAsFirstElementInArray(JsonNode jsonArr) {
    List<JsonNode> array = (List<JsonNode>) jsonArr.getStoredElements();
    if (array.isEmpty()) {
      JsonNode newObj = new JsonObject();
      objectStack.add(newObj);
      nextForm = STATUS.KEY;
      currForm = STATUS.ITER;
    } else {
      error = true;
      currentStatus = "Status:Invalid";
    }
  }

  private void subObjectInArray() {
    JsonNode newObj = new JsonObject();
    objectStack.add(newObj);
    nextForm = STATUS.KEY;
    currForm = STATUS.ITER;
  }

  private void objectStartActions() {

    if (objectStack.isEmpty() && separatorStack.isEmpty()) {
      JsonNode newObj = new JsonObject();
      objectStack.add(newObj);
      nextForm = STATUS.KEY;
      currForm = STATUS.ITER;
    } else {
      JsonNode object = objectStack.get(objectStack.size() - 1);
      String sep = separatorStack.get(separatorStack.size() - 1);

      String caseKey = object.getClass().getSimpleName() + ":" + sep;

      switch (caseKey) {
        case "JsonObject::":
          objectAsAValue();
          break;

        case "JsonArray::":
          objectAsFirstElementInArray(object);
          break;

        case "JsonArray:,":
          subObjectInArray();
          break;

        default:
          error = true;
          currentStatus = "Status:Invalid";
      }
    }
  }

  private void openingCurlyEncountered() {
    try {
      validator = validator.input('{');
    } catch (InvalidJsonException e) {
      error = true;
      currentStatus = "Status:Invalid";
    }

    currentStatus = validator.output();
    if (!error) {
      objectStartActions();
    }
  }

  private void anyKeyinObjectButFirst() {
    separatorStack.remove(separatorStack.size() - 1);
    keyStack.add(currentString);
    nextForm = STATUS.VALUE;
    currForm = STATUS.ITER;
    prevForm = STATUS.KEY;
    currentString = "";
  }

  private void firstKeyInASubObject() {
    keyStack.add(currentString);
    nextForm = STATUS.VALUE;
    currForm = STATUS.ITER;
    prevForm = STATUS.KEY;
    currentString = "";
  }

  private void keyActions() {
    if (objectStack.size() > 1) {
      JsonNode topObjParent = objectStack.get(objectStack.size() - 2);
      if (topObjParent instanceof JsonObject) {
        anyKeyinObjectButFirst();
      } else {
        firstKeyInASubObject();
      }
    } else {
      anyKeyinObjectButFirst();
    }
  }

  private void stringValues() {
    if (currForm == STATUS.KEY) {
      keyStack.add(currentString);
      nextForm = STATUS.VALUE;
      prevForm = STATUS.KEY;
      currForm = STATUS.ITER;
      currentString = "";
    } else {
      separatorStack.remove(separatorStack.size() - 1);
      String key = keyStack.remove(keyStack.size() - 1);
      JsonNode object = objectStack.remove(objectStack.size() - 1);
      JsonObject node = null;
      if (object instanceof JsonObject) {
        node = (JsonObject) object;
        try {
          node.add(key, new JsonString(currentString));
        } catch (IllegalArgumentException e) {
          error = true;
          currentStatus = "Status:Invalid";
        }
      }
      objectStack.add(node);
      nextForm = STATUS.KEY;
      currForm = STATUS.ITER;
      prevForm = STATUS.VALUE;
      currentString = "";
    }
  }

  private void firstStringInJsonArray(JsonNode jsonArr) {
    List<JsonNode> array = (List<JsonNode>) jsonArr.getStoredElements();
    if (array.isEmpty()) {
      JsonNode storedArr = objectStack.remove(objectStack.size() -1);
      JsonArray node = null;
      if (storedArr instanceof JsonArray) {
        node = (JsonArray) storedArr;
        node.add(new JsonString(currentString));
      }
      objectStack.add(node);
      nextForm = STATUS.VALUE;
      currForm = STATUS.ITER;
      prevForm = STATUS.VALUE;
      currentString = "";
    }
  }

  private void stringElementInAnArray() {
    separatorStack.remove(separatorStack.size() - 1);
    JsonNode array = objectStack.remove(objectStack.size() - 1);
    JsonArray node = null;
    if (array instanceof JsonArray) {
      node = (JsonArray) array;
      node.add(new JsonString(currentString));
    }
    objectStack.add(node);
    nextForm = STATUS.VALUE;
    currForm = STATUS.ITER;
    prevForm = STATUS.VALUE;
    currentString = "";
  }

  private void firstStringElementInASubArray() {
    JsonNode array = objectStack.remove(objectStack.size() - 1);
    JsonArray node = null;
    if (array instanceof JsonArray) {
      node = (JsonArray) array;
      node.add(new JsonString(currentString));
    }
    objectStack.add(node);
    nextForm = STATUS.VALUE;
    currForm = STATUS.ITER;
    prevForm = STATUS.VALUE;
    currentString = "";
  }

  private void valueActions() {
    JsonNode parentObj = objectStack.get(objectStack.size() - 2);
    if (parentObj instanceof JsonObject) {
      stringElementInAnArray();
    } else {
      JsonNode arrayToAdd = objectStack.get(objectStack.size() - 1);
      JsonArray node = (JsonArray) arrayToAdd;
      List<JsonNode> array = (List<JsonNode>) node.getStoredElements();
      if (array.isEmpty()) {
        firstStringElementInASubArray();
      } else {
        stringElementInAnArray();
      }
    }
  }

  private void processEmptyString() {
    switch (currForm) {
      case ITER:
        currForm = nextForm;
        break;

      case VALUE:
        stringValues();
        break;

      default:
        currentStatus = "Status:Invalid";
        error = true;
        break;
    }
  }

  private void stringActions() {
    if (currentString.isEmpty()) {
      processEmptyString();
    } else {
      JsonNode topObj = objectStack.get(objectStack.size() - 1);

      if (topObj instanceof JsonObject && separatorStack.isEmpty()) {
        keyStack.add(currentString);
        currentString = "";
        currForm = STATUS.ITER;
        nextForm = STATUS.VALUE;
        prevForm = STATUS.KEY;
      } else {
        String sep = separatorStack.get(separatorStack.size() - 1);
        String caseKey = topObj.getClass().getSimpleName() + ":" + sep;
        switch (caseKey) {
          case "JsonObject:,":
            keyActions();
            break;

          case "JsonObject::":
            stringValues();
            break;

          case "JsonArray::":
            firstStringInJsonArray(topObj);
            break;

          case "JsonArray:,":
            valueActions();
            break;

          default:
            error = true;
            currentStatus = "Status:Invalid";
        }
      }
    }
  }

  private void doubleQuotesEncountered() {
    try {
      validator = validator.input('"');
    } catch (InvalidJsonException e) {
      error = true;
      currentStatus = "Status:Invalid";
    }
    currentStatus = validator.output();
    if (!error) {
      stringActions();
    }
  }

  private void sepActions(char c) {
    switch (currForm) {
      case VALUE:
        currentString = currentString + c;
        break;

      case ITER:
        separatorStack.add(String.valueOf(c));
        break;
    }
  }

  private void commaEncountered() {
    try {
      validator = validator.input(',');
    } catch (InvalidJsonException e) {
      error = true;
      currentStatus = "Status:Invalid";
    }

    currentStatus = validator.output();
    if (!error) {
      sepActions(',');
    }
  }


  private void colonEncountered() {
    try {
      validator = validator.input(':');
    } catch (InvalidJsonException e) {
      error = true;
      currentStatus = "Status:Invalid";
    }

    currentStatus = validator.output();
    if (!error) {
      sepActions(':');
    }
  }

  private void closeAnObjectValue(JsonNode objToAdd) {
    separatorStack.remove(separatorStack.size() - 1);
    String key = keyStack.remove(keyStack.size() - 1);
    JsonNode object = objectStack.remove(objectStack.size() - 1);
    JsonObject node = null;
    if (object instanceof JsonObject) {
      node = (JsonObject) object;
      try {
        node.add(key, objToAdd);
      } catch (IllegalArgumentException e) {
        error = true;
        currentStatus = "Status:Invalid";
      }
    }
    objectStack.add(node);
    nextForm = STATUS.KEY;
    currForm = STATUS.ITER;
    prevForm = STATUS.VALUE;
  }

  private void closingObjectInArray(JsonNode jsonArr, JsonNode objToAdd) {
    List<JsonNode> array = (List<JsonNode>) jsonArr.getStoredElements();
    if (array.isEmpty()) {
      JsonNode storedArr = objectStack.remove(objectStack.size() - 1);
      JsonArray node = null;
      if (storedArr instanceof JsonArray) {
        node = (JsonArray) storedArr;
        node.add(objToAdd);
      }
      objectStack.add(node);
      nextForm = STATUS.VALUE;
      currForm = STATUS.ITER;
      prevForm = STATUS.VALUE;
    }
  }

  private void closingSubObjectInArray(JsonNode objToAdd) {
    separatorStack.remove(separatorStack.size() - 1);
    JsonNode storedArr = objectStack.remove(objectStack.size() - 1);
    JsonArray node = null;
    if (storedArr instanceof JsonArray) {
      node = (JsonArray) storedArr;
      node.add(objToAdd);
    }
    objectStack.add(node);
    nextForm = STATUS.VALUE;
    currForm = STATUS.ITER;
    prevForm = STATUS.VALUE;
  }

  private void closingObjectActions() {
    JsonNode object = objectStack.remove(objectStack.size() - 1);
    if (objectStack.isEmpty() && separatorStack.isEmpty() && keyStack.isEmpty()) {
      root = object;
      currentStatus = "Status:Valid";
    } else {
      JsonNode topObj = objectStack.get(objectStack.size() - 1);
      String sep = separatorStack.get(separatorStack.size() - 1);
      String caseKey = topObj.getClass().getSimpleName() + ":" + sep;

      switch (caseKey) {
        case "JsonObject::":
          closeAnObjectValue(object);
          break;

        case "JsonArray::":
          closingObjectInArray(topObj, object);
          break;

        case "JsonArray:,":
          closingSubObjectInArray(object);
          break;

        default:
          error = true;
          currentStatus = "Status:Invalid";
      }
    }
  }

  private void closingCurlyEncountered() {
    try{
      validator = validator.input('}');
    } catch (InvalidJsonException e) {
      error = true;
      currentStatus = "Status:Invalid";
    }

    currentStatus = validator.output();
    if (!error) {
      closingObjectActions();
    }
  }

  private void newArrayValue() {
    JsonNode newArr = new JsonArray();
    objectStack.add(newArr);
    nextForm = STATUS.VALUE;
    currForm = STATUS.ITER;
    prevForm = STATUS.VALUE;
  }

  private void subArrayFirst(JsonNode topArr) {
    List<JsonNode> array = (List<JsonNode>) topArr.getStoredElements();
    if (array.isEmpty()) {
      JsonNode newArr = new JsonArray();
      objectStack.add(newArr);
      nextForm = STATUS.VALUE;
      currForm = STATUS.ITER;
      prevForm = STATUS.VALUE;
    } else {
      currentStatus = "Status:Invalid";
      error = true;
    }
  }

  private void subArray() {
    JsonNode newArr = new JsonArray();
    objectStack.add(newArr);
    nextForm = STATUS.VALUE;
    currForm = STATUS.ITER;
    prevForm = STATUS.VALUE;
  }

  private void newArrayActions() {
    String sep = separatorStack.get(separatorStack.size() - 1);
    JsonNode topObj = objectStack.get(objectStack.size() - 1);
    String caseKey = topObj.getClass().getSimpleName() + ":" + sep;

    switch (caseKey) {
      case "JsonObject::":
        newArrayValue();
        break;

      case "JsonArray::":
        subArrayFirst(topObj);
        break;

      case "JsonArray:,":
        subArray();
        break;

      default:
        error = true;
        currentStatus = "Status:Invalid";
        break;
    }
  }

  private void openingSquareEncountered() {
    try {
      validator = validator.input('[');
    } catch (InvalidJsonException e) {
      error = true;
      currentStatus = "Status:Invalid";
    }

    currentStatus = validator.output();
    if (!error) {
      newArrayActions();
    }
  }

  private void directValueArrayClosed(JsonNode objToAdd){
    separatorStack.remove(separatorStack.size() - 1);
    JsonNode storedObj = objectStack.remove(objectStack.size() - 1);
    String key = keyStack.remove(keyStack.size() - 1);
    JsonObject node = null;
    if (storedObj instanceof JsonObject) {
      node = (JsonObject) storedObj;
      try {
        node.add(key, objToAdd);
      } catch (IllegalArgumentException e) {
        error = true;
        currentStatus = "Status:Invalid";
      }
    }
    objectStack.add(node);
    nextForm = STATUS.KEY;
    currForm = STATUS.ITER;
    prevForm = STATUS.VALUE;
  }

  private void closingFirstSubArray(JsonNode jsonArr, JsonNode objToAdd) {
    List<JsonNode> array = (List<JsonNode>) jsonArr.getStoredElements();
    if (array.isEmpty()) {
      JsonNode storedArr = objectStack.remove(objectStack.size() - 1);
      JsonArray node = null;
      if (storedArr instanceof JsonArray) {
        node = (JsonArray) storedArr;
        node.add(objToAdd);
      }
      objectStack.add(node);
      nextForm = STATUS.VALUE;
      currForm = STATUS.ITER;
      prevForm = STATUS.VALUE;
    } else {
      currentStatus = "Status:Invalid";
      error = true;
    }
  }

  private void closingSubArray(JsonNode objToAdd) {
    separatorStack.remove(separatorStack.size() - 1);
    JsonNode storedObj = objectStack.remove(objectStack.size() - 1);
    JsonArray node = null;
    if (storedObj instanceof JsonArray) {
      node = (JsonArray) storedObj;
      node.add(objToAdd);
    }
    objectStack.add(node);
    nextForm = STATUS.VALUE;
    currForm = STATUS.ITER;
    prevForm = STATUS.VALUE;
  }

  private void closingArrayActions() {
    JsonNode object= objectStack.remove(objectStack.size() - 1);
    JsonNode topObj = objectStack.get(objectStack.size() - 1);
    String sep = separatorStack.get(separatorStack.size() - 1);
    String caseKey = topObj.getClass().getSimpleName() + ":" + sep;

    switch (caseKey) {
      case "JsonObject::":
        directValueArrayClosed(object);
        break;

      case "JsonArray::":
        closingFirstSubArray(topObj, object);
        break;

      case "JsonArray:,":
        closingSubArray(object);
        break;

      default:
        error = true;
        currentStatus = "Status:Invalid";
    }
  }

  private void closingSquareEncountered() {
    try {
      validator = validator.input(']');
    } catch (InvalidJsonException e) {
      error = true;
      currentStatus = "Status:Invalid";
    }

    currentStatus = validator.output();
    if (!error) {
      closingArrayActions();
    }
  }

  private void allOtherChars(char c) {
    try {
      validator = validator.input(c);
    } catch (InvalidJsonException e) {
      error = true;
      currentStatus = "Status:Invalid";
    }

    currentStatus = validator.output();
    if (!error) {
      currentString = currentString + c;
    }
  }

  @Override
  public JsonParser<JsonNode> input(char c) throws InvalidJsonException {
    if (currentStatus.equals("Status:Invalid")) {
      currentStatus = "Status:Invalid";
      throw new InvalidJsonException("Invalid Status");
    }

    if (currentStatus.equals("Status:Valid")) {
      if (c == ' ') {
        currentStatus = "Status:Valid";
      } else {
        currentStatus = "Status:Invalid";
        throw new InvalidJsonException("Invalid Status");
      }
    }

    if (objectStack.isEmpty() && (c != ' ' && c != '{')) {
      currentStatus = "Status:Invalid";
      throw new InvalidJsonException("Invalid JSON Start");
    }

    if (Character.isLetter(c)) {
      alphabetEncountered(c);
      if (this.error) {
        throw new InvalidJsonException("Invalid Status");
      } else {
        return this;
      }
    }

    if (Character.isDigit(c)) {
      numericCharacterEncountered(c);
      if (this.error) {
        throw new InvalidJsonException("Invalid Status");
      } else {
        return this;
      }
    }

    if (Character.isWhitespace(c)) {
      spaceEncountered();
      if (this.error) {
        throw new InvalidJsonException("Invalid Status");
      } else {
        return this;
      }
    }

    switch (c) {
      case '{':
        openingCurlyEncountered();
        if (this.error) {
          throw new InvalidJsonException("Invalid Status");
        }
        break;

      case '"' :
        doubleQuotesEncountered();
        if (this.error) {
          throw new InvalidJsonException("Invalid Status");
        }
        break;

      case ',' :
        commaEncountered();
        if (this.error) {
          throw new InvalidJsonException("Invalid Status");
        }
        break;

      case ':':
        colonEncountered();
        if (this.error) {
          throw new InvalidJsonException("Invalid Status");
        }
        break;

      case '}' :
        closingCurlyEncountered();
        if (this.error) {
          throw new InvalidJsonException("Invalid Status");
        }
        break;

      case '[' :
        openingSquareEncountered();
        if (this.error) {
          throw new InvalidJsonException("Invalid Status");
        }
        break;

      case ']':
        closingSquareEncountered();
        if (this.error) {
          throw new InvalidJsonException("Invalid Status");
        }
        break;

      default:
        allOtherChars(c);
        if (this.error) {
          throw new InvalidJsonException("Invalid Status");
        }
        break;
    }

    return this;
  }

  @Override
  public JsonNode output() {
    if (currentStatus.equals("Status:Valid")) {
      return root;
    } else {
      return null;
    }
  }
}
