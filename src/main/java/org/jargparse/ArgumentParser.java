package org.jargparse;

import org.jargparse.argtypes.Argument;
import org.jargparse.argtypes.Flag;
import org.jargparse.argtypes.Option;
import org.jargparse.argtypes.Positional;
import org.jargparse.util.*;
import org.jargparse.util.Arrays;

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
        addArgument(new Flag("-h", "--help", "show this message and exit", "show help"));
        addArgument(new Flag("-v", "--version", "show program's version number and exit", "show version"));
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

        if ((boolean) output.get("show version")) {
            System.out.println(appVersion);
            return Collections.emptyMap();
        } else if ((boolean) output.get("show help")) {
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
            output.put(realOption.parseResultKey, optionValues.get(option));
        }
        for (String flag : flagValues.keySet()) {
            Flag realFlag = Flag.findFromArgumentListByName(argumentList, flag);
            if (realFlag == null)
                throw new UnexpectedArgumentException(flag, Argument.Type.FLAG);
            output.put(realFlag.parseResultKey, flagValues.get(flag));
        }
    }

    private void putAllPositionalsToOutputData() {
        List<Positional> onlyPositionals = Lists.cast(Lists.filter(argumentList, new Predicate<Argument>() {
            @Override
            public boolean test(Argument argument) {
                return argument.getType() == Argument.Type.POSITIONAL;
            }
        }));
        List<Positional> onlyRequired = Lists.filter(onlyPositionals, new Predicate<Positional>() {
            @Override
            public boolean test(Positional positional) {
                return positional.usage == Positional.Usage.REQUIRED;
            }
        });
        List<Positional> onlyOptional = Lists.filter(onlyPositionals, new Predicate<Positional>() {
            @Override
            public boolean test(Positional positional) {
                return positional.usage == Positional.Usage.OPTIONAL;
            }
        });
        Positional last = (onlyPositionals.size() > 0) ? onlyPositionals.get(onlyPositionals.size() - 1) : null;
        Positional positionalWithZeroOrMoreUsage = (last != null && last.usage == Positional.Usage.ZERO_OR_MORE) ?
                last : null;

        if (positionalValues.size() < onlyRequired.size())
            throw new MissingPositionalsException(onlyRequired.subList(positionalValues.size(), onlyOptional.size()));
        int endOfRequired = 0;
        for (int i = 0; i < onlyRequired.size(); i++) {
            output.put(onlyRequired.get(i).parseResultKey, positionalValues.get(i));
            endOfRequired = i;
        }
        if (endOfRequired + 1 < positionalValues.size()) {
            int endOfOptional = 0;
            for (int i = 0; i < onlyOptional.size(); i++) {
                output.put(onlyOptional.get(i).parseResultKey, positionalValues.get(endOfRequired + 1 + i));
                endOfOptional = endOfRequired + 1 + i;
            }
            // Note: positional with usage Positional.Usage#ZERO_OR_MORE can "eat" last arguments,
            // But if it isn't present and we have arguments - throwing exception
            if (endOfOptional + 1 < positionalValues.size()) {
                if (positionalWithZeroOrMoreUsage != null) {
                    List<String> items = positionalValues.subList(endOfOptional + 1, positionalValues.size());
                    output.put(positionalWithZeroOrMoreUsage.parseResultKey, items);
                } else
                    throw new UnexpectedPositionalsException(positionalValues.subList(endOfOptional + 1,
                            positionalValues.size()));
            }
        }

        if (positionalWithZeroOrMoreUsage != null &&
                !output.containsKey(positionalWithZeroOrMoreUsage.parseResultKey))
            output.put(positionalWithZeroOrMoreUsage.parseResultKey, Collections.emptyList());
        registerNotReceivedOptionalPositionals(onlyOptional);
    }

    private void registerNotReceivedOptionalPositionals(List<Positional> positionals) {
        for (Positional optional : positionals)
            if (!output.containsKey(optional.parseResultKey))
                output.put(optional.parseResultKey, optional.defaultValue);
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
            Maps.set(flagValues, stringArg, true);
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
            Maps.set(optionValues, patternArgument.name, patternArgument.defaultValue);
    }

    private boolean isOptionSet(Argument patternArgument) {
        return !optionValues.containsKey(patternArgument.name) && !optionValues.containsKey(patternArgument.longName);
    }

    private void disableNotReceivedFlag(Flag patternArgument) {
        Maps.set(flagValues, patternArgument.getSuitableName(), false);
    }

    private boolean isFlagSet(Argument patternArgument) {
        return !flagValues.containsKey(patternArgument.name) && !flagValues.containsKey(patternArgument.longName);
    }
}
