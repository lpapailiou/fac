package parser.parsetree.statements;

import parser.parsetree.Component;
import parser.parsetree.Keyword;
import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is a wrapper class for while loops.
 * It holds a condition and a list of nested statements.
 */
public class WhileStatement extends Component {

    private final Component condition;
    private List<Component> componentList = new ArrayList<>();

    /**
     * This constructor will wrap a conditional expression and a statement list to represent a while statement.
     * While the conditional statement is mandatory, nested statements are optional.
     *
     * @param condition  the conditional expression.
     * @param statements the statement list of the while body.
     * @param left       the start index.
     * @param right      the end index.
     */
    public WhileStatement(Component condition, Object statements, int left, int right) {
        super(left, right);
        this.condition = condition;
        if (statements != null) {
            this.componentList = ((NestedStatement) statements).getStatements();
        }
    }

    /**
     * Returns the conditional expression.
     *
     * @return the conditional expression.
     */
    public Component getCondition() {
        return condition;
    }

    /**
     * Returns the nested statements of the while body as statement list.
     *
     * @return the statement list.
     */
    @Override
    public List<Component> getStatements() {
        return componentList;
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
        StringBuilder out = new StringBuilder("\nwhile ");
        out.append(condition);
        out.append(" {\n");
        List<String> componentStrings = new ArrayList<>();
        for (Component st : componentList) {
            componentStrings.addAll(Arrays.asList(st.toString().split("\n")));
        }
        for (String str : componentStrings) {
            out.append(PRETTY_PRINT_INDENT).append(str).append("\n");
        }
        out.append("}\n");
        return out.toString();
    }

    /**
     * This method returns the while statement as representation of the parse tree.
     *
     * @return a snipped of the parse tree.
     */
    @Override
    public String getParseTree() {
        StringBuilder out = getStringBuilder(this);
        appendKeyword(out, Keyword.WHILE, 1);
        appendLine(out, "Condition", 1);
        appendNestedComponents(out, condition, 2);
        appendKeyword(out, Keyword.CBL, 1);
        appendNestedStatements(out, componentList, 1);
        appendKeyword(out, Keyword.CBR, 1);
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
