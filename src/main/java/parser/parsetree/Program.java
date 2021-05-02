package parser.parsetree;

import java.util.ArrayList;
import java.util.List;

public class Program implements Traversable {

    List<Statement> statementList = new ArrayList<>();

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
