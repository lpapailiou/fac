package parser.exceptions;

public class TypeMismatchException extends GrammarException {

    public TypeMismatchException(String message) {
        super(message);
    }

    public TypeMismatchException(String message, Throwable err) {
        super(message, err);
    }

}
