package parser.parsetree;

import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a wrapper class for assignment statements.
 * Its instances will hold an assignment operator, an identifier and an assigned value.
 */
public class AssignmentStatement extends Statement {

    private BinOp op;
    private String identifier;
    private Statement statement;

    public AssignmentStatement(Object op, Object identifier, Statement statement) {
        this.op = BinOp.getName(op.toString());
        this.identifier = identifier.toString();
        this.statement = statement;
    }

    public String getIdentifier() {
        return identifier;
    }

    public BinOp getOperator() {
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
