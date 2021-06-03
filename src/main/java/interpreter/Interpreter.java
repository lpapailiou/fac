package interpreter;

import exceptions.*;
import parser.parsetree.*;
import parser.parsetree.interfaces.Declaration;
import parser.parsetree.interfaces.Traversable;
import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Interpreter implements Visitor {

    private List<Declaration> declarationScope = new ArrayList<>();
    private List<FunctionDefStatement> functionScope = new ArrayList<>();
    private int whileDepth = 0;

    @Override
    public void visit(Program acceptor) {
        checkBreakStatement(acceptor, acceptor.getStatements(), false);
        traverse(acceptor);
    }

    @Override
    public void visit(Component acceptor) {
        System.out.println("FALL THROUGH: " + acceptor.getClass());
        if (acceptor instanceof VariableDeclaration) {
            visit((VariableDeclaration) acceptor);
        } else if (acceptor instanceof ParamDeclaration) {
            visit((ParamDeclaration) acceptor);
        } else if (acceptor instanceof AssignmentStatement) {
            visit((AssignmentStatement) acceptor);
        } else if (acceptor instanceof FunctionCallStatement) {
            visit((FunctionCallStatement) acceptor);
        } else if (acceptor instanceof PrintCallStatement) {
            visit((PrintCallStatement) acceptor);
        } else if (acceptor instanceof FunctionDefStatement) {
            visit((FunctionDefStatement) acceptor);
        } else if (acceptor instanceof IfThenElseStatement) {
            visit((IfThenElseStatement) acceptor);
        } else if (acceptor instanceof IfThenStatement) {
            visit((IfThenStatement) acceptor);
        } else if (acceptor instanceof WhileStatement) {
            visit((WhileStatement) acceptor);
        } else {
            // fall through
        }
    }

    @Override
    public void visit(VariableDeclaration acceptor) {
        addDeclarationToScope(acceptor);
        Type expectedType = acceptor.getType();
        Type effectiveType = getTypeOfOperand(acceptor.getValue());
        if (expectedType != effectiveType) {
            throw new TypeMismatchException("Type of variable <" + acceptor.getIdentifier() + "> is <" + expectedType.getIdentifier() + "> and cannot assign value <" + acceptor.getStatements().toString().replaceAll("\\[", "").replaceAll("]", "") + ">!");
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
        Type effectiveType = getTypeOfOperand(acceptor.getValue());
        BinOp binOp = acceptor.getOperator();

        if (expectedType != effectiveType) {
            if (expectedType != Type.STRING || binOp == BinOp.EQUAL) {
                throw new TypeMismatchException("Type of variable <" + acceptor.getIdentifier() + "> is <" + expectedType.getIdentifier() + "> and cannot assign value <" + acceptor.getValue().toString().replaceAll("\\[", "").replaceAll("]", "") + ">!");
            }
        } else if (expectedType != Type.NUMERIC && binOp != BinOp.EQUAL) {
            if (expectedType == Type.STRING && binOp == BinOp.PLUSEQ) {
                return;
            }
            throw new TypeMismatchException("Type of variable <" + acceptor.getIdentifier() + "> does not allow the use of the binOp <" + acceptor.getOperator().asString() + "> to assign value <" + acceptor.getValue().toString().replaceAll("\\[", "").replaceAll("]", "") + ">!");
        }
    }

    @Override
    public void visit(FunctionCallStatement acceptor) {
        validateFunctionCall(acceptor);
    }

    @Override
    public void visit(PrintCallStatement acceptor) {
        Object value = acceptor.getValue();
        if (value != null) {
            getTypeOfOperand(acceptor.getValue());
        }
    }

    @Override
    public void visit(FunctionDefStatement acceptor) {
        Type defType = acceptor.getType();
        Object returnValue = acceptor.getReturnStatement();
        Type retType = getTypeOfOperand(returnValue);
        if (defType != retType) {
            throw new TypeMismatchException("Return type <" + retType.getIdentifier() + "> of function <" + acceptor.getIdentifier() + "(" + acceptor.paramTypeListAsString() + ")> does not match defined type <" + defType.getIdentifier() + ">!");
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
        }
    }

    protected void checkBreakStatement(Traversable parent, List<Component> components, boolean hold) {
        for (int i = 0; i < components.size(); i++) {
            if (components.get(i) instanceof BreakStatement) {
                if (whileDepth <= 0) {
                    throw new GrammarException("Not in loop! Break statement is not possible at position <" + parent + ">!");
                } else if (i < components.size() - 1) {
                    throw new GrammarException("Unreachable code! Break statement is not possible at position <" + parent + ">.");
                }
                if (!hold) {
                    if (whileDepth > 0) {
                        whileDepth--;
                    } else {
                        throw new GrammarException("Too many break components in loop <" + parent + ">!");
                    }
                }
            }
        }
    }

    private Type getType(UnaryExpression statement) {
        Type type = getTypeOfOperand(statement.getOperand());
        UnOp op = statement.getOperator();
        if ((op == UnOp.EXCL && type != Type.BOOLEAN) || ((op == UnOp.MINUS || op == UnOp.INC || op == UnOp.DEC) && type != Type.NUMERIC)) {
            throw new OperatorMismatchException("UnOp of expression <" + statement.getOperator().asString() + "> may not be used in context <" + statement.toString().replaceAll("\n", "") + ">!");
        }
        return type;
    }

    private Type getType(Constant statement) {
        return getTypeOfOperand(statement.getValue());
    }

    private Type getType(BinaryExpression statement) {
        Type type = getTypeOfOperand(statement.getOperand1());
        Type type2 = getTypeOfOperand(statement.getOperand2());
        BinOp binOp = statement.getOperator();
        if (type == Type.STRING || type2 == Type.STRING) {
            if (binOp != BinOp.PLUS) {
                throw new OperatorMismatchException("BinOp of expression <" + statement.getOperator().asString() + "> may not be used in context <" + statement.toString().replaceAll("\n", "") + ">!");
            }
            return Type.STRING;
        }
        if (type != type2) {
            throw new TypeMismatchException("Types of expression <" + statement.toString().replaceAll("\n", "") + "> do not match!");
        }
        if (type == Type.BOOLEAN) {
            throw new TypeMismatchException("Types of expression <" + statement.toString().replaceAll("\n", "") + "> do not match with binOp <" + binOp.asString() + ">!");
        }
        return type;
    }

    private Type getType(BinaryCondition statement) {
        Type type1 = getTypeOfOperand(statement.getOperand1());
        Type type2 = getTypeOfOperand(statement.getOperand2());

        if (type1 != type2) {
            throw new TypeMismatchException("Types of conditional statement <" + statement.toString().replaceAll("\n", "") + "> do not match!");
        }
        BinOp binOp = statement.getOperator();
        if (type1 != Type.NUMERIC && (binOp == BinOp.GREATER || binOp == BinOp.GREQ || binOp == BinOp.LEQ || binOp == BinOp.LESS)) {
            throw new OperatorMismatchException("BinOp <" + binOp.asString() + "> must not be used for non-numeric statements!");
        } else if (type1 != Type.BOOLEAN && (binOp == BinOp.AND || binOp == BinOp.OR)) {
            throw new OperatorMismatchException("BinOp <" + binOp.asString() + "> must not be used for non-boolean statements!");
        }

        return Type.BOOLEAN;
    }

    private Type getType(UnaryCondition statement) {
        Type type = getTypeOfOperand(statement.getOperand());
        if (type != Type.BOOLEAN) {
            throw new TypeMismatchException("Types of conditional statement <" + statement.toString().replaceAll("\n", "") + "> do not match!");
        }
        return type;
    }

    private Type getType(FunctionCallStatement statement) {
        FunctionDefStatement function = getFunction(statement.getIdentifier(), statement.getArgumentCount());
        return function.getType();
    }

    protected Type getTypeOfOperand(Object operand) {
        if (operand == null) {
            throw new GrammarException("Operand must never be null!");
        }
        Type type;
        if (operand instanceof FunctionCallStatement) {
            type = getType((FunctionCallStatement) operand);
        } else if (operand instanceof BinaryExpression) {
            type = getType((BinaryExpression) operand);
        } else if (operand instanceof UnaryExpression) {
            type = getType((UnaryExpression) operand);
        } else if (operand instanceof Constant) {
            type = getType((Constant) operand);
        } else if (operand instanceof BinaryCondition) {
            type = getType((BinaryCondition) operand);
        } else if (operand instanceof UnaryCondition) {
            type = getType((UnaryCondition) operand);
        } else {
            type = Type.getTypeForValue(operand);
            if (type == Type.VARIABLE) {
                type = getDeclaration(operand.toString()).getType();
            }
        }
        return type;
    }

    private void validateFunctionCall(FunctionCallStatement functionCall) {
        FunctionDefStatement function = getFunction(functionCall.getIdentifier(), functionCall.getArgumentCount());
        List<Component> callArgs = functionCall.getArgumentList();
        List<String> functionParams = Arrays.asList(function.paramTypeListAsString().split(", "));
        for (int i = 0; i < callArgs.size(); i++) {
            Type caller = getTypeOfOperand(callArgs.get(i));
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

    private void addFunDeclarationToScope(FunctionDefStatement function) {
        if (isFunctionExisting(function)) {
            throw new UniquenessViolationException("Function <" + function.getType().getIdentifier() + " " + function.getIdentifier() + "(" + function.paramListAsString() + ")" + "> is already defined!");
        } else if (!isFunctionDefineable(function)) {
            throw new GrammarException("Function <" + function.getType().getIdentifier() + " " + function.getIdentifier() + "(" + function.paramListAsString() + ")" + "> cannot be defined as it conflicts with similar function!");
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
            throw new MissingDeclarationException("Function <" + identifier + "> with " + parameterCount + " parameter was never defined!");
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
        Declaration declaration = declarationScope.stream().filter(dec -> dec.getIdentifier().equals(identifier)).findAny().orElse(null);
        if (declaration == null) {
            throw new MissingDeclarationException("Declaration <" + identifier + "> was never instantiated!");
        }
        return declaration;
    }

    protected void printDeclarations() {
        System.out.println("\ndeclaration scope after traversal: ");
        for (Declaration st : declarationScope) {
            System.out.print("\t - " + st);
        }
    }

    private void traverse(Traversable node) {
        if (node != null) {
            preValidation(node);
            List<Component> components = node.getStatements();

            for (Component st : components) {
                this.traverse(st);
            }
            if (!(node instanceof Program)) {
                node.accept(this);
            }
        }
    }

    protected void preValidation(Traversable node) {
        if (node instanceof WhileStatement) {
            whileDepth++;
        } else if (node instanceof FunctionDefStatement) {
            addFunDeclarationToScope((FunctionDefStatement) node);
        }
    }


}
