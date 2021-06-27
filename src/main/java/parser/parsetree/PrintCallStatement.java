package parser.parsetree;

import parser.parsetree.interfaces.Visitor;

/**
 * This is a wrapper class for print call statements.
 * An instance holds a value, which will be a chain of arguments.
 */
public class PrintCallStatement extends Component {


    private Object value;

    /**
     * This constructor will create an empty wrapper for a print statement.
     *
     * @param left  the start index.
     * @param right the end index.
     */
    public PrintCallStatement(int left, int right) {
        super(left, right);
    }

    /**
     * This constructor will wrap an object to be printed to the console.
     * This object can be a 'primitive', an expression or a statement, but cannot be multiple statements.
     *
     * @param value the value to print.
     * @param left  the start index.
     * @param right the end index.
     */
    public PrintCallStatement(Object value, int left, int right) {
        this(left, right);
        this.value = value;
    }

    /**
     * Returns the value to be printed to the console.
     *
     * @return the value to print.
     */
    public Object getValue() {
        return value;
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
        if (value != null) {
            if (value instanceof ValueWrapper && ((ValueWrapper) value).hasBrackets()) {
                return "print" + value.toString() + ";\n";
            }
            return "print(" + value.toString() + ");\n";
        }
        return "print();\n";
    }

    /**
     * This method returns the print call statement as representation of the parse tree.
     *
     * @return a snipped of the parse tree.
     */
    @Override
    public String getParseTree() {
        StringBuilder out = getStringBuilder(this);
        appendKeyword(out, Keyword.PRINT, 1);
        if (value == null) {
            appendKeyword(out, Keyword.BL, 1);
            appendKeyword(out, Keyword.BR, 1);
        } else if (value instanceof BinaryCondition) {
            appendNestedComponents(out, value, 1);
        } else {
            appendKeyword(out, Keyword.BL, 1);
            evaluateExpression(out, value, 1);
            appendKeyword(out, Keyword.BR, 1);
        }
        appendKeyword(out, Keyword.STOP, 1);
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
