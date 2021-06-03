package parser.parsetree;


import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a wrapper class for unary arithmetic expressions.
 * Its instances will hold an assignment operator and an operand.
 */
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
        String out = operand.toString();
        if (op == UnOp.DEC || op == UnOp.INC) {
            out += op.getOperator();
        } else {
            out = op.getOperator() + out;
        }
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