package parser.parsetree;

import java.util.ArrayList;
import java.util.List;

public class IfThenElseStatement extends IfThenStatement {

    private List<Statement> statementList2 = new ArrayList<>();

    public IfThenElseStatement(Object condition, Object statementList1, Object statementList2) {
        super(condition, statementList1);
        if (statementList2 != null) {
            this.statementList2.addAll(((NestedStatement) statementList2).statementList);
        }
    }

    @Override
    public List<Statement> getStatements() {
        List<Statement> statements = super.getStatements();
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

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
