package parser.parsetree;

import parser.exceptions.GrammarException;
import parser.exceptions.TypeMismatchException;

import java.util.Arrays;

public enum Type {

    STRING("string", "'[a-z0-9_\\,\\.\\(\\)\\;\\:\\/\\+\\-\\*\\/ \\s\\t\\f\\r\\n]*'", "''"),
    NUMERIC("number", "-?[0-9]\\d*(\\.\\d+)?", "0"),
    BOOLEAN("boolean", "true|false", "false"),
    VARIABLE("var", "[a-z_]+([0-9])*", null),
    NONE("none", "*", null);

    private final String description;
    private final String pattern;
    private final String defaultValue;

    Type(String description, String pattern, String defaultValue) {
        this.description = description;
        this.pattern = pattern;
        this.defaultValue = defaultValue;
    }

    public String getDescription() {
        return description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public static Type getByName(Object type) {
        Type result =  Arrays.stream(Type.values()).filter(t -> t.description.equals(type.toString())).findAny().orElseGet(null);
        if (result != null) {
            return result;
        }
        throw new IllegalArgumentException("Type " + type.toString() + " is not available!");
    }

    public boolean accepts(Object obj) {
        try {
            return obj.toString().matches(pattern);
        } catch (RuntimeException e) {
            throw new GrammarException("No valid type for <" + obj + "> found!", e);
        }
    }

    public static Type getTypeForValue(Object obj) {
        Type[] types = Type.values();
        for (Type type : types) {
            if (type.accepts(obj)) {
                return type;
            }
        }
        throw new TypeMismatchException("Unknown type for <" + obj + ">!");
    }
}
