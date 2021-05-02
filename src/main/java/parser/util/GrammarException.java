package parser.util;

public class GrammarException extends RuntimeException {

    public GrammarException(String message) {
        super(message);
    }

    public GrammarException(String message, Throwable err) {
        super(message, err);
    }

}