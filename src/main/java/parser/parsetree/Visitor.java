package parser.parsetree;

import parser.parsetree.VariableDeclaration;
import parser.parsetree.Program;
import parser.parsetree.Statement;

public interface Visitor {

    void visit(Program acceptor);

    void visit(Statement acceptor);

    void visit(AssignmentStatement acceptor);
}
