package common.exceptions;

// Serializable olması şart değil, çünkü bu exception'ı ağdan göndermeyeceğiz.
public class SerializationException extends Exception {
  public SerializationException(String message, Throwable cause) {
    super(message, cause);
  }
}
