package parser.parsetree;

import java.util.ArrayList;
import java.util.List;

public class UnaryStatement extends Statement {

    Operator op;
    Object o1;

    public UnaryStatement(String op, Object o1) {
        this.op = Operator.valueOf(op);
        this.o1 = o1;

    }


    @Override
    public List<Statement> getStatements() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return op.getOperator() + " " + o1.toString();
    }       // TODO, not implemented yet

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
