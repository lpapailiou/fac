package parser.validation;

import jdk.nashorn.internal.ir.FunctionCall;
import parser.parsetree.*;
import parser.util.GrammarException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
/*
        -*** RULES ***-
        - variables must be defined before they can be called (string identifier; or string identifier = 'blabla';)
        - function calls, expressions, conditions and cannot be used to initialize variables
        - a variable can be 'uninitialized'. in this case, it will get a default value ('' or 0 or false)
        - variable names must be unique
        - variable cannot be called outside the scope they were initialized in
        - functions can only be defined once
        - functions may be overridden - in this case, the type must match and the parameter count must not match
        - an expression can have one or more values. if it has two values, both values must be of the same type
        - a conditional statement must always have two values of the same type. they must evaluate to a boolean value
        - ifthen and ifthenelse-statements can have empty bodies and define local variables
        - to call a function, it must exist and the caller must match the parameter types of the callee
        - function calls can be made anywhere senseful
        - function definitions do not allow recursions
        - print calls allow primitives, expressions, conditionals and function calls
 */
public class Validator implements Visitor {

    List<Declaration> declarationScope = new ArrayList<>();
    List<FunctionDefStatement> functionScope = new ArrayList<>();

    @Override
    public void visit(Program acceptor) {
        traverse(acceptor);
        System.out.println(declarationScope);
    }

    @Override
    public void visit(Statement acceptor) {
        System.out.println("\tnot specifically checked: " + acceptor.getClass());
    }

    @Override
    public void visit(AssignmentStatement acceptor) {
        Declaration declaration = getDeclaration(acceptor.getIdentifier());
        Type expectedType = declaration.getType();
        Type effectiveType = getTypeOfOperand(acceptor.getStatements().get(0));
        if (expectedType != effectiveType) {
            throw new GrammarException("Type of variable <" + acceptor.getIdentifier() + "> is <" + expectedType.getDescription() + "> and cannot assign value <" + acceptor.getStatements() + ">!");
        }

        // TODO. check for function calls and stuff
    }

    /*@Override
    public void visit(ConditionalStatement acceptor) {
    }
    @Override
    public void visit(BinaryExpression acceptor) {}
    @Override
    public void visit(UnaryExpression acceptor) {}
    */

    @Override
    public void visit(FunctionCallStatement acceptor) {
        validateFunctionCall(acceptor);
    }

    @Override
    public void visit(FunctionDefStatement acceptor) {       // todo: scope of break
        addFunDeclarationToScope(acceptor);
        removeDeclarations(acceptor);
    }

    @Override
    public void visit(IfThenStatement acceptor) {       // todo: scope of break
        removeDeclarations(acceptor);
    }

    @Override
    public void visit(NestedStatement acceptor) {       // todo: scope of break

    }

    @Override
    public void visit(ParamDeclaration acceptor) {
        addDeclarationToScope(acceptor);
    }

    /*
    @Override
    public void visit(ParamStatement acceptor) {
    }
    @Override
    public void visit(PrintCallStatement acceptor) {
    }*/

    @Override
    public void visit(VariableDeclaration acceptor) {
        addDeclarationToScope(acceptor);
    }

    @Override
    public void visit(WhileStatement acceptor) {
        removeDeclarations(acceptor);
    }

    private Type getTypeOfExpression(ExpressionStatement statement) {
        Type type = Type.NONE;
        if (statement instanceof BinaryExpression) {
            type = getTypeOfOperand(((BinaryExpression)statement).getOperand1());
            Type type2 = getTypeOfOperand(((BinaryExpression)statement).getOperand2());
            if (type != type2) {
                throw new GrammarException("Types of expression <" + statement + "> do not match!");
            }
        } else if (statement instanceof UnaryExpression) {
            type = getTypeOfOperand(((UnaryExpression)statement).getOperand());
        }

        return type;
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
        if (operand instanceof FunctionCallStatement) {
            FunctionDefStatement function = getFunction(((FunctionCallStatement) operand).getIdentifier(), ((FunctionCallStatement) operand).getParamCount());
            type = function.getType();
        } else if (operand instanceof BinaryExpression) {
            type = getTypeOfExpression((BinaryExpression) operand);
        } else if (operand instanceof UnaryExpression) {
            type = getTypeOfExpression((UnaryExpression) operand);
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

    private void validateFunctionCall(FunctionCallStatement functionCall) {
        FunctionDefStatement function = getFunction(functionCall.getIdentifier(), functionCall.getParamCount());
        List<String> callParams = functionCall.getParameterList();
        List<String> functionParams = Arrays.asList(function.paramTypeListAsString().split(", "));

        for (int i = 0; i < callParams.size(); i++) {
            Type caller = getTypeOfOperand(callParams.get(i));
            Type callee = Type.getName(functionParams.get(i));
            if (caller != callee) {
                throw new GrammarException("Function parameters do not match with function " + function.getIdentifier() + "(" + function.paramTypeListAsString() + ")>!");
            }
        }
    }

    private void removeDeclarations(Traversable acceptor) {
        List<Declaration> declarations = acceptor.getStatements().stream().filter(st -> st instanceof Declaration).map(st -> (Declaration) st).collect(Collectors.toList());
        declarationScope.removeAll(declarations);
    }

    private void addDeclarationToScope(Declaration declaration) {
        if (isVariableInScope(declaration.getIdentifier())) {
            throw new GrammarException("variable identifier <" + declaration.getIdentifier() + "> is already defined!");
        } else {
            declarationScope.add(declaration);
        }
    }

    private void addFunDeclarationToScope(FunctionDefStatement function) {
        if (isFunctionExisting(function)) {
            throw new GrammarException("Function <" + function.getType().getDescription() + " " + function.getIdentifier() + "(" + function.paramListAsString() + ")" + "> is already defined!");
        } else if (!isFunctionDefineable(function)) {
            throw new GrammarException("Function <" + function.getType().getDescription() + " " + function.getIdentifier() + "(" + function.paramListAsString() + ")" + "> cannot be defined as it conflicts with similar function!");
        } else {
            functionScope.add(function);
        }
    }

    private boolean isFunctionExisting(FunctionDefStatement function) {
        FunctionDefStatement definition =  functionScope.stream().filter(fun -> fun.getIdentifier().equals(function.getIdentifier()) && fun.getType() == function.getType() && fun.paramTypeListAsString().equals(function.paramTypeListAsString())).findAny().orElse(null);
        return definition != null;
    }

    private FunctionDefStatement getFunction(String identifier, int parameterCount) {
        FunctionDefStatement definition =  functionScope.stream().filter(fun -> fun.getIdentifier().equals(identifier) && fun.getParamCount() == parameterCount).findAny().orElse(null);
        if (definition == null) {
            throw new GrammarException("Function <" + identifier + "> with " + parameterCount + " parameter was never defined!");
        }
        return definition;
    }

    private boolean isFunctionDefineable(FunctionDefStatement function) {
        FunctionDefStatement definition =  functionScope.stream().filter(fun -> fun.getIdentifier().equals(function.getIdentifier()) && (fun.getType() != function.getType() || fun.getParamCount() == function.getParamCount())).findAny().orElse(null);
        return definition == null;
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
