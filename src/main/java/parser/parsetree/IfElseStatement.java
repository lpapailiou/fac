package parser.parsetree;

import java.util.List;

public class IfElseStatement extends Statement {

    Object condition;
    Object statementList1;
    Object statementList2;

    public IfElseStatement(Object condition, Object statementList1) {
        this.condition = condition;
        this.statementList1 = statementList1;
    }

    public IfElseStatement(Object condition, Object statementList1, Object statementList2) {
        this(condition, statementList1);
        this.statementList2 = statementList2;
    }


    @Override
    public String toString() {
        String out = "\nif " + condition + " {\n";
        for (Statement st : ((NestedStatement) statementList1).statementList) {
            out += "\t" + st;
        }

        if (statementList2 != null) {
            out += "} else { \n";
            for (Statement st : ((NestedStatement) statementList2).statementList) {
                out += "\t" + st;
            }
        }
        out += "}\n\n";
        return  out;
    }
}
