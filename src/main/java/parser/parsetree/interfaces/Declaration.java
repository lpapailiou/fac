package parser.parsetree.interfaces;

import parser.parsetree.Type;

public interface Declaration {

    Type getType();

    String getIdentifier();

    Object getValue();

    void setValue(Object obj);

    void reset();
}
