package parser.parsetree.instructions;

import parser.parsetree.Component;
import parser.parsetree.UnaryOperator;

/**
 * This is a helper class to avoid duplicate code for unary expressions and conditions.
 */
public abstract class UnaryExpr extends Component {

    private final UnaryOperator operator;
    private final Object operand;

    /**
     * The constructor is used to set the location from the source file of this code fragment.
     *
     * @param operator the operator.
     * @param operand  the operand.
     * @param left     the start index.
     * @param right    the end index.
     */
    UnaryExpr(Object operator, Object operand, int left, int right) {
        super(left, right);
        this.operator = UnaryOperator.getByLiteral(operator.toString());
        this.operand = operand;
    }

    /**
     * Returns the unary operator.
     *
     * @return the operator.
     */
    public UnaryOperator getOperator() {
        return operator;
    }

    /**
     * Returns the operand.
     *
     * @return the operand.
     */
    public Object getOperand() {
        return operand;
    }
}
