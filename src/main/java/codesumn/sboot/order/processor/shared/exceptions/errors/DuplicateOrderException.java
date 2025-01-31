package codesumn.sboot.order.processor.shared.exceptions.errors;

public class DuplicateOrderException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Duplicate order! An order with the same items already exists.";

    public DuplicateOrderException() {
        super(DEFAULT_MESSAGE);
    }

    public DuplicateOrderException(String message) {
        super(message);
    }
}
