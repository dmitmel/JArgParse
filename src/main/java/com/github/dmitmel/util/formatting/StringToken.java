package com.github.dmitmel.util.formatting;

interface StringToken {
    String getValue();
    Type getType();

    enum Type {
        SIMPLE, STYLED
    }
}
