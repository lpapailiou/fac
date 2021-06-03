package parser.parsetree;

import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a wrapper class for if-then statements.
 * It holds one condition and the statement list.
 */
public class IfThenStatement extends Statement {

    protected Object condition;
    protected List<Statement> statementList1 = new ArrayList<>();

    public IfThenStatement(Object condition, Object statementList1) {
        this.condition = condition;
        if (statementList1 != null) {
            this.statementList1.addAll(((Statement) statementList1).getStatements());
        }
    }

    public Object getCondition() {
        return condition;
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
        String out = "\nif ";
        boolean isCond = condition instanceof ConditionalExpression;
        if (!isCond) {
            out += "(";
        }
        out += condition;
        if (!isCond) {
            out += ")";
        }
        out += " {\n";
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
