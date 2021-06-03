package parser.parsetree;


/**
 * This is a wrapper for a break statement. It is basically used as marker.
 */
public class BreakStatement extends Statement {

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


}