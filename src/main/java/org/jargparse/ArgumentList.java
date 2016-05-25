package org.jargparse;

import org.jargparse.argtypes.Argument;
import org.jargparse.argtypes.Flag;
import org.jargparse.argtypes.Option;
import org.jargparse.argtypes.Positional;
import org.jargparse.util.Predicate;
import org.jargparse.util.Lists;
import org.jargparse.util.Strings;
import org.jargparse.util.formatting.StringTokenBuilder;

import java.util.*;

public class ArgumentList implements List<Argument> {
    public static final int RIGHT_MARGIN = 80;
    public static final String INDENT_BEFORE_ARGUMENT_HELP_LINE = "  ";

    private final String appDescription;
    private final String appName;

    private List<Argument> underlying;
    private Argument lastAddedArgument;

    // Variables for generating help and usage info
    private StringTokenBuilder helpMessageBuilder;
    private StringTokenBuilder usageMessageBuilder;
    private String newlineSpacingForUsageArgs;

    public ArgumentList(String appName, String appDescription, Argument... arguments) {
        this(appName, appDescription, new ArrayList<>(Arrays.asList(arguments)));
    }

    public ArgumentList(String appName, String appDescription, List<Argument> arguments) {
        underlying = arguments;
        this.appDescription = appDescription;
        this.appName = appName;
    }

    public void verifyArgument(Argument newArgument) {
        if (newArgument.getType() == Argument.Type.POSITIONAL)
            verifyNewPositional((Positional) newArgument);

        if (newArgument.getType() == Argument.Type.OPTION && !newArgument.isMetaVarDefined())
            throw new IllegalArgumentException("option without meta var");
        testForIdenticalNaming(newArgument);

        lastAddedArgument = newArgument;
    }

    private void verifyNewPositional(Positional positional) {
        if (lastAddedArgument != null && lastAddedArgument.getType() == Argument.Type.POSITIONAL) {
            Positional.Usage lastAddedPositionalUsage = ((Positional) lastAddedArgument).usage;

            assert (lastAddedPositionalUsage != Positional.Usage.ZERO_OR_MORE) :
                    "can\'t add positionals after positional with usage \"zero or more\"";

            assert lastAddedPositionalUsage != Positional.Usage.OPTIONAL
                    && positional.usage != Positional.Usage.REQUIRED :
                    "can\'t add positional with usage required after optional one";
        }
    }

    private void testForIdenticalNaming(Argument newArgument) {
        if (areThere2IdenticalNames(newArgument))
            throw new ArgumentExistsException(String.format(
                    "argument list already contains argument with name \"%s\"", newArgument.name));
        if (areThere2IdenticalLongNames(newArgument))
            throw new ArgumentExistsException(String.format(
                    "argument list already contains argument with long name \"%s\"", newArgument.longName));
    }

    private boolean areThere2IdenticalLongNames(Argument newArgument) {
        boolean thereAre2IdenticalLongNames = false;

        for (Argument testing : underlying) {
            if (testing.getType() == Argument.Type.OPTION || testing.getType() == Argument.Type.FLAG) {
                if (testing.longNameEquals(newArgument.longName)) {
                    thereAre2IdenticalLongNames = true;
                }
            }
        }

        return thereAre2IdenticalLongNames;
    }

    private boolean areThere2IdenticalNames(Argument newArgument) {
        boolean thereAre2IdenticalNames = false;

        for (Argument testing : underlying) {
            if (testing.getType() == Argument.Type.OPTION || testing.getType() == Argument.Type.FLAG) {
                if (testing.name.equals(newArgument.name)) {
                    thereAre2IdenticalNames = true;
                }
            }
        }

        return thereAre2IdenticalNames;
    }

    // Wrappers for underlying collection
    @Override public int size() { return underlying.size(); }
    @Override public boolean isEmpty() { return underlying.isEmpty(); }
    @Override public boolean contains(Object o) { return underlying.contains(o); }
    @Override public Iterator<Argument> iterator() { return underlying.iterator(); }
    @Override public Object[] toArray() { return underlying.toArray(); }
    @Override public <T> T[] toArray(T[] a) { return underlying.toArray(a); }
    @Override public boolean add(Argument argument) {
        verifyArgument(argument);
        return underlying.add(argument);
    }
    @Override public boolean remove(Object o) { return underlying.remove(o); }
    @Override public boolean containsAll(Collection<?> c) { return underlying.containsAll(c); }
    @Override public boolean addAll(Collection<? extends Argument> c) {
        for (Argument argument : c)
            verifyArgument(argument);
        return underlying.addAll(c);
    }
    @Override public boolean addAll(int index, Collection<? extends Argument> c) {
        for (Argument argument : c)
            verifyArgument(argument);
        return underlying.addAll(index, c);
    }
    @Override public boolean retainAll(Collection<?> c) { return underlying.retainAll(c); }
    @Override public boolean removeAll(Collection<?> c) { return underlying.removeAll(c); }
    @Override public void clear() { underlying.clear(); }
    @Override public Argument get(int index) { return underlying.get(index); }
    @Override public Argument set(int index, Argument element) {
        verifyArgument(element);
        return underlying.set(index, element);
    }
    @Override public void add(int index, Argument element) {
        verifyArgument(element);
        underlying.add(index, element);
    }
    @Override public Argument remove(int index) { return underlying.remove(index); }
    @Override public int indexOf(Object o) { return underlying.indexOf(o); }
    @Override public int lastIndexOf(Object o) { return underlying.lastIndexOf(o); }
    @Override public ListIterator<Argument> listIterator() { return underlying.listIterator(); }
    @Override public ListIterator<Argument> listIterator(int index) { return underlying.listIterator(index); }
    @Override public List<Argument> subList(int fromIndex, int toIndex) {
        return underlying.subList(fromIndex, toIndex); }

    public Argument getByName(String name) {
        for (Argument argument : this)
            if (argument.nameEquals(name) || argument.longNameEquals(name))
                return argument;
        return null;
    }

    public String constructHelpMessage() {
        helpMessageBuilder = new StringTokenBuilder();
        helpMessageBuilder.append(usageAsTokenBuilder()).append("\n\n");
        helpMessageBuilder.appendWordsString(appDescription).append("\n\n");

        addPositionalsHelpSection();
        helpMessageBuilder.append('\n');
        addNonPositionalsHelpSection();

        return helpMessageBuilder.joinWithRightMargin(RIGHT_MARGIN);
    }

    private void addPositionalsHelpSection() {
        List<Argument> onlyPositionals = Lists.filter(this,
                Argument.Type.makePredicate(Argument.Type.POSITIONAL));
        if (onlyPositionals.size() > 0) {
            helpMessageBuilder.append("positional arguments:\n");
            for (Argument positional : onlyPositionals)
                addHelpAboutPositional(positional);
        }
    }

    private void addHelpAboutPositional(Argument positional) {
        StringBuilder currentLineBuilder = new StringBuilder(INDENT_BEFORE_ARGUMENT_HELP_LINE);
        currentLineBuilder.append(positional.metaVar);
        makeIndentBetweenArgNameAndInfoInto(currentLineBuilder);
        currentLineBuilder.append(positional.helpInfo).append('\n');
        helpMessageBuilder.append(currentLineBuilder);
    }

    private void addNonPositionalsHelpSection() {
        List<Argument> nonPositionals = Lists.filter(this,
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
            currentLineBuilder.append('\n').append(newlineSpacingForUsageArgs).append("     ");
        else
            currentLineBuilder.append(Strings.duplicateChar(' ', 23 + 1 - currentLineBuilder.length()));
    }

    public String constructUsageMessage() {
        return usageAsTokenBuilder().joinWithRightMargin(RIGHT_MARGIN);
    }

    private StringTokenBuilder usageAsTokenBuilder() {
        usageMessageBuilder = new StringTokenBuilder();

        usageMessageBuilder.append("usage:");
        usageMessageBuilder.append(appName);

        newlineSpacingForUsageArgs = countNewLineSpacingForUsageArgs();
        usageMessageForAllArguments();

        return usageMessageBuilder;
    }

    private String countNewLineSpacingForUsageArgs() {
        return Strings.duplicateChar(' ', 6 + 1 + appName.length() + 1);
    }

    private void usageMessageForAllArguments() {
        List<Argument> nonPositionals = Lists.filter(this,
                Argument.Type.makePredicate(Argument.Type.FLAG, Argument.Type.OPTION));
        for (Argument nonPositional : nonPositionals)
            usageMessageForAnyArgument(nonPositional);

        usageMessageBuilder.append("\n".concat(newlineSpacingForUsageArgs));
        // Positionals must be last in this list
        List<Argument> onlyPositionals = Lists.filter(this,
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
