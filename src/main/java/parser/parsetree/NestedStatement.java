package parser.parsetree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NestedStatement extends Statement {

    List<Statement> statementList = new ArrayList<>();

    public NestedStatement(Object st) {
        statementList.add((Statement) st);
    }

    public NestedStatement(Object st, Object obj) {
        this(st);
        statementList.addAll(((NestedStatement) obj).statementList);
    }

    public List<Statement> getStatementList() {
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


}