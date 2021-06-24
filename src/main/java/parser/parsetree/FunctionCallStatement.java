package parser.parsetree;

import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a wrapper class for function call statements.
 * It holds the identifier and a parameter list.
 */
public class FunctionCallStatement extends Component {


    private final String identifier;
    private List<Component> argumentList = new ArrayList<>();
    private boolean isStatement;

    /**
     * This constructor initializes a wrapper for a function call without arguments.
     *
     * @param identifier the identifier of the called function.
     * @param left       the start index.
     * @param right      the end index.
     */
    public FunctionCallStatement(Object identifier, int left, int right) {
        super(left, right);
        this.identifier = identifier.toString();
    }

    /**
     * This constructor initializes a wrapper for a function call with arguments.
     *
     * @param identifier the identifier of the called function.
     * @param args       the arguments of the function call.
     * @param left       the start index.
     * @param right      the end index.
     */
    public FunctionCallStatement(Object identifier, Object args, int left, int right) {
        this(identifier, left, right);
        if (args != null) {
            if (args instanceof List) {
                this.argumentList = (List<Component>) args;
            } else {
                this.argumentList = ((Argument) args).getStatements();
            }
        }
    }

    /**
     * This constructor replaces an already initialized function call statement. Its purpose is to
     * indicate that this function call is actually a statement and not a part of an expression
     * by explicitly including a semicolon at the end.
     *
     * @param functionCall the function call statement to replace.
     * @param right        the end index.
     */
    public FunctionCallStatement(FunctionCallStatement functionCall, int right) {
        this(functionCall.identifier, functionCall.argumentList, functionCall.getLocation()[0], right);
        this.isStatement = true;
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
     * Returns true if this function call is an independent statement.
     *
     * @return true if this function call is an independent statement.
     */
    public boolean isStatement() {
        return isStatement;
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
        StringBuilder out = new StringBuilder(identifier + "(");
        for (int i = 0; i < argumentList.size(); i++) {
            Component arg = argumentList.get(i);
            out.append(arg);
            if (i != argumentList.size() - 1) {
                out.append(", ");
            }
        }
        if (isStatement) {
            out.append(");\n");
        } else {
            out.append(")");
        }
        return out.toString();
    }

    /**
     * This method returns the function call statement as representation of the parse tree.
     *
     * @return a snipped of the parse tree.
     */
    @Override
    public String getParseTree() {
        StringBuilder out = getStringBuilder(this);
        appendIdentifier(out, identifier, 1);

        if (argumentList.size() == 1 && argumentList.get(0) instanceof BinaryCondition) {
            appendNestedComponents(out, argumentList.get(0), 1);
        } else {
            appendKeyword(out, Keyword.BL, 1);
            int offset = 0;
            for (int i = 0; i < argumentList.size(); i++) {
                Component arg = argumentList.get(i);
                appendLine(out, "Argument", 1 + offset);
                appendLine(out, "Expression", 2 + offset);
                appendLine(out, evaluateExpression(arg), 3 + offset);
                appendNestedComponents(out, arg, 4 + offset);
                if (i != argumentList.size() - 1) {
                    appendKeyword(out, Keyword.COMMA, 2 + offset);
                }
                offset++;
            }
            appendKeyword(out, Keyword.BR, 1);
        }
        if (isStatement) {
            appendKeyword(out, Keyword.STOP, 1);
        }
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
