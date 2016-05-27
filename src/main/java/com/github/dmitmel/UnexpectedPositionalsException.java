package com.github.dmitmel;

import java.util.List;

public class UnexpectedPositionalsException extends ArgumentParseException {
    private List<String> positionals;
    public List<String> getPositionals() {
        return positionals;
    }

    public UnexpectedPositionalsException(List<String> positionals) {
        super(positionals.toString());
        this.positionals = positionals;
    }
}
