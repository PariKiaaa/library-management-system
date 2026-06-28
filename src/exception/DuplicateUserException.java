package src.exception;

/**
 * Thrown when trying to register a user that already exists.
 */
public class DuplicateUserException extends Exception {

    public DuplicateUserException(String message) {
        super(message);
    }
}