package parser.exceptions;

public class TypeMismatchException extends RuntimeException {

    public TypeMismatchException(String message) {
        super(message);
    }

    public TypeMismatchException(String message, Throwable err) {
        super(message, err);
    }

}
