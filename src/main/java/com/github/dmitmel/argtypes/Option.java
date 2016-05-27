package com.github.dmitmel.argtypes;

import com.github.dmitmel.ArgumentList;

public class Option extends Argument {
    public String defaultValue;

    @Override
    public Type getType() {
        return Type.OPTION;
    }

    public Option(String name, String helpInfo, String metaVar, String defaultValue) {
        this(name, null, helpInfo, metaVar, defaultValue);
    }

    public Option(String name, String longName, String helpInfo, String metaVar, String defaultValue) {
        super.name = name;
        super.longName = longName;
        super.helpInfo = helpInfo;
        super.metaVar = metaVar;
        this.defaultValue = defaultValue;
    }

    public static Option findFromArgumentListByName(ArgumentList argumentList, String receivedName) {
        return (Option) findFromArgumentListByName(argumentList, Type.OPTION, receivedName);
    }
}
