package parser.parsetree;


import java.util.ArrayList;
import java.util.List;

public class UnaryExpression extends ExpressionStatement {

    Operator op;
    Object operand1;

    public UnaryExpression(Object operand1) {
        this.operand1 = operand1;
        if (op == null) {
            op = Operator.NONE;
        }
    }

    public UnaryExpression(Object op, Object operand1, Object operand2) {
        this(operand1);
        this.op = Operator.getName(op);
    }

    public Object getOperand() {
        return operand1;
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
        return statements;
    }

    @Override
    public String toString() {
        return operand1.toString();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}