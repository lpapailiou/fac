package exceptions;

/**
 * This exception may be thrown during syntactical validation.
 * It is used, when a declaration declares an identifier which is already in the declaration scope.
 */
public class UniquenessViolationException extends GrammarException {

    /**
     * Throws an exception pointing out that a variable was already declared, thus not valid in this context.
     *
     * @param message the exception message.
     */
    public UniquenessViolationException(String message) {
        super(message);
    }

}
