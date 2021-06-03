package parser.parsetree;


import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;

public class UnaryExpression extends ArithmeticExpression {

    private UnOp op;
    private Object operand;

    public UnaryExpression(Object op, Object operand) {
        this.op = UnOp.getName(op);
        this.operand = operand;
    }

    public Object getOperand() {
        return operand;
    }

    public UnOp getOperator() {
        return op;
    }

    @Override
    public List<Statement> getStatements() {
        List<Statement> statements = new ArrayList<>();
        if (operand instanceof Statement) {
            statements.add((Statement) operand);
        }
        return statements;
    }

    @Override
    public String toString() {
        String out = op.getOperator() + operand.toString();
        if (operand instanceof FunctionCallStatement) {
            out = out.substring(0, out.length()-2);
        }
        return out;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}