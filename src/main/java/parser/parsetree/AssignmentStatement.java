package parser.parsetree;

import java.util.ArrayList;
import java.util.List;

public class AssignmentStatement extends Statement {

    Operator op;
    String identifier;
    Statement statement;

    public AssignmentStatement(Object op, Object identifier, Statement statement) {
        this.op = Operator.getName(op.toString());
        this.identifier = identifier.toString();
        this.statement = statement;
    }

    public String getIdentifier() {
        return identifier;
    }


    @Override
    public List<Statement> getStatements() {
        List<Statement> statements = new ArrayList<Statement>();
        statements.add(statement);
        return statements;
    }

    @Override
    public String toString() {
        return identifier + " " + op.getOperator() + " " + statement + ";\n";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
