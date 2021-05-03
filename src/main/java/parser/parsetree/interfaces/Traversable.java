package parser.parsetree.interfaces;

import parser.parsetree.Statement;

import java.util.List;

public interface Traversable extends Acceptor {

    List<Statement> getStatements();

}
