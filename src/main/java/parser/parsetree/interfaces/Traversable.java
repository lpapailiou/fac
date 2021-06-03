package parser.parsetree.interfaces;

import parser.parsetree.Statement;

import java.util.List;

/**
 * This interface is used to mark all instances of the parse tree as traversable and acceptor, which will facilitate
 * further processing. Additionally, the method getStatements() must be implemented for easy access of nested
 * components.
 */
public interface Traversable extends Acceptor {

    /**
     * Returns the nested statements of a parse tree component.
     * @return the list of nested statements.
     */
    List<Statement> getStatements();

}
