package exceptions;

/**
 * This exception may be thrown during syntactical validation.
 * It is used, when a variable is used which is out of scope (i.e. not declared in the current context).
 */
public class MissingDeclarationException extends GrammarException {

    /**
     * Throws an exception pointing out that the validated declaration is missing in the current declaration scope.
     *
     * @param message the exception message.
     */
    public MissingDeclarationException(String message) {
        super(message);
    }

}
