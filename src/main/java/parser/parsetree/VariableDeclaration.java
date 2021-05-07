package parser.parsetree;

import parser.parsetree.interfaces.Declaration;
import parser.parsetree.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;

public class VariableDeclaration extends Statement implements Declaration {

    private Type type;
    private Object identifier;
    private Object value;
    private Object initValue;

    public VariableDeclaration(Object type, Object identifier, Object value) {
        this.type = Type.getByName(type);
        this.identifier = identifier;
        this.value = (value == null) ? Type.getByName(type).getDefaultValue() : value;
        this.initValue = value;
    }

    public void reset() {
        value = initValue;
    }

    @Override
    public List<Statement> getStatements() {
        List<Statement> statements = new ArrayList<>();
        if (value instanceof Statement) {
            statements.add((Statement) value);
        }
        return statements;
    }

    @Override
    public String toString() {
        String out =  type.getDescription() + " " + identifier + " = " + value;
        if (value instanceof FunctionCallStatement) {
            out = out.substring(0, out.length()-2);
        }
        return  out + ";\n";
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getIdentifier() {
        return identifier.toString();
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object obj) {
        this.value = obj;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
