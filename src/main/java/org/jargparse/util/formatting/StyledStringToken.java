package org.jargparse.util.formatting;

import java.util.Objects;

public class StyledStringToken implements StringToken {
    private String value;
    @Override
    public String getValue() {
        return value;
    }

    private String newLineSpacing;
    public String getNewLineSpacing() {
        return newLineSpacing;
    }

    public StyledStringToken(String value, String newLineSpacing) {
        Objects.requireNonNull(newLineSpacing);
        this.newLineSpacing = newLineSpacing;
        this.value = value;
    }

    @Override
    public Type getType() {
        return Type.STYLED;
    }

    @Override
    public String toString() {
        return value;
    }
}
