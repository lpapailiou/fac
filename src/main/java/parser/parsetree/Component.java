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

    protected static final String PARSE_TREE_PREFIX = "$ ";
    protected static final String PRETTY_PRINT_INDENT = "    ";
    private final int[] location;

    /**
     * The constructor is used to set the location from the source file of this code fragment.
     *
     * @param left  the start index.
     * @param right the end index.
     */
    Component(int left, int right) {
        this.location = new int[]{left, right};
    }

    /**
     * Returns an argument wrapper.
     *
     * @param arg   the argument to wrap.
     * @param left  the start index of the location of the code fragment.
     * @param right the end index of the location of the code fragment.
     * @return the argument wrapper class.
     */
    public static Argument param(Object arg, int left, int right) {
        return new Argument(arg, left, right);
    }

    /**
     * Returns an argument wrapper, which points to the following argument.
     *
     * @param arg   the argument to wrap.
     * @param next  the following argument.
     * @param left  the start index of the location of the code fragment.
     * @param right the end index of the location of the code fragment.
     * @return the argument wrapper.
     */
    public static Argument param(Object arg, Object next, int left, int right) {
        return new Argument(arg, next, left, right);
    }

    /**
     * Returns an assignment statement wrapper. This method is used for incrementing or decrementing
     * variables.
     *
     * @param op         the incrementing or decrementing operator.
     * @param identifier the variable identifier to modify.
     * @param left       the start index of the location of the code fragment.
     * @param right      the end index of the location of the code fragment.
     * @return the assignment statement wrapper.
     */
    public static AssignmentStatement assgn(Object op, Object identifier, int left, int right) {
        return new AssignmentStatement("=", identifier, new UnaryExpression(op, identifier, left, right), left, right);
    }

    /**
     * Returns an assignment statement wrapper. This method is used for 'regular' variable assignments.
     *
     * @param op         the assignment operator
     * @param identifier the variable identifier.
     * @param value      the value to assign.
     * @param left       the start index of the location of the code fragment.
     * @param right      the end index of the location of the code fragment.
     * @return the assignment statement wrapper.
     */
    public static AssignmentStatement assgn(Object op, Object identifier, Object value, int left, int right) {
        return new AssignmentStatement(op, identifier, value, left, right);
    }

    /**
     * Returns a binary conditional expression wrapper.
     *
     * @param op       the binary operator.
     * @param operand1 the first operand.
     * @param operand2 the second operand.
     * @param left     the start index of the location of the code fragment.
     * @param right    the end index of the location of the code fragment.
     * @return the binary conditional expression wrapper.
     */
    public static BinaryCondition cond(Object op, Object operand1, Object operand2, int left, int right) {
        return new BinaryCondition(op, operand1, operand2, left, right);
    }

    /**
     * Returns a binary expression wrapper.
     *
     * @param op       the binary operator.
     * @param operand1 the first operand.
     * @param operand2 the second operand.
     * @param left     the start index of the location of the code fragment.
     * @param right    the end index of the location of the code fragment.
     * @return the binary expression wrapper.
     */
    public static BinaryExpression expr(Object op, Object operand1, Object operand2, int left, int right) {
        return new BinaryExpression(op, operand1, operand2, left, right);
    }

    /**
     * Returns a break statement wrapper.
     *
     * @param left  the start index of the location of the code fragment.
     * @param right the end index of the location of the code fragment.
     * @return the break statement wrapper.
     */
    public static BreakStatement brk(int left, int right) {
        return new BreakStatement(left, right);
    }

    /**
     * Returns a re-packed function call wrapper, which contains the stop token.
     * This indicates that this component is used as independent statement.
     *
     * @param funCall an existing function call instance.
     * @param right   the end index of the location of the code fragment.
     * @return the function call wrapper.
     */
    public static FunctionCallStatement fun(FunctionCallStatement funCall, int right) {
        return new FunctionCallStatement(funCall, right);
    }

    /**
     * Returns a function call statement wrapper.
     *
     * @param identifier the identifier of the function call.
     * @param left       the start index of the location of the code fragment.
     * @param right      the end index of the location of the code fragment.
     * @return the function call wrapper.
     */
    public static FunctionCallStatement fun(Object identifier, int left, int right) {
        return new FunctionCallStatement(identifier, left, right);
    }

    /**
     * Returns a function call statement wrapper.
     *
     * @param identifier the identifier of the function call.
     * @param arg        the argument (chain) of the function call.
     * @param left       the start index of the location of the code fragment.
     * @param right      the end index of the location of the code fragment.
     * @return the function call wrapper.
     */
    public static FunctionCallStatement fun(Object identifier, Object arg, int left, int right) {
        if (arg instanceof ConditionalExpression) {
            return new FunctionCallStatement(identifier, new Argument(arg, ((ConditionalExpression) arg).getLocation()[0], ((ConditionalExpression) arg).getLocation()[1]), left, right);
        }
        return new FunctionCallStatement(identifier, arg, left, right);
    }

    /**
     * Returns a function definition wrapper.
     *
     * @param type            the return type of the function.
     * @param identifier      the identifier of the function.
     * @param returnStatement the return statement of the function.
     * @param left            the start index of the location of the code fragment.
     * @param right           the end index of the location of the code fragment.
     * @return the function definition wrapper.
     */
    public static FunctionDefStatement funDefEmpty(Object type, Object identifier, Object returnStatement, int left, int right) {
        return new FunctionDefStatement(type, identifier, null, null, returnStatement, left, right);
    }

    /**
     * Returns a function definition wrapper.
     *
     * @param type            the return type of the function.
     * @param identifier      the identifier of the function.
     * @param statements      the nested statements of the function.
     * @param returnStatement the return statement of the function.
     * @param left            the start index of the location of the code fragment.
     * @param right           the end index of the location of the code fragment.
     * @return the function definition wrapper.
     */
    public static FunctionDefStatement funDef(Object type, Object identifier, Object statements, Object returnStatement, int left, int right) {
        return new FunctionDefStatement(type, identifier, null, statements, returnStatement, left, right);
    }

    /**
     * Returns a function definition wrapper.
     *
     * @param type            the return type of the function.
     * @param identifier      the identifier of the function.
     * @param params          the parameter definition.
     * @param returnStatement the return statement of the function.
     * @param left            the start index of the location of the code fragment.
     * @param right           the end index of the location of the code fragment.
     * @return the function definition wrapper.
     */
    public static FunctionDefStatement funDefEmpty(Object type, Object identifier, Object params, Object returnStatement, int left, int right) {
        return new FunctionDefStatement(type, identifier, params, null, returnStatement, left, right);
    }

    /**
     * Returns a function definition wrapper.
     *
     * @param type            the return type of the function.
     * @param identifier      the identifier of the function.
     * @param params          the parameter definition.
     * @param statements      the nested statements of the function.
     * @param returnStatement the return statement of the function.
     * @param left            the start index of the location of the code fragment.
     * @param right           the end index of the location of the code fragment.
     * @return the function definition wrapper.
     */
    public static FunctionDefStatement funDef(Object type, Object identifier, Object params, Object statements, Object returnStatement, int left, int right) {
        return new FunctionDefStatement(type, identifier, params, statements, returnStatement, left, right);
    }

    /**
     * Returns an if-then statement wrapper.
     *
     * @param condition  the conditional expression.
     * @param statements the nested statements.
     * @param left       the start index of the location of the code fragment.
     * @param right      the end index of the location of the code fragment.
     * @param cleft      the start index of the location of the nested code fragment.
     * @param cright     the end index of the location of the nested code fragment.
     * @return the if-then wrapper.
     */
    public static IfThenStatement ifThen(Object condition, Object statements, int left, int right, int cleft, int cright) {
        if (!(condition instanceof ConditionalExpression)) {
            condition = new ValueWrapper(condition, cleft, cright);
        }
        return new IfThenStatement(condition, statements, left, right);
    }

    /**
     * Returns an if-then-else statement wrapper.
     *
     * @param condition      the conditional expression.
     * @param ifStatements   the nested if-then statements.
     * @param elseStatements the nested else statements.
     * @param left           the start index of the location of the code fragment.
     * @param right          the end index of the location of the code fragment.
     * @param cleft          the start index of the location of the nested code fragment.
     * @param cright         the end index of the location of the nested code fragment.
     * @return the if-then-else wrapper.
     */
    public static IfThenStatement ifThen(Object condition, Object ifStatements, Object elseStatements, int left, int right, int cleft, int cright) {
        if (!(condition instanceof ConditionalExpression)) {
            condition = new ValueWrapper(condition, cleft, cright);
        }
        return new IfThenElseStatement(condition, ifStatements, elseStatements, left, right);
    }

    /**
     * Returns a nested statement wrapper. It will wrap one statement each and form a chain with the
     * help of a pointer to the next statement.
     *
     * @param statement the statement to wrap.
     * @return the nested statement wrapper.
     */
    public static NestedStatement nest(Object statement) {
        return new NestedStatement(statement);
    }

    /**
     * Returns a nested statement wrapper. It will wrap one statement each and form a chain with the
     * help of a pointer to the next statement.
     *
     * @param statement     the statement to wrap.
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
     * @param left       the start index of the location of the code fragment.
     * @param right      the end index of the location of the code fragment.
     * @return the parameter declaration wrapper.
     */
    public static ParamDeclaration paramDecl(Object type, Object identifier, int left, int right) {
        return new ParamDeclaration(type, identifier, left, right);
    }

    /**
     * Returns a parameter declaration wrapper.
     *
     * @param type          the data type of the declaration.
     * @param identifier    the identifier of the declaration.
     * @param nextParameter the next parameter declaration.
     * @param left          the start index of the location of the code fragment.
     * @param right         the end index of the location of the code fragment.
     * @return the parameter declaration wrapper.
     */
    public static ParamDeclaration paramDecl(Object type, Object identifier, Object nextParameter, int left, int right) {
        return new ParamDeclaration(type, identifier, nextParameter, left, right);
    }

    /**
     * Returns an empty print call statement wrapper.
     *
     * @param left  the start index of the location of the code fragment.
     * @param right the end index of the location of the code fragment.
     * @return the print call wrapper.
     */
    public static PrintCallStatement print(int left, int right) {
        return new PrintCallStatement(left, right);
    }

    /**
     * Returns a print call statement wrapper.
     *
     * @param object the object to print.
     * @param left   the start index of the location of the code fragment.
     * @param right  the end index of the location of the code fragment.
     * @return the print call wrapper.
     */
    public static PrintCallStatement print(Object object, int left, int right) {
        return new PrintCallStatement(object, left, right);
    }

    /**
     * Returns a program wrapper. This will be the root of the parse tree.
     *
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
     * @param left    the start index of the location of the code fragment.
     * @param right   the end index of the location of the code fragment.
     * @return the unary conditional expression wrapper.
     */
    public static UnaryCondition cond(Object op, Object operand, int left, int right) {
        return new UnaryCondition(op, operand, left, right);
    }

    /**
     * Returns a unary expression wrapper.
     *
     * @param op      the operator.
     * @param operand the operand.
     * @param left    the start index of the location of the code fragment.
     * @param right   the end index of the location of the code fragment.
     * @return the unary expression wrapper.
     */
    public static UnaryExpression expr(Object op, Object operand, int left, int right) {
        return new UnaryExpression(op, operand, left, right);
    }

    /**
     * Returns a constant wrapper if the passed object is not already a wrapped component.
     *
     * @param object the constant to wrap.
     * @param left   the start index of the location of the code fragment.
     * @param right  the end index of the location of the code fragment.
     * @return a wrapped primitive.
     */
    public static Component wrap(Object object, int left, int right) {
        if (object instanceof Component) {
            return (Component) object;
        }
        return new ValueWrapper(object, false, left, right);
    }

    /**
     * Returns a constant wrapper for primitives used in conditional statements.
     *
     * @param object      the constant to wrap.
     * @param hasBrackets indicates if the wrapped object has brackets around.
     * @param left        the start index of the location of the code fragment.
     * @param right       the end index of the location of the code fragment.
     * @return a wrapped primitive.
     */
    public static Component wrap(Object object, boolean hasBrackets, int left, int right) {
        return new ValueWrapper(object, hasBrackets, left, right);
    }

    /**
     * Returns a variable declaration wrapper.
     *
     * @param type       the data type of the declaration.
     * @param identifier the identifier of the variable.
     * @param left       the start index of the location of the code fragment.
     * @param right      the end index of the location of the code fragment.
     * @return the variable declaration wrapper.
     */
    public static VariableDeclaration decl(Object type, String identifier, int left, int right) {
        return new VariableDeclaration(type, identifier, left, right);
    }

    /**
     * Returns a variable declaration wrapper.
     *
     * @param type       the data type of the declaration.
     * @param identifier the identifier of the variable.
     * @param value      the initial value to assign.
     * @param left       the start index of the location of the code fragment.
     * @param right      the end index of the location of the code fragment.
     * @return the variable declaration wrapper.
     */
    public static VariableDeclaration decl(Object type, String identifier, Object value, int left, int right) {
        return new VariableDeclaration(type, identifier, value, left, right);
    }

    /**
     * Returns a while statement wrapper.
     *
     * @param condition  the conditional expression.
     * @param statements the nested statements of the while body.
     * @param left       the start index of the location of the code fragment.
     * @param right      the end index of the location of the code fragment.
     * @param cleft      the start index of the location of the nested code fragment.
     * @param cright     the end index of the location of the nested code fragment.
     * @return the while statement wrapper.
     */
    public static WhileStatement loop(Object condition, Object statements, int left, int right, int cleft, int cright) {
        if (!(condition instanceof ConditionalExpression)) {
            condition = new ValueWrapper(condition, cleft, cright);
        }
        return new WhileStatement(condition, statements, left, right);
    }

    /**
     * Returns an empty component list. During parse tree generation, this list will be filled with the
     * top-level statements of the code.
     *
     * @return an empty component list.
     */
    public static LinkedList<Component> stmtList() {
        return new LinkedList<>();
    }

    /**
     * Returns the isolated class name of an object as string.
     *
     * @param object the object to extract the class name from.
     * @return the class name as string.
     */
    protected static String getClassName(Object object) {
        String name = object.getClass().getName();
        return name.substring(name.lastIndexOf(".") + 1);
    }

    /**
     * Returns a string builder with the class name of a class and the parse tree prefix.
     * This content will be the head of a parse tree output for a program component.
     *
     * @param node the node to create a string builder for.
     * @return the string builder which contains the class name.
     */
    protected static StringBuilder getStringBuilder(Traversable node) {
        return new StringBuilder(PARSE_TREE_PREFIX + getClassName(node) + "\n");
    }

    /**
     * With this method, a parse tree section is constructed by appending an object.
     *
     * @param out          the string builder used to construct the parse tree.
     * @param obj          the object to append.
     * @param nestingDepth the nesting depth of the parse tree.
     */
    protected static void appendLine(StringBuilder out, Object obj, int nestingDepth) {
        for (int i = 0; i < nestingDepth; i++) {
            out.append(PRETTY_PRINT_INDENT);
        }
        out.append(PARSE_TREE_PREFIX).append(obj).append("\n");
    }

    // ------------------------------------------ output helper methods ------------------------------------------

    /**
     * With this method, a parse tree section is constructed by appending a keyword.
     *
     * @param out          the string builder used to construct the parse tree.
     * @param keyword      the keyword to append.
     * @param nestingDepth the nesting depth of the parse tree.
     */
    protected static void appendKeyword(StringBuilder out, Keyword keyword, int nestingDepth) {
        appendLine(out, keyword.name(), nestingDepth);
        appendLine(out, keyword.getLiteral(), nestingDepth + 1);
    }

    /**
     * With this method, a parse tree section is constructed by appending a type.
     *
     * @param out          the string builder used to construct the parse tree.
     * @param type         the type to append.
     * @param nestingDepth the nesting depth of the parse tree.
     */
    protected static void appendType(StringBuilder out, Type type, int nestingDepth) {
        appendLine(out, getClassName(type), nestingDepth);
        appendLine(out, type.getLiteral(), nestingDepth + 1);
    }

    /**
     * With this method, a parse tree section is constructed by appending a binary operator.
     *
     * @param out          the string builder used to construct the parse tree.
     * @param op           the binary operator to append.
     * @param nestingDepth the nesting depth of the parse tree.
     */
    protected static void appendBinOp(StringBuilder out, BinOp op, int nestingDepth) {
        appendLine(out, getClassName(op), nestingDepth);
        appendLine(out, op.getLiteral(), nestingDepth + 1);
    }

    /**
     * With this method, a parse tree section is constructed by appending a unary operator.
     *
     * @param out          the string builder used to construct the parse tree.
     * @param op           the unary operator to append.
     * @param nestingDepth the nesting depth of the parse tree.
     */
    protected static void appendUnOp(StringBuilder out, UnOp op, int nestingDepth) {
        appendLine(out, getClassName(op), nestingDepth);
        appendLine(out, op.getLiteral(), nestingDepth + 1);
    }

    /**
     * With this method, a parse tree section is constructed by appending an identifier.
     *
     * @param out          the string builder used to construct the parse tree.
     * @param identifier   the identifier to append.
     * @param nestingDepth the nesting depth of the parse tree.
     */
    protected static void appendIdentifier(StringBuilder out, String identifier, int nestingDepth) {
        appendLine(out, "Identifier", nestingDepth);
        appendLine(out, identifier, nestingDepth + 1);
    }

    /**
     * With this method, a parse tree section is constructed by appending a complex component or primitive.
     *
     * @param out          the string builder used to construct the parse tree.
     * @param obj          the complex component to append.
     * @param nestingDepth the nesting depth of the parse tree.
     */
    protected static void appendNestedComponents(StringBuilder out, Object obj, int nestingDepth) {
        if (obj == null) {
            return;
        }
        if (obj instanceof Component) {
            String[] components = ((Component) obj).getParseTree().split("\n");
            for (String str : components) {
                for (int i = 0; i < nestingDepth; i++) {
                    out.append(PRETTY_PRINT_INDENT);
                }
                out.append(str).append("\n");
            }
        } else {
            appendLine(out, Type.getByInput(obj), nestingDepth);
            appendLine(out, obj.toString(), nestingDepth + 1);
        }
    }

    /**
     * With this method, a parse tree section is constructed by appending nested statements.
     *
     * @param out           the string builder used to construct the parse tree.
     * @param componentList the list of nested statements to append
     * @param nestingDepth  the nesting depth of the parse tree.
     */
    protected void appendNestedStatements(StringBuilder out, List<Component> componentList, int nestingDepth) {
        int offset = 0;
        for (int i = 0; i < componentList.size(); i++) {
            Component comp = componentList.get(i);
            appendLine(out, "NestedStatement", nestingDepth + offset);
            appendLine(out, "Statement", nestingDepth + 1 + offset);
            appendNestedComponents(out, comp, nestingDepth + 2 + offset);
            offset++;
        }
    }

    /**
     * With this method, the source of an 'expression' within the parse tree is reconstructed.
     *
     * @param object the object to evaluate.
     * @return the name of the parser token as string.
     */
    protected String evaluateExpression(Object object) {
        if (object instanceof BinaryExpression) {
            BinOp op = ((BinaryExpression) object).getOperator();
            if (op == BinOp.MUL || op == BinOp.DIV || op == BinOp.MOD) {
                return "ExpressionWithPrecedence";
            }
            return "ExpressionWithoutPrecedence";
        }
        return "ComponentWithValue";
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

    /**
     * Returns the location from the source file of this code fragment.
     *
     * @return the location of this code fragment.
     */
    @Override
    public int[] getLocation() {
        return location;
    }
}
