package parser.parsetree;

import parser.parsetree.interfaces.Declaration;
import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a wrapper class for parameter declarations of a function body.
 * It is constructed like a linked list. The leftmost declaration points to its neighbor to the right, etc.
 */
public class ParamDeclaration extends Statement implements Declaration {

    private Type type;
    private String identifier;
    private Object value;
    private Object initValue;
    private ParamDeclaration next;

    public ParamDeclaration(Object type, Object identifier) {
        this.type = Type.getByName(type);
        this.identifier = identifier.toString();
        if (this.type == Type.BOOLEAN) {
            value = false;
        } else if (this.type == Type.NUMERIC) {
            value = 0;
        } else {
            value = "''";
        }
        initValue = value;
    }

    public ParamDeclaration(Object type, Object identifier, Object obj) {
        this(type, identifier);
        next = (ParamDeclaration) obj;
    }

    public void reset() {
        value = initValue;
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

    @Override
    public void setValue(Object obj) {
        this.value = obj;
    }
}
