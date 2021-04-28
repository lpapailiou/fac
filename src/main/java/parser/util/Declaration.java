package parser.util;

public class Declaration implements Acceptor {

    private String type;
    private String identifier;
    private String value;

    public Declaration(String type, String identifier, String value) {
        this.type = type;
        this.identifier = identifier;
        this.value = value;
    }

    @Override
    public String toString() {
        return type + " " + identifier + " = " + value;
    }

    @Override
    public void accept (Visitor visitor) {
        visitor.visit(this);
    }

}
