package com.github.dmitmel;

import com.github.dmitmel.argtypes.Argument;
import com.github.dmitmel.argtypes.Positional;
import com.github.dmitmel.util.Lists;
import com.github.dmitmel.util.formatting.StringTokenBuilder;

import java.util.List;

class UsageInfoGenerator {
    private ArgumentList argumentList;
    private StringTokenBuilder usageMessageBuilder;
    private String newlineSpacingForUsageArgs;

    public UsageInfoGenerator(ArgumentList argumentList) {
        this.argumentList = argumentList;
    }

    public String constructUsageMessage() {
        return usageAsTokenBuilder().joinWithRightMargin(ArgumentList.RIGHT_MARGIN_FOR_TRANSLATING_TO_STRING);
    }

    StringTokenBuilder usageAsTokenBuilder() {
        usageMessageBuilder = new StringTokenBuilder();

        usageMessageBuilder.append("usage:");
        usageMessageBuilder.append(argumentList.appName);

        newlineSpacingForUsageArgs = argumentList.countNewLineSpacingForUsageArgs();
        usageMessageForAllArguments();

        return usageMessageBuilder;
    }

    private void usageMessageForAllArguments() {
        List<Argument> nonPositionals = Lists.filter(argumentList,
                Argument.Type.makePredicate(Argument.Type.FLAG, Argument.Type.OPTION));
        for (Argument nonPositional : nonPositionals)
            usageMessageForAnyArgument(nonPositional);

        usageMessageBuilder.append("\n".concat(newlineSpacingForUsageArgs));
        // Positionals must be last in this list
        List<Argument> onlyPositionals = Lists.filter(argumentList,
                Argument.Type.makePredicate(Argument.Type.POSITIONAL));
        for (Argument positional : onlyPositionals)
            usageMessageForAnyArgument(positional);
    }

    private void usageMessageForAnyArgument(Argument argument) {
        String usagePart = "";

        switch (argument.getType()) {
            case POSITIONAL:
                StringBuilder builder = new StringBuilder(String.format("[%s", argument.metaVar));
                if (((Positional) argument).usage == Positional.Usage.ZERO_OR_MORE)
                    builder.append("...");
                builder.append(']');
                usagePart = builder.toString();
                break;

            case FLAG:
                if (argument.isNameValid())
                    usagePart = String.format("[%s]", argument.name);
                else if (argument.isLongNameValid())
                    usagePart = String.format("[%s]", argument.longName);
                // #addArgument(Argument) method checks if name is present, so here won't be "else" condition
                break;

            case OPTION:
                if (argument.isNameValid())
                    usagePart = String.format("[%s %s]", argument.name, argument.metaVar);
                else if (argument.isLongNameValid())
                    usagePart = String.format("[%s %s]", argument.longName, argument.metaVar);
                usageMessageBuilder.appendWithNewlineSpacing(
                        String.format("[%s %s]", argument.name, argument.metaVar), newlineSpacingForUsageArgs);
                break;
        }

        usageMessageBuilder.appendWithNewlineSpacing(usagePart, newlineSpacingForUsageArgs);
    }
}
