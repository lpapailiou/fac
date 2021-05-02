package parser.validation;

import parser.parsetree.*;
import parser.util.GrammarException;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class Validator implements Visitor {

    List<Declaration> variableDeclarationList;
    List<Statement> statementList;

    @Override
    public void visit(Program acceptor) {
        variableDeclarationList = acceptor.getDeclarations();
        declarationUniquenessCheck(variableDeclarationList);

        statementList = acceptor.getStatements();
        traverse(acceptor);



    }
    /*
    ASSIGN 	        ::= VAR:e1 EQUAL:op EXPR:e2 STOP                                            {: RESULT = new AssignmentStatement(op, e1, e2); :}
                    | VAR:e1 EQUAL:op COND:e2 STOP                                          {: RESULT = new AssignmentStatement(op, e1, e2); :}
                    | VAR:e1 ASSIGN_OP:op EXPR:e2 STOP                                      {: RESULT = new AssignmentStatement(op, e1, e2); :}
    ; */

    private void isVariableDeclaredCheck() {

    }

    private void declarationUniquenessCheck(List<Declaration> declarationList) {
        for (Declaration decl1 : declarationList) {
            for (Declaration decl2 : declarationList) {
                if (decl1.getIdentifier().equals(decl2.getIdentifier()) && decl1 != decl2) {
                    throw new GrammarException("variable identifier <" + decl1.getIdentifier() + "> is not unique!");
                }
            }
        }
    }

    @Override
    public void visit(AssignmentStatement acceptor) {

    }

    @Override
    public void visit(Statement acceptor) {

    }

    public void traverse(Traversable node) {
        if (node != null) {
            List<Statement> statements = node.getStatements();

            for (Statement st : statements) {
                traverse(st);
            }
            System.out.println("NODE " + node.getClass() + " " + node);
            if (!(node instanceof Program)) {
                node.accept(this);
            }
        }
    }


}
