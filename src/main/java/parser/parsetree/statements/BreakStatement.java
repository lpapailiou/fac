package parser.parsetree.statements;


import parser.parsetree.Component;
import parser.parsetree.Keyword;

/**
 * This is a wrapper for a break statement. It is basically used as marker.
 */
public class BreakStatement extends Component {

    /**
     * This constructor is used to pass the location of the code fragment.
     *
     * @param left  the start index.
     * @param right the end index.
     */
    public BreakStatement(int left, int right) {
        super(left, right);
    }

    /**
     * The toString method provides a pretty-printable String
     * of this parse tree component.
     * In this case the hard coded word 'break;'.
     *
     * @return the pretty-printed code.
     */
    @Override
    public String toString() {
        return "break;\n";
    }

    /**
     * This method returns the break statement as representation of the parse tree.
     *
     * @return a snipped of the parse tree.
     */
    @Override
    public String getParseTree() {
        StringBuilder out = getStringBuilder(this);
        appendKeyword(out, Keyword.BREAK, 1);
        appendKeyword(out, Keyword.STOP, 1);
        return out.toString();
    }


}