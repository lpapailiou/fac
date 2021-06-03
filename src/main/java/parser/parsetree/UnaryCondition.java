package parser.parsetree;

import parser.parsetree.BinOp;
import parser.parsetree.Statement;
import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;

public class UnaryCondition extends ConditionalStatement {

    private UnOp op;
    private Object operand;

    public UnaryCondition(Object op, Object o) {
        this.op = UnOp.getName(op.toString());
        this.operand = o;
    }

    public Object getOperand() {
        return operand;
    }

    public UnOp getOperator() {
        return op;
    }

    @Override
    public List<Statement> getStatements() {
        List<Statement> statements = new ArrayList<>();
        if (operand instanceof Statement) {
            statements.add((Statement) operand);
        }
        return statements;
    }

    @Override
    public String toString() {
        return "(" + op.getOperator() + operand.toString() + ")";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
