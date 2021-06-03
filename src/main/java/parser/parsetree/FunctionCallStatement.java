package parser.parsetree;

import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a wrapper class for function call statements.
 * It holds the identifier and a parameter list.
 */
public class FunctionCallStatement extends Statement {


    private String identifier;
    private List<Statement> parameterList = new ArrayList<>();

    public FunctionCallStatement(Object identifier) {
        this.identifier = identifier.toString();
    }

    public FunctionCallStatement(Object identifier, Object params) {
        this(identifier);
        if (params != null) {
            this.parameterList = ((Arguments) params).getStatements();
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getParamCount() {
        return parameterList.size();
    }

    public List<Statement> getParameterList() {
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
