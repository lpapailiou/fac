package parser.parsetree;

import java.util.ArrayList;
import java.util.List;

public class VariableDeclaration extends Statement implements Declaration {

    private Type type;
    private String identifier;
    private String value;

    public VariableDeclaration(Object type, String identifier, String value) {
        this.type = Type.getName(type);
        this.identifier = identifier;
        this.value = value;
    }

    @Override
    public List<Statement> getStatements() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return type.getDescription() + " " + identifier + " = " + value + ";\n";
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
