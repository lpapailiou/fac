package parser.parsetree;

import parser.parsetree.interfaces.Traversable;
import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This is a superclass for all program components. it is also used as factory to initialize
 * the required instances of the parse tree components.
 */
public abstract class Component implements Traversable {

    /**
     * Returns an argument wrapper.
     *
     * @param arg the argument to wrap.
     * @return the argument wrapper class.
     */
    public static Argument param(Object arg) {
        return new Argument(arg);
    }

    /**
     * Returns an argument wrapper, which points to the following argument.
     *
     * @param arg  the argument to wrap.
     * @param next the following argument.
     * @return the argument wrapper.
     */
    public static Argument param(Object arg, Object next) {
        return new Argument(arg, next);
    }

    /**
     * Returns an assignment statement wrapper. This method is used for incrementing or decrementing
     * variables.
     *
     * @param op         the incrementing or decrementing operator.
     * @param identifier the variable identifier to modify.
     * @return the assignment statement wrapper.
     */
    public static AssignmentStatement assgn(Object op, Object identifier) {
        return new AssignmentStatement("=", identifier, new UnaryExpression(op, identifier));
    }

    /**
     * Returns an assignment statement wrapper. This method is used for 'regular' variable assignments.
     *
     * @param op         the assignment operator
     * @param identifier the variable identifier.
     * @param value      the value to assign.
     * @return the assignment statement wrapper.
     */
    public static AssignmentStatement assgn(Object op, Object identifier, Object value) {
        return new AssignmentStatement(op, identifier, value);
    }

    /**
     * Returns a binary conditional expression wrapper.
     *
     * @param op       the binary operator.
     * @param operand1 the first operand.
     * @param operand2 the second operand.
     * @return the binary conditional expression wrapper.
     */
    public static BinaryCondition cond(Object op, Object operand1, Object operand2) {
        return new BinaryCondition(op, operand1, operand2);
    }

    /**
     * Returns a binary expression wrapper.
     *
     * @param op       the binary operator.
     * @param operand1 the first operand.
     * @param operand2 the second operand.
     * @return the binary expression wrapper.
     */
    public static BinaryExpression expr(Object op, Object operand1, Object operand2) {
        return new BinaryExpression(op, operand1, operand2);
    }

    /**
     * Returns a break statement wrapper.
     *
     * @return the break statement wrapper.
     */
    public static BreakStatement brk() {
        return new BreakStatement();
    }

    /**
     * Returns a constant wrapper.
     *
     * @param constant the constant to wrap.
     * @return the constant wrapper.
     */
    public static Constant constnt(Object constant) {
        return new Constant(constant);
    }

    /**
     * Returns a function call statement wrapper.
     *
     * @param identifier the identifier of the function call.
     * @return the function call wrapper.
     */
    public static FunctionCallStatement fun(Object identifier) {
        return new FunctionCallStatement(identifier);
    }

    /**
     * Returns a function call statement wrapper.
     *
     * @param identifier the identifier of the function call.
     * @param arg        the argument (chain) of the function call.
     * @return the function call wrapper.
     */
    public static FunctionCallStatement fun(Object identifier, Object arg) {
        if (arg instanceof ConditionalExpression) {
            return new FunctionCallStatement(identifier, new Argument(arg));
        }
        return new FunctionCallStatement(identifier, arg);
    }

    /**
     * Returns a function definition wrapper.
     *
     * @param type            the return type of the function.
     * @param identifier      the identifier of the function.
     * @param returnStatement the return statement of the function.
     * @return the function definition wrapper.
     */
    public static FunctionDefStatement funDefEmpty(Object type, Object identifier, Object returnStatement) {
        return new FunctionDefStatement(type, identifier, null, null, returnStatement);
    }

    /**
     * Returns a function definition wrapper.
     *
     * @param type            the return type of the function.
     * @param identifier      the identifier of the function.
     * @param statements      the nested statements of the function.
     * @param returnStatement the return statement of the function.
     * @return the function definition wrapper.
     */
    public static FunctionDefStatement funDef(Object type, Object identifier, Object statements, Object returnStatement) {
        return new FunctionDefStatement(type, identifier, null, statements, returnStatement);
    }

    /**
     * Returns a function definition wrapper.
     *
     * @param type            the return type of the function.
     * @param identifier      the identifier of the function.
     * @param params          the parameter definition.
     * @param returnStatement the return statement of the function.
     * @return the function definition wrapper.
     */
    public static FunctionDefStatement funDefEmpty(Object type, Object identifier, Object params, Object returnStatement) {
        return new FunctionDefStatement(type, identifier, params, null, returnStatement);
    }

    /**
     * Returns a function definition wrapper.
     *
     * @param type            the return type of the function.
     * @param identifier      the identifier of the function.
     * @param params          the parameter definition.
     * @param statements      the nested statements of the function.
     * @param returnStatement the return statement of the function.
     * @return the function definition wrapper.
     */
    public static FunctionDefStatement funDef(Object type, Object identifier, Object params, Object statements, Object returnStatement) {
        return new FunctionDefStatement(type, identifier, params, statements, returnStatement);
    }

    /**
     * Returns an if-then statement wrapper.
     * @param condition the conditional expression.
     * @param statements the nested statements.
     * @return the if-then wrapper.
     */
    public static IfThenStatement ifThen(Object condition, Object statements) {
        if (!(condition instanceof ConditionalExpression)) {
            condition = new Constant(condition);
        }
        return new IfThenStatement(condition, statements);
    }

    /**
     * Returns an if-then-else statement wrapper.
     * @param condition the conditional expression.
     * @param ifStatements the nested if-then statements.
     * @param elseStatements the nested else statements.
     * @return the if-then-else wrapper.
     */
    public static IfThenStatement ifThen(Object condition, Object ifStatements, Object elseStatements) {
        if (!(condition instanceof ConditionalExpression)) {
            condition = new Constant(condition);
        }
        return new IfThenElseStatement(condition, ifStatements, elseStatements);
    }

    /**
     * Returns a nested statement wrapper. It will wrap one statement each and form a chain with the
     * help of a pointer to the next statement.
     * @param statement the statement to wrap.
     * @return the nested statement wrapper.
     */
    public static NestedStatement nest(Object statement) {
        return new NestedStatement(statement);
    }

    /**
     * Returns a nested statement wrapper. It will wrap one statement each and form a chain with the
     * help of a pointer to the next statement.
     * @param statement the statement to wrap.
     * @param nextStatement the next statement.
     * @return the nested statement wrapper.
     */
    public static NestedStatement nest(Object statement, Object nextStatement) {
        return new NestedStatement(statement, nextStatement);
    }

    /**
     * Returns a parameter declaration wrapper.
     *
     * @param type       the data type of the declaration.
     * @param identifier the identifier of the declaration.
     * @return the parameter declaration wrapper.
     */
    public static ParamDeclaration paramDecl(Object type, Object identifier) {
        return new ParamDeclaration(type, identifier);
    }

    /**
     * Returns a parameter declaration wrapper.
     * @param type the data type of the declaration.
     * @param identifier the identifier of the declaration.
     * @param nextParameter the next parameter declaration.
     * @return the parameter declaration wrapper.
     */
    public static ParamDeclaration paramDecl(Object type, Object identifier, Object nextParameter) {
        return new ParamDeclaration(type, identifier, nextParameter);
    }

    /**
     * Returns an empty print call statement wrapper.
     *
     * @return the print call wrapper.
     */
    public static PrintCallStatement print() {
        return new PrintCallStatement();
    }

    /**
     * Returns a print call statement wrapper.
     *
     * @param object the object to print.
     * @return the print call wrapper.
     */
    public static PrintCallStatement print(Object object) {
        return new PrintCallStatement(object);
    }

    /**
     * Returns a program wrapper. This will be the root of the parse tree.
     * @param componentList the components (i.e. top-level statements) the program contains.
     * @return the program wrapper.
     */
    public static Program prog(List<Component> componentList) {
        return new Program(componentList);
    }

    /**
     * Returns a unary conditional expression wrapper.
     *
     * @param op      the operator.
     * @param operand the operand.
     * @return the unary conditional expression wrapper.
     */
    public static UnaryCondition cond(Object op, Object operand) {
        return new UnaryCondition(op, operand);
    }

    /**
     * Returns a unary expression wrapper.
     *
     * @param op      the operator.
     * @param operand the operand.
     * @return the unary expression wrapper.
     */
    public static UnaryExpression expr(Object op, Object operand) {
        return new UnaryExpression(op, operand);
    }

    /**
     * Returns a variable declaration wrapper.
     *
     * @param type       the data type of the declaration.
     * @param identifier the identifier of the variable.
     * @return the variable declaration wrapper.
     */
    public static VariableDeclaration decl(Object type, String identifier) {
        return new VariableDeclaration(type, identifier);
    }

    /**
     * Returns a variable declaration wrapper.
     *
     * @param type       the data type of the declaration.
     * @param identifier the identifier of the variable.
     * @param value      the initial value to assign.
     * @return the variable declaration wrapper.
     */
    public static VariableDeclaration decl(Object type, String identifier, Object value) {
        return new VariableDeclaration(type, identifier, value);
    }

    /**
     * Returns a while statement wrapper.
     *
     * @param condition  the conditional expression.
     * @param statements the nested statements of the while body.
     * @return the while statement wrapper.
     */
    public static WhileStatement loop(Object condition, Object statements) {
        if (!(condition instanceof ConditionalExpression)) {
            condition = new Constant(condition);
        }
        return new WhileStatement(condition, statements);
    }

    /**
     * Returns an empty component list. During parse tree generation, this list will be filled with the
     * top-level statements of the code.
     * @return an empty component list.
     */
    public static LinkedList<Component> stmtList() {
        return new LinkedList<>();
    }

    /**
     * This method will return an empty list, as no nested statements are expected or
     * the nested statements will be validated otherwise.
     * If statements are expected, this method will be overridden by the according sub class.
     *
     * @return an empty statement list.
     */
    @Override
    public List<Component> getStatements() {
        return new ArrayList<>();
    }

    /**
     * This method accepts a visitor. The visitor will then have access to this instance
     * for code validation and execution. The relevant sub classes will overwrite this
     * method, the other sub classes will be processed indirectly.
     *
     * @param visitor the visitor to accept.
     */
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
