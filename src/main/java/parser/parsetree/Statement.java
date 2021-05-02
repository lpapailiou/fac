package parser.parsetree;

public class Statement implements Acceptor {


    @Override
    public String toString() {
        return "[statement]";
    }

    @Override
    public void accept (Visitor visitor) {
        visitor.visit(this);
    }
}
