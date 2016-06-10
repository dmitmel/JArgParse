package tests;

import github.dmitmel.jargparse.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class MainTests extends Assert {
    @Test(expected = ArgumentExistsException.class)
    public void testAdding2IdenticalArgs() {
        ArgumentParser parser = new ArgumentParser("my_cool_app", "some app description");
        parser.addArgument(new Flag("-v", "some help", "VERBOSE"));
    }

    @Test(expected = UnexpectedArgumentException.class)
    public void testUsingUnknownArgument() {
        makeTestParser().run("-abc");
    }

    @Test
    public void testHelpInformationGenerating() {
        System.out.println(makeTestParser().constructHelpMessage());
    }

    @Test
    public void testParsingRequiredPositionals() {
        ParsingResult result = makeTestParser().run("a");
        assertEquals("a", result.getString("REQUIRED_VALUE"));
        assertEquals("some value", result.getString("OPTIONAL_VALUE"));
    }

    @Test(expected = MissingPositionalsException.class)
    public void testParsingMissingPositionals() {
        makeTestParser().run();
    }

    @Test
    public void testParsingOptionalPositionals() {
        ParsingResult result = makeTestParser().run("a", "b");
        assertEquals("a", result.getString("REQUIRED_VALUE"));
        assertEquals("b", result.getString("OPTIONAL_VALUE"));
    }

    @Test
    public void testParsingMixedArguments() {
        ParsingResult result = makeTestParser()
                .run("--some-long-flag", "required", "-V", "optional", "--number", "123", "listItem1", "listItem2",
                        "listItem3");
        assertEquals("123", result.getString("SOME_NUMBER"));
        assertEquals(false, result.getBoolean("SHOW_HELP"));
        assertEquals(true, result.getBoolean("SOME_LONG_FLAG"));
        assertEquals("optional", result.getString("OPTIONAL_VALUE"));
        assertEquals(false, result.getBoolean("SHOW_VERSION"));
        assertEquals("required", result.getString("REQUIRED_VALUE"));
        assertEquals(true, result.getBoolean("VERBOSE"));

        List<String> values = result.getList("SOME_VALUES");
        assertEquals(3, values.size());
        for (int i = 0; i < 3; i++)
            assertEquals("listItem" + (i + 1), values.get(i));
    }

    @Test
    public void testParserWithOnlyOptionalPositionals() {
        ArgumentParser parser = new ArgumentParser("my_cool_app", "some app description");
        parser.addArgument(new Positional("help1", "a", Positional.Usage.OPTIONAL,
                "defaultValue1"));
        parser.addArgument(new Positional("help2", "b", Positional.Usage.OPTIONAL,
                "defaultValue2"));

        ParsingResult result1 = parser.run();
        assertEquals("defaultValue1", result1.getString("a"));
        assertEquals("defaultValue2", result1.getString("b"));

        ParsingResult result2 = parser.run("a");
        assertEquals("a", result2.getString("a"));
        assertEquals("defaultValue2", result2.getString("b"));

        ParsingResult result3 = parser.run("a", "b");
        assertEquals("a", result3.getString("a"));
        assertEquals("b", result3.getString("b"));
    }

    @Test(expected = UnexpectedPositionalsException.class)
    public void testParsingUnexpectedPositionals() {
        new ArgumentParser("my_cool_app", "some app description").run("a", "b", "c");
    }

    private ArgumentParser makeTestParser() {
        ArgumentParser parser = new ArgumentParser("my_cool_app",
                "Test application using JArgParse argument parser."
        );
        parser.addArgument(new Positional("This is required positional argument",
                "REQUIRED_VALUE"));
        parser.addArgument(new Positional("This is optional positional argument with default value",
                "OPTIONAL_VALUE",
                Positional.Usage.OPTIONAL,
                "some value"));
        parser.addArgument(new Positional("This is values list args",
                "SOME_VALUES",
                Positional.Usage.ZERO_OR_MORE));
        parser.addArgument(new Option("-n",
                "--number",
                "Input number",
                "SOME_NUMBER",
                "1"));
        parser.addArgument(new Flag("-V",
                "--verbose",
                "Print what code does",
                "VERBOSE"));
        parser.addArgument(new Flag(null,
                "--some-long-flag",
                "Some long flag",
                "SOME_LONG_FLAG"));

        return parser;
    }
}
