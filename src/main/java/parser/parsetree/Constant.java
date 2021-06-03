package parser.parsetree;

import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a wrapper class for constants. A constant may be an expression or a value of any type.
 */
public class Constant extends Statement {

    private Object obj;

    public Constant(Object o) {
        this.obj = o;
    }

    public Object getValue() {
        return obj;
    }

    @Override
    public List<Statement> getStatements() {
        List<Statement> statements = new ArrayList<>();
        if (obj instanceof Statement) {
            statements.add((Statement) obj);
        }
        return statements;
    }

    @Override
    public String toString() {
        return obj.toString();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
