package parser.parsetree;

import java.util.ArrayList;
import java.util.List;

public class Program implements Acceptor {

    List<Declaration> declarationList = new ArrayList<>();
    List<Statement> statementList = new ArrayList<>();

    public Program(List<Statement> o1) {
        this.statementList.addAll(o1);
        for (Statement st : statementList) {
            if (st instanceof Declaration) {
                declarationList.add((Declaration) st);
            }
        }
    }

    public List<Declaration> getDeclarations() {
        return declarationList;
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
    public void accept (Visitor visitor) {
        visitor.visit(this);
    }
}
