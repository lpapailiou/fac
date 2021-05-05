package parser.parsetree;

import parser.parsetree.interfaces.Traversable;
import parser.parsetree.interfaces.Visitor;

import java.util.LinkedList;
import java.util.List;

public abstract class Statement implements Traversable {

    @Override
    public String toString() {
        return "[statement]";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public static VariableDeclaration decl(Object t, Object e1, Object e2) {
        return new VariableDeclaration(t, e1, e2);
    }

    public static VariableDeclaration decl(Object t, Object e1) {
        return new VariableDeclaration(t, e1, null);
    }

    public static AssignmentStatement assgn(Object op, Object e1, Object e2) {
        return new AssignmentStatement(op, e1, (Statement) e2);
    }

    public static UnaryExpression expr(Object e1) {
        return new UnaryExpression(e1);
    }

    public static BinaryExpression expr(Object op, Object e1, Object e2) {
        return new BinaryExpression(op, e1, e2);
    }

    public static ConditionalStatement cond(Object op, Object e1, Object e2) {
        return new ConditionalStatement(op, e1, e2);
    }

    public static PrintCallStatement print(Object obj) {
        return new PrintCallStatement(obj);
    }

    public static FunctionCallStatement fun(Object n) {
        return new FunctionCallStatement(n);
    }

    public static FunctionCallStatement fun(Object n, Object p) {
        return new FunctionCallStatement(n, p);
    }

    public static ParamStatement param(Object obj) {
        return new ParamStatement(obj);
    }

    public static ParamStatement param(Object obj, Object list) {
        return new ParamStatement(obj, list);
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
        if (!(c instanceof ConditionalStatement)) {
            c = new ConditionalStatement(c);
        }
        return new IfThenStatement(c, obj);
    }

    public static IfThenStatement ifThen(Object c, Object obj1, Object obj2) {
        if (!(c instanceof ConditionalStatement)) {
            c = new ConditionalStatement(c);
        }
        return new IfThenElseStatement(c, obj1, obj2);
    }

    public static WhileStatement loop(Object c, Object obj) {
        if (!(c instanceof ConditionalStatement)) {
            c = new ConditionalStatement(c);
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
        return new LinkedList<Statement>();
    }

    public static Program prog(List<Statement> list) {
        return new Program(list);
    }

}
