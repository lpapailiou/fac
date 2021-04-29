package parser.util;

import java.util.function.BiFunction;

public enum ASSGN {
    EQUAL((s1, s2) -> s2) {
        @Override
        public String getOperator() {
            return "=";
        }
    },
    PLUSEQ((s1, s2) -> s2) {
        @Override
        public String getOperator() {
            return "+=";
        }
    },
    MINEQ((s1, s2) -> s2) {
        @Override
        public String getOperator() {
            return "-=";
        }
    },
    MULEQ((s1, s2) -> s2) {
        @Override
        public String getOperator() {
            return "*=";
        }
    },
    DIVEQ((s1, s2) -> s2) {
        @Override
        public String getOperator() {
            return "/=";
        }
    }
    ;

    private BiFunction<Object, Object, Object> function;

    ASSGN(BiFunction<Object, Object, Object> function) {
        this.function = function;
    }



    public String getOperator() {
        return "NONE";
    }

}
