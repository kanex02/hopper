package nz.ac.canterbury.seng302.tab.authentication;

/**
 * Custom exception for when passwords are attempted to be stored without being hashed
 */
public class InsecurePasswordException extends Exception {

    /**
     * Constructs a new instance of this class with no error message
     */
    public InsecurePasswordException() {
        super();
    }

    /**
     * Constructs a new instance of this class with a specified error message
     */
    public InsecurePasswordException(String message) {
        super(message);
    }
}
