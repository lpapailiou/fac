package exceptions;

/**
 * This exception may be thrown during syntactical validation.
 * It is used, when the data type of an operand of an expression does not match the expression type or
 * the data type of the partnering operand.
 */
public class TypeMismatchException extends GrammarException {

    /**
     * Throws an exception pointing out that the data types are not valid in the context of the current expression.
     *
     * @param message the exception message.
     */
    public TypeMismatchException(String message) {
        super(message);
    }

}
