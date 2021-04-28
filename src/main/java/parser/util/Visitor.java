package parser.util;

public interface Visitor {

    void visit(Program acceptor);

    void visit(Declaration acceptor);

    void visit(Statement acceptor);
}
