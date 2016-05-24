package org.jargparse.argtypes;

import org.jargparse.ArgumentList;

public class Option extends Argument {
    public String defaultValue;

    @Override
    public Type getType() {
        return Type.OPTION;
    }

    public Option(String name, String helpInfo, String parseResultKey, String defaultValue) {
        this.name = name;
        this.helpInfo = helpInfo;
        super.parseResultKey = parseResultKey;
        this.defaultValue = defaultValue;
    }

    public Option(String name, String longName, String helpInfo, String parseResultKey, String defaultValue) {
        this(name, longName, helpInfo, metaVarFromName(name), parseResultKey, defaultValue);
    }

    public Option(String name, String longName, String helpInfo, String metaVar, String parseResultKey,
                  String defaultValue) {
        super.name = name;
        super.longName = longName;
        super.helpInfo = helpInfo;
        super.metaVar = metaVar;
        super.parseResultKey = parseResultKey;
        this.defaultValue = defaultValue;
    }

    public static Option findFromArgumentListByName(ArgumentList argumentList, String receivedName) {
        return (Option) findFromArgumentListByName(argumentList, Type.OPTION, receivedName);
    }
}
