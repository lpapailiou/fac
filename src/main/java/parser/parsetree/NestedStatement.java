package parser.parsetree;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a helper class for wrapping multiple statements in one object.
 * It is constructed like a linked list and meant to be collect nested statements (e.g. from if-then statements
 * or loops).
 */
public class NestedStatement extends Statement {

    private Statement statement;
    private NestedStatement next;

    /**
     * This constructor will take one statement and wrap it.
     *
     * @param st the statement to wrap.
     */
    NestedStatement(Object st) {
        statement = (Statement) st;
    }

    /**
     * This constructor will take two statements. The first statement will be wrapped in this instance.
     * The second statement will serve as pointer to the next statement.
     *
     * @param st     the statement to wrap.
     * @param nextSt the next statement to point to.
     */
    NestedStatement(Object st, Object nextSt) {
        this(st);
        next = (NestedStatement) nextSt;
    }

    /**
     * Returns all chained statements as list.
     *
     * @return the list of nested statements.
     */
    @Override
    public List<Statement> getStatements() {
        List<Statement> statements = new ArrayList<>();
        statements.add(statement);
        while (next != null) {
            statements.add(next.statement);
            next = next.next;
        }
        return statements;
    }

    /**
     * This toString method provides an empty string only, as it will be overwritten
     * by the nested statements of this instance.
     *
     * @return an empty string.
     */
    @Override
    public String toString() {
        String out = "";
        return out;
    }

}