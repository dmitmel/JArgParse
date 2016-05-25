package org.jargparse.argtypes;

import org.jargparse.util.Predicate;

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
        REQUIRED, OPTIONAL, ZERO_OR_MORE;

        public static Predicate<Positional> makePredicate(final Usage... expectedUsages) {
            return new Predicate<Positional>() {
                @Override
                public boolean test(Positional t) {
                    for (Usage type : expectedUsages)
                        if (t.usage == type)
                            return true;
                    return false;
                }
            };
        }
    }
}
