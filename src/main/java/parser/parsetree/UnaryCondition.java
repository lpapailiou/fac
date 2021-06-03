package parser.parsetree;

import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a wrapper class for unary conditional expressions.
 * Its instances will hold an assignment operator and an operand.
 */
public class UnaryCondition extends ConditionalExpression {

    private UnOp op;
    private Object operand;

    public UnaryCondition(Object op, Object o) {
        this.op = UnOp.getName(op.toString());
        this.operand = o;
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
        return "(" + op.getOperator() + operand.toString() + ")";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
