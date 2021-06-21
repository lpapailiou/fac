package parser.parsetree;

import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is a wrapper class for while loops.
 * It holds a condition and a list of nested statements.
 */
public class WhileStatement extends Component {

    private Object condition;
    private List<Component> componentList = new ArrayList<>();

    /**
     * This constructor will wrap a conditional expression and a statement list to represent a while statement.
     * While the conditional statement is mandatory, nested statements are optional.
     *
     * @param condition  the conditional expression.
     * @param statements the statement list of the while body.
     */
    public WhileStatement(Object condition, Object statements) {
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
    public Object getCondition() {
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
        String out = "\nwhile ";
        boolean isCond = condition instanceof ConditionalExpression;
        if (!isCond) {
            out += "(";
        }
        out += condition;
        if (!isCond) {
            out += ")";
        }
        out += " {\n";

        List<String> componentStrings = new ArrayList<>();
        for (Component st : componentList) {
            componentStrings.addAll(Arrays.asList(st.toString().split("\n")));
        }
        for (String str : componentStrings) {
            out += "\t" + str + "\n";
        }
        out += "}\n";

        return out;
    }

    /**
     * This method returns the while statement as representation of the parse tree.
     *
     * @return a snipped of the parse tree.
     */
    @Override
    public String getParseTree() {
        String out = this.getClass().getName();
        out = "+ " + out.substring(out.lastIndexOf(".") + 1) + "\n";
        out += "\t+ " + "WHILE" + "\n";
        out += "\t\t+ " + "CONDITION" + "\n";
        List<String> conditionComponents = Arrays.asList((((Component) condition)).getParseTree().split("\n"));
        for (String str : conditionComponents) {
            out += "\t\t\t " + str + "\n";
        }
        if (!componentList.isEmpty()) {
            out += "\t\t+ " + "BODY" + "\n";
            for (Component c : componentList) {
                List<String> components = Arrays.asList((c).getParseTree().split("\n"));
                for (String str : components) {
                    out += "\t\t\t " + str + "\n";
                }
            }
        }
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
