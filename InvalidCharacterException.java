public class InvalidCharacterException extends Exception{
    private static final String MESSAGE = "contains invalid character '@'";

    public InvalidCharacterException() {
        super(MESSAGE);
    }

    public InvalidCharacterException(String message) {
        super((message));
    }
}
