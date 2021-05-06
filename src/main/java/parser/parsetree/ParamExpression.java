package parser.parsetree;

import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;

public class ParamExpression extends Statement {

    private Statement param;
    private ParamExpression next;

    public ParamExpression(Object o1) {
        param = (Statement) o1;
    }

    public ParamExpression(Object o1, Object o2) {
        this(o1);
        next = (ParamExpression) o2;
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