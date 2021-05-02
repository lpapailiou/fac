package parser.parsetree;


import java.util.ArrayList;
import java.util.List;

public class BreakStatement extends Statement {

    @Override
    public List<Statement> getStatements() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return "break;\n";
    }


}