package parser.parsetree.statements;

import parser.parsetree.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a helper class for wrapping multiple statements in one object.
 * It is constructed like a linked list and meant to be collect nested statements (e.g. from if-then statements
 * or loops).
 */
public class NestedStatement extends Component {

    private final Component component;
    private NestedStatement next;

    /**
     * This constructor will take one component and wrap it.
     *
     * @param st the component to wrap.
     */
    public NestedStatement(Object st) {
        super(0, 0);
        component = (Component) st;
    }

    /**
     * This constructor will take two statements. The first component will be wrapped in this instance.
     * The second component will serve as pointer to the next component.
     *
     * @param st     the component to wrap.
     * @param nextSt the next component to point to.
     */
    public NestedStatement(Object st, Object nextSt) {
        this(st);
        next = (NestedStatement) nextSt;
    }

    /**
     * Returns all chained statements as list.
     *
     * @return the list of nested statements.
     */
    @Override
    public List<Component> getStatements() {
        List<Component> components = new ArrayList<>();
        components.add(component);
        while (next != null) {
            components.add(next.component);
            next = next.next;
        }
        return components;
    }

    /**
     * This toString method provides null, as it will be overwritten
     * by the nested statements of this instance.
     *
     * @return an empty string.
     */
    @Override
    public String getParseTree() {
        return null;
    }

}