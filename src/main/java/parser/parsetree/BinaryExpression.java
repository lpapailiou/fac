package parser.parsetree;


/**
 * This is a wrapper class for binary arithmetic expressions.
 * Its instances will hold an assignment operator and two operands.
 */
public class BinaryExpression extends ArithmeticExpression {

    private final BinOp op;
    private final Object operand1;
    private final Object operand2;

    /**
     * This constructor will instantiate a wrapper for a binary expression.
     *
     * @param op       the operator.
     * @param operand1 the first operand.
     * @param operand2 the second operand.
     * @param left     the start index.
     * @param right    the end index.
     */
    BinaryExpression(Object op, Object operand1, Object operand2, int left, int right) {
        super(left, right);
        this.op = BinOp.getByLiteral(op);
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    /**
     * Returns the binary operator.
     *
     * @return the operator.
     */
    public BinOp getOperator() {
        return op;
    }

    /**
     * Returns the first operand.
     *
     * @return the first operand.
     */
    public Object getOperand1() {
        return operand1;
    }

    /**
     * Returns the second operand.
     *
     * @return the second operand.
     */
    public Object getOperand2() {
        return operand2;
    }

    /**
     * The toString method provides a pretty-printable String
     * of this parse tree component.
     * It is generated by the contents of this instance and may not be equal to the original code.
     *
     * @return the pretty-printed code.
     */
    @Override
    public String toString() {
        String out = operand1.toString();
        out += " " + op.getLiteral() + " " + operand2.toString();
        return out;
    }

    /**
     * This method returns the binary expression as representation of the parse tree.
     *
     * @return a snipped of the parse tree.
     */
    @Override
    public String getParseTree() {
        StringBuilder out = getStringBuilder(this);
        appendLine(out, "Expression", 1);
        appendLine(out, evaluateExpression(operand1), 2);
        appendNestedComponents(out, operand1, 3);
        appendBinOp(out, op, 1);
        appendLine(out, "Expression", 1);
        appendLine(out, evaluateExpression(operand2), 2);
        appendNestedComponents(out, operand2, 3);
        return out.toString();
    }

}