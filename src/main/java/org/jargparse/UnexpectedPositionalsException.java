package org.jargparse;

import org.jargparse.argtypes.Positional;

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
