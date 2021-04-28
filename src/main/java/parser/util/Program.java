package parser.util;

public class Program implements Acceptor {

    Object o1;
    Object o2;

    public Program(Object o1, Object o2) {
        this.o1 = o1;
        this.o2 = o2;
    }

    @Override
    public String toString() {
        return "declarations: " + o1 + "\nstatements:" + o2;
    }

    @Override
    public void accept (Visitor visitor) {
        visitor.visit(this);
    }
}
