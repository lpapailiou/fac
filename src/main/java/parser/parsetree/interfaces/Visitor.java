package parser.parsetree.interfaces;

import parser.parsetree.*;

public interface Visitor {

    void visit(Program acceptor);

    void visit(Statement acceptor);




    void visit(AssignmentStatement acceptor);

    void visit(FunctionCallStatement acceptor);

    void visit(FunctionDefStatement acceptor);

    void visit(IfThenStatement acceptor);

    void visit(IfThenElseStatement acceptor);

    void visit(ParamDeclaration acceptor);

    void visit(VariableDeclaration acceptor);

    void visit(WhileStatement acceptor);

}
