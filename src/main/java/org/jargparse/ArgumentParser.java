package org.jargparse;

import org.jargparse.argtypes.Argument;
import org.jargparse.argtypes.Flag;
import org.jargparse.argtypes.Option;
import org.jargparse.argtypes.Positional;
import org.jargparse.util.*;
import org.jargparse.util.Arrays;
import org.jargparse.util.IterableUtils;

import java.util.*;

public class ArgumentParser {
    public String appName;
    public String appDescription;
    public String appVersion;
    public ArgumentList argumentList;

    // Collections for parsing
    private List<String> positionalValues = new ArrayList<>(0);
    private Map<String, String> optionValues = new HashMap<>(0);
    private Map<String, Boolean> flagValues = new HashMap<>(0);
    private Map<String, Object> output = new HashMap<>(0);

    public ArgumentParser(String appName, String appDescription, String appVersion) {
        this(appName, appDescription, appVersion, new ArrayList<Argument>());
    }

    public ArgumentParser(String appName, String appDescription, String appVersion, List<Argument> patternArguments) {
        this.appName = appName;
        this.appDescription = appDescription;
        this.appVersion = appVersion;
        this.argumentList = new ArgumentList(appName, appDescription, patternArguments);

        addInitialArguments();
    }

    private void addInitialArguments() {
        addArgument(new Flag("-h", "--help", "show this message and exit", "SHOW_HELP"));
        addArgument(new Flag("-v", "--version", "show program's version number and exit", "SHOW_VERSION"));
    }

    public void addArgument(Argument newArgument) {
        argumentList.add(newArgument);
    }

    private String constructHelpMessage() {
        return argumentList.constructHelpMessage();
    }

    public Map<String, Object> run(String... args) {
        output = new HashMap<>(0);
        flagValues = new HashMap<>(0);
        optionValues = new HashMap<>(0);
        positionalValues = new ArrayList<>(0);

        parseArgumentValues(args);
        registerNotReceivedNonPositionals();
        putNonPositionalsToOutputData();

        if ((boolean) output.get("SHOW_VERSION")) {
            System.out.println(appVersion);
            return Collections.emptyMap();
        } else if ((boolean) output.get("SHOW_HELP")) {
            System.out.println(constructHelpMessage());
            return Collections.emptyMap();
        }

        putAllPositionalsToOutputData();

        return output;
    }

    private void putNonPositionalsToOutputData() {
        for (String option : optionValues.keySet()) {
            Option realOption = Option.findFromArgumentListByName(argumentList, option);
            if (realOption == null)
                throw new UnexpectedArgumentException(option, Argument.Type.OPTION);
            output.put(realOption.metaVar, optionValues.get(option));
        }
        for (String flag : flagValues.keySet()) {
            Flag realFlag = Flag.findFromArgumentListByName(argumentList, flag);
            if (realFlag == null)
                throw new UnexpectedArgumentException(flag, Argument.Type.FLAG);
            output.put(realFlag.metaVar, flagValues.get(flag));
        }
    }

    private void putAllPositionalsToOutputData() {
        List<Positional> onlyPositionals = Lists.cast(Lists.filter(argumentList,
                Argument.Type.makePredicate(Argument.Type.POSITIONAL)));
        List<Positional> onlyRequired = Lists.filter(onlyPositionals,
                Positional.Usage.makePredicate(Positional.Usage.REQUIRED));

        if (onlyRequired.size() > positionalValues.size())
            throw new MissingPositionalsException(onlyRequired.subList(positionalValues.size(),
                    onlyRequired.size()));

        ListIterator<Positional> iterator = onlyPositionals.listIterator();
        int i = 0;
        while (iterator.hasNext()) {
            Positional next = iterator.next();

            switch (next.usage) {
                case OPTIONAL:
                    if (i >= positionalValues.size())
                        output.put(next.metaVar, next.defaultValue);
                    else
                        output.put(next.metaVar, positionalValues.get(i));
                    break;

                case REQUIRED:
                    output.put(next.metaVar, positionalValues.get(i));
                    break;

                case ZERO_OR_MORE:
                    if (i < positionalValues.size()) {
                        List<String> values = new ArrayList<>(0);
                        for (String item : positionalValues.subList(i, positionalValues.size())) {
                            values.add(item);
                            i++;
                        }
                        output.put(next.metaVar, values);
                    }
                    break;
            }

            i++;
        }

        // If iterator has next - there isn't positional with usage Positional.Usage#ZERO_OR_MORE
        if (i < positionalValues.size())
            throw new UnexpectedPositionalsException(positionalValues.subList(i, positionalValues.size()));
    }


    private void parseArgumentValues(String[] args) {
        Iterator<String> iterator = Arrays.iteratorFromArray(args);

        while (iterator.hasNext()) {
            String stringArg = iterator.next();

            // Options with both long and short names must have next token (value), so checking its appearance
            Argument testOption = argumentList.getByName(stringArg);
            if (iterator.hasNext() && stringArg.startsWith("-") &&
                    testOption != null && testOption.getType() == Argument.Type.OPTION) {
                String optionValue = iterator.next();
                optionValues.putAll(new Pair<>(stringArg, optionValue).asOneElementMap());
            } else
                // Otherwise, it's flag or a positional
                retrieveFlagOrPositional(stringArg);
        }
    }

    private void retrieveFlagOrPositional(String stringArg) {
        if (Flag.isStringArgumentFlag(stringArg))
            flagValues.put(stringArg, true);
        else
            positionalValues.add(stringArg);
    }

    private void registerNotReceivedNonPositionals() {
        for (Argument patternArgument : argumentList) {
            if (patternArgument.getType() == Argument.Type.FLAG && isFlagSet(patternArgument))
                disableNotReceivedFlag((Flag) patternArgument);
            else if (patternArgument.getType() == Argument.Type.OPTION && isOptionSet(patternArgument))
                disableNotReceivedOption((Option) patternArgument);
        }
    }

    private void disableNotReceivedOption(Option patternArgument) {
        if (patternArgument.defaultValue != null)
            optionValues.put(patternArgument.name, patternArgument.defaultValue);
    }

    private boolean isOptionSet(Argument patternArgument) {
        return !optionValues.containsKey(patternArgument.name) && !optionValues.containsKey(patternArgument.longName);
    }

    private void disableNotReceivedFlag(Flag patternArgument) {
        flagValues.put(patternArgument.getSuitableName(), false);
    }

    private boolean isFlagSet(Argument patternArgument) {
        return !flagValues.containsKey(patternArgument.name) && !flagValues.containsKey(patternArgument.longName);
    }

    public String parsingExceptionToString(ArgumentParseException e) {
        String usageMessage = argumentList.constructUsageMessage();
        StringBuilder builder = new StringBuilder(usageMessage);
        builder.append('\n').append(appName).append(": ");

        if (e instanceof UnexpectedArgumentException) {
            builder.append("unexpected ");
            builder.append(((UnexpectedArgumentException) e).getType().toString().toLowerCase());
            builder.append(" \"").append(((UnexpectedArgumentException) e).getArgument()).append('\"');
        } else if (e instanceof UnexpectedPositionalsException) {
            builder.append("unexpected positionals: ");
            builder.append(IterableUtils.join(((UnexpectedPositionalsException) e).getPositionals(), ", "));
        } else if (e instanceof MissingPositionalsException) {
            builder.append("missing positionals: ");
            builder.append(IterableUtils.join(((MissingPositionalsException) e).getPositionals(), ", "));
        }

        return builder.toString();
    }
}
