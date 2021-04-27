package parser.util;

public class Declaration {

    private Object type;
    private String identifier;
    private Object value;

    public Declaration(Object type, String identifier, Object value) {
        this.type = type;
        this.identifier = identifier;
        this.value = value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

}
