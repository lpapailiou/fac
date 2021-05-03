package parser.parsetree;


import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;

public class BinaryExpression extends ExpressionStatement {

    private Operator op;
    private Object operand1;
    private Object operand2;

    public BinaryExpression(Object operand1) {
        this.operand1 = operand1;
    }

    public BinaryExpression(Object op, Object operand1, Object operand2) {
        this(operand1);
        this.op = Operator.getName(op);
        this.operand2 = operand2;
    }

    public Object getOperand1() {
        return operand1;
    }

    public Object getOperand2() {
        return operand2;
    }

    public Operator getOperator() {
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
        return operand1.toString() + " " + op.getOperator() + " " + operand2.toString();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}