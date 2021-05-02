package parser.parsetree;

import java.util.List;

public interface Traversable extends Acceptor{

    List<Statement> getStatements();

}
