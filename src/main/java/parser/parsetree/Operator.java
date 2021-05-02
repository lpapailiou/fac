package parser.parsetree;

import parser.util.GrammarException;

import java.util.Arrays;
import java.util.function.BiFunction;

public enum Operator {

    EQUAL("=", (s1, s2) -> s1),
    AND("&&", (s1, s2) -> (Boolean.parseBoolean((String) s1) && Boolean.parseBoolean((String) s2))),
    OR("||", (s1, s2) -> (Boolean.parseBoolean((String) s1) || Boolean.parseBoolean((String) s2))),
    EQ("==", (s1, s2) -> {
        Type type = Type.getType(s1);
        if (type == Type.STRING) {
            return s1.toString().equals(s2.toString());
        } else if (type == Type.NUMERIC) {
            return Double.parseDouble(s1.toString()) == Double.parseDouble(s2.toString());
        } else if (type == Type.BOOLEAN) {
            return Boolean.parseBoolean(s1.toString()) == Boolean.parseBoolean(s2.toString());
        }
        throw new GrammarException("unknown operation type!");
    }),
    NEQ("!=", (s1, s2) -> s2),
    GREATER(">", (s1, s2) -> s2),
    GREQ(">=", (s1, s2) -> s2),
    LEQ("<=", (s1, s2) -> s2),
    LESS("<", (s1, s2) -> s2),
    PLUSEQ("+=", (s1, s2) -> s2),
    MINEQ("-=", (s1, s2) -> s2),
    MULEQ("*=", (s1, s2) -> s2),
    DIVEQ("/=", (s1, s2) -> s2),
    PLUS("+", (s1, s2) -> s2),
    MINUS("-", (s1, s2) -> s2),
    MUL("*", (s1, s2) -> s2),
    DIV("/", (s1, s2) -> s2)
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
        return function.apply(a,b);
    }

    public Object apply(Object a) {
        return function.apply(a,a);
    }

    public static Operator getName(Object op) {
        Operator result =  Arrays.stream(Operator.values()).filter(o -> o.operator.equals(op.toString())).findAny().orElseGet(null);
        if (result != null) {
            return result;
        }
        throw new IllegalArgumentException("Operator " + op.toString() + " is not available!");
    }

}
