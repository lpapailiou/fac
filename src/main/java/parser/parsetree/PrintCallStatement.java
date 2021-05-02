package parser.parsetree;

import java.util.ArrayList;
import java.util.List;

public class PrintCallStatement extends Statement {


    Object value;

    public PrintCallStatement(Object value) {
        this.value = value;
    }

    @Override
    public List<Statement> getStatements() {
        List<Statement> statements = new ArrayList<>();
        if (value != null) {
            statements.add((Statement) value);
        }
        return statements;
    }

    @Override
    public String toString() {
        if (value != null) {
            String out = "print(" + value.toString();
            if (value instanceof FunctionCallStatement) {
                out = out.substring(0, out.length()-2);
            }
            return out += ");\n";
        }
        return "print();";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
