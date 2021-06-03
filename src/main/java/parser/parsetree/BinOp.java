package parser.parsetree;

import java.util.Arrays;
import java.util.function.BiFunction;

/**
 * This enum holds all available operators for binary expressions.
 * As expressions can be executed, every operator will provide its own execution method.
 */
public enum BinOp {

    EQUAL("=", (a, b) -> b),
    PLUSEQ("+=", (a, b) -> {
        if (Type.getTypeForValue(a) == Type.STRING || Type.getTypeForValue(b) == Type.STRING) {
            return "'" + (a.toString() + (b.toString())).replaceAll("'", "") + "'";
        } else {
            return Double.parseDouble(a.toString()) + Double.parseDouble(b.toString());
        }
    }),
    MINEQ("-=", (a, b) -> Double.parseDouble(a.toString()) - Double.parseDouble(b.toString())),
    MULEQ("*=", (a, b) -> Double.parseDouble(a.toString()) * Double.parseDouble(b.toString())),
    DIVEQ("/=", (a, b) -> Double.parseDouble(a.toString()) / Double.parseDouble(b.toString())),
    MODEQ("%=", (a, b) -> Double.parseDouble(a.toString()) % Double.parseDouble(b.toString())),
    AND("&&", (a, b) -> (Boolean.parseBoolean(a.toString()) && Boolean.parseBoolean(b.toString()))),
    OR("||", (a, b) -> (Boolean.parseBoolean(a.toString()) || Boolean.parseBoolean(b.toString()))),
    EQ("==", (a, b) -> {
        Type type = Type.getTypeForValue(a);
        if (type == Type.BOOLEAN) {
            return Boolean.parseBoolean(a.toString()) == Boolean.parseBoolean(a.toString());
        } else if (type == Type.NUMERIC) {
            return Double.parseDouble(a.toString()) == Double.parseDouble(b.toString());
        } else {
            return a.toString().equals(b.toString());
        }
    }),
    NEQ("!=", (a, b) -> {
        Type type = Type.getTypeForValue(a);
        if (type == Type.BOOLEAN) {
            return Boolean.parseBoolean(a.toString()) != Boolean.parseBoolean(a.toString());
        } else if (type == Type.NUMERIC) {
            return Double.parseDouble(a.toString()) != Double.parseDouble(b.toString());
        } else {
            return !a.toString().equals(b.toString());
        }
    }),
    GREATER(">", (a, b) -> Double.parseDouble(a.toString()) > Double.parseDouble(b.toString())),
    GREQ(">=", (a, b) -> Double.parseDouble(a.toString()) >= Double.parseDouble(b.toString())),
    LEQ("<=", (a, b) -> Double.parseDouble(a.toString()) <= Double.parseDouble(b.toString())),
    LESS("<", (a, b) -> Double.parseDouble(a.toString()) < Double.parseDouble(b.toString())),
    PLUS("+", (a, b) -> {
        if (Type.getTypeForValue(a) == Type.STRING || Type.getTypeForValue(b) == Type.STRING) {
            return "'" + (a.toString() + (b.toString())).replaceAll("'", "") + "'";
        } else {
            return Double.parseDouble(a.toString()) + Double.parseDouble(b.toString());
        }
    }),
    MINUS("-", (a, b) -> Double.parseDouble(a.toString()) - Double.parseDouble(b.toString())),
    MUL("*", (a, b) -> Double.parseDouble(a.toString()) * Double.parseDouble(b.toString())),
    DIV("/", (a, b) -> Double.parseDouble(a.toString()) / Double.parseDouble(b.toString())),
    MOD("%", (a, b) -> Double.parseDouble(a.toString()) % Double.parseDouble(b.toString()));

    private String operator;
    private BiFunction<Object, Object, Object> function;

    BinOp(String operator, BiFunction<Object, Object, Object> function) {
        this.operator = operator;
        this.function = function;
    }

    public String asString() {
        return operator;
    }

    public Object apply(Object a, Object b) {
        return function.apply(a, b);
    }

    public static BinOp getName(Object op) {
        BinOp result =  Arrays.stream(BinOp.values()).filter(o -> o.operator.equals(op.toString())).findAny().orElseGet(null);
        if (result != null) {
            return result;
        }
        throw new IllegalArgumentException("BinOp " + op.toString() + " is not available!");
    }

}
