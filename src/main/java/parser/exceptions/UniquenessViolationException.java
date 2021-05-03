package parser.exceptions;

public class UniquenessViolationException extends RuntimeException {

    public UniquenessViolationException(String message) {
        super(message);
    }

    public UniquenessViolationException(String message, Throwable err) {
        super(message, err);
    }

}
