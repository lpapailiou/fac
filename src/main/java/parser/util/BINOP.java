package parser.util;

import java.util.function.BiFunction;

public enum BINOP {
    CONCAT((s1, s2) -> s1.toString() + s2.toString()) {
        @Override
        public String getOperator() {
            return "+";
        }
    };

    private BiFunction<Object, Object, Object> function;

    BINOP(BiFunction<Object, Object, Object> function) {
        this.function = function;
    }



    public String getOperator() {
        return "NONE";
    }

    public Object apply(String s1, String s2) {
        return function.apply(s1, s2);
    }




}
