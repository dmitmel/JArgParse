package com.github.dmitmel.jargparse.argtypes;

import com.github.dmitmel.jargparse.ArgumentList;
import com.github.dmitmel.jargparse.util.Predicate;

public abstract class Argument {
    public String name;             // Field is ignored in positional args
    public String helpInfo;
    public String longName;         // Field is ignored in positional args
    /**
     * Stores data about meta var of this argument. Meta vars can do two things:
     *
     * <ol>
     *     <li>They can be used in generating usage and help info (meta vars are in upper case):
     *         <pre><code>
     * my_app [--option SOME_OPTION] [--flag]
     *        [SOME_POSITIONAL]
     *         </code></pre>
     *     </li>
     *
     *     <li>They can be used as keys for values in result map.</li>
     * </ol>
     */
    public String metaVar;

    // You can see shortenings of this method (you don't need to specify type)
    public static Argument findFromArgumentListByName(ArgumentList argumentList, Type type,
                                                      String receivedName) {
        for (Argument argument : argumentList)
            if (argument.getType() == type)
                if ((argument.name != null && argument.name.equals(receivedName)) ||
                        (argument.longName != null && argument.longName.equals(receivedName)))
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

    /**
     * Returns name of argument if it's valid, otherwise returns long name. If neither long name nor short aren't
     * valid - throws {@link IllegalStateException}.
     *
     * @return name of argument if it's valid, otherwise returns long name
     * @throws IllegalStateException if neither long name nor short aren't valid
     */
    public String getSuitableName() {
        String suitable;
        if (isNameValid())
            suitable = name;
        else if (isLongNameValid())
            suitable = longName;
        else
            throw new IllegalStateException("argument \"" + this + "\" doesn\'t contain any suitable names");
        return suitable;
    }

    public abstract Type getType();

    public enum Type {
        FLAG, OPTION, POSITIONAL;

        public static <T extends Argument> Predicate<T> makePredicate(final Type... expectedTypes) {
            return new Predicate<T>() {
                @Override
                public boolean test(T t) {
                    for (Type type : expectedTypes)
                        if (t.getType() == type)
                            return true;
                    return false;
                }
            };
        }
    }
}
