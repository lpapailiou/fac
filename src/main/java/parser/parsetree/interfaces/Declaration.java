package parser.parsetree.interfaces;

import parser.parsetree.Type;

/**
 * This interface is designed to mark declaring statements as such.
 * As declarations are possible for variables but also function parameters, the
 * methods to implement will facilitate access to all types of declarations.
 */
public interface Declaration {

    /**
     * Returns the data type of the declared variable.
     *
     * @return the data type
     */
    Type getType();

    /**
     * Returns the identifier of the declared variable.
     *
     * @return the identifier.
     */
    String getIdentifier();

    /**
     * Returns the value of the declared variable.
     *
     * @return the value.
     */
    Object getValue();

    /**
     * Allows to set a new value to this variable.
     *
     * @param obj the value to set.
     */
    void setValue(Object obj);

    /**
     * Allows to reset the value of this variable to the value which was originally declared.
     * This is especially useful for local variables (e.g. in while loops) and would allow
     * the code to run multiple times.
     */
    void reset();
}
