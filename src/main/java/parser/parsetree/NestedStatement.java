package parser.parsetree;

import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;

public class NestedStatement extends Statement {

    private Statement statement;
    private Statement next;

    public NestedStatement(Object st) {
        statement = (Statement) st;
    }

    public NestedStatement(Object st, Object obj) {
        this(st);
        next = (Statement) obj;
    }

    @Override
    public List<Statement> getStatements() {
        List<Statement> statements = new ArrayList<>();
        statements.add(statement);
        while (next != null) {
            statements.add(next);
            if (!(next instanceof BreakStatement)) {
                next = ((NestedStatement) next).next;
            } else {
                next = null;
            }
        }
        return statements;
    }

    @Override
    public String toString() {
        String out = "";
        List<Statement> statementList = getStatements();
        for (Statement st : statementList) {
            out += st;
        }
        return out;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }


}