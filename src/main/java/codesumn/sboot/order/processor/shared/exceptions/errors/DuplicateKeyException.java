package codesumn.sboot.order.processor.shared.exceptions.errors;


public class DuplicateKeyException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Duplicate key detected! Customer code or " +
            "product code already exists.";

    public DuplicateKeyException() {
        super(DEFAULT_MESSAGE);
    }

    public DuplicateKeyException(String message) {
        super(message != null ? message : DEFAULT_MESSAGE);
    }
}
