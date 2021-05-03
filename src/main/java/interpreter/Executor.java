package interpreter;

import parser.parsetree.*;
import parser.parsetree.interfaces.Traversable;
import parser.validation.Validator;

public class Executor extends Validator {

    @Override
    public void visit(Program acceptor) {
    }

    @Override
    public void visit(Statement acceptor) {
    }

    @Override
    public void visit(AssignmentStatement acceptor) {
    }

    @Override
    public void visit(FunctionCallStatement acceptor) {
    }

    @Override
    public void visit(FunctionDefStatement acceptor) {
    }

    @Override
    public void visit(IfThenStatement acceptor) {
    }

    @Override
    public void visit(IfThenElseStatement acceptor) {
    }

    @Override
    public void visit(ParamDeclaration acceptor) {
    }

    @Override
    public void visit(VariableDeclaration acceptor) {
    }

    @Override
    public void visit(WhileStatement acceptor) {
    }

    @Override
    protected void traverse(Traversable node) {
        super.traverse(node);
    }

}
