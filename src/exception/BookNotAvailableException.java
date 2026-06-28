package src.exception;

/**
 * Thrown when a requested book is not available.
 */
public class BookNotAvailableException extends Exception {

    public BookNotAvailableException(String message) {
        super(message);
    }
}