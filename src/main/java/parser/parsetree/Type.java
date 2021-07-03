package parser.parsetree;

import exceptions.TypeMismatchException;

import java.util.Arrays;

/**
 * this enum defines all available data types of this toy language.
 * There is a type each for string, numeric and boolean values. No null values are allowed.
 * Additionally, a type for variables is provided. It will be used as marker during code validation.
 */
public enum Type {

    /**
     * This type is the representation of string values. It does not apply completely to Java strings.
     */
    STRING("string", "'[a-z0-9_\\,\\.\\(\\)\\;\\:\\/\\+\\-\\*\\/ \\s\\t\\f\\r\\n]*'", "''"),
    /**
     * This type is the representation of numeric values. It supports integer-like numbers as well as floating-point
     * numbers. Values may be positive or negative.
     */
    NUMERIC("number", "-?[0-9]\\d*(\\.\\d+)?", "0"),
    /**
     * This type is the representation of boolean values.
     */
    BOOLEAN("boolean", "true|false", "false"),
    /**
     * This type is a marker for variables, whose values will be evaluated separately.
     */
    VARIABLE("var", "[a-z_]+([0-9])*", null);

    private final String identifier;
    private final String pattern;
    private final String defaultValue;

    /**
     * This constructor initializes the enum for data types.
     *
     * @param identifier   the string marker of the data type.
     * @param pattern      the regex pattern to test for.
     * @param defaultValue the default value for the data type.
     */
    Type(String identifier, String pattern, String defaultValue) {
        this.identifier = identifier;
        this.pattern = pattern;
        this.defaultValue = defaultValue;
    }

    /**
     * This method will look up a defined data type by an input string of the data type identifier.
     *
     * @param input the data type identifier as string to look for.
     * @return the enum for the searched data type.
     */
    public static Type getByLiteral(Object input) {
        Type result = Arrays.stream(Type.values()).filter(t -> t.identifier.equals(input.toString())).findAny().orElseGet(null);
        if (result != null) {
            return result;
        }
        throw new IllegalArgumentException("Type " + input.toString() + " is not available!");
    }

    /**
     * This method tests an input value with the regex pattern of the defined data types. If the pattern matches,
     * the type is returned as enum.
     *
     * @param obj the value to be tested for a matching type.
     * @return the data type as enum.
     */
    public static Type getByInput(Object obj) {
        Type[] types = Type.values();
        for (Type type : types) {
            if (type.accepts(obj)) {
                return type;
            }
        }
        String result = obj.toString();
        if (result.contains("Infinity")) {
            throw new ArithmeticException("Invalid arithmetic operation! Result evaluated to Infinity!");
        } else if (result.contains("NaN")) {
            throw new ArithmeticException("Invalid arithmetic operation! Result is not a number!");
        }
        throw new TypeMismatchException("Unknown type for <" + obj + ">!");
    }

    /**
     * Returns the identifier for this data type.
     *
     * @return the identifier.
     */
    public String getLiteral() {
        return identifier;
    }

    /**
     * Returns the default value for this data type.
     *
     * @return the default value.
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * This method tests an input value with the regex pattern of this enum.
     *
     * @param obj the value to be tested for a matching type.
     * @return true if the test succeeded.
     */
    private boolean accepts(Object obj) {
        try {
            return obj.toString().matches(pattern);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return false;
    }
}
