package parser.parsetree;


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
    public String toString() {
        if (o2 != null) {
            return o1.toString() + " " + op.getOperator() + " " + o2.toString();
        }
        return o1.toString();
    }

}