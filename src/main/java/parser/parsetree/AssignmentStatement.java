package parser.parsetree;

import java.util.ArrayList;
import java.util.List;

public class AssignmentStatement extends Statement {

    Operator op;
    Object identifier;
    Statement statement;

    public AssignmentStatement(Object op, Object identifier, Statement statement) {
        this.op = Operator.getName(op.toString());
        this.identifier = identifier;
        this.statement = statement;
    }


    @Override
    public List<Statement> getStatements() {
        List<Statement> statements = new ArrayList<Statement>();
        statements.add(statement);
        return statements;
    }

    @Override
    public String toString() {
        return identifier.toString() + " " + op.getOperator() + " " + statement.toString() + ";\n";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
