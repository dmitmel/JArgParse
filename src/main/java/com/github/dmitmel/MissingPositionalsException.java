package com.github.dmitmel;

import com.github.dmitmel.argtypes.Positional;

import java.util.List;

public class MissingPositionalsException extends ArgumentParseException {
    private List<Positional> positionals;
    public List<Positional> getPositionals() {
        return positionals;
    }

    public MissingPositionalsException(List<Positional> positionals) {
        super(positionals.toString());
        this.positionals = positionals;
    }
}