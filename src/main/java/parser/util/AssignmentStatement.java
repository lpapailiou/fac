package parser.util;

public class AssignmentStatement extends Statement {

    BINOP op;
    Object o1;
    Object o2;

    public AssignmentStatement(String op, Object o1, Object o2) {
        this.op = BINOP.valueOf(op);
        this.o1 = o1;
        this.o2 = o2;
    }


    @Override
    public String toString() {
        return o1.toString() + " " + op.getOperator() + " " + o2.toString();
    }


}
