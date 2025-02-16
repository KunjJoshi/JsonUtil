package validator;

import java.util.ArrayList;
import java.util.List;
import parser.AbstractParser;
import parser.InvalidJsonException;
import parser.JsonParser;
import parser.STATUS;

/**
 * The JSON Validator Class Validates JSON String ensuring that, the given string is in correct
 * order, character by character. It uses a Stack based implementation. Here it has different set of
 * instructions for every character, that it encounters. It also maintains State of the string, i.e.
 * it maintains whether the next character shall go in a Key String, Value String or forms JSON. It
 * also implements its own helper functions, which uses its private variables for its functions.
 */

public class JsonValidator extends AbstractParser {

  List<String> stack;
  int top;
  STATUS currForm;
  STATUS nextForm;
  STATUS prevForm;
  String currentStatus;
  String currentString;
  boolean error;
  Helper helper;

  /**
   * The Constructor for JSON Validator which initializes the variables for JSON Parsing. It has a
   * Stack variable, to maintain input key-value pairs, as well as objects and arrays. A
   * currentStatus variable setting it to Empty. It updates after every character. A currentString
   * that maintains characters in a String format. A currentForm that maintains Status of next
   * Character input The top variable that will maintain stack.
   */
  public JsonValidator() {
    stack = new ArrayList<>();
    currentStatus = "Status:Empty";
    currentString = "";
    currForm = STATUS.ITER;
    top = 0;
    error = false;
    helper = new Helper();
  }


  @Override
  public JsonParser<String> input(char c) throws InvalidJsonException {
    if (currentStatus.equals("Status:Invalid")) {
      currentStatus = "Status:Invalid";
      throw new InvalidJsonException("Invalid Json Format");
    }

    if (currentStatus.equals("Status:Valid")) {
      if (c == ' ') {
        currentStatus = "Status:Valid";
      } else {
        currentStatus = "Status:Invalid";
        throw new InvalidJsonException("Unused Character After a Valid JSON");
      }
    }

    if (stack.isEmpty() && (c != ' ' && c != '{')) {
      currentStatus = "Status:Invalid";
      throw new InvalidJsonException("Invalid JSON Start");
    }

    if (Character.isLetter(c)) {
      helper.alphabetEncountered(c, this);
      if (this.error) {
        throw new InvalidJsonException("Invalid JSON Format in Processing a alphabet character");
      } else {
        return this;
      }
    }

    if (Character.isDigit(c)) {
      helper.numericCharacterEncountered(c, this);
      if (this.error) {
        throw new InvalidJsonException("Invalid JSON Format in Processing a numeric character");
      } else {
        return this;
      }
    }

    if (Character.isWhitespace(c)) {
      helper.spaceEncountered(this);
      if (this.error) {
        throw new InvalidJsonException("Invalid JSON Format in Processing a space character");
      } else {
        return this;
      }
    }

    switch (c) {
      case '{':
        helper.openingCurlyEncountered(this);
        if (this.error) {
          throw new InvalidJsonException("Invalid JSON Format in Processing {");
        }
        break;

      case '"':
        helper.doubleQuotesEncountered(this);
        if (this.error) {
          throw new InvalidJsonException("Invalid JSON Format in Processing \"");
        }
        break;

      case ':':
        helper.colonEncountered(this);
        if (this.error) {
          throw new InvalidJsonException("Invalid JSON Format in Processing :");
        }
        break;

      case ',':
        helper.commaEncountered(this);
        if (this.error) {
          throw new InvalidJsonException("Invalid JSON Format in Processing ,");
        }
        break;

      case '[':
        helper.openSquareEncountered(this);
        if (this.error) {
          throw new InvalidJsonException("Invalid JSON Format in Processing [");
        }
        break;

      case ']':
        helper.closingSquareEncountered(this);
        if (this.error) {
          throw new InvalidJsonException("Invalid JSON Format in Processing ]");
        }
        break;

      case '}':
        helper.closingCurlyEncountered(this);
        if (this.error) {
          throw new InvalidJsonException("Invalid JSON Format in Processing }");
        }
        break;

      default:
        helper.allOtherChars(c, this);
        if (this.error) {
          throw new InvalidJsonException("Invalid JSON Format in Processing character");
        }
        break;
    }
    return this;
  }

  @Override
  public String output() {
    return currentStatus;
  }
}
