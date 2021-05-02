package parser.parsetree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParameterStatement extends Statement {

    List<String> paramList = new ArrayList<>();

    public ParameterStatement(Object o1) {
        paramList.add(o1.toString());
    }

    public ParameterStatement(Object o1, Object o2) {
        this(o1);
        paramList.addAll(((ParameterStatement) o2).paramList);
    }


    @Override
    public List<Statement> getStatements() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        String out = "";
        for (int i = 0; i < paramList.size(); i++) {
            out += paramList.get(i);
            if (i < paramList.size()-1) {
                out += ", ";
            }
        }

        return out;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }


}