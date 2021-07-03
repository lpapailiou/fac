package parser.parsetree.instructions;

import parser.parsetree.BinaryOperator;
import parser.parsetree.Component;

/**
 * This is a helper class to avoid duplicate code for binary expressions and conditions.
 */
public abstract class BinaryExpr extends Component {

    private final BinaryOperator operator;
    private final Object operand1;
    private final Object operand2;

    /**
     * The constructor is used to set the location from the source file of this code fragment.
     *
     * @param operator the operator.
     * @param operand1 the first operand.
     * @param operand2 the second operand.
     * @param left     the start index.
     * @param right    the end index.
     */
    BinaryExpr(Object operator, Object operand1, Object operand2, int left, int right) {
        super(left, right);
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.operator = BinaryOperator.getByLiteral(operator.toString());
    }

    /**
     * Returns the binary operator.
     *
     * @return the operator.
     */
    public BinaryOperator getOperator() {
        return operator;
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
}
