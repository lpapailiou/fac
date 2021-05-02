package parser.parsetree;


import java.util.ArrayList;
import java.util.List;

public class ExpressionStatement extends Statement {

    Operator op;
    Object o1;
    Object o2;

    public ExpressionStatement(Object o1) {
        this.o1 = o1;
    }

    public ExpressionStatement(String op, Object o1, Object o2) {
        this(o1);
        this.op = Operator.valueOf(op);
        this.o2 = o2;
    }


    @Override
    public List<Statement> getStatements() {
        List<Statement> statements = new ArrayList<>();
        if (o1 instanceof Statement) {
            statements.add((Statement) o1);
        }
        if (o1 != null && o2 instanceof Statement) {
            statements.add((Statement) o2);
        }
        return statements;
    }

    @Override
    public String toString() {
        if (o2 != null) {
            return o1.toString() + " " + op.getOperator() + " " + o2.toString();
        }
        return o1.toString();
    }

}