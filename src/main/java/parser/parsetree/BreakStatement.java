package parser.parsetree;


/**
 * This is a wrapper for a break statement. It is basically used as marker.
 */
public class BreakStatement extends Component {

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

    @Override
    public String getParseTree() {
        String out = this.getClass().getName();
        out = "+ " + out.substring(out.lastIndexOf(".") + 1) + "\n";
        return out;
    }


}