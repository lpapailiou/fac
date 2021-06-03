package parser.parsetree;

import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a wrapper class for while loops.
 * It holds a condition and a list of nested statements.
 */
public class WhileStatement extends Statement {

    private Object condition;
    private List<Statement> statementList = new ArrayList<>();

    public WhileStatement(Object condition, Object statements) {
        this.condition = condition;
        if (statements != null) {
            this.statementList = ((NestedStatement) statements).getStatements();
        }
    }

    public Object getCondition() {
        return condition;
    }

    @Override
    public List<Statement> getStatements() {
        return statementList;
    }

    @Override
    public String toString() {

        String out = "\nwhile ";
        boolean isCond = condition instanceof ConditionalExpression;
        if (!isCond) {
            out += "(";
        }
        out += condition;
        if (!isCond) {
            out += ")";
        }
        out += " {\n";

        for (Statement st : statementList) {
            out+= "\t" + st;
        }
        out += "}\n";

        return  out;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
