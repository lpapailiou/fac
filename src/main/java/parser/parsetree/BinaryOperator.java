package parser.parsetree;

import java.util.Arrays;
import java.util.function.BiFunction;

/**
 * This enum holds all available operators for binary expressions.
 * As expressions can be executed, every operator will provide its own execution method.
 */
public enum BinaryOperator {

    /**
     * The equal operator is technically not a binary operation, but is included in this enum for easier
     * processing (as assignments like +=, -=, etc. are binary operations).
     * When applied, this operator will return the second value passed.
     */
    EQUAL("=", (a, b) -> b),
    /**
     * This operator will return either a concatenated string of at least one of the operands is a string
     * or perform an arithmetic addition.
     */
    PLUSEQ("+=", (a, b) -> {
        if (Type.getByInput(a) == Type.STRING || Type.getByInput(b) == Type.STRING) {
            return "'" + (a.toString() + (b.toString())).replaceAll("'", "") + "'";
        } else {
            return Double.parseDouble(a.toString()) + Double.parseDouble(b.toString());
        }
    }),
    /**
     * With this operator, a subtraction is performed.
     */
    MINEQ("-=", (a, b) -> Double.parseDouble(a.toString()) - Double.parseDouble(b.toString())),
    /**
     * With this operator, a multiplication is performed.
     */
    MULEQ("*=", (a, b) -> Double.parseDouble(a.toString()) * Double.parseDouble(b.toString())),
    /**
     * With this operator, a division is performed.
     */
    DIVEQ("/=", (a, b) -> Double.parseDouble(a.toString()) / Double.parseDouble(b.toString())),
    /**
     * With this operator, a modulo operation is performed.
     */
    MODEQ("%=", (a, b) -> Double.parseDouble(a.toString()) % Double.parseDouble(b.toString())),
    /**
     * This operator will apply the logical and, by evaluating both operands.
     */
    AND("&&", (a, b) -> (Boolean.parseBoolean(a.toString()) && Boolean.parseBoolean(b.toString()))),
    /**
     * This operator will apply the logical or, by evaluating both operands.
     */
    OR("||", (a, b) -> (Boolean.parseBoolean(a.toString()) || Boolean.parseBoolean(b.toString()))),
    /**
     * This operator will test for equality of 'primitive' values. Strings are evaluated
     * as usual in Java with the equal method.
     */
    EQ("==", (a, b) -> {
        Type type = Type.getByInput(a);
        if (type == Type.BOOLEAN) {
            return Boolean.parseBoolean(a.toString()) == Boolean.parseBoolean(a.toString());
        } else if (type == Type.NUMERIC) {
            return Double.parseDouble(a.toString()) == Double.parseDouble(b.toString());
        } else {
            return a.toString().equals(b.toString());
        }
    }),
    /**
     * This operator will test for inequality of 'primitive' values. Strings are evaluated
     * as usual in Java with the !equal method.
     */
    NEQ("!=", (a, b) -> {
        Type type = Type.getByInput(a);
        if (type == Type.BOOLEAN) {
            return Boolean.parseBoolean(a.toString()) != Boolean.parseBoolean(a.toString());
        } else if (type == Type.NUMERIC) {
            return Double.parseDouble(a.toString()) != Double.parseDouble(b.toString());
        } else {
            return !a.toString().equals(b.toString());
        }
    }),
    /**
     * This operator tests if the first operand is greater than the second operand.
     */
    GREATER(">", (a, b) -> Double.parseDouble(a.toString()) > Double.parseDouble(b.toString())),
    /**
     * This operator tests if the first operand is greater than or equal to the second operand.
     */
    GREQ(">=", (a, b) -> Double.parseDouble(a.toString()) >= Double.parseDouble(b.toString())),
    /**
     * This operator tests if the first operand is less than the second operand.
     */
    LEQ("<=", (a, b) -> Double.parseDouble(a.toString()) <= Double.parseDouble(b.toString())),
    /**
     * This operator tests if the first operand is less than or equal to the second operand.
     */
    LESS("<", (a, b) -> Double.parseDouble(a.toString()) < Double.parseDouble(b.toString())),
    /**
     * This operator will return either a concatenated string of at least one of the operands is a string
     * or perform an arithmetic addition.
     */
    PLUS("+", (a, b) -> {
        if (Type.getByInput(a) == Type.STRING || Type.getByInput(b) == Type.STRING) {
            return "'" + (a.toString() + (b.toString())).replaceAll("'", "") + "'";
        } else {
            return Double.parseDouble(a.toString()) + Double.parseDouble(b.toString());
        }
    }),
    /**
     * With this operator, a subtraction is performed.
     */
    MINUS("-", (a, b) -> Double.parseDouble(a.toString()) - Double.parseDouble(b.toString())),
    /**
     * With this operator, a multiplication is performed.
     */
    MUL("*", (a, b) -> Double.parseDouble(a.toString()) * Double.parseDouble(b.toString())),
    /**
     * With this operator, a division is performed.
     */
    DIV("/", (a, b) -> Double.parseDouble(a.toString()) / Double.parseDouble(b.toString())),
    /**
     * With this operator, a modulo operation is performed.
     */
    MOD("%", (a, b) -> Double.parseDouble(a.toString()) % Double.parseDouble(b.toString()));

    private final String operator;
    private final BiFunction<Object, Object, Object> function;

    /**
     * This constructor initializes the enum for binary operators.
     *
     * @param operator the operator.
     * @param function the BiFunction to be applied to two operands.
     */
    BinaryOperator(String operator, BiFunction<Object, Object, Object> function) {
        this.operator = operator;
        this.function = function;
    }

    /**
     * This method will look up a defined binary operator by an input string.
     *
     * @param input the string representation of the binary operator.
     * @return the enum for the searched operator.
     */
    public static BinaryOperator getByLiteral(Object input) {
        BinaryOperator result = Arrays.stream(BinaryOperator.values()).filter(o -> o.operator.equals(input.toString())).findAny().orElseGet(null);
        if (result != null) {
            return result;
        }
        throw new IllegalArgumentException("BinaryOperator " + input.toString() + " is not available!");
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
     * This method applies the operator to two operands. It is used for the execution of code.
     *
     * @param a the first operand.
     * @param b the second operand.
     * @return the result of the performed operation.
     */
    public Object apply(Object a, Object b) {
        return function.apply(a, b);
    }

}
