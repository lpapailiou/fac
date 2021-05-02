package parser.parsetree;

import java.util.ArrayList;
import java.util.List;

public class ParamStatement extends Statement {

    String paramIdentifier;
    ParamStatement next;

    public ParamStatement(Object o1) {
        paramIdentifier = o1.toString();
    }

    public ParamStatement(Object o1, Object o2) {
        this(o1);
        next = (ParamStatement) o2;
    }


    @Override
    public List<Statement> getStatements() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        String out = paramIdentifier;
        if (next != null) {
            out += ", " + next;
        }
        return out;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }


}