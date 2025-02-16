package validator;

import static parser.STATUS.KEY;
import static parser.STATUS.VALUE;
import static parser.STATUS.ITER;

/**
 * The Helper class contains helper functions for input() method in JsonValidator.
 */
class Helper {

  //Stack Manipulation Functions
  private void push(String s, JsonValidator jv) {
    jv.stack.add(s);
    jv.top += 1;
  }

  private String pop(JsonValidator jv) {
    jv.top -= 1;
    return jv.stack.remove(jv.top);
  }

  //Helper function to properly process {
  private void openingCurlyDuringJSON(JsonValidator jv) {
    if (jv.top != 0) {
      String topOfStack = jv.stack.get(jv.top - 1);
      if (!topOfStack.startsWith(",") && !topOfStack.startsWith(":") && !topOfStack.startsWith(
          "[")) {
        jv.currentStatus = "Status:Invalid";
        jv.error = true;
      } else {
        jv.nextForm = KEY;
        jv.prevForm = VALUE;
        jv.currForm = ITER;
        this.push("{", jv);
        jv.currentStatus = "Status:Incomplete";
      }
    } else {
      jv.nextForm = KEY;
      jv.currForm = ITER;
      this.push("{", jv);
      jv.currentStatus = "Status:Incomplete";
    }
  }

  //Wrapper function to properly process {
  void openingCurlyEncountered(JsonValidator jv) {
    switch (jv.currForm) {
      case KEY:
        jv.currentStatus = "Status:Invalid";
        jv.error = true;
        break;

      case VALUE:
        jv.currentString = jv.currentString + '{';
        jv.currentStatus = "Status:Incomplete";
        break;

      case ITER:
        openingCurlyDuringJSON(jv);
        break;

      default:
        break;

    }
  }

  //Helper functions to properly process a completed string
  //Helper function when completed string is preceded by :
  private void objectString(String topOfStack, JsonValidator jv) {
    String appendingValue = this.pop(jv);
    if (!appendingValue.startsWith("{")) {
      jv.currentStatus = "Status:Invalid";
      jv.error = true;
    } else {
      appendingValue = appendingValue + ' ' + topOfStack;
      this.push(appendingValue, jv);
      jv.currentString = "";
      jv.prevForm = jv.currForm;
      jv.nextForm = KEY;
      jv.currForm = ITER;
      jv.currentStatus = "Status:Incomplete";
    }
  }

  //Helper function when completed string is preceded by ,
  private void arrayValueOrKeyString(String topOfStack, JsonValidator jv) {
    String appendingValue = this.pop(jv);
    if (!(appendingValue.startsWith("[") || appendingValue.startsWith("{"))) {
      jv.currentStatus = "Status:Invalid";
      jv.error = true;
    } else {
      appendingValue = appendingValue + ' ' + topOfStack;
      this.push(appendingValue, jv);
      jv.currentString = "";
      jv.prevForm = jv.currForm;
      jv.nextForm = VALUE;
      jv.currForm = ITER;
      jv.currentStatus = "Status:Incomplete";
    }
  }

  //Wrapper function to properly process a completed string
  private void endingDoubleQuotesThatCompleteAString(JsonValidator jv) {
    String topOfStack = this.pop(jv);
    if (!topOfStack.equals(",") && !topOfStack.equals(":") && !topOfStack.equals("{")
        && !topOfStack.equals("[")) {
      jv.currentStatus = "Status:Invalid";
      jv.error = true;
    } else {
      topOfStack = topOfStack + '"' + jv.currentString + '"';
      switch (topOfStack.charAt(0)) {
        case ':':
          objectString(topOfStack, jv);
          break;
        case ',':
          arrayValueOrKeyString(topOfStack, jv);
          break;
        case '{':
          this.push(topOfStack, jv);
          jv.currentString = "";
          jv.prevForm = jv.currForm;
          jv.currForm = ITER;
          jv.nextForm = VALUE;
          jv.currentStatus = "Status:Incomplete";
          break;
        case '[':
          this.push(topOfStack, jv);
          jv.currentString = "";
          jv.prevForm = jv.currForm;
          jv.nextForm = VALUE;
          jv.currForm = ITER;
          jv.currentStatus = "Status:Incomplete";
          break;
        default:
          jv.currentStatus = "Status:Invalid";
          jv.error = true;
      }
    }
  }

  //Helper function that process a double quote starting a string
  private void stringStarter(JsonValidator jv) {
    String topOfStack = jv.stack.get(jv.top - 1);
    if (!topOfStack.endsWith(",") && !topOfStack.endsWith(":") && !topOfStack.endsWith("{")
        && !topOfStack.endsWith("[")) {
      jv.currentStatus = "Status:Invalid";
      jv.error = true;
    } else {
      jv.currForm = jv.nextForm;
      jv.currentStatus = "Status:Incomplete";
    }
  }

  //Wrapper function to process all kinds of "
  void doubleQuotesEncountered(JsonValidator jv) {
    if (jv.currentString.isEmpty()) {
      switch (jv.currForm) {
        case KEY:
          jv.currentStatus = "Status:Invalid";
          jv.error = true;
          break;

        case VALUE:
          endingDoubleQuotesThatCompleteAString(jv);
          break;

        case ITER:
          stringStarter(jv);
          break;

        default:
          //No possible Status
          break;
      }
    } else {
      endingDoubleQuotesThatCompleteAString(jv);
    }
  }

  //Helper function to process a Colon(:) separator
  private void colonSeparator(JsonValidator jv) {
    String topOfStack = jv.stack.get(jv.top - 1);
    if (!topOfStack.startsWith("{")) {
      jv.currentStatus = "Status:Invalid";
      jv.error = true;
    } else {
      if (jv.prevForm == KEY && jv.nextForm == VALUE) {
        this.push(":", jv);
        jv.currForm = ITER;
        jv.nextForm = VALUE;
        jv.currentStatus = "Status:Incomplete";
      } else {
        jv.currentStatus = "Status:Invalid";
        jv.error = true;
      }
    }
  }

  //Wrapper function to process a :
  void colonEncountered(JsonValidator jv) {
    switch (jv.currForm) {
      case KEY:
        jv.currentStatus = "Status:Invalid";
        jv.error = true;
        break;

      case VALUE:
        jv.currentString = jv.currentString + " :";
        jv.currentStatus = "Status:Incomplete";
        break;

      case ITER:
        colonSeparator(jv);
        break;

      default:
        //No possible Status
        break;
    }
  }

  //Helper function to process a Comma (,) Separator
  private void commaSeperator(JsonValidator jv) {
    if ((jv.prevForm == VALUE && jv.nextForm == VALUE) || (jv.prevForm == VALUE
        && jv.nextForm == KEY)) {
      this.push(",", jv);
      jv.currForm = ITER;
      jv.currentStatus = "Status:Incomplete";
    } else {
      jv.currentStatus = "Status:Invalid";
      jv.error = true;
    }
  }

  //Wrapper function to process a (,)
  void commaEncountered(JsonValidator jv) {
    switch (jv.currForm) {
      case KEY:
        jv.currentStatus = "Status:Invalid";
        jv.error = true;
        break;

      case VALUE:
        jv.currentString = jv.currentString + " ,";
        jv.currentStatus = "Status:Incomplete";
        break;

      case ITER:
        commaSeperator(jv);
        break;

      default:
        //No possible Status
        break;
    }
  }

  //Helper function to process a beginning array ([)
  private void startingArray(JsonValidator jv) {
    String topOfStack = jv.stack.get(jv.top - 1);
    if (!topOfStack.startsWith("[") && !topOfStack.startsWith(":") && !topOfStack.startsWith(
        ",")) {
      jv.currentStatus = "Status:Invalid";
      jv.error = true;
    } else {
      if (jv.nextForm == VALUE) {
        jv.currForm = ITER;
        jv.currentStatus = "Status:Incomplete";
        this.push("[", jv);
      } else {
        jv.currentStatus = "Status:Invalid";
        jv.error = true;
      }
    }
  }

  //Wrapper function to process a beginning of an array ([)
  void openSquareEncountered(JsonValidator jv) {
    switch (jv.currForm) {
      case KEY:
        jv.currentStatus = "Status:Invalid";
        jv.error = true;
        break;

      case VALUE:
        jv.currentString = jv.currentString + " [";
        jv.currentStatus = "Status:Incomplete";
        break;

      case ITER:
        startingArray(jv);
        break;

      default:
        //No possible Status
        break;
    }
  }

  //Helper functions to process a completed Array Value
  private void arrayAsAValueInObject(String topOfStack, String appendingValue, JsonValidator jv) {
    String parentString = this.pop(jv);
    if (!(parentString.startsWith("{"))) {
      jv.currentStatus = "Status:Invalid";
      jv.error = true;
    } else {
      String newStr = parentString + " " + appendingValue + " " + topOfStack;
      this.push(newStr, jv);
      jv.prevForm = VALUE;
      jv.currForm = ITER;
      jv.nextForm = KEY;
      jv.currentStatus = "Status:Incomplete";
    }
  }

  private void arrayAsASubArray(String topOfStack, String appendingValue, JsonValidator jv) {
    String parentString = pop(jv);
    if (!(parentString.startsWith("["))) {
      jv.currentStatus = "Status:Invalid";
      jv.error = true;
    } else {
      String newStr = parentString + " " + appendingValue + " " + topOfStack;
      this.push(newStr, jv);
      jv.prevForm = VALUE;
      jv.currForm = ITER;
      jv.nextForm = VALUE;
      jv.currentStatus = "Status:Incomplete";
    }
  }

  private void firstSubArray(String topOfStack, String appendingValue, JsonValidator jv) {
    String newStr = appendingValue + " " + topOfStack;
    this.push(newStr, jv);
    jv.prevForm = VALUE;
    jv.currForm = ITER;
    jv.nextForm = VALUE;
    jv.currentStatus = "Status:Incomplete";
  }

  //Wrapper function for a completed Array Object([)
  private void completedArrayObject(JsonValidator jv) {
    if (jv.nextForm != KEY) {
      String topOfStack = this.pop(jv);
      if (!(topOfStack.startsWith("[") && (topOfStack.endsWith("\"") || topOfStack.endsWith("]")
          || topOfStack.endsWith("}"))) && !(topOfStack.equals("["))) {
        jv.currentStatus = "Status:Invalid";
        jv.error = true;
      } else {
        topOfStack = topOfStack + " ]";
        String appendingValue = this.pop(jv);
        switch (appendingValue.charAt(0)) {
          case ':':
            arrayAsAValueInObject(topOfStack, appendingValue, jv);
            break;

          case ',':
            arrayAsASubArray(topOfStack, appendingValue, jv);
            break;

          case '[':
            firstSubArray(topOfStack, appendingValue, jv);
            break;

          default:
            jv.error = true;
            jv.currentStatus = "Status:Invalid";
            break;
        }
      }
    } else {
      jv.currentStatus = "Status:Invalid";
      jv.error = true;
    }
  }

  //Wrapper function for completed array Object Character (])
  void closingSquareEncountered(JsonValidator jv) {
    switch (jv.currForm) {
      case KEY:
        jv.currentStatus = "Status:Invalid";
        jv.error = true;
        break;

      case VALUE:
        jv.currentString = jv.currentString + " ]";
        jv.currentStatus = "Status:Incomplete";
        break;

      case ITER:
        completedArrayObject(jv);
        break;

      default:
        //No possible Status
        break;
    }
  }

  //Helper functions for Completed Objects
  private void objectAsAValue(String topOfStack, String appendingValue, JsonValidator jv) {
    String parentString = this.pop(jv);
    if (!(parentString.startsWith("{"))) {
      jv.currentStatus = "Status:Invalid";
      jv.error = true;
    } else {
      String newStr = parentString + " " + appendingValue + " " + topOfStack;
      this.push(newStr, jv);
      jv.prevForm = VALUE;
      jv.currForm = ITER;
      jv.nextForm = KEY;
      jv.currentStatus = "Status:Incomplete";
    }
  }

  private void objectAsAnElementInArray(String topOfStack, String appendingValue,
      JsonValidator jv) {
    String parentString = this.pop(jv);
    if (!(parentString.startsWith("["))) {
      jv.currentStatus = "Status:Invalid";
      jv.error = true;
    } else {
      String newStr = parentString + " " + appendingValue + " " + topOfStack;
      this.push(newStr, jv);
      jv.prevForm = VALUE;
      jv.currForm = ITER;
      jv.nextForm = VALUE;
      jv.currentStatus = "Status:Incomplete";
    }
  }


  private void objectAsFirstElementInArray(String topOfStack, String appendingValue,
      JsonValidator jv) {
    String newStr = appendingValue + " " + topOfStack;
    this.push(newStr, jv);
    jv.prevForm = VALUE;
    jv.currForm = ITER;
    jv.nextForm = VALUE;
    jv.currentStatus = "Status:Incomplete";
  }

  //Wrapper function for a Completed Object
  private void completedObject(JsonValidator jv) {
    if (jv.nextForm != KEY) {
      jv.currentStatus = "Status:Invalid";
      jv.error = true;
    } else {
      String topOfStack = this.pop(jv);
      if (jv.stack.isEmpty() && topOfStack.startsWith("{")) {
        jv.currentStatus = "Status:Valid";
        topOfStack = topOfStack + " }";
        return;
      }

      if (topOfStack.startsWith("{")) {
        topOfStack = topOfStack + " }";
        if (!(jv.stack.isEmpty())) {
          String appendingValue = this.pop(jv);
          switch (appendingValue.charAt(0)) {
            case ':':
              objectAsAValue(topOfStack, appendingValue, jv);
              break;

            case ',':
              objectAsAnElementInArray(topOfStack, appendingValue, jv);
              break;

            case '[':
              objectAsFirstElementInArray(topOfStack, appendingValue, jv);
              break;

            default:
              jv.error = true;
              jv.currentStatus = "Status:Invalid";
              break;
          }
        }
      } else {
        jv.currentStatus = "Status:Invalid";
        jv.error = true;
      }
    }

  }

  //Wrapper function for }
  void closingCurlyEncountered(JsonValidator jv) {
    switch (jv.currForm) {
      case KEY:
        jv.currentStatus = "Status:Invalid";
        jv.error = true;
        break;

      case VALUE:
        jv.currentString = jv.currentString + " }";
        jv.currentStatus = "Status:Incomplete";
        break;

      case ITER:
        completedObject(jv);
        break;

      default:
        //No Possible STATUS
        break;
    }
  }

  //Wrapper function for empty spaces
  void spaceEncountered(JsonValidator jv) {
    switch (jv.currForm) {
      case KEY:
        jv.currentStatus = "Status:Invalid";
        jv.error = true;
        break;

      case VALUE:
        jv.currentString = jv.currentString + " ";
        jv.currentStatus = "Status:Incomplete";
        break;

      case ITER:
        if (!(jv.currentStatus.equals("Status:Valid"))) {
          jv.currentStatus = "Status:Incomplete";
        } else {
          jv.currentStatus = "Status:Valid";
        }
        break;

      default:
        //No Possible STATUS
        break;
    }
  }

  //Wrapper function for numbers
  void numericCharacterEncountered(char c, JsonValidator jv) {
    switch (jv.currForm) {
      case KEY:
        if (jv.currentString.isEmpty()) {
          jv.currentStatus = "Status:Invalid";
          jv.error = true;
        } else {
          jv.currentString = jv.currentString + c;
          jv.currentStatus = "Status:Incomplete";
        }
        break;

      case VALUE:
        jv.currentString = jv.currentString + c;
        jv.currentStatus = "Status:Incomplete";
        break;

      case ITER:
        jv.currentStatus = "Status:Invalid";
        jv.error = true;
        break;

      default:
        //NO Possible STATUS
        break;
    }
  }

  //Wrapper function for alphabets
  void alphabetEncountered(char c, JsonValidator jv) {
    switch (jv.currForm) {
      case KEY:
      case VALUE:
        jv.currentString = jv.currentString + c;
        jv.currentStatus = "Status:Incomplete";
        break;

      case ITER:
        jv.currentStatus = "Status:Invalid";
        jv.error = true;
        break;

      default:
        //No Possible Status
        break;
    }
  }

  //Wrapper function for all remaining Characters
  void allOtherChars(char c, JsonValidator jv) {
    switch (jv.currForm) {
      case KEY:
      case ITER:
        jv.currentStatus = "Status:Invalid";
        jv.error = true;
        break;

      case VALUE:
        jv.currentString = jv.currentString + c;
        jv.currentStatus = "Status:Incomplete";
        break;

      default:
        //No Possible Status
        break;
    }
  }
  
}
