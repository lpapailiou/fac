package exceptions;

/**
 * This exception may be thrown during syntactical validation.
 * It is used, when an operator does not match the data type to process.
 */
public class OperatorMismatchException extends GrammarException {

    /**
     * Throws an exception pointing out that the operator is not valid within the context of the present data type(s).
     *
     * @param message the exception message.
     */
    public OperatorMismatchException(String message) {
        super(message);
    }

}
