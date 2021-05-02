package parser.parsetree;

public class AssignmentStatement extends Statement {

    Operator op;
    Object o1;
    Object o2;

    public AssignmentStatement(Object op, Object o1, Object o2) {
        this.op = Operator.getName(op.toString());
        this.o1 = o1;
        this.o2 = o2;
    }


    @Override
    public String toString() {
        return o1.toString() + " " + op.getOperator() + " " + o2.toString() + ";\n";
    }


}
