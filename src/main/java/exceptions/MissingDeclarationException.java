package exceptions;

/**
 * This exception may be thrown during syntactical validation.
 * It is used, when a variable is used which is out of scope (i.e. not declared in the current context).
 */
public class MissingDeclarationException extends GrammarException {

    /**
     * Throws a generic grammar exception.
     *
     * @param message the exception message.
     */
    public MissingDeclarationException(String message) {
        super(message);
    }

    /**
     * Throws a generic grammar exception and may wrap an already thrown exception.
     *
     * @param message the exception message.
     * @param t       the exception to wrap.
     */
    public MissingDeclarationException(String message, Throwable t) {
        super(message, t);
    }

}
