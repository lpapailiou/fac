package exceptions;

/**
 * This exception may be thrown during syntactical validation.
 * It is used, when a declaration declares an identifier which is already in the declaration scope.
 */
public class UniquenessViolationException extends GrammarException {

    /**
     * Throws a generic grammar exception.
     *
     * @param message the exception message.
     */
    public UniquenessViolationException(String message) {
        super(message);
    }

    /**
     * Throws a generic grammar exception and may wrap an already thrown exception.
     *
     * @param message the exception message.
     * @param t       the exception to wrap.
     */
    public UniquenessViolationException(String message, Throwable t) {
        super(message, t);
    }

}
