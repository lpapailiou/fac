package parser.parsetree;

/**
 * This is a wrapper class for constants. A constant may be an expression or a value of any type.
 */
public class ValueWrapper extends Component {

    private final Object obj;
    private boolean hasBrackets;

    /**
     * This constructor will create a wrapper for one single object.
     *
     * @param o     the object to wrap.
     * @param left  the start index.
     * @param right the end index.
     */
    ValueWrapper(Object o, int left, int right) {
        super(left, right);
        this.obj = o;
    }

    /**
     * This constructor will create a wrapper for one single object.
     * With the additional <code>hasBrackets</code>, an indicator can be passed if
     * the object is surrounded by round brackets.
     *
     * @param o           the object to wrap.
     * @param hasBrackets indicator for surrounding brackets.
     * @param left        the start index.
     * @param right       the end index.
     */
    ValueWrapper(Object o, boolean hasBrackets, int left, int right) {
        this(o, left, right);
        this.hasBrackets = hasBrackets;
    }

    /**
     * Returns the wrapped object.
     *
     * @return the wrapped object.
     */
    public Object getValue() {
        return obj;
    }

    /**
     * Returns true if has surrounding brackets.
     *
     * @return true if has surrounding brackets.
     */
    public boolean hasBrackets() {
        return hasBrackets;
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
        String out = obj.toString();
        if (hasBrackets) {
            return "(" + out + ")";
        }
        return out;
    }

    /**
     * This method returns this constant as representation of the parse tree.
     *
     * @return a snipped of the parse tree.
     */
    @Override
    public String getParseTree() {
        StringBuilder out = getStringBuilder(this);
        if (hasBrackets) {
            appendKeyword(out, Keyword.BL, 1);
        }
        appendNestedComponents(out, obj, 1);
        if (hasBrackets) {
            appendKeyword(out, Keyword.BR, 1);
        }
        return out.toString();
    }

}
