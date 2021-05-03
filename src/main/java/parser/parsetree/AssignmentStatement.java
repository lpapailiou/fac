package parser.parsetree;

import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;

public class AssignmentStatement extends Statement {

    private Operator op;
    private String identifier;
    private Statement statement;

    public AssignmentStatement(Object op, Object identifier, Statement statement) {
        this.op = Operator.getName(op.toString());
        this.identifier = identifier.toString();
        this.statement = statement;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Operator getOperator() {
        return op;
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
