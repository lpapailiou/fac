package parser.util;

public class Statement implements Acceptor {



    @Override
    public void accept (Visitor visitor) {
        visitor.visit(this);
    }
}
