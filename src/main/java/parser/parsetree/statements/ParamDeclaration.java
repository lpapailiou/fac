package parser.parsetree.statements;

import parser.parsetree.Component;
import parser.parsetree.Type;
import parser.parsetree.interfaces.Declaration;
import parser.parsetree.interfaces.Visitor;

/**
 * This is a wrapper class for parameter declarations of a function body.
 * It is constructed like a linked list. The leftmost declaration points to its neighbor to the right, etc.
 */
public class ParamDeclaration extends Component implements Declaration {

    private final Type type;
    private final String identifier;
    private final Object initValue;
    private Object value;
    private ParamDeclaration next;

    /**
     * This constructor will create a wrapper for a parameter declaration.
     * The value of the declared parameter will be initialized to the default value of the according data type.
     *
     * @param type       the data type of the declared parameter.
     * @param identifier the identifier of the parameter.
     * @param left       the start index.
     * @param right      the end index.
     */
    public ParamDeclaration(Object type, String identifier, int left, int right) {
        super(left, right);
        this.type = Type.getByLiteral(type);
        this.identifier = identifier;
        value = Type.getByLiteral(type).getDefaultValue();
        initValue = value;
    }

    /**
     * This constructor will create a wrapper for a parameter declaration.
     * The value of the declared parameter will be initialized to the default value of the according data type.
     * Additionally, the following parameter will be linked to this parameter declaration.
     *
     * @param type       the data type of the declared parameter.
     * @param identifier the identifier of the parameter.
     * @param nextParam  the following parameter to be set as pointer.
     */
    public ParamDeclaration(Object type, String identifier, Object nextParam, int left, int right) {
        this(type, identifier, left, right);
        next = (ParamDeclaration) nextParam;
    }

    /**
     * Returns the data type of this parameter declaration.
     *
     * @return the data type.
     */
    @Override
    public Type getType() {
        return type;
    }

    /**
     * Returns the identifier of this parameter declaration.
     *
     * @return the identifier.
     */
    @Override
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Returns the value of this parameter.
     *
     * @return the value.
     */
    @Override
    public Object getValue() {
        return value;
    }

    /**
     * Allows to set a new value to this parameter.
     *
     * @param obj the value to set.
     */
    @Override
    public void setValue(Object obj) {
        this.value = obj;
    }

    /**
     * Returns the instance of the following parameter declaration.
     *
     * @return the following parameter.
     */
    public ParamDeclaration getNext() {
        return next;
    }

    /**
     * Resets the value of this parameter to the initially initialized default value.
     */
    public void reset() {
        value = initValue;
    }

    /**
     * The toString method provides a pretty-printable String
     * of this parse tree component.
     * It is generated by the contents of this instance and may not be equal to the original code.
     *
     * @return the pretty-printed code.
     */
    @Override
    public String toString() {
        return type.getLiteral() + " " + identifier;
    }

    /**
     * This method returns the parameter declaration as representation of the parse tree.
     *
     * @return a snipped of the parse tree.
     */
    @Override
    public String getParseTree() {
        StringBuilder out = getStringBuilder(this);
        appendType(out, type, 1);
        appendIdentifier(out, identifier, 1);
        return out.toString();
    }

    /**
     * This method accepts a visitor. The visitor will then have access to this instance
     * for code validation and execution.
     *
     * @param visitor the visitor to accept.
     */
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
