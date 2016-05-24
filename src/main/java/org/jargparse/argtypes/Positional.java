package org.jargparse.argtypes;

public class Positional extends Argument {
    public Usage usage;
    public String defaultValue;

    @Override
    public Type getType() {
        return Type.POSITIONAL;
    }

    public Positional(String helpInfo, String metaVar, String parseResultKey) {
        this(helpInfo, metaVar, parseResultKey, Usage.REQUIRED);
    }

    public Positional(String helpInfo, String metaVar, String parseResultKey, Usage usage) {
        this(helpInfo, metaVar, parseResultKey, usage, null);
    }

    public Positional(String helpInfo, String metaVar, String parseResultKey, Usage usage, String defaultValue) {
        super.helpInfo = helpInfo;
        super.metaVar = metaVar;
        super.parseResultKey = parseResultKey;
        this.usage = usage;
        this.defaultValue = defaultValue;
    }

    public enum Usage {
        REQUIRED, OPTIONAL, ZERO_OR_MORE
    }
}
