package parser.parsetree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FunctionCallStatement extends Statement {


    private String identifier;
    private List<String> parameterList = new ArrayList<>();

    public FunctionCallStatement(Object identifier) {
        this.identifier = identifier.toString();
    }

    public FunctionCallStatement(Object identifier, Object params) {
        this(identifier);
        if (params != null) {
            this.parameterList = Arrays.asList(params.toString().split(", "));
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getParamCount() {
        return parameterList.size();
    }

    public List<String> getParameterList() {
        return parameterList;
    }

    @Override
    public List<Statement> getStatements() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        String out = identifier + "(";
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
