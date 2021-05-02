package parser.parsetree;

import java.util.ArrayList;
import java.util.List;

public class ParamDeclaration extends Statement implements Declaration {

    private Type type;
    private String identifier;
    private Object value;
    private ParamDeclaration next;

    public ParamDeclaration(Object type, Object identifier) {
        this.type = Type.getName(type);
        this.identifier = identifier.toString();
    }

    public ParamDeclaration(Object type, Object identifier, Object obj) {
        this(type, identifier);
        next = (ParamDeclaration) obj;
    }

    public ParamDeclaration getNext() {
        return next;
    }

    @Override
    public List<Statement> getStatements() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        String out = type.getDescription() + " " + identifier;
        return out;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
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
}
