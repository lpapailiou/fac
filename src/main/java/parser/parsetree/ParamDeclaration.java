package parser.parsetree;

import java.util.ArrayList;
import java.util.List;

public class ParamDeclaration extends Statement {

    private String type;
    private String identifier;
    private ParamDeclaration next;

    public ParamDeclaration(Object type, Object identifier) {
        this.type = type.toString();
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
        String out = type + " " + identifier;
        return out;
    }

    @Override
    public void accept (Visitor visitor) {
        visitor.visit(this);
    }

}
