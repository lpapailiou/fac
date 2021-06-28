package parser.parsetree;

/**
 * This enum is a helper structure to generate parse tree output easier.
 * It contains keys for reserved words, but also special characters like brackets.
 */
public enum Keyword {

    IF("if"),
    ELSE("else"),
    DEF("def"),
    WHILE("while"),
    BREAK("break"),
    RETURN("return"),
    STOP(";"),
    COMMA(","),
    BL("("),
    BR(")"),
    CBL("{"),
    CBR("}"),
    PRINT("print");

    private final String literal;

    /**
     * Constructor to initialize keys.
     *
     * @param literal the literal to pass.
     */
    Keyword(String literal) {
        this.literal = literal;
    }

    /**
     * Returns the literal of the keyword.
     *
     * @return the literal as string.
     */
    public String getLiteral() {
        return literal;
    }

}
