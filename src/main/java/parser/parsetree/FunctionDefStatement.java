package parser.parsetree;

import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is a wrapper class for function definitions.
 * It holds the declared type of the function, the identifier, the parameter declarations, the
 * statements of the function body and the return statement.
 */
public class FunctionDefStatement extends Component {


    private Type type;
    private String identifier;
    private List<ParamDeclaration> paramDeclarationList = new ArrayList<>();
    private List<Component> componentList = new ArrayList<>();
    private Object returnStatement;

    /**
     * This constructor will create a wrapper for a function definition.
     *
     * @param type            the return type.
     * @param identifier      the identifier.
     * @param params          the declared parameters.
     * @param statements      the nested statements.
     * @param returnStatement the return statement.
     */
    public FunctionDefStatement(Object type, Object identifier, Object params, Object statements, Object returnStatement) {
        this.type = Type.getByName(type);
        this.identifier = identifier.toString();
        if (params != null) {
            ParamDeclaration decl = (ParamDeclaration) params;
            while (decl != null) {
                paramDeclarationList.add(decl);
                decl = decl.getNext();
            }
        }
        if (statements != null) {
            this.componentList.addAll(((NestedStatement) statements).getStatements());
        }
        this.returnStatement = returnStatement;

    }

    /**
     * Returns the identifier of the function definition.
     *
     * @return the identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Returns the return type of the function.
     *
     * @return the return type.
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the return statement of the function.
     *
     * @return the return statement.
     */
    public Object getReturnStatement() {
        return returnStatement;
    }

    /**
     * Returns the count of parameters of the function.
     *
     * @return the parameter count.
     */
    public int getParamCount() {
        return paramDeclarationList.size();
    }

    /**
     * Returns the parameter types as concatenated string.
     *
     * @return the parameter list as string.
     */
    public String paramListAsString() {
        String out = "";
        for (int i = 0; i < paramDeclarationList.size(); i++) {
            out += paramDeclarationList.get(i);
            if (i < paramDeclarationList.size() - 1) {
                out += ", ";
            }
        }
        return out;
    }

    /**
     * Returns the parameter types as concatenated string.
     *
     * @return the parameter type list as string.
     */
    public String paramTypeListAsString() {
        String out = "";
        for (int i = 0; i < paramDeclarationList.size(); i++) {
            out += paramDeclarationList.get(i).getType().getIdentifier();
            if (i < paramDeclarationList.size() - 1) {
                out += ", ";
            }
        }
        return out;
    }

    /**
     * Returns the nested statements of the function. The parameter declaration is included, the
     * return statement is excluded.
     *
     * @return the parameter list.
     */
    @Override
    public List<Component> getStatements() {
        List<Component> components = new ArrayList<>();
        components.addAll(paramDeclarationList);
        components.addAll(componentList);
        return components;
    }

    /**
     * The toString method provides a pretty-printable String
     * of this parse tree component.
     * It is generated by the contents of this instance and may not be equal to the original code.
     *
     * @return the pretty-printed code.
     */
    @Override
    public String toString() {
        String out = "\ndef " + type.getIdentifier() + " " + identifier + "(";
        out += paramListAsString();
        out += ") {\n";

        List<String> componentStrings = new ArrayList<>();
        for (Component st : componentList) {
            componentStrings.addAll(Arrays.asList(st.toString().split("\n")));
        }
        for (String str : componentStrings) {
            out += "\t" + str + "\n";
        }

        out += "\treturn " + returnStatement + ";\n}\n\n";
        return out;
    }

    /**
     * This method accepts a visitor. The visitor will then have access to this instance
     * for code validation and execution.
     *
     * @param visitor the visitor to accept.
     */
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
