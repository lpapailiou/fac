package parser.parsetree;

import java.util.Arrays;
import java.util.function.Function;

/**
 * This enum holds all available operators for unary expressions.
 * As expressions can be executed, every operator will provide its own execution method.
 */
public enum UnOp {

    MINUS("-", (e) -> - Double.parseDouble(e.toString())),
    INC("++", (e) -> Double.parseDouble(e.toString()) + 1),
    DEC("--", (e) -> Double.parseDouble(e.toString()) - 1),
    EXCL("!", (e) -> !Boolean.parseBoolean(e.toString()));

    private String operator;
    private Function<Object, Object> function;

    UnOp(String operator, Function<Object, Object> function) {
        this.operator = operator;
        this.function = function;
    }

    public String asString() {
        return operator;
    }

    public Object apply(Object e) {
        return function.apply(e);
    }

    public static UnOp getName(Object op) {
        UnOp result =  Arrays.stream(UnOp.values()).filter(o -> o.operator.equals(op.toString())).findAny().orElseGet(null);
        if (result != null) {
            return result;
        }
        throw new IllegalArgumentException("UnOp " + op.toString() + " is not available!");
    }

}
