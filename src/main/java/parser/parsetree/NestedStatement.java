package parser.parsetree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NestedStatement extends Statement {

    List<Statement> statementList = new ArrayList<>();

    public NestedStatement(Object st) {
        if (st != null) {
            statementList.add((Statement) st);
        }
    }

    public NestedStatement(Object st, Object obj) {
        this(st);
        if (obj != null) {
            statementList.addAll(((NestedStatement) obj).statementList);
        }
    }

    @Override
    public List<Statement> getStatements() {
        return statementList;
    }

    @Override
    public String toString() {
        String out = "";
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