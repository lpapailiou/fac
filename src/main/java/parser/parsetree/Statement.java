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
public abstract class Statement implements Traversable {

    public static VariableDeclaration decl(Object t, String e1, Object e2) {
        return new VariableDeclaration(t, e1, e2);
    }

    public static VariableDeclaration decl(Object t, String e1) {
        return new VariableDeclaration(t, e1);
    }

    public static AssignmentStatement assgn(Object op, Object e1, Object e2) {
        return new AssignmentStatement(op, e1, e2);
    }

    public static AssignmentStatement assgn(Object op, Object o) {
        return new AssignmentStatement("=", o, new UnaryExpression(op, o));
    }

    public static Constant constnt(Object obj) {
        return new Constant(obj);
    }

    public static UnaryExpression expr(Object op, Object e1) {
        return new UnaryExpression(op, e1);
    }

    public static BinaryExpression expr(Object op, Object e1, Object e2) {
        return new BinaryExpression(op, e1, e2);
    }

    public static UnaryCondition cond(Object op, Object e) {
        return new UnaryCondition(op, e);
    }

    public static BinaryCondition cond(Object op, Object e1, Object e2) {
        return new BinaryCondition(op, e1, e2);
    }

    public static PrintCallStatement print(Object obj) {
        return new PrintCallStatement(obj);
    }

    public static PrintCallStatement print() {
        return new PrintCallStatement(null);
    }

    public static FunctionCallStatement fun(Object n) {
        return new FunctionCallStatement(n);
    }

    public static FunctionCallStatement fun(Object n, Object p) {
        if (p instanceof ConditionalExpression) {
            return new FunctionCallStatement(n, new Argument(p));
        }
        return new FunctionCallStatement(n, p);
    }

    public static Argument param(Object obj) {
        return new Argument(obj);
    }

    public static Argument param(Object obj, Object list) {
        return new Argument(obj, list);
    }

    public static ParamDeclaration paramDecl(Object t, Object v) {
        return new ParamDeclaration(t, v);
    }

    public static ParamDeclaration paramDecl(Object t, Object v, Object p) {
        return new ParamDeclaration(t, v, p);
    }

    public static FunctionDefStatement funDef(Object t, Object n, Object p, Object st, Object r) {
        return new FunctionDefStatement(t, n, p, st, r);
    }

    public static FunctionDefStatement funDef(Object t, Object n, Object st, Object r) {
        return new FunctionDefStatement(t, n, null, st, r);
    }

    public static FunctionDefStatement funDefEmpty(Object t, Object n, Object p, Object r) {
        return new FunctionDefStatement(t, n, p, null, r);
    }

    public static FunctionDefStatement funDefEmpty(Object t, Object n, Object r) {
        return new FunctionDefStatement(t, n, null, null, r);
    }

    public static IfThenStatement ifThen(Object c, Object obj) {
        if (!(c instanceof ConditionalExpression)) {
            c = new Constant(c);
        }
        return new IfThenStatement(c, obj);
    }

    public static IfThenStatement ifThen(Object c, Object obj1, Object obj2) {
        if (!(c instanceof ConditionalExpression)) {
            c = new Constant(c);
        }
        return new IfThenElseStatement(c, obj1, obj2);
    }

    public static WhileStatement loop(Object c, Object obj) {
        if (!(c instanceof ConditionalExpression)) {
            c = new Constant(c);
        }
        return new WhileStatement(c, obj);
    }

    public static BreakStatement brk() {
        return new BreakStatement();
    }

    public static NestedStatement nest(Object obj) {
        return new NestedStatement(obj);
    }

    public static NestedStatement nest(Object obj1, Object obj2) {
        return new NestedStatement(obj1, obj2);
    }

    public static LinkedList<Statement> stmtList() {
        return new LinkedList<>();
    }

    public static Program prog(List<Statement> list) {
        return new Program(list);
    }

    /**
     * This method will return an empty list, as no nested statements are expected or
     * the nested statements will be validated otherwise.
     * If statements are expected, this method will be overridden by the according sub class.
     *
     * @return an empty statement list.
     */
    @Override
    public List<Statement> getStatements() {
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
