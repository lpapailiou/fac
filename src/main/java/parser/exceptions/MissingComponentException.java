package parser.exceptions;

public class MissingComponentException extends RuntimeException {

    public MissingComponentException(String message) {
        super(message);
    }

    public MissingComponentException(String message, Throwable err) {
        super(message, err);
    }

}
