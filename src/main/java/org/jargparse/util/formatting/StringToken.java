package org.jargparse.util.formatting;

public interface StringToken {
    String getValue();
    Type getType();

    enum Type {
        SIMPLE, STYLED
    }
}
