package github.dmitmel.jargparse;

import github.dmitmel.jargparse.util.Lists;
import github.dmitmel.jargparse.util.formatting.StringTokenBuilder;

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
        StringBuilder usagePart = new StringBuilder(0);

        switch (argument.getType()) {
            case POSITIONAL:
                usagePart.append('[');
                usagePart.append(argument.metaVar);
                if (((Positional) argument).usage == Positional.Usage.ZERO_OR_MORE)
                    usagePart.append("...");
                usagePart.append(']');
                break;

            case FLAG:
                usagePart.append('[');
                if (argument.isNameValid()) {
                    usagePart.append(argument.name);

                    if (argument.isLongNameValid())
                        usagePart.append(" | ").append(argument.longName);
                } else if (argument.isLongNameValid())
                    usagePart.append(argument.longName);
                usagePart.append(']');
                // #addArgument(Argument) method checks if name is present, so here won't be "else" condition
                break;

            case OPTION:
                usagePart.append('[');
                if (argument.isNameValid()) {
                    usagePart.append(argument.name);

                    if (argument.isLongNameValid())
                        usagePart.append(" | ").append(argument.longName);
                } else if (argument.isLongNameValid())
                    usagePart.append(argument.longName);

                usagePart.append(' ').append(argument.metaVar).append(']');
                break;
        }

        usageMessageBuilder.appendWithNewlineSpacing(usagePart.toString(), newlineSpacingForUsageArgs);
    }
}
