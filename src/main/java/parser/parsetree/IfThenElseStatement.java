package parser.parsetree;

import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;

public class IfThenElseStatement extends IfThenStatement {

    private List<Statement> statementList2 = new ArrayList<>();

    IfThenElseStatement(Object condition, Object statementList1, Object statementList2) {
        super(condition, statementList1);
        if (statementList2 != null) {
            this.statementList2.addAll(((Statement) statementList2).getStatements());
        }
    }

    public List<Statement> getIfStatements() {
        return super.getStatements();
    }

    public List<Statement> getElseStatements() {
        List<Statement> statements = new ArrayList<>();
        if (!statementList2.isEmpty()) {
            statements.addAll(statementList2);
        }
        return statements;
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
