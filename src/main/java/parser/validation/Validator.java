package parser.validation;

import parser.parsetree.*;
import parser.parsetree.interfaces.Declaration;
import parser.parsetree.interfaces.Traversable;
import parser.parsetree.interfaces.Visitor;
import parser.exceptions.GrammarException;
import parser.exceptions.MissingComponentException;
import parser.exceptions.TypeMismatchException;
import parser.exceptions.UniquenessViolationException;

import javax.swing.plaf.nimbus.State;
import javax.swing.plaf.synth.SynthTabbedPaneUI;
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
        - function definitions do not allow recursions (???)
        - functions must always have a return value
        - the return type of a function must equal the defined type
        - print calls allow primitives, expressions, conditionals and function calls
        - break statements are allowed in while loops only. in nested structures, there will be checks for unreachable code.
 */
public class Validator implements Visitor {

    private List<Declaration> declarationScope = new ArrayList<>();
    private List<FunctionDefStatement> functionScope = new ArrayList<>();
    private int whileDepth = 0;

    @Override
    public void visit(Program acceptor) {
        checkBreakStatement(acceptor, acceptor.getStatements(), false);
        traverse(acceptor);
        printDeclarations();
    }

    @Override
    public void visit(Statement acceptor) {
        //System.out.println("\tnot specifically checked: " + acceptor.getClass());
    }

    @Override
    public void visit(VariableDeclaration acceptor) {
        addDeclarationToScope(acceptor);

        Type expectedType = acceptor.getType();
        Type effectiveType = getTypeOfOperand(acceptor.getValue());
        if (expectedType != effectiveType) {
            throw new TypeMismatchException("Type of variable <" + acceptor.getIdentifier() + "> is <" + expectedType.getDescription() + "> and cannot assign value <" + acceptor.getStatements() + ">!");
        }
    }

    @Override
    public void visit(ParamDeclaration acceptor) {
        addDeclarationToScope(acceptor);
    }

    @Override
    public void visit(AssignmentStatement acceptor) {
        Declaration declaration = getDeclaration(acceptor.getIdentifier());
        Type expectedType = declaration.getType();
        Type effectiveType = getTypeOfOperand(acceptor.getStatements().get(0));
        if (expectedType != effectiveType) {
            throw new TypeMismatchException("Type of variable <" + acceptor.getIdentifier() + "> is <" + expectedType.getDescription() + "> and cannot assign value <" + acceptor.getStatements() + ">!");
        }
    }

    @Override
    public void visit(FunctionCallStatement acceptor) {
        validateFunctionCall(acceptor);
    }

    @Override
    public void visit(PrintCallStatement acceptor) {
    }

    @Override
    public void visit(FunctionDefStatement acceptor) {
        Type defType = acceptor.getType();
        Object returnValue = acceptor.getReturnValue();
        Type retType = getTypeOfOperand(returnValue);
        if (defType != retType) {
            throw new TypeMismatchException("Return type <" + retType.getDescription() + "> of function <" + acceptor.getIdentifier() + "(" + acceptor.paramTypeListAsString() + ")> does not match defined type <" + defType.getDescription() + ">!");
        }
        checkBreakStatement(acceptor, acceptor.getStatements(), false);
        removeDeclarations(acceptor);
    }

    @Override
    public void visit(IfThenStatement acceptor) {
        checkBreakStatement(acceptor, acceptor.getStatements(), true);
        removeDeclarations(acceptor);
    }

    @Override
    public void visit(IfThenElseStatement acceptor) {
        checkBreakStatement(acceptor, acceptor.getIfStatements(), true);
        checkBreakStatement(acceptor, acceptor.getElseStatements(), false);
        removeDeclarations(acceptor);
    }

    @Override
    public void visit(WhileStatement acceptor) {
        checkBreakStatement(acceptor, acceptor.getStatements(), false);
        removeDeclarations(acceptor);
        if (whileDepth > 0) {
            whileDepth--;
        } else {
            throw new GrammarException("impossible break location");        // TODO: remove
        }
        System.out.println("while depth: " + whileDepth);
    }



    private void checkBreakStatement(Traversable parent, List<Statement> statements, boolean hold) {
        for (int i = 0; i < statements.size(); i++) {
            if (statements.get(i) instanceof BreakStatement) {
                System.out.println("while depth: " + whileDepth);
                if (whileDepth <= 0) {
                    throw new GrammarException("Not in loop! Break statement is not possible at position <" + parent + ">!");
                } else if (i < statements.size()-1) {
                    throw new GrammarException("Unreachable code! Break statement is not possible at position <" + parent + ">.");
                }
                if (!hold) {
                    if (whileDepth > 0) {
                        whileDepth--;
                    } else {
                        throw new GrammarException("impossible break location");        // TODO: remove
                    }
                }
            }
        }
    }

    private Type getType(BinaryExpression statement) {
        Type type = getTypeOfOperand(statement.getOperand1());
        Type type2 = getTypeOfOperand(statement.getOperand2());
        if (type != type2) {
            throw new TypeMismatchException("Types of expression <" + statement.toString().replaceAll("\n", "") + "> do not match!");
        }
        return type;
    }

    private Type getType(UnaryExpression statement) {
        return getTypeOfOperand(statement.getOperand());
    }

    private Type getType(ConditionalStatement statement) {
        Type type1 = getTypeOfOperand(statement.getOperand1());
        Type type2 = getTypeOfOperand(statement.getOperand2());
        if (type2 == null) {
            if (Type.getTypeForValue(type1) == Type.BOOLEAN) {
                return type1;
            } else {
                throw new TypeMismatchException("Type of <" + statement.getOperand1() + "> is no boolean!");
            }
        }

        if (type1 != type2) {
            throw new TypeMismatchException("Types of conditional statement <" + statement.toString().replaceAll("\n", "") + "> do not match!");
        }
        Operator operator = statement.getOperator();
        if (type1 != Type.NUMERIC && (operator == Operator.GREATER || operator == Operator.GREQ || operator == Operator.LEQ || operator == Operator.LESS)) {
            throw new GrammarException("Operator <" + operator.getOperator() + "> must not be used for non-numeric statements!");
        } else if (type1 != Type.BOOLEAN && (operator == Operator.AND || operator == Operator.OR)) {
            throw new GrammarException("Operator <" + operator.getOperator() + "> must not be used for non-boolean statements!");
        }

        return Type.BOOLEAN;
    }

    private Type getType(FunctionCallStatement statement) {
        FunctionDefStatement function = getFunction(statement.getIdentifier(), statement.getParamCount());
        return function.getType();
    }

    private Type getTypeOfOperand(Object operand) {
        Type type;
        if (operand instanceof FunctionCallStatement) {
            type = getType((FunctionCallStatement) operand);
        } else if (operand instanceof BinaryExpression) {
            type = getType((BinaryExpression) operand);
        } else if (operand instanceof UnaryExpression) {
            type = getType((UnaryExpression) operand);
        } else if (operand instanceof ConditionalStatement) {
            type = getType((ConditionalStatement) operand);
        } else {
            type = Type.getTypeForValue(operand);
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
            Type callee = Type.getByName(functionParams.get(i));
            if (caller != callee) {
                throw new GrammarException("Function parameters do not match with function <" + function.getIdentifier() + "(" + function.paramTypeListAsString() + ")>!");
            }
        }
    }

    protected void removeDeclarations(Traversable acceptor) {
        List<Declaration> declarations = acceptor.getStatements().stream().filter(st -> st instanceof Declaration).map(st -> (Declaration) st).collect(Collectors.toList());
        declarationScope.removeAll(declarations);
    }

    protected void addDeclarationToScope(Declaration declaration) {
        if (isVariableInScope(declaration.getIdentifier())) {
            throw new UniquenessViolationException("variable identifier <" + declaration.getIdentifier() + "> is already defined!");
        } else {
            declarationScope.add(declaration);
        }
    }

    protected void addFunDeclarationToScope(FunctionDefStatement function) {
        if (isFunctionExisting(function)) {
            throw new UniquenessViolationException("Function <" + function.getType().getDescription() + " " + function.getIdentifier() + "(" + function.paramListAsString() + ")" + "> is already defined!");
        } else if (!isFunctionDefineable(function)) {
            throw new GrammarException("Function <" + function.getType().getDescription() + " " + function.getIdentifier() + "(" + function.paramListAsString() + ")" + "> cannot be defined as it conflicts with similar function!");
        } else {
            functionScope.add(function);
        }
    }

    private boolean isFunctionExisting(FunctionDefStatement function) {
        FunctionDefStatement definition = functionScope.stream().filter(fun -> fun.getIdentifier().equals(function.getIdentifier()) && fun.getType() == function.getType() && fun.paramTypeListAsString().equals(function.paramTypeListAsString())).findAny().orElse(null);
        return definition != null;
    }

    protected FunctionDefStatement getFunction(String identifier, int parameterCount) {
        FunctionDefStatement definition = functionScope.stream().filter(fun -> fun.getIdentifier().equals(identifier) && fun.getParamCount() == parameterCount).findAny().orElse(null);
        if (definition == null) {
            throw new MissingComponentException("Function <" + identifier + "> with " + parameterCount + " parameter was never defined!");
        }
        return definition;
    }

    private boolean isFunctionDefineable(FunctionDefStatement function) {
        FunctionDefStatement definition = functionScope.stream().filter(fun -> fun.getIdentifier().equals(function.getIdentifier()) && (fun.getType() != function.getType() || fun.getParamCount() == function.getParamCount())).findAny().orElse(null);
        return definition == null;
    }

    private boolean isVariableInScope(String identifier) {
        Declaration declaration = declarationScope.stream().filter(dec -> dec.getIdentifier().equals(identifier)).findAny().orElse(null);
        return declaration != null;
    }

    protected Declaration getDeclaration(String identifier) {
        Declaration declaration =  declarationScope.stream().filter(dec -> dec.getIdentifier().equals(identifier)).findAny().orElse(null);
        if (declaration == null) {
            throw new MissingComponentException("Declaration <" + identifier + "> was never instantiated!");
        }
        return declaration;
    }

    protected void printDeclarations() {
        System.out.println("\ndeclaration scope after execution: ");
        for (Declaration st : declarationScope) {
            System.out.print("\t - " + st);
        }
    }

    private void traverse(Traversable node) {
        if (node != null) {
            if (node instanceof WhileStatement) {
                whileDepth++;
            } else if (node instanceof FunctionDefStatement) {
                addFunDeclarationToScope((FunctionDefStatement) node);
            }
            List<Statement> statements = node.getStatements();

            for (Statement st : statements) {
                traverse(st);
            }
            if (!(node instanceof Program)) {
                node.accept(this);
            }
        }
    }


}
