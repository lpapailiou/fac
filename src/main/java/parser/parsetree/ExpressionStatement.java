package parser.parsetree;


import java.util.ArrayList;
import java.util.List;

public class ExpressionStatement extends Statement {

    Operator op;
    Object operand1;
    Object operand2;

    public ExpressionStatement(Object operand1) {
        this.operand1 = operand1;
    }

    public ExpressionStatement(Object op, Object operand1, Object operand2) {
        this(operand1);
        this.op = Operator.getName(op);

        this.operand2 = operand2;
    }

    public Object getOperand1() {
        return operand1;
    }

    public Object getOperand2() {
        return operand2;
    }

    public Operator getOperator() {
        return op;
    }

    @Override
    public List<Statement> getStatements() {
        List<Statement> statements = new ArrayList<>();
        if (operand1 instanceof Statement) {
            statements.add((Statement) operand1);
        }
        if (operand2 != null && operand2 instanceof Statement) {
            statements.add((Statement) operand2);
        }
        return statements;
    }

    @Override
    public String toString() {
        if (operand2 != null) {
            return operand1.toString() + " " + op.getOperator() + " " + operand2.toString();
        }
        return operand1.toString();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}