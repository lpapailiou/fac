package parser.parsetree;

import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a wrapper class for function call statements.
 * It holds the identifier and a parameter list.
 */
public class FunctionCallStatement extends Component {


    private String identifier;
    private List<Component> argumentList = new ArrayList<>();

    /**
     * This constructor initializes a wrapper for a function call without arguments.
     *
     * @param identifier the identifier of the called function.
     */
    public FunctionCallStatement(Object identifier) {
        this.identifier = identifier.toString();
    }

    /**
     * This constructor initializes a wrapper for a function call with arguments.
     *
     * @param identifier the identifier of the called function.
     * @param args       the arguments of the function call.
     */
    public FunctionCallStatement(Object identifier, Object args) {
        this(identifier);
        if (args != null) {
            this.argumentList = ((Argument) args).getStatements();
        }
    }

    /**
     * Returns the identifier of the called function.
     *
     * @return the identifier of the called function.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Returns the count of arguments of the function call.
     *
     * @return the argument count.
     */
    public int getArgumentCount() {
        return argumentList.size();
    }

    /**
     * Returns the arguments of the function call as list.
     *
     * @return the argument list.
     */
    public List<Component> getArgumentList() {
        return argumentList;
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
        String out = identifier + "(";
        for (int i = 0; i < argumentList.size(); i++) {
            out += argumentList.get(i);
            if (i != argumentList.size() - 1) {
                out += ", ";
            }
        }
        out += ");\n";

        return out;
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
