package parser.parsetree;

import java.util.ArrayList;
import java.util.List;

public class BinaryStatement extends Statement {        // TODO: not implemented

    Operator op;
    Object o1;
    Object o2;

    public BinaryStatement(String op, Object o1, Object o2) {
        this.op = Operator.valueOf(op);
        this.o1 = o1;
        this.o2 = o2;
    }


    @Override
    public List<Statement> getStatements() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return o1.toString() + " " + op.getOperator() + " " + o2.toString() + ";\n";
    }


}
