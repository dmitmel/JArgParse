package com.github.dmitmel.argtypes;

import com.github.dmitmel.util.Predicate;

public class Positional extends Argument {
    public Usage usage;
    public String defaultValue;

    @Override
    public Type getType() {
        return Type.POSITIONAL;
    }

    public Positional(String helpInfo, String metaVar) {
        this(helpInfo, metaVar, Usage.REQUIRED);
    }

    public Positional(String helpInfo, String metaVar, Usage usage) {
        this(helpInfo, metaVar, usage, null);
    }

    public Positional(String helpInfo, String metaVar, Usage usage, String defaultValue) {
        super.helpInfo = helpInfo;
        super.metaVar = metaVar;
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
