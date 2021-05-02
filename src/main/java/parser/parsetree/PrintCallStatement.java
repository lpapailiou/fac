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
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return "print(" + value.toString() + ");\n";
    }
}
