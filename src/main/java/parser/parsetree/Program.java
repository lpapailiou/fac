package parser.parsetree;

import parser.parsetree.interfaces.Traversable;
import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a wrapper class for the program instance. This is the root of the parse tree.
 * It will hold all first-level components of the program as statement list. Potentially,
 * every statement can have nested statements.
 */
public class Program implements Traversable {

    private List<Statement> statementList = new ArrayList<>();

    public Program(List<Statement> o1) {
        this.statementList.addAll(o1);
    }

    public List<Statement> getStatements() {
        return statementList;
    }

    @Override
    public String toString() {
        String out = "";
        for (Statement st : statementList) {
            out += st.toString();
        }

        return  out;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }


}
