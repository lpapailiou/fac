package parser.parsetree;

import java.util.Arrays;
import java.util.function.Function;

public enum UnOp {

    MINUS("-", (e) -> - Double.parseDouble(e.toString())),
    EXCL("!", (e) -> !Boolean.parseBoolean(e.toString())),
    NONE("", (e) -> {
        Type type = Type.getTypeForValue(e);
        if (type == Type.BOOLEAN) {
            return Boolean.parseBoolean(e.toString());
        }
        return e;
    })
    ;

    private String operator;
    private Function<Object, Object> function;

    UnOp(String operator, Function<Object, Object> function) {
        this.operator = operator;
        this.function = function;
    }

    public String getOperator() {
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
