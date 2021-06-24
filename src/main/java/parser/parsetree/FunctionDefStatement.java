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


    private final Type type;
    private final String identifier;
    private final List<ParamDeclaration> paramDeclarationList = new ArrayList<>();
    private final List<Component> componentList = new ArrayList<>();
    private final Object returnStatement;

    /**
     * This constructor will create a wrapper for a function definition.
     *
     * @param type            the return type.
     * @param identifier      the identifier.
     * @param params          the declared parameters.
     * @param statements      the nested statements.
     * @param returnStatement the return statement.
     * @param left            the start index.
     * @param right           the end index.
     */
    public FunctionDefStatement(Object type, Object identifier, Object params, Object statements, Object returnStatement, int left, int right) {
        super(left, right);
        this.type = Type.getByLiteral(type);
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
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < paramDeclarationList.size(); i++) {
            out.append(paramDeclarationList.get(i));
            if (i < paramDeclarationList.size() - 1) {
                out.append(", ");
            }
        }
        return out.toString();
    }

    /**
     * Returns the parameter types as concatenated string.
     *
     * @return the parameter type list as string.
     */
    public String paramTypeListAsString() {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < paramDeclarationList.size(); i++) {
            out.append(paramDeclarationList.get(i).getType().getLiteral());
            if (i < paramDeclarationList.size() - 1) {
                out.append(", ");
            }
        }
        return out.toString();
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
        StringBuilder out = new StringBuilder("\ndef " + type.getLiteral() + " " + identifier + "(");
        out.append(paramListAsString());
        out.append(") {\n");

        List<String> componentStrings = new ArrayList<>();
        for (Component st : componentList) {
            componentStrings.addAll(Arrays.asList(st.toString().split("\n")));
        }
        for (String str : componentStrings) {
            out.append(PRETTY_PRINT_INDENT).append(str).append("\n");
        }

        out.append(PRETTY_PRINT_INDENT + "return ").append(returnStatement).append(";\n}\n\n");
        return out.toString();
    }

    /**
     * This method returns this function definition as representation of the parse tree.
     *
     * @return a snipped of the parse tree.
     */
    @Override
    public String getParseTree() {
        StringBuilder out = getStringBuilder(this);
        appendKeyword(out, Keyword.DEF, 1);
        appendType(out, type, 1);
        appendIdentifier(out, identifier, 1);
        appendKeyword(out, Keyword.BL, 1);
        int pOffset = 0;
        for (int i = 0; i < paramDeclarationList.size(); i++) {
            Component decl = paramDeclarationList.get(i);
            appendNestedComponents(out, decl, 1 + pOffset);
            if (i < paramDeclarationList.size() - 1) {
                appendKeyword(out, Keyword.COMMA, 2 + pOffset);
            }
            pOffset++;
        }
        appendKeyword(out, Keyword.BR, 1);
        appendKeyword(out, Keyword.CBL, 1);
        int sOffset = 0;
        for (int i = 0; i < componentList.size(); i++) {
            Component comp = componentList.get(i);
            appendLine(out, "NestedStatement", 1 + sOffset);
            appendLine(out, "Statement", 2 + sOffset);
            appendNestedComponents(out, comp, 3 + sOffset);
            sOffset++;
        }
        appendLine(out, "ReturnStatement", 1);
        appendKeyword(out, Keyword.RETURN, 2);
        appendNestedComponents(out, returnStatement, 2);
        appendKeyword(out, Keyword.STOP, 2);
        appendKeyword(out, Keyword.CBR, 1);
        return out.toString();
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
