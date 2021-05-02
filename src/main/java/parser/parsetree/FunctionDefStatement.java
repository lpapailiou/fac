package parser.parsetree;

import java.util.ArrayList;
import java.util.List;

public class FunctionDefStatement extends Statement {


    String type;
    String name;
    List<ParamDeclaration> paramDeclarationList = new ArrayList<>();
    List<Statement> statementList = new ArrayList<>();
    Object returnVal;

    public FunctionDefStatement(Object type, Object name, Object params, Object statements, Object returnVal) {
        this.type = type.toString();
        this.name = name.toString();
        if (params != null) {
            ParamDeclaration decl = (ParamDeclaration) params;
            while (decl != null) {
                paramDeclarationList.add(decl);
                decl = decl.getNext();
            }
        }
        this.statementList.addAll(((NestedStatement) statements).statementList);
        this.returnVal = returnVal;

    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public List<Statement> getStatements() {
        List<Statement> statements = new ArrayList<>();
        statements.addAll(paramDeclarationList);
        statements.addAll(statementList);
        return statements;
    }

    @Override
    public String toString() {
        String out = "\ndef " + type + " " + name + "(";
        for (int i = 0; i < paramDeclarationList.size(); i++) {
            out += paramDeclarationList.get(i);
            if (i < paramDeclarationList.size()-1) {
                out += ", ";
            }
        }
        out += ") {\n";
        for (Statement st: statementList) {
            out += "\t" + st.toString();
        }

        out += "\treturn " + returnVal + ";\n}\n\n";
        return out;
    }
}
