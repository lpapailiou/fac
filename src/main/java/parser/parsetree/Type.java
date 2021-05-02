package parser.parsetree;

import java.util.Arrays;

public enum Type {

    STRING("string", "'[a-z0-9_\\,\\.\\(\\)\\;\\:\\/\\+\\-\\*\\/ \\s\\t\\f\\r\\n]*'"),
    NUMERIC("number", "-?[0-9]\\d*(\\.\\d+)?"),
    BOOLEAN("boolean", "true|false");

    private final String description;
    private final String pattern;

    Type(String description, String pattern) {
        this.description = description;
        this.pattern = pattern;
    }

    public String getDescription() {
        return description;
    }

    public static Type getName(Object type) {
        Type result =  Arrays.stream(Type.values()).filter(t -> t.description.equals(type.toString())).findAny().orElseGet(null);
        if (result != null) {
            return result;
        }
        throw new IllegalArgumentException("Type " + type.toString() + " is not available!");
    }

    public boolean isOf(Object obj) {
        return obj.toString().matches(pattern);
    }
}
