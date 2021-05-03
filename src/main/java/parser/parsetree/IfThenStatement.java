package parser.parsetree;

import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;

public class IfThenStatement extends Statement {

    protected Object condition;
    protected List<Statement> statementList1 = new ArrayList<>();

    public IfThenStatement(Object condition, Object statementList1) {
        this.condition = condition;
        if (statementList1 != null) {
            this.statementList1.addAll(((Statement) statementList1).getStatements());
        }
    }

    @Override
    public List<Statement> getStatements() {
        List<Statement> statements = new ArrayList<>();
        if (!statementList1.isEmpty()) {
            statements.addAll(statementList1);
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

        out += "}\n\n";
        return  out;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
