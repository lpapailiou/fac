package parser.parsetree;


import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;

public class BinaryExpression extends ArithmeticExpression {

    private BinOp op;
    private Object operand1;
    private Object operand2;

    public BinaryExpression(Object op, Object operand1, Object operand2) {
        this.op = BinOp.getName(op);
        this.operand1 = operand1;
        this.operand2 = operand2;
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
        String out = operand1.toString();
        if (operand1 instanceof FunctionCallStatement) {
            out = out.substring(0, out.length()-2);
        }
        out += " " + op.getOperator() + " " + operand2.toString();
        if (operand2 instanceof FunctionCallStatement) {
            out = out.substring(0, out.length()-2);
        }
        return out;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}