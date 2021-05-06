package parser.parsetree;

import java.util.Arrays;
import java.util.function.BiFunction;

public enum Operator {

    EQUAL("=", (a, b) -> b),
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
    NONE("", (a, b) -> {
        Type type = Type.getTypeForValue(a);
        if (type == Type.BOOLEAN) {
            return Boolean.parseBoolean(a.toString());
        }
        return a;
    })
    ;


    private String operator;
    private BiFunction<Object, Object, Object> function;

    Operator(String operator, BiFunction<Object, Object, Object> function) {
        this.operator = operator;
        this.function = function;
    }

    public String getOperator() {
        return operator;
    }

    public Object apply(Object a, Object b) {
        return function.apply(a, b);
    }

    public static Operator getName(Object op) {
        Operator result =  Arrays.stream(Operator.values()).filter(o -> o.operator.equals(op.toString())).findAny().orElseGet(null);
        if (result != null) {
            return result;
        }
        throw new IllegalArgumentException("Operator " + op.toString() + " is not available!");
    }

}
