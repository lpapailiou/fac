package parser.exceptions;

public class MissingDeclarationException extends GrammarException {

    public MissingDeclarationException(String message) {
        super(message);
    }

    public MissingDeclarationException(String message, Throwable err) {
        super(message, err);
    }

}
