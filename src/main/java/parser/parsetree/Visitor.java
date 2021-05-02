package parser.parsetree;

public interface Visitor {

    void visit(Program acceptor);

    void visit(Statement acceptor);




    void visit(AssignmentStatement acceptor);

    //void visit(ConditionalStatement acceptor);

    //void visit(BinaryExpression acceptor);

    void visit(FunctionCallStatement acceptor);

    void visit(FunctionDefStatement acceptor);

    void visit(IfThenStatement acceptor);

    void visit(NestedStatement acceptor);

    void visit(ParamDeclaration acceptor);

    //void visit(ParamStatement acceptor);

    //void visit(PrintCallStatement acceptor);

    //void visit(UnaryExpression acceptor);

    void visit(VariableDeclaration acceptor);

    void visit(WhileStatement acceptor);

}
