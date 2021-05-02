package parser.validation;

import parser.parsetree.*;

import java.util.List;

public class Validator implements Visitor {

    @Override
    public void visit(Program acceptor) {
        List<Declaration> variableDeclarationList = acceptor.getDeclarations();
        List<Statement> statementList = acceptor.getStatements();
    }

    @Override
    public void visit(VariableDeclaration acceptor) {

    }

    @Override
    public void visit(Statement acceptor) {

    }
}
