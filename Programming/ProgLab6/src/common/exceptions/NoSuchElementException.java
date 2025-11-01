package common.exceptions;

public class NoSuchElementException extends Exception {
    public NoSuchElementException(String message) {
        super(message);
    }

    //Belirli bir ID veya Key ile eleman bulunamadığında kullanılabilir
}
