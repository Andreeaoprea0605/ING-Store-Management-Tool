package ing.interview.store_management.exception;

public class InsufficientProductQuantityException extends RuntimeException {

    public InsufficientProductQuantityException(String message) {
        super(message);
    }
}