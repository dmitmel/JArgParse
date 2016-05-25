package org.jargparse.util.formatting;

interface StringToken {
    String getValue();
    Type getType();

    enum Type {
        SIMPLE, STYLED
    }
}
