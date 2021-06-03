package parser.parsetree;

import parser.parsetree.interfaces.Declaration;
import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a wrapper class for variable declarations.
 * It holds the data type of the variable, the identifier and a value.
 * During initialization, the initialized value will be stored, which will allow to run parts of the code -
 * or the whole code - multiple times.
 */
public class VariableDeclaration extends Statement implements Declaration {

    private Type type;
    private Object identifier;
    private Object value;
    private Object initValue;

    public VariableDeclaration(Object type, Object identifier, Object value) {
        this.type = Type.getByName(type);
        this.identifier = identifier;
        this.value = (value == null) ? Type.getByName(type).getDefaultValue() : value;
        this.initValue = value;
    }

    public void reset() {
        value = initValue;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getIdentifier() {
        return identifier.toString();
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object obj) {
        this.value = obj;
    }

    /**
     * The toString method provides a pretty-printable String
     * of this parse tree component.
     * It is generated by the contents of this instance and may not be equal to the original code.
     * @return the pretty-printed code.
     */
    @Override
    public String toString() {
        String out =  type.getDescription() + " " + identifier + " = " + value;
        if (value instanceof FunctionCallStatement) {
            out = out.substring(0, out.length()-2);
        }
        return  out + ";\n";
    }

    /**
     * This method accepts a visitor. The visitor will then have access to this instance
     * for code validation and execution.
     * @param visitor the visitor to accept.
     */
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
