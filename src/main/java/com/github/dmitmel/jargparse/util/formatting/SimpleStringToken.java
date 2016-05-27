package com.github.dmitmel.jargparse.util.formatting;

class SimpleStringToken implements StringToken {
    private String value;
    @Override
    public String getValue() {
        return value;
    }

    public SimpleStringToken(String value) {
        this.value = value;
    }

    @Override
    public Type getType() {
        return Type.SIMPLE;
    }

    @Override
    public String toString() {
        return value;
    }
}
