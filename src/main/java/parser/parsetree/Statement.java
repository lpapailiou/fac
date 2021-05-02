package parser.parsetree;

public abstract class Statement implements Traversable {

    @Override
    public String toString() {
        return "[statement]";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
