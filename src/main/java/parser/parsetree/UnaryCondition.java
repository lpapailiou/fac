package parser.parsetree;

/**
 * This is a wrapper class for unary conditional expressions.
 * Its instances will hold an assignment operator and an operand.
 */
public class UnaryCondition extends ConditionalExpression {

    private UnOp op;
    private Object operand;

    /**
     * This constructor will instantiate a wrapper for a unary conditional expression.
     * @param op the operator.
     * @param o the operand.
     */
    UnaryCondition(Object op, Object o) {
        this.op = UnOp.getName(op.toString());
        this.operand = o;
    }

    /**
     * Returns the unary operator.
     * @return the operator.
     */
    public UnOp getOperator() {
        return op;
    }

    /**
     * Returns the operand.
     * @return the operand.
     */
    public Object getOperand() {
        return operand;
    }

    /**
     * The toString method provides a pretty-printable String
     * of this parse tree component.
     * It is generated by the contents of this instance and may not be equal to the original code.
     * @return the pretty-printed code.
     */
    @Override
    public String toString() {
        return "(" + op.asString() + operand.toString() + ")";
    }

}
