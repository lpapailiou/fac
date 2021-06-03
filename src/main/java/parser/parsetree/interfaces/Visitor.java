package parser.parsetree.interfaces;

import parser.parsetree.*;

/**
 * This interface allows to interact easily with the whole parse tree by visiting the parse tree components,
 * which are marked as acceptors. The less complex parse tree components will be visited indirectly. In consequence,
 * the visitor will collect them with the fall-through method visit(Component acceptor).
 */
public interface Visitor {

    /**
     * Visits the program - the root of the parse tree.
     *
     * @param acceptor the Program to visit.
     */
    void visit(Program acceptor);

    /**
     * Visits a generic statement. This case will be most likely a fall-through action.
     *
     * @param acceptor the generic Component to visit.
     */
    void visit(Component acceptor);

    /**
     * Visits a variable declaration.
     *
     * @param acceptor the VariableDeclaration to visit.
     */
    void visit(VariableDeclaration acceptor);

    /**
     * Visits a parameter declaration of a function.
     *
     * @param acceptor the ParamDeclaration to visit.
     */
    void visit(ParamDeclaration acceptor);

    /**
     * Visits an assignment statement.
     *
     * @param acceptor the AssignmentStatement to visit.
     */
    void visit(AssignmentStatement acceptor);

    /**
     * Visits a function call statement.
     *
     * @param acceptor the FunctionCallStatement to visit.
     */
    void visit(FunctionCallStatement acceptor);

    /**
     * Visits a print call statement.
     *
     * @param acceptor the PrintCallStatement to visit.
     */
    void visit(PrintCallStatement acceptor);

    /**
     * Visits a function definition.
     *
     * @param acceptor the FunctionDefStatement to visit.
     */
    void visit(FunctionDefStatement acceptor);

    /**
     * Visits an if-then statement.
     *
     * @param acceptor the IfThenStatement to visit.
     */
    void visit(IfThenStatement acceptor);

    /**
     * Visits an if-then-else statement.
     *
     * @param acceptor the IfThenElseStatement to visit.
     */
    void visit(IfThenElseStatement acceptor);

    /**
     * Visits a while statement.
     *
     * @param acceptor the WhileStatement to visit.
     */
    void visit(WhileStatement acceptor);

}
