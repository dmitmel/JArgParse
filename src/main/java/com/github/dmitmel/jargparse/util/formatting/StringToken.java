package com.github.dmitmel.jargparse.util.formatting;

interface StringToken {
    String getValue();
    Type getType();

    enum Type {
        SIMPLE, STYLED
    }
}
