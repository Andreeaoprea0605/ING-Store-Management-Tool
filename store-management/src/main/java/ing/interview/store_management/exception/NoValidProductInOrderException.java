package ing.interview.store_management.exception;

public class NoValidProductInOrderException extends RuntimeException {
    public NoValidProductInOrderException(String message) {
        super(message);
    }
}
