package parser.parsetree.statements;

import parser.parsetree.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a helper class for arguments of a function call. Each argument will be held by its own wrapper
 * and point to the next argument if present - similar to a linked list.
 * Instances of this class are used only during construction of the parse tree.
 */
public class Argument extends Component {

    private final Component arg;
    private Argument next;

    /**
     * This constructor will take one argument of a function call to wrap it.
     *
     * @param arg   one single argument of a function call.
     * @param left  the start index.
     * @param right the end index.
     */
    public Argument(Object arg, int left, int right) {
        super(left, right);
        this.arg = (Component) arg;
    }

    /**
     * This constructor will take one argument and wrap it. Additionally, it will take the following argument
     * and use it as pointer during code validation and execution.
     *
     * @param arg     one single argument of a function call.
     * @param nextArg the adjacent argument wrapper.
     * @param left    the start index.
     * @param right   the end index.
     */
    public Argument(Object arg, Object nextArg, int left, int right) {
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
     * This method returns null as this instance will not be part of the parse tree.
     *
     * @return a snipped of the parse tree.
     */
    @Override
    public String getParseTree() {
        return null;
    }

}