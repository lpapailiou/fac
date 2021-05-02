package parser.parsetree;

public class UnaryStatement extends Statement {

    Operator op;
    Object o1;

    public UnaryStatement(String op, Object o1) {
        this.op = Operator.valueOf(op);
        this.o1 = o1;

    }


    @Override
    public String toString() {
        return op.getOperator() + " " + o1.toString();
    }       // TODO, not implemented yet


}
