package common.exceptions;

/**
 * Script modunda yanlış veri girildiğinde fırlatılır.
 */
public class IncorrectInputInScriptException extends Exception {
    public IncorrectInputInScriptException() { super(); }
    public IncorrectInputInScriptException(String message) { super(message); }
}