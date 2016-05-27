package com.github.dmitmel;

import com.github.dmitmel.argtypes.Argument;
import com.github.dmitmel.argtypes.Positional;
import com.github.dmitmel.util.Strings;
import com.github.dmitmel.util.formatting.StringTokenBuilder;

import java.util.*;

public class ArgumentList implements List<Argument> {
    public static final int RIGHT_MARGIN_FOR_TRANSLATING_TO_STRING = 80;
    public static final String INDENT_BEFORE_ARGUMENT_HELP_LINE = "  ";

    final String appDescription;
    final String appName;

    private List<Argument> underlying;
    private Argument lastAddedArgument;

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
        return new HelpInfoGenerator(this).constructHelpMessage();
    }

    public String constructUsageMessage() {
        return new UsageInfoGenerator(this).constructUsageMessage();
    }


    StringTokenBuilder usageAsTokenBuilder() {
        return new UsageInfoGenerator(this).usageAsTokenBuilder();
    }

    String countNewLineSpacingForUsageArgs() {
        return Strings.duplicateChar(' ', 6 + 1 + appName.length() + 1);
    }
}
