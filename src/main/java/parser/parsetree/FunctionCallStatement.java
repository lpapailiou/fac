package parser.parsetree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FunctionCallStatement extends Statement {


    String name;
    List<String> parameterList;     // TODO

    public FunctionCallStatement(Object name, Object params) {
        this.name = name.toString();
        this.parameterList = ((ParameterStatement) params).paramList;
    }


    @Override
    public List<Statement> getStatements() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        String out = name + "(";
        for (int i = 0; i < parameterList.size(); i++) {
            out += parameterList.get(i);
            if (i != parameterList.size()-1) {
                out += ", ";
            }
        }
        out += ");\n";

        return out;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
