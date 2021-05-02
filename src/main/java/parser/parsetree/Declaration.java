package parser.parsetree;

public interface Declaration {

    Type getType();

    String getIdentifier();

    Object getValue();
}
