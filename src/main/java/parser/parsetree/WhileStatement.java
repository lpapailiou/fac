package parser.parsetree;

import java.util.List;

public class WhileStatement extends Statement {

    Object condition;
    List<Statement> statementList;

    public WhileStatement(Object condition, Object statements) {
        this.condition = condition;
        this.statementList = ((NestedStatement) statements).getStatements();
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
}
