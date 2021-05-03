package parser.parsetree;

import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;

public class WhileStatement extends Statement {

    private Object condition;
    private List<Statement> statementList = new ArrayList<>();

    public WhileStatement(Object condition, Object statements) {
        this.condition = condition;
        if (statements != null) {
            this.statementList = ((NestedStatement) statements).getStatements();
        }
    }


    @Override
    public List<Statement> getStatements() {
        return statementList;
    }

    @Override
    public String toString() {

        String out = "\nwhile (" + condition + ") {\n";
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
