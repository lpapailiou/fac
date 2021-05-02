package parser.parsetree;

import parser.parsetree.VariableDeclaration;
import parser.parsetree.Program;
import parser.parsetree.Statement;

public interface Visitor {

    void visit(Program acceptor);

    void visit(Statement acceptor);




    void visit(AssignmentStatement acceptor);

    void visit(BinaryStatement acceptor);

    void visit(ConditionalStatement acceptor);

    void visit(ExpressionStatement acceptor);

    void visit(FunctionCallStatement acceptor);

    void visit(FunctionDefStatement acceptor);

    void visit(IfThenStatement acceptor);

    void visit(NestedStatement acceptor);

    void visit(ParamDeclaration acceptor);

    void visit(ParameterStatement acceptor);

    void visit(PrintCallStatement acceptor);

    void visit(UnaryStatement acceptor);

    void visit(VariableDeclaration acceptor);

    void visit(WhileStatement acceptor);

}
