package parser.parsetree;

import java.util.Arrays;
import java.util.function.BiFunction;

public enum Operator {

    EQUAL("=", (s1, s2) -> s2),     // TODO
    AND("&&", (s1, s2) -> s2),
    OR("||", (s1, s2) -> s2),
    EQ("==", (s1, s2) -> s2),
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

    public static Operator getName(Object op) {
        Operator result =  Arrays.stream(Operator.values()).filter(o -> o.operator.equals(op.toString())).findAny().orElseGet(null);
        if (result != null) {
            return result;
        }
        throw new IllegalArgumentException("Operator " + op.toString() + " is not available!");
    }

}
