package mars.core;

public class MarsApplicationException extends Exception {
    public MarsApplicationException(String message) {
        super(message);
    }

    public MarsApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
