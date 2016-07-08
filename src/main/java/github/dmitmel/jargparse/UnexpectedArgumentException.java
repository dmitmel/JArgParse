package github.dmitmel.jargparse;

public class UnexpectedArgumentException extends ArgumentParseException {
    private String argument;
    public String getArgument() {
        return argument;
    }

    private Argument.Type type;
    public Argument.Type getType() {
        return type;
    }

    public UnexpectedArgumentException(String argument, Argument.Type type) {
        super(String.format("\"%s\", \"%s\"", argument, type));
        this.argument = argument;
        this.type = type;
    }
}
