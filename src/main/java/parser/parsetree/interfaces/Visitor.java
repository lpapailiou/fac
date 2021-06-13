package parser.parsetree.interfaces;

import parser.parsetree.*;

/**
 * This interface allows to interact easily with the whole parse tree by visiting the parse tree components,
 * which are marked as acceptors. The less complex parse tree components will be visited indirectly. In consequence,
 * the visitor will collect them with the fall-through method visit(Component acceptor).
 */
public interface Visitor {

    /**
     * Visits an assignment statement wrapper.
     *
     * @param acceptor the AssignmentStatement to visit.
     */
    void visit(AssignmentStatement acceptor);

    /**
     * Visits a generic parse tree component. This case will be most likely a fall-through action.
     *
     * @param acceptor the generic Component to visit.
     */
    void visit(Component acceptor);

    /**
     * Visits a function call statement wrapper.
     *
     * @param acceptor the FunctionCallStatement to visit.
     */
    void visit(FunctionCallStatement acceptor);

    /**
     * Visits a function definition wrapper.
     *
     * @param acceptor the FunctionDefStatement to visit.
     */
    void visit(FunctionDefStatement acceptor);

    /**
     * Visits an if-then-else statement wrapper.
     *
     * @param acceptor the IfThenElseStatement to visit.
     */
    void visit(IfThenElseStatement acceptor);

    /**
     * Visits an if-then statement wrapper.
     *
     * @param acceptor the IfThenStatement to visit.
     */
    void visit(IfThenStatement acceptor);

    /**
     * Visits a parameter declaration wrapper of a function.
     *
     * @param acceptor the ParamDeclaration to visit.
     */
    void visit(ParamDeclaration acceptor);

    /**
     * Visits a print call statement wrapper.
     *
     * @param acceptor the PrintCallStatement to visit.
     */
    void visit(PrintCallStatement acceptor);

    /**
     * Visits the program wrapper - the root of the parse tree.
     *
     * @param acceptor the Program to visit.
     */
    void visit(Program acceptor);

    /**
     * Visits a variable declaration wrapper.
     *
     * @param acceptor the VariableDeclaration to visit.
     */
    void visit(VariableDeclaration acceptor);

    /**
     * Visits a while statement wrapper.
     *
     * @param acceptor the WhileStatement to visit.
     */
    void visit(WhileStatement acceptor);

}
