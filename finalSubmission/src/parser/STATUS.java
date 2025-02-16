package parser;

/**
 * Using an ENUM to maintain Status of Characters that are being input to JSON String.
 * KEY : The next character is being input to a Key String.
 * VALUE: The next character is being input to a Value String.
 * ITER: The next character is being used to form the JSON.
 */
public enum STATUS {
  KEY,
  VALUE,
  ITER
}
