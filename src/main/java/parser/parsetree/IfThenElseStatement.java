package parser.parsetree;

import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is a wrapper class for if-then-else statements.
 * It holds one condition and two statement lists, one each for the if and the else body.
 */
public class IfThenElseStatement extends IfThenStatement {

    private final List<Component> componentListElse = new ArrayList<>();

    /**
     * This constructor will wrap the contents of an if-then-else statement. It will initialize the
     * if-then-statement as superclass first, then add the else statement list to this instance.
     * While the conditional statement is mandatory, nested statements are optional.
     *
     * @param condition      the conditional expression.
     * @param statementList1 the if-then statement list.
     * @param statementList2 the else statement list.
     * @param left           the start index.
     * @param right          the end index.
     */
    IfThenElseStatement(Object condition, Object statementList1, Object statementList2, int left, int right) {
        super(condition, statementList1, left, right);
        if (statementList2 != null) {
            this.componentListElse.addAll(((Component) statementList2).getStatements());
        }
    }

    /**
     * Returns the statements of the if-then body.
     *
     * @return the if-then statement list.
     */
    public List<Component> getIfStatements() {
        return super.getStatements();
    }

    /**
     * Returns the statements of the else body.
     *
     * @return the else statement list.
     */
    public List<Component> getElseStatements() {
        List<Component> components = new ArrayList<>();
        if (!componentListElse.isEmpty()) {
            components.addAll(componentListElse);
        }
        return components;
    }

    /**
     * Returns the combined statements of the if-then and the else body.
     *
     * @return the complete statement list.
     */
    @Override
    public List<Component> getStatements() {
        List<Component> components = super.getStatements();
        if (!componentListElse.isEmpty()) {
            components.addAll(componentListElse);
        }
        return components;
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
        StringBuilder out = new StringBuilder("\nif ");
        out.append(condition);
        out.append(" {\n");
        List<String> componentStrings1 = new ArrayList<>();
        for (Component st : componentListIf) {
            componentStrings1.addAll(Arrays.asList(st.toString().split("\n")));
        }
        for (String str : componentStrings1) {
            out.append(PRETTY_PRINT_INDENT).append(str).append("\n");
        }
        out.append("} else { \n");
        List<String> componentStrings2 = new ArrayList<>();
        for (Component st : componentListElse) {
            componentStrings2.addAll(Arrays.asList(st.toString().split("\n")));
        }
        for (String str : componentStrings2) {
            out.append(PRETTY_PRINT_INDENT).append(str).append("\n");
        }
        out.append("}\n\n");
        return out.toString();
    }

    /**
     * This method returns the if-then-else statement as representation of the parse tree.
     *
     * @return a snipped of the parse tree.
     */
    @Override
    public String getParseTree() {
        StringBuilder out = getStringBuilder(this);
        appendKeyword(out, Keyword.IF, 1);
        appendNestedComponents(out, condition, 1);
        appendKeyword(out, Keyword.CBL, 1);
        for (Component c : componentListIf) {
            appendNestedComponents(out, c, 2);
        }
        appendKeyword(out, Keyword.CBR, 1);
        appendKeyword(out, Keyword.ELSE, 1);
        appendKeyword(out, Keyword.CBL, 1);
        for (Component c : componentListElse) {
            appendNestedComponents(out, c, 2);
        }
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
