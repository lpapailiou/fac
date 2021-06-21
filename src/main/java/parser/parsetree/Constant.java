package parser.parsetree;

import java.util.Arrays;
import java.util.List;

/**
 * This is a wrapper class for constants. A constant may be an expression or a value of any type.
 */
public class Constant extends Component {

    private Object obj;

    /**
     * This constructor will create a wrapper for one single object.
     *
     * @param o the object to wrap.
     */
    Constant(Object o) {
        this.obj = o;
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
     * The toString method provides a pretty-printable String
     * of this parse tree component.
     * It is generated by the contents of this instance and may not be equal to the original code.
     *
     * @return the pretty-printed code.
     */
    @Override
    public String toString() {
        return obj.toString();
    }

    /**
     * This method returns this constant as representation of the parse tree.
     *
     * @return a snipped of the parse tree.
     */
    @Override
    public String getParseTree() {
        String out = this.getClass().getName();
        out = "+ " + out.substring(out.lastIndexOf(".") + 1) + "\n";
        if (obj instanceof Component) {
            List<String> components = Arrays.asList(((Component) obj).getParseTree().split("\n"));
            for (String str : components) {
                out += "\t " + str + "\n";
            }
        } else {
            out += "\t+ " + Type.getTypeForValue(obj) + "\n";
        }
        return out;
    }

}
