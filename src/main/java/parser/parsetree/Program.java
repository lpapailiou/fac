package parser.parsetree;

import parser.parsetree.interfaces.Traversable;
import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is a wrapper class for the program instance. This is the root of the parse tree.
 * It will hold all first-level components of the program as statement list. Potentially,
 * every statement can have nested statements.
 */
public class Program implements Traversable {

    private List<Component> componentList = new ArrayList<>();

    /**
     * This constructor initializes the wrapper for the root of the generated parse tree.
     * It contains the list of all top-level statements.
     *
     * @param componentList the list of all top-level statements.
     */
    public Program(List<Component> componentList) {
        this.componentList.addAll(componentList);
    }

    /**
     * Returns the list of top-level statements.
     *
     * @return the statement list.
     */
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
        String out = "";
        for (Component st : componentList) {
            out += st.toString();
        }

        return out;
    }

    @Override
    public String getParseTree() {
        String out = this.getClass().getName();
        out = "+ " + out.substring(out.lastIndexOf(".") + 1) + "\n";
        List<String> components = new ArrayList<>();
        for (Component st : componentList) {
            components.addAll(Arrays.asList(st.getParseTree().split("\n")));
        }
        for (String str : components) {
            out += "\t " + str + "\n";
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
