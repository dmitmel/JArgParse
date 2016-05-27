package com.github.dmitmel;

import com.github.dmitmel.argtypes.Argument;
import com.github.dmitmel.argtypes.Flag;
import com.github.dmitmel.argtypes.Option;
import com.github.dmitmel.util.Lists;
import com.github.dmitmel.util.Strings;
import com.github.dmitmel.util.formatting.StringTokenBuilder;

import java.util.List;

class HelpInfoGenerator {
    private ArgumentList argumentList;
    private StringTokenBuilder helpMessageBuilder;

    public HelpInfoGenerator(ArgumentList argumentList) {
        this.argumentList = argumentList;
    }

    public String constructHelpMessage() {
        helpMessageBuilder = new StringTokenBuilder();
        helpMessageBuilder.append(argumentList.usageAsTokenBuilder()).append("\n\n");
        helpMessageBuilder.appendWordsString(argumentList.appDescription).append("\n\n");

        addPositionalsHelpSection();
        helpMessageBuilder.append('\n');
        addNonPositionalsHelpSection();

        return helpMessageBuilder.joinWithRightMargin(ArgumentList.RIGHT_MARGIN_FOR_TRANSLATING_TO_STRING);
    }

    private void addPositionalsHelpSection() {
        List<Argument> onlyPositionals = Lists.filter(argumentList,
                Argument.Type.makePredicate(Argument.Type.POSITIONAL));
        if (onlyPositionals.size() > 0) {
            helpMessageBuilder.append("positional arguments:\n");
            for (Argument positional : onlyPositionals)
                addHelpAboutPositional(positional);
        }
    }

    private void addHelpAboutPositional(Argument positional) {
        StringBuilder currentLineBuilder = new StringBuilder(ArgumentList.INDENT_BEFORE_ARGUMENT_HELP_LINE);
        currentLineBuilder.append(positional.metaVar);
        makeIndentBetweenArgNameAndInfoInto(currentLineBuilder);
        currentLineBuilder.append(positional.helpInfo).append('\n');
        helpMessageBuilder.append(currentLineBuilder);
    }

    private void addNonPositionalsHelpSection() {
        List<Argument> nonPositionals = Lists.filter(argumentList,
                Argument.Type.makePredicate(Argument.Type.FLAG, Argument.Type.OPTION));
        if (nonPositionals.size() > 0) {
            helpMessageBuilder.append("optional arguments:\n");
            for (Argument nonPositional : nonPositionals) {
                if (nonPositional.getType() == Argument.Type.OPTION)
                    addHelpAboutOption((Option) nonPositional);
                else if (nonPositional.getType() == Argument.Type.FLAG)
                    addHelpAboutFlag((Flag) nonPositional);
            }
        }
    }

    private void addHelpAboutFlag(Flag flag) {
        StringBuilder currentLineBuilder = new StringBuilder("  ");

        if (flag.isNameValid()) {
            currentLineBuilder.append(flag.name);

            if (flag.isLongNameValid()) {
                currentLineBuilder.append(", ").append(flag.longName);
            }

        } else if (flag.isLongNameValid()) {
            currentLineBuilder.append(flag.longName);

        } else
            throw new IllegalStateException("argument " + flag + " doesn\'t contain any names");

        makeIndentBetweenArgNameAndInfoInto(currentLineBuilder);

        currentLineBuilder.append(flag.helpInfo).append('\n');
        helpMessageBuilder.append(currentLineBuilder.toString());
    }

    private void addHelpAboutOption(Option option) {
        StringBuilder currentLineBuilder = new StringBuilder("  ");

        if (option.isNameValid()) {
            currentLineBuilder.append(option.name);
            if (option.isMetaVarDefined())
                currentLineBuilder.append(' ').append(option.metaVar);

            if (option.isLongNameValid()) {
                currentLineBuilder.append(", ").append(option.longName);
                if (option.isMetaVarDefined())
                    currentLineBuilder.append(' ').append(option.metaVar);
            }

        } else if (option.isLongNameValid()) {
            currentLineBuilder.append(option.longName);
            if (option.isMetaVarDefined())
                currentLineBuilder.append(' ').append(option.metaVar);

        } else
            throw new IllegalStateException("argument " + option + " doesn\'t contain any names");

        makeIndentBetweenArgNameAndInfoInto(currentLineBuilder);

        currentLineBuilder.append(option.helpInfo).append('\n');
        helpMessageBuilder.append(currentLineBuilder.toString());
    }

    private void makeIndentBetweenArgNameAndInfoInto(StringBuilder currentLineBuilder) {
        if (currentLineBuilder.length() > 23)
            currentLineBuilder.append('\n').append(argumentList.countNewLineSpacingForUsageArgs()).append("     ");
        else
            currentLineBuilder.append(Strings.duplicateChar(' ', 23 + 1 - currentLineBuilder.length()));
    }
}
