package exceptions;

/**
 * This exception may be thrown during syntactical validation.
 * It is used, when an operator does not match the data type to process.
 */
public class OperatorMismatchException extends GrammarException {

    /**
     * Throws a generic grammar exception.
     *
     * @param message the exception message.
     */
    public OperatorMismatchException(String message) {
        super(message);
    }

    /**
     * Throws a generic grammar exception and may wrap an already thrown exception.
     *
     * @param message the exception message.
     * @param t       the exception to wrap.
     */
    public OperatorMismatchException(String message, Throwable t) {
        super(message, t);
    }

}
