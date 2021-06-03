package parser.parsetree.interfaces;

/**
 * This interface marks a class as acceptor for the visitor pattern.
 * The implementing class must implement the accept method for visitors.
 */
public interface Acceptor {

    /**
     * Accepts a visitor for further processing.
     *
     * @param visitor the visitor class to accept.
     */
    void accept(Visitor visitor);
}
