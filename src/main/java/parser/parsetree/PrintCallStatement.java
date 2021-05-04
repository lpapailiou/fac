package parser.parsetree;

import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;

public class PrintCallStatement extends Statement {


    private Object value;

    public PrintCallStatement(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public List<Statement> getStatements() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        if (value != null) {
            String out = "print(" + value.toString();
            if (value instanceof FunctionCallStatement) {
                out = out.substring(0, out.length()-2);
            }
            out += ");\n";
            return out;
        }
        return "print();\n";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
