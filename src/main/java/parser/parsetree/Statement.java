package parser.parsetree;

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
        return new VariableDeclaration(t, e1.toString(), e2.toString());
    }

    public static AssignmentStatement assgn(Object op, Object e1, Object e2) {
        return new AssignmentStatement(op, e1, (Statement) e2);
    }

    public static ExpressionStatement expr(Object e1) {
        return new ExpressionStatement(e1);
    }

    public static ExpressionStatement expr(Object op, Object e1, Object e2) {
        return new ExpressionStatement(op, e1, e2);
    }

    public static ConditionalStatement cond(Object op, Object e1, Object e2) {
        return new ConditionalStatement(op, e1, e2);
    }

    public static PrintCallStatement print(Object obj) {
        return new PrintCallStatement(obj);
    }

    public static FunctionCallStatement fun(Object n, Object p) {
        return new FunctionCallStatement(n, p);
    }

    public static ParameterStatement param(Object obj) {
        return new ParameterStatement(obj);
    }

    public static ParameterStatement param(Object obj, Object list) {
        return new ParameterStatement(obj, list);
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
        return new IfThenStatement(c, obj1, obj2);
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
