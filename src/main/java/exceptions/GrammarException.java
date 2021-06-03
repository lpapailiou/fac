package exceptions;

/**
 * This is a superclass for a few more specific custom exceptions.
 * It is used for exceptions happening during the syntactical or semantic validation of the parse tree.
 */
public class GrammarException extends RuntimeException {

    /**
     * Throws a generic grammar exception.
     *
     * @param message the exception message.
     */
    public GrammarException(String message) {
        super(message);
    }

    /**
     * Throws a generic grammar exception and may wrap an already thrown exception.
     *
     * @param message the exception message.
     * @param t       the exception to wrap.
     */
    public GrammarException(String message, Throwable t) {
        super(message, t);
    }

}
