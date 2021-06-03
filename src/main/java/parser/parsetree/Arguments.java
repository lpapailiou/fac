package parser.parsetree;

import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a helper class for arguments of a function call. Each instance will be held by its own wrapper
 * and point to the next argument if present - similar to a linked list.
 */
public class Arguments extends Statement {

    private Statement param;
    private Arguments next;

    public Arguments(Object o1) {
        param = (Statement) o1;
    }

    public Arguments(Object o1, Object o2) {
        this(o1);
        next = (Arguments) o2;
    }

    @Override
    public List<Statement> getStatements() {
        List<Statement> statements = new ArrayList<>();
        statements.add(param);
        while (next != null) {
            statements.add(next.param);
            next = next.next;
        }
        return statements;
    }

    @Override
    public String toString() {
        String out = param.toString();
        if (next != null) {
            out += ", " + next;
        }
        return out;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }


}