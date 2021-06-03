package parser.parsetree;

import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;

public class BinaryCondition extends ConditionalExpression {

    private BinOp op;
    private Object operand1;
    private Object operand2;

    public BinaryCondition(Object o1) {
        this.operand1 = o1;
    }

    public BinaryCondition(Object op, Object o1, Object o2) {
        this(o1);
        this.operand2 = o2;
        this.op = BinOp.getName(op.toString());
    }

    public Object getOperand1() {
        return operand1;
    }

    public Object getOperand2() {
        return operand2;
    }

    public BinOp getOperator() {
        return op;
    }

    @Override
    public List<Statement> getStatements() {
        List<Statement> statements = new ArrayList<>();
        if (operand1 instanceof Statement) {
            statements.add((Statement) operand1);
        }
        if (operand2 instanceof Statement) {
            statements.add((Statement) operand2);
        }
        return statements;
    }

    @Override
    public String toString() {
        if (op != null) {
            return "(" + operand1.toString() + " " + op.getOperator() + " " + operand2.toString() + ")";
        }
        return "(" + operand1.toString() + ")";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
