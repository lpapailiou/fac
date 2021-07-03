package parser.parsetree;

import java.util.Arrays;
import java.util.function.Function;

/**
 * This enum holds all available operators for unary expressions.
 * As expressions can be executed, every operator will provide its own execution method.
 */
public enum UnaryOperator {

    /**
     * With this operator, a subtraction is performed.
     */
    MINUS("-", (e) -> -Double.parseDouble(e.toString())),
    /**
     * This operator will increment a numeric value by 1.
     */
    INC("++", (e) -> Double.parseDouble(e.toString()) + 1),
    /**
     * This operator will decrement a numeric value by 1.
     */
    DEC("--", (e) -> Double.parseDouble(e.toString()) - 1),
    /**
     * This operator negates a boolean value.
     */
    EXCL("!", (e) -> !Boolean.parseBoolean(e.toString()));

    private final String operator;
    private final Function<Object, Object> function;

    /**
     * This constructor initializes the enum for unary operators.
     *
     * @param operator the operator.
     * @param function the Function to be applied to the operand.
     */
    UnaryOperator(String operator, Function<Object, Object> function) {
        this.operator = operator;
        this.function = function;
    }

    /**
     * This method will look up a defined unary operator by an input string.
     *
     * @param input the string representation of the unary operator.
     * @return the enum for the searched operator.
     */
    public static UnaryOperator getByLiteral(Object input) {
        UnaryOperator result = Arrays.stream(UnaryOperator.values()).filter(o -> o.operator.equals(input.toString())).findAny().orElseGet(null);
        if (result != null) {
            return result;
        }
        throw new IllegalArgumentException("UnaryOperator " + input.toString() + " is not available!");
    }

    /**
     * Returns the operator as string.
     *
     * @return the string representation of the operator.
     */
    public String getLiteral() {
        return operator;
    }

    /**
     * This method applies the operator to an operand. It is used for the execution of code.
     *
     * @param e the operand.
     * @return the result of the performed operation.
     */
    public Object apply(Object e) {
        return function.apply(e);
    }

}
