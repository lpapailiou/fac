package parser.parsetree;

import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;

public class NestedStatement extends Statement {

    private Statement statement;
    private NestedStatement next;

    public NestedStatement(Object st) {
        statement = (Statement) st;
    }

    public NestedStatement(Object st, Object obj) {
        this(st);
        next = (NestedStatement) obj;
    }

    @Override
    public List<Statement> getStatements() {
        List<Statement> statements = new ArrayList<>();
        statements.add(statement);
        while (next != null) {
            statements.add(next.statement);
            next = next.next;
        }
        return statements;
    }

    @Override
    public String toString() {
        String out = "";
        return out;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }


}