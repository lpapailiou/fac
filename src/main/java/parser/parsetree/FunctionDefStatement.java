package parser.parsetree;

import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;

public class FunctionDefStatement extends Statement {


    private Type type;
    private String identifier;
    private List<ParamDeclaration> paramDeclarationList = new ArrayList<>();
    private List<Statement> statementList = new ArrayList<>();
    private Object returnVal;

    public FunctionDefStatement(Object type, Object name, Object params, Object statements, Object returnVal) {
        this.type = Type.getByName(type);
        this.identifier = name.toString();
        if (params != null) {
            ParamDeclaration decl = (ParamDeclaration) params;
            while (decl != null) {
                paramDeclarationList.add(decl);
                decl = decl.getNext();
            }
        }
        if (statements != null) {
            this.statementList.addAll(((NestedStatement) statements).getStatements());
        }
        this.returnVal = returnVal;

    }

    public String getIdentifier() {
        return identifier;
    }

    public Type getType() {
        return type;
    }

    public Object getReturnValue() {
        return returnVal;
    }

    public int getParamCount() {
        return paramDeclarationList.size();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public List<Statement> getStatements() {
        List<Statement> statements = new ArrayList<>();
        statements.addAll(paramDeclarationList);        // TODO: needed?
        statements.addAll(statementList);
        return statements;
    }

    public String paramListAsString() {
        String out = "";
        for (int i = 0; i < paramDeclarationList.size(); i++) {
            out += paramDeclarationList.get(i);
            if (i < paramDeclarationList.size()-1) {
                out += ", ";
            }
        }
        return out;
    }

    public String paramTypeListAsString() {
        String out = "";
        for (int i = 0; i < paramDeclarationList.size(); i++) {
            out += paramDeclarationList.get(i).getType().getDescription();
            if (i < paramDeclarationList.size()-1) {
                out += ", ";
            }
        }
        return out;
    }

    @Override
    public String toString() {
        String out = "\ndef " + type.getDescription() + " " + identifier + "(";
        out += paramListAsString();
        out += ") {\n";
        for (Statement st: statementList) {
            out += "\t" + st.toString();
        }

        out += "\treturn " + returnVal + ";\n}\n\n";
        return out;
    }
}
