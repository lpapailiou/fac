package parser.validation;

import parser.parsetree.*;
import parser.util.GrammarException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Validator implements Visitor {

    List<Declaration> declarationScope = new ArrayList<>();

    @Override
    public void visit(Program acceptor) {
        traverse(acceptor);
        //System.out.println(declarationList);
    }

    @Override
    public void visit(AssignmentStatement acceptor) {
        Declaration declaration = getDeclaration(acceptor.getIdentifier());
        Type expectedType = declaration.getType();
        Object value = declaration.getValue();
        Type effectiveType = getTypeOfOperand(acceptor.getStatements().get(0));

        if (expectedType != effectiveType) {
            throw new GrammarException("Type of variable <" + acceptor.getIdentifier() + "> is <" + expectedType.getDescription() + "> and cannot assign value <" + acceptor.getStatements() + ">!");
        }

    }

    @Override
    public void visit(BinaryStatement acceptor) {

    }

    @Override
    public void visit(ConditionalStatement acceptor) {

    }

    @Override
    public void visit(ExpressionStatement acceptor) {


    }

    private Type getTypeOfExpression(ExpressionStatement statement) {
        Type type1 = getTypeOfOperand(statement.getOperand1());
        Type type2 = getTypeOfOperand(statement.getOperand2());
        if (type2 == null) {
            return type1;
        }
        if (type1 != type2) {
            throw new GrammarException("Types of expression <" + statement + "> do not match!");
        }
        return type1;
    }

    private Type getTypeOfCondition(ConditionalStatement statement) {
        Type type1 = getTypeOfOperand(statement.getOperand1());
        Type type2 = getTypeOfOperand(statement.getOperand2());
        if (type2 == null) {
            return type1;
        }
        if (type1 != type2) {
            throw new GrammarException("Types of conditional statement <" + statement + "> do not match!");
        }
        Operator operator = statement.getOperator();
        if (type1 != Type.NUMERIC && (operator == Operator.GREATER || operator == Operator.GREQ || operator == Operator.LEQ || operator == Operator.LESS)) {
            throw new GrammarException("Operator <" + operator.getOperator() + "> must not be used for non-numeric statements!");
        } else if (type1 != Type.BOOLEAN && (operator == Operator.AND || operator == Operator.OR)) {
            throw new GrammarException("Operator <" + operator.getOperator() + "> must not be used for non-boolean statements!");
        }

        return Type.BOOLEAN;
    }

    private Type getTypeOfOperand(Object operand) {
        if (operand == null) {
            return null;
        }

        Type type;
        if (operand instanceof ExpressionStatement) {
            type = getTypeOfExpression((ExpressionStatement) operand);
        } else if (operand instanceof ConditionalStatement) {
            type = getTypeOfCondition((ConditionalStatement) operand);
        } else {
            type = Type.getType(operand);
            if (type == Type.VARIABLE) {
                type = getDeclaration(operand.toString()).getType();
            }
        }
        return type;
    }

    @Override
    public void visit(FunctionCallStatement acceptor) {

    }

    @Override
    public void visit(Statement acceptor) {
        System.out.println("\t" + acceptor.getClass());
    }

    @Override
    public void visit(FunctionDefStatement acceptor) {
        List<Declaration> declarations = acceptor.getStatements().stream().filter(st -> st instanceof Declaration).map(st -> (Declaration) st).collect(Collectors.toList());
        declarationScope.removeAll(declarations);
    }

    @Override
    public void visit(IfThenStatement acceptor) {

    }

    @Override
    public void visit(NestedStatement acceptor) {

    }

    @Override
    public void visit(ParamDeclaration acceptor) {
        addDeclarationToScope(acceptor);
    }

    @Override
    public void visit(ParameterStatement acceptor) {

    }

    @Override
    public void visit(PrintCallStatement acceptor) {

    }

    @Override
    public void visit(UnaryStatement acceptor) {

    }

    @Override
    public void visit(VariableDeclaration acceptor) {
        addDeclarationToScope(acceptor);
    }

    @Override
    public void visit(WhileStatement acceptor) {

    }

    private void addDeclarationToScope(Declaration declaration) {
        if (isVariableInScope(declaration.getIdentifier())) {
            throw new GrammarException("variable identifier <" + declaration.getIdentifier() + "> is already defined!");
        } else {
            declarationScope.add(declaration);
        }
    }

    private boolean isVariableInScope(String identifier) {
        Declaration declaration =  declarationScope.stream().filter(dec -> dec.getIdentifier().equals(identifier)).findAny().orElse(null);
        return declaration != null;
    }

    private Declaration getDeclaration(String identifier) {
        Declaration declaration =  declarationScope.stream().filter(dec -> dec.getIdentifier().equals(identifier)).findAny().orElse(null);
        if (declaration == null) {
            throw new GrammarException("Declaration <" + identifier + "> was never instantiated!");
        }
        return declaration;
    }

    private void traverse(Traversable node) {
        if (node != null) {
            List<Statement> statements = node.getStatements();

            for (Statement st : statements) {
                traverse(st);
            }
            //System.out.println("NODE " + node.getClass() + " " + node);
            if (!(node instanceof Program)) {
                System.out.println("VISITING NODE " + node.getClass().toString().replaceAll("class parser.parsetree.", ""));
                node.accept(this);
            }
        }
    }


}
