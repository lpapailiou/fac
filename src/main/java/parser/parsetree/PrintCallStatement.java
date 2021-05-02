package parser.parsetree;

public class PrintCallStatement extends Statement {


    Object value;

    public PrintCallStatement(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "print(" + value.toString() + ");\n";
    }
}
