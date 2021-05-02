package parser.parsetree;

public class VariableDeclaration extends Statement implements Declaration {

    private String type;
    private String identifier;
    private String value;

    public VariableDeclaration(String type, String identifier, String value) {
        this.type = type;
        this.identifier = identifier;
        this.value = value;
    }

    @Override
    public String toString() {
        return type + " " + identifier + " = " + value + ";\n";
    }

    @Override
    public void accept (Visitor visitor) {
        visitor.visit(this);
    }

}
