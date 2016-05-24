package org.jargparse.argtypes;

import org.jargparse.ArgumentList;

public abstract class Argument {
    // Fields that will be used in any sub-classes
    public String name;             // Field is ignored in positional args
    public String helpInfo;
    public String longName;         // Field is ignored in positional args
    public String parseResultKey;
    public String metaVar;

    public static String metaVarFromName(String name) {
        return name.toUpperCase().replace('-', '_');
    }

    public static Argument findFromArgumentListByName(ArgumentList argumentList, Type type, String receivedName) {
        for (Argument argument : argumentList)
            if (argument.getType() == type)
                if (argument.nameEquals(receivedName) || argument.longNameEquals(receivedName))
                    return argument;

        return null;
    }

    // Compare operations
    public boolean longNameEquals(String other) {
        return longName != null && longName.equals(other);
    }
    public boolean nameEquals(String other) {
        return name != null && name.equals(other);
    }

    // Definition test operations
    public boolean isMetaVarDefined() {
        return getType() != Type.FLAG && metaVar != null && !metaVar.isEmpty();
    }
    public boolean isLongNameValid() {
        return getType() != Type.POSITIONAL && longName != null && longName.startsWith("--");
    }
    public boolean isNameValid() {
        return getType() != Type.POSITIONAL && name != null && name.startsWith("-");
    }

    public String getSuitableName() {
        String suitable;
        if (isNameValid())
            suitable = name;
        else if (isLongNameValid())
            suitable = longName;
        else
            throw new IllegalStateException("argument " + this + " doesn\'t contain any names");
        return suitable;
    }

    public abstract Type getType();

    public enum Type {
        FLAG, OPTION, POSITIONAL
    }
}
