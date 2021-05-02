package parser.parsetree;

import java.util.ArrayList;
import java.util.List;

public class ConditionalStatement extends Statement {

    Operator op;
    Object o1;
    Object o2;

    public ConditionalStatement(Object op, Object o1, Object o2) {
        this(o1);
        this.o2 = o2;
        this.op = Operator.getName(op.toString());
    }

    public ConditionalStatement(Object o1) {
        this.o1 = o1;
    }

    @Override
    public List<Statement> getStatements() {
        List<Statement> statements = new ArrayList<>();
        if (o1 instanceof Statement) {
            statements.add((Statement) o1);
        }
        if (o2 instanceof Statement) {
            statements.add((Statement) o2);
        }
        return statements;
    }

    @Override
    public String toString() {
        if (op != null) {
            return "(" + o1.toString() + " " + op.getOperator() + " " + o2.toString() + ")";
        }
        return "(" + o1.toString() + ")";
    }

}
