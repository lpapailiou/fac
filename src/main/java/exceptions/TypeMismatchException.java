package exceptions;

/**
 * This exception may be thrown during syntactical validation.
 * It is used, when the data type of an operand of an expression does not match the expression type or
 * the data type of the partnering operand.
 */
public class TypeMismatchException extends GrammarException {

    /**
     * Throws a generic grammar exception.
     * @param message the exception message.
     */
    public TypeMismatchException(String message) {
        super(message);
    }

    /**
     * Throws a generic grammar exception and may wrap an already thrown exception.
     * @param message the exception message.
     * @param t the exception to wrap.
     */
    public TypeMismatchException(String message, Throwable t) {
        super(message, t);
    }

}
