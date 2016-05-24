package org.jargparse.argtypes;

import org.jargparse.ArgumentList;

public class Flag extends Argument {
    @Override
    public Type getType() {
        return Type.FLAG;
    }

    public Flag(String name, String helpInfo, String parseResultKey) {
        super.name = name;
        super.helpInfo = helpInfo;
        super.parseResultKey = parseResultKey;
    }

    public Flag(String name, String longName, String helpInfo, String parseResultKey) {
        super.name = name;
        super.longName = longName;
        super.helpInfo = helpInfo;
        super.parseResultKey = parseResultKey;
    }

    public static boolean isStringArgumentFlag(String stringArg) {
        return stringArg.startsWith("-");
    }

    public static Flag findFromArgumentListByName(ArgumentList argumentList, String receivedName) {
        return (Flag) findFromArgumentListByName(argumentList, Type.FLAG, receivedName);
    }
}
