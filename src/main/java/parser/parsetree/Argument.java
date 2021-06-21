package parser.parsetree;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a helper class for arguments of a function call. Each instance will be held by its own wrapper
 * and point to the next argument if present - similar to a linked list.
 */
public class Argument extends Component {

    private Component arg;
    private Argument next;

    /**
     * This constructor will take one single argument, which is either the last or the only argument of a
     * function call.
     *
     * @param arg the argument to wrap.
     */
    Argument(Object arg, int left, int right) {
        super(left, right);
        this.arg = (Component) arg;
    }

    /**
     * This constructor will take one argument and wrap it. Additionally, it will take the following argument
     * and use it as pointer during code validation and execution.
     *
     * @param arg     the argument to wrap.
     * @param nextArg the next argument to point to.
     */
    Argument(Object arg, Object nextArg, int left, int right) {
        this(arg, left, right);
        next = (Argument) nextArg;
    }

    /**
     * This method will return the whole argument chain of a function definition.
     * it will be called only for the first argument of an argument chain.
     *
     * @return the list of arguments.
     */
    @Override
    public List<Component> getStatements() {
        List<Component> components = new ArrayList<>();
        components.add(arg);
        while (next != null) {
            components.add(next.arg);
            next = next.next;
        }
        return components;
    }

    /**
     * This method returns the whole argument chain as representation of the parse tree.
     *
     * @return a snipped of the parse tree.
     */
    @Override
    public String getParseTree() {
        String out = this.getClass().getName();
        if (next != null) {
            out += " " + next;
        }
        return out;
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
        String out = arg.toString();
        if (next != null) {
            out += ", " + next;
        }
        return out;
    }

}