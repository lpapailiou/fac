package parser.parsetree;


import java.util.Arrays;
import java.util.List;

/**
 * This is a wrapper class for binary arithmetic expressions.
 * Its instances will hold an assignment operator and two operands.
 */
public class BinaryExpression extends ArithmeticExpression {

    private BinOp op;
    private Object operand1;
    private Object operand2;

    /**
     * This constructor will instantiate a wrapper for a binary expression.
     *
     * @param op       the operator.
     * @param operand1 the first operand.
     * @param operand2 the second operand.
     */
    BinaryExpression(Object op, Object operand1, Object operand2, int left, int right) {
        super(left, right);
        this.op = BinOp.getName(op);
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
        if (operand1 instanceof FunctionCallStatement) {
            out = out.substring(0, out.length() - 2);
        }
        out += " " + op.asString() + " " + operand2.toString();
        if (operand2 instanceof FunctionCallStatement) {
            out = out.substring(0, out.length() - 2);
        }
        return out;
    }

    @Override
    public String getParseTree() {
        String out = this.getClass().getName();
        out = "+ " + out.substring(out.lastIndexOf(".") + 1) + "\n";

        if (operand1 instanceof Component) {
            List<String> components = Arrays.asList(((Component) operand1).getParseTree().split("\n"));
            for (String str : components) {
                out += "\t" + str + "\n";
            }
        } else {
            out += "\t+ " + Type.getTypeForValue(operand1) + "\n";
        }

        out += "\t+ " + "OPERATOR" + "\n";

        if (operand2 instanceof Component) {
            List<String> components = Arrays.asList(((Component) operand2).getParseTree().split("\n"));
            for (String str : components) {
                out += "\t" + str + "\n";
            }
        } else {
            out += "\t+ " + Type.getTypeForValue(operand2) + "\n";
        }

        return out;
    }

}