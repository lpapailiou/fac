package parser.exceptions;

public class OperatorMismatchException extends GrammarException {

    public OperatorMismatchException(String message) {
        super(message);
    }

    public OperatorMismatchException(String message, Throwable err) {
        super(message, err);
    }

}
