package exceptions;

public class UniquenessViolationException extends GrammarException {

    public UniquenessViolationException(String message) {
        super(message);
    }

    public UniquenessViolationException(String message, Throwable err) {
        super(message, err);
    }

}
