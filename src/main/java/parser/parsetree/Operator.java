package parser.parsetree;

import parser.exceptions.TypeMismatchException;

import java.util.Arrays;
import java.util.function.BiFunction;

public enum Operator {

    EQUAL("=", (s1, s2) -> s2),
    AND("&&", (s1, s2) -> ((Boolean) s1 && ((Boolean) s2))),
    OR("||", (s1, s2) -> ((Boolean) s1 || ((Boolean) s2))),
    EQ("==", (s1, s2) -> {
        if (Type.getTypeForValue(s1) == Type.STRING) {
            return s1.toString().equals(s2.toString());
        } else {
            return s1 == s2;
        }
    }),
    NEQ("!=", (s1, s2) -> {
        if (Type.getTypeForValue(s1) == Type.STRING) {
            return !s1.toString().equals(s2.toString());
        } else {
            return s1 != s2;
        }
    }),
    GREATER(">", (s1, s2) -> Double.parseDouble(s1.toString()) > Double.parseDouble(s2.toString())),
    GREQ(">=", (s1, s2) -> Double.parseDouble(s1.toString()) >= Double.parseDouble(s2.toString())),
    LEQ("<=", (s1, s2) -> Double.parseDouble(s1.toString()) <= Double.parseDouble(s2.toString())),
    LESS("<", (s1, s2) -> Double.parseDouble(s1.toString()) < Double.parseDouble(s2.toString())),
    PLUSEQ("+=", (s1, s2) -> {
        if (Type.getTypeForValue(s1) == Type.STRING) {
            return "'" + (s1.toString() + (s2.toString())).replaceAll("'", "") + "'";
        } else {
            return Double.parseDouble(s1.toString()) + Double.parseDouble(s2.toString());
        }
    }),
    MINEQ("-=", (s1, s2) -> Double.parseDouble(s1.toString()) - Double.parseDouble(s2.toString())),
    MULEQ("*=", (s1, s2) -> Double.parseDouble(s1.toString()) * Double.parseDouble(s2.toString())),
    DIVEQ("/=", (s1, s2) -> Double.parseDouble(s1.toString()) / Double.parseDouble(s2.toString())),
    PLUS("+", (s1, s2) -> {
        if (Type.getTypeForValue(s1) == Type.STRING) {
            return "'" + (s1.toString() + (s2.toString())).replaceAll("'", "") + "'";
        } else {
            return Double.parseDouble(s1.toString()) + Double.parseDouble(s2.toString());
        }
    }),
    MINUS("-", (s1, s2) -> Double.parseDouble(s1.toString()) - Double.parseDouble(s2.toString())),
    MUL("*", (s1, s2) -> Double.parseDouble(s1.toString()) * Double.parseDouble(s2.toString())),
    DIV("/", (s1, s2) -> Double.parseDouble(s1.toString()) / Double.parseDouble(s2.toString())),
    NONE("", null)
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
        if (Type.getTypeForValue(a) == Type.BOOLEAN) {
            a = Boolean.parseBoolean(a.toString());
            b = Boolean.parseBoolean(b.toString());
        }
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
