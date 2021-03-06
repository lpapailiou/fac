package validator;

import exceptions.*;
import parser.parsetree.*;
import parser.parsetree.instructions.*;
import parser.parsetree.interfaces.Declaration;
import parser.parsetree.interfaces.Traversable;
import parser.parsetree.interfaces.Visitor;
import parser.parsetree.statements.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * The purpose of this class is to validate a parse tree for semantic correctness.
 * It is implemented as visitor and walks the parse tree depth-first, while a node will be evaluated after
 * its children.
 * In case of invalid semantics, an according error is thrown.
 * Example for usage (where the rootSymbol is the resulting symbol of a parse process):
 * <code>Validator validator = new Validator();
 * ((Program) rootSymbol.value).accept(validator);</code>
 */
public class Validator implements Visitor {

    private final List<List<Declaration>> declarationScope = new ArrayList<>();   // variable declaration context
    private final List<FunctionDefStatement> functionScope = new ArrayList<>();   // function declaration context
    private int whileDepth = 0;                                                   // control counter for break statement check

    /**
     * This method visits an assignment statement.
     * It first looks for the declaration of the identifier. If found, the expected data type and the
     * effective data type of the variable are validated.
     * Additionally, the assignment operator is validated in context of the data types.
     *
     * @param acceptor the AssignmentStatement to visit.
     */
    @Override
    public void visit(AssignmentStatement acceptor) {
        Declaration declaration = getDeclaration(acceptor, acceptor.getIdentifier());
        Type expectedType = declaration.getType();
        Type effectiveType = getTypeOfOperand(acceptor, acceptor.getValue());
        BinaryOperator binaryOperator = acceptor.getOperator();
        if (expectedType != effectiveType) {
            if (expectedType != Type.STRING || binaryOperator == BinaryOperator.EQUAL) {
                throw new TypeMismatchException("Type of variable <" + acceptor.getIdentifier() + "> is <" + expectedType.getLiteral() + "> and cannot assign value <" + acceptor.getValue().toString().replaceAll("\\[", "").replaceAll("]", "") + "> at location " + Arrays.toString(acceptor.getLocation()) + "!");
            }
        } else if (expectedType != Type.NUMERIC && binaryOperator != BinaryOperator.EQUAL) {
            if (expectedType == Type.STRING && binaryOperator == BinaryOperator.PLUSEQ) {
                return;
            }
            throw new TypeMismatchException("Type of variable <" + acceptor.getIdentifier() + "> does not allow the use of the binaryOperator <" + acceptor.getOperator().getLiteral() + "> to assign value <" + acceptor.getValue().toString().replaceAll("\\[", "").replaceAll("]", "") + "> at location " + Arrays.toString(acceptor.getLocation()) + "!");
        }
    }

    /**
     * This method visits a generic component (i.e. a component which has not an own specific visit method).
     * This is a fall-through action.
     *
     * @param acceptor the generic Component to visit.
     */
    @Override
    public void visit(Component acceptor) {
    }

    /**
     * This method visits a function call statement. It will look up already declared functions with
     * matching parameter count.
     * If declared, it will check if the data types of parameters of the callee and arguments of the caller match.
     *
     * @param acceptor the FunctionCallStatement to visit.
     */
    @Override
    public void visit(FunctionCallStatement acceptor) {
        FunctionDefStatement function = getFunction(acceptor, acceptor.getIdentifier(), acceptor.getArgumentCount());
        List<Component> callArgs = acceptor.getArgumentList();
        List<String> functionParams = Arrays.asList(function.paramTypeListAsString().split(", "));
        for (int i = 0; i < callArgs.size(); i++) {
            Type caller = getTypeOfOperand(acceptor, callArgs.get(i));
            Type callee = Type.getByLiteral(functionParams.get(i));
            if (caller != callee) {
                throw new GrammarException("Function parameters do not match with function <" + function.getIdentifier() + "(" + function.paramTypeListAsString() + ") at location " + Arrays.toString(acceptor.getLocation()) + "!");
            }
        }
        closeCurrentScope();
    }

    /**
     * This method visits a function definition statement. It validates if the declared return type matches the
     * data type of the return statement. Additionally, contained break statements are validated.
     * At this point, the identifier scope validation is already done and all contained statements are
     * validated. As last step, the function identifier is cleared from scope.
     *
     * @param acceptor the FunctionDefStatement to visit.
     */
    @Override
    public void visit(FunctionDefStatement acceptor) {
        Type defType = acceptor.getType();
        Object returnValue = acceptor.getReturnStatement();
        Type retType = getTypeOfOperand(acceptor, returnValue);
        if (defType != retType) {
            throw new TypeMismatchException("Return type <" + retType.getLiteral() + "> of function <" + acceptor.getIdentifier() + "(" + acceptor.paramTypeListAsString() + ")> does not match defined type <" + defType.getLiteral() + "> at location " + Arrays.toString(acceptor.getLocation()) + "!");
        }
        checkBreakStatement(acceptor, acceptor.getStatements(), false);
        closeCurrentScope();
    }

    /**
     * This method will validate an if-then-else statement for nested break statements for each block.
     * Additionally, all contained variable declarations are removed from scope.
     *
     * @param acceptor the IfThenElseStatement to visit.
     */
    @Override
    public void visit(IfThenElseStatement acceptor) {
        checkBreakStatement(acceptor, acceptor.getIfStatements(), true);
        checkBreakStatement(acceptor, acceptor.getElseStatements(), false);
        closeCurrentScope();
    }

    /**
     * This method will validate an if-then-else statement for nested break statements.
     * Additionally, all contained variable declarations are removed from scope.
     *
     * @param acceptor the IfThenStatement to visit.
     */
    @Override
    public void visit(IfThenStatement acceptor) {
        checkBreakStatement(acceptor, acceptor.getStatements(), true);
        closeCurrentScope();
    }

    /**
     * This method will add the declared parameter to the variable scope.
     *
     * @param acceptor the ParamDeclaration to visit.
     */
    @Override
    public void visit(ParamDeclaration acceptor) {
        addDeclarationToScope(acceptor);
    }

    /**
     * This method will evaluate the type of the argument of a print statement, if not empty.
     *
     * @param acceptor the PrintCallStatement to visit.
     */
    @Override
    public void visit(PrintCallStatement acceptor) {
        Object value = acceptor.getValue();
        if (value != null) {
            getTypeOfOperand(acceptor, acceptor.getValue());
        }
    }

    /**
     * This method will validate for nested break statements. It will start the depth-first-traversal
     * of the parse tree to validate all program components.
     *
     * @param acceptor the Program to visit.
     */
    @Override
    public void visit(Program acceptor) {
        checkBreakStatement(acceptor, acceptor.getStatements(), false);
        traverse(acceptor);
    }

    /**
     * This method will add the variable declaration to scope. Additionally, it will validate if the declared
     * data type and the data type of the assigned value match.
     *
     * @param acceptor the VariableDeclaration to visit.
     */
    @Override
    public void visit(VariableDeclaration acceptor) {
        addDeclarationToScope(acceptor);
        Type expectedType = acceptor.getType();
        Type effectiveType = getTypeOfOperand(acceptor, acceptor.getValue());
        if (expectedType != effectiveType) {
            throw new TypeMismatchException("Type of variable <" + acceptor.getIdentifier() + "> is <" + expectedType.getLiteral() + "> and cannot assign value <" + acceptor.getStatements().toString().replaceAll("\\[", "").replaceAll("]", "") + "> at location " + Arrays.toString(acceptor.getLocation()) + "!");
        }
    }

    /**
     * This method will visit a while statement and validate nested break statements.
     * Additionally, all local variable declarations are cleared from scope and the break statement validation
     * counter is decremented by one.
     *
     * @param acceptor the WhileStatement to visit.
     */
    @Override
    public void visit(WhileStatement acceptor) {
        checkBreakStatement(acceptor, acceptor.getStatements(), false);
        closeCurrentScope();
        if (whileDepth > 0) {
            whileDepth--;
        }
    }

    /**
     * This method will perform thee depth-first traversal of the parse tree recursively.
     * It validates first the nested components of a component, then the component itself.
     * If necessary, pre-validations are done for specific nodes.
     *
     * @param node the parse tree node to traverse
     */
    private void traverse(Traversable node) {
        if (node != null) {

            preValidate(node);

            processStatements(node);

            if (!(node instanceof Program)) {   // the program node starts the traversal and does not have to be visited again
                node.accept(this);
            }
        }
    }

    // ------------------------------------------ helper methods ------------------------------------------

    /**
     * This methods takes care about the pre-validations before the content of a node and then the node itself are visited.
     * Entering a while loop will increase the whileDepth counter (e.g. one nested break statement is allowed). Entering
     * a function definition will add its declaration to scope. Additionally, the type of a condition is checked.
     *
     * @param node the parse tree node to pre-validate.
     */
    protected void preValidate(Traversable node) {
        if (node instanceof FunctionCallStatement || node instanceof FunctionDefStatement || node instanceof IfThenStatement || node instanceof Program || node instanceof WhileStatement) {
            openNewScope();

            Component condition = null;
            if (node instanceof WhileStatement) {
                whileDepth++;
                condition = ((WhileStatement) node).getCondition();
            } else if (node instanceof IfThenStatement) {
                condition = ((IfThenStatement) node).getCondition();
            } else if (node instanceof FunctionDefStatement) {
                addFunDeclarationToScope((FunctionDefStatement) node);
            }

            if (condition != null) {
                Type type = getTypeOfOperand(condition, condition);
                if (type != Type.BOOLEAN) {
                    throw new TypeMismatchException("Type <" + type + "> is not valid at location " + Arrays.toString(condition.getLocation()) + "!");
                }
            }
        }
    }

    /**
     * This method will control the traversal of nested components of a parse tree node.
     * Its purpose is to manage the edge case, where an if-then-else statement has two bodies,
     * which means that it requires two inner contexts.
     *
     * @param node the parse tree node as parent of child nodes to traverse.
     */
    private void processStatements(Traversable node) {
        if (node instanceof IfThenElseStatement) {
            traverseStatements(((IfThenElseStatement) node).getIfStatements());
            closeCurrentScope();
            openNewScope();
            traverseStatements(((IfThenElseStatement) node).getElseStatements());
        } else {
            traverseStatements(node.getStatements());
        }
    }

    /**
     * This helper method will traverse a statement list. This method should reduce duplicate code fragments.
     *
     * @param components the program components to traverse.
     */
    private void traverseStatements(List<Component> components) {
        for (Component st : components) {
            this.traverse(st);
        }
    }

    /**
     * This method will open a new variable scope. This occurs when a complex structure (e.g. an if-then statement or a while statement)
     * is opened, so the contained nested variables will be placed in a new isolated scope.
     */
    protected void openNewScope() {
        declarationScope.add(0, new ArrayList<>());
    }

    /**
     * This method will close the current variable scope. This occurs when a complex structure (e.g. an if-then statement or a while statement)
     * is left, so the contained nested variables will not be valid anymore in the outer context.
     */
    protected void closeCurrentScope() {
        declarationScope.remove(0);
    }

    /**
     * This method will check if a break statement is at a correct position.
     * It must be placed within a while loop (indicator: whileDepth counter is > 0) and it must be the last
     * statement of a statement list (otherwise there would be unreachable code).
     * Within an if-else-then component, one break statement for each body is allowed.
     * If the break statement is placed correctly, the whileDepth counter will be decreased by one.
     *
     * @param parent     the parent parse tree component.
     * @param components the nested parse tree components.
     * @param hold       the indicator, if the decrease of the whileDepth counter should be executed (only relevant for if-then-else components).
     */
    protected void checkBreakStatement(Traversable parent, List<Component> components, boolean hold) {
        for (int i = 0; i < components.size(); i++) {
            if (components.get(i) instanceof BreakStatement) {
                if (whileDepth <= 0) {
                    throw new GrammarException("Misplaced break statement! Break statement is not possible at position <" + parent + "> at location " + Arrays.toString(components.get(i).getLocation()) + "!");
                } else if (i < components.size() - 1) {
                    throw new GrammarException("Unreachable code! Break statement is not possible at position <" + parent + "> at location " + Arrays.toString(components.get(i).getLocation()) + "!");
                }
                if (!hold) {
                    whileDepth--;
                }
            }
        }
    }

    /**
     * This method will evaluate the data type of an object, which can be any type of program component which results in a value (e.g. a 'raw value', an expression
     * or the result of a function call).
     * If the object to evaluate is more complex, this method will follow the component chain until the final result is found.
     *
     * @param parent  the parent parse tree component.
     * @param operand the operand of which the data type should be evaluated.
     * @return the data type of the operand.
     */
    protected Type getTypeOfOperand(Traversable parent, Object operand) {
        if (operand == null) {
            throw new GrammarException("Operand must never be null!");
        }
        Type type;
        if (operand instanceof FunctionCallStatement) {
            type = getType(parent, (FunctionCallStatement) operand);
        } else if (operand instanceof BinaryExpression) {
            type = getType(parent, (BinaryExpression) operand);
        } else if (operand instanceof UnaryExpression) {
            type = getType(parent, (UnaryExpression) operand);
        } else if (operand instanceof ValueWrapper) {
            type = getType(parent, (ValueWrapper) operand);
        } else if (operand instanceof BinaryCondition) {
            type = getType(parent, (BinaryCondition) operand);
        } else if (operand instanceof UnaryCondition) {
            type = getType(parent, (UnaryCondition) operand);
        } else {
            type = Type.getByInput(operand);
            if (type == Type.VARIABLE) {
                type = getDeclaration(parent, operand.toString()).getType();
            }
        }
        return type;
    }

    /**
     * This method evaluates the return type of the declaration of the called function.
     *
     * @param parent  the parent parse tree component.
     * @param operand the operand to be evaluated.
     * @return the type of the operand.
     */
    private Type getType(Traversable parent, FunctionCallStatement operand) {
        FunctionDefStatement function = getFunction(parent, operand.getIdentifier(), operand.getArgumentCount());
        traverse(operand);                  // make sure function call is validated
        return function.getType();
    }

    /**
     * This method evaluates the data type of a binary expression.
     * If one of both operands is a string type, the result will evaluate to a string, otherwise it will be numeric.
     * Additionally, the operators must match (i.e. only a plus is allowed for string operations).
     *
     * @param parent  the parent parse tree component.
     * @param operand the operand to be evaluated.
     * @return the type of the operand.
     */
    private Type getType(Traversable parent, BinaryExpression operand) {
        Type type = getTypeOfOperand(parent, operand.getOperand1());
        Type type2 = getTypeOfOperand(parent, operand.getOperand2());
        BinaryOperator binaryOperator = operand.getOperator();
        if (type == Type.STRING || type2 == Type.STRING) {
            if (binaryOperator != BinaryOperator.PLUS) {
                throw new OperatorMismatchException("BinaryOperator of expression <" + operand.getOperator().getLiteral() + "> may not be used in context <" + operand.toString().replaceAll("\n", "") + "> at location " + Arrays.toString(parent.getLocation()) + "!");
            }
            return Type.STRING;
        }
        if (type != type2) {
            throw new TypeMismatchException("Types of expression <" + operand.toString().replaceAll("\n", "") + "> do not match at location " + Arrays.toString(parent.getLocation()) + "!");
        }
        if (type == Type.BOOLEAN) {
            throw new TypeMismatchException("Types of expression <" + operand.toString().replaceAll("\n", "") + "> do not match with binaryOperator <" + binaryOperator.getLiteral() + "> at location " + Arrays.toString(parent.getLocation()) + "!");
        }
        return type;
    }

    /**
     * This methods validates the operand of a unary arithmetic expression and validates if the operator matches.
     *
     * @param parent  the parent parse tree component.
     * @param operand the operand to be evaluated.
     * @return the type of the operand.
     */
    private Type getType(Traversable parent, UnaryExpression operand) {
        Type type = getTypeOfOperand(parent, operand.getOperand());
        UnaryOperator op = operand.getOperator();
        if ((op == UnaryOperator.EXCL && type != Type.BOOLEAN) || ((op == UnaryOperator.MINUS || op == UnaryOperator.INC || op == UnaryOperator.DEC) && type != Type.NUMERIC)) {
            throw new OperatorMismatchException("UnaryOperator of expression <" + operand.getOperator().getLiteral() + "> may not be used in context <" + operand.toString().replaceAll("\n", "") + "> at location " + Arrays.toString(parent.getLocation()) + "!");
        }
        return type;
    }

    /**
     * This method returns the data type of the contained value of a constant.
     *
     * @param parent  the parent parse tree component.
     * @param operand the operand to be evaluated.
     * @return the type of the operand.
     */
    private Type getType(Traversable parent, ValueWrapper operand) {
        return getTypeOfOperand(parent, operand.getValue());
    }

    /**
     * This method returns the data type of a binary conditional expression. It must evaluate to a boolean value.
     * Operators are validated as well (e.g. 'less' can be used in numeric context only).
     *
     * @param parent  the parent parse tree component.
     * @param operand the operand to be evaluated.
     * @return the type of the operand.
     */
    private Type getType(Traversable parent, BinaryCondition operand) {
        Type type1 = getTypeOfOperand(parent, operand.getOperand1());
        Type type2 = getTypeOfOperand(parent, operand.getOperand2());

        if (type1 != type2) {
            throw new TypeMismatchException("Types of conditional statement <" + operand.toString().replaceAll("\n", "") + "> do not match at location " + Arrays.toString(parent.getLocation()) + "!");
        }
        BinaryOperator binaryOperator = operand.getOperator();
        if (type1 != Type.NUMERIC && (binaryOperator == BinaryOperator.GREATER || binaryOperator == BinaryOperator.GREQ || binaryOperator == BinaryOperator.LEQ || binaryOperator == BinaryOperator.LESS)) {
            throw new OperatorMismatchException("BinaryOperator <" + binaryOperator.getLiteral() + "> must not be used for non-numeric statement at location " + Arrays.toString(parent.getLocation()) + "!");
        } else if (type1 != Type.BOOLEAN && (binaryOperator == BinaryOperator.AND || binaryOperator == BinaryOperator.OR)) {
            throw new OperatorMismatchException("BinaryOperator <" + binaryOperator.getLiteral() + "> must not be used for non-boolean statement at location " + Arrays.toString(parent.getLocation()) + "!");
        }

        return Type.BOOLEAN;
    }

    /**
     * This method returns the data type of a unary conditional expression. It must evaluate to a boolean value.
     *
     * @param parent  the parent parse tree component.
     * @param operand the operand to be evaluated.
     * @return the type of the operand.
     */
    private Type getType(Traversable parent, UnaryCondition operand) {
        Type type = getTypeOfOperand(parent, operand.getOperand());
        if (type != Type.BOOLEAN) {
            throw new TypeMismatchException("Types of conditional statement <" + operand.toString().replaceAll("\n", "") + "> do not match at location " + Arrays.toString(parent.getLocation()) + "!");
        }
        return type;
    }

    /**
     * This method will add a variable declaration to the variable scope, if the identifier was not already declared.
     * Otherwise, it will throw a runtime exception.
     *
     * @param declaration the declaration to add to scope.
     */
    protected void addDeclarationToScope(Declaration declaration) {
        if (isVariableInCurrentScope(declaration.getIdentifier())) {
            throw new UniquenessViolationException("variable identifier <" + declaration.getIdentifier() + "> at location " + Arrays.toString(((Component) declaration).getLocation()) + " is already defined!");
        } else {
            declarationScope.get(0).add(declaration);
        }
    }

    /**
     * This method checks if a variable declaration is already in the current scope by using the passed identifier.
     *
     * @param identifier the identifier to check.
     * @return true if the variable was already declared.
     */
    private boolean isVariableInCurrentScope(String identifier) {
        Declaration declaration = declarationScope.get(0).stream().filter(dec -> dec.getIdentifier().equals(identifier)).findFirst().orElse(null);
        return declaration != null;
    }

    /**
     * This method will search a fitting variable declaration by the passed identifier.
     * If the variable is not in scope, it will return a runtime exception.
     *
     * @param parent     the parent parse tree component.
     * @param identifier the identifier to check.
     * @return the matching declaration for the passed identifier.
     */
    protected Declaration getDeclaration(Traversable parent, String identifier) {
        Declaration declaration = declarationScope.stream().flatMap(Collection::stream).filter(dec -> dec.getIdentifier().equals(identifier)).findFirst().orElse(null);
        if (declaration == null) {
            throw new MissingDeclarationException("Declaration <" + identifier + "> at location " + Arrays.toString(parent.getLocation()) + " was never instantiated!");
        }
        return declaration;
    }

    /**
     * This method will check if a function was already defined (i.e. already in scope) or definable (i.e. possible to overwrite).
     * If no problems occur, the function definition will be added to scope.
     *
     * @param function the function definition to add to scope.
     */
    private void addFunDeclarationToScope(FunctionDefStatement function) {
        if (isFunctionExisting(function)) {
            throw new UniquenessViolationException("Function <" + function.getType().getLiteral() + " " + function.getIdentifier() + "(" + function.paramListAsString() + ")" + "> at location " + Arrays.toString(function.getLocation()) + " is already defined!");
        } else if (!isFunctionDefinable(function)) {
            throw new GrammarException("Function <" + function.getType().getLiteral() + " " + function.getIdentifier() + "(" + function.paramListAsString() + ")" + "> at location " + Arrays.toString(function.getLocation()) + " cannot be defined as it conflicts with similar function!");
        } else {
            functionScope.add(function);
        }
    }

    /**
     * This method checks if a matching function definition exists. The identifier, the return type and the count and types of the parameters must match.
     *
     * @param function the function definition to check.
     * @return true if the function was already defined.
     */
    private boolean isFunctionExisting(FunctionDefStatement function) {
        FunctionDefStatement definition = functionScope.stream().filter(fun -> fun.getIdentifier().equals(function.getIdentifier()) && fun.getType() == function.getType() && fun.paramTypeListAsString().equals(function.paramTypeListAsString())).findAny().orElse(null);
        return definition != null;
    }

    /**
     * This method checks if there is already a similar function defined, so overwriting is not possible. It checks for matching identifiers, unequal return types and different parameter count.
     *
     * @param function the function definition to check.
     * @return true, if the function can be defined safely.
     */
    private boolean isFunctionDefinable(FunctionDefStatement function) {
        FunctionDefStatement definition = functionScope.stream().filter(fun -> fun.getIdentifier().equals(function.getIdentifier()) && (fun.getType() != function.getType() || fun.getParamCount() == function.getParamCount())).findAny().orElse(null);
        return definition == null;
    }

    /**
     * This method looks for a fitting function definition which matches a given identifier and the parameter count.
     *
     * @param parent         the parent parse tree component.
     * @param identifier     the identifier of the function.
     * @param parameterCount the parameter count of the function.
     * @return the function definition of the found function.
     */
    protected FunctionDefStatement getFunction(Traversable parent, String identifier, int parameterCount) {
        FunctionDefStatement definition = functionScope.stream().filter(fun -> fun.getIdentifier().equals(identifier) && fun.getParamCount() == parameterCount).findAny().orElse(null);
        if (definition == null) {
            throw new MissingDeclarationException("Function <" + identifier + "> with " + parameterCount + " parameter was never defined for location " + Arrays.toString(parent.getLocation()) + "!");
        }
        return definition;
    }

}
