package parser.parsetree;

import java.util.ArrayList;
import java.util.List;

public class IfThenStatement extends Statement {

    Object condition;
    List<Statement> statementList1 = new ArrayList<>();
    List<Statement> statementList2 = new ArrayList<>();

    public IfThenStatement(Object condition, Object statementList1) {
        this.condition = condition;
        this.statementList1.addAll(((NestedStatement) statementList1).statementList);
    }

    public IfThenStatement(Object condition, Object statementList1, Object statementList2) {
        this(condition, statementList1);
        this.statementList2.addAll(((NestedStatement) statementList2).statementList);
    }


    @Override
    public List<Statement> getStatements() {
        List<Statement> statements = new ArrayList<>();
        if (!statementList1.isEmpty()) {
            statements.addAll(statementList1);
        }
        if (!statementList2.isEmpty()) {
            statements.addAll(statementList2);
        }
        return statements;
    }

    @Override
    public String toString() {
        String out = "\nif " + condition + " {\n";
        if (!statementList1.isEmpty()) {
            for (Statement st : statementList1) {
                out += "\t" + st;
            }
        }

        if (!statementList2.isEmpty()) {
            out += "} else { \n";
            for (Statement st : statementList2) {
                out += "\t" + st;
            }
        }
        out += "}\n\n";
        return  out;
    }
}
