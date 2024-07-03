package nz.ac.canterbury.seng302.tab;

public class InvalidFormException extends IllegalArgumentException {
    public InvalidFormException() {}

    public InvalidFormException(String message) {
        super(message);
    }

    public InvalidFormException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidFormException(Throwable cause) {
        super(cause);
    }
}
