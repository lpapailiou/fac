package parser.parsetree;


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

    /**
     * The toString method provides a pretty-printable String
     * of this parse tree component.
     * It is generated by the contents of this instance and may not be equal to the original code.
     * @return the pretty-printed code.
     */
    @Override
    public String toString() {
        String out = operand.toString();
        if (op == UnOp.DEC || op == UnOp.INC) {
            out += op.asString();
        } else {
            out = op.asString() + out;
        }
        if (operand instanceof FunctionCallStatement) {
            out = out.substring(0, out.length()-2);
        }
        return out;
    }

}