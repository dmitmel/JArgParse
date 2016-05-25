package org.jargparse.util.formatting;

import java.util.Objects;

/**
 * Class for styled string tokens. They can contain this features:
 * <ol>
 *     <li>new line spacing, that can be injected if there was newline before token</li>
 * </ol>
 */
class StyledStringToken implements StringToken {
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
