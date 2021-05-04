package parser.parsetree;


import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;

public class UnaryExpression extends ExpressionStatement {

    private Object operand;

    public UnaryExpression(Object operand) {
        this.operand = operand;
    }

    public Object getOperand() {
        return operand;
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
        String out = "(" + operand.toString();
        if (operand instanceof FunctionCallStatement) {
            out = out.substring(0, out.length()-2);
        }
        out += ")";
        return out;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}