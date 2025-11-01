package common.exceptions;

/**
 * Form ile oluşturulan nesne geçersizse fırlatılır.
 */
public class InvalidFormException extends Exception {
    public InvalidFormException(String message) { super(message); }
}