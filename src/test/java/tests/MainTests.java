package tests;

import com.github.dmitmel.*;
import com.github.dmitmel.argtypes.Flag;
import com.github.dmitmel.argtypes.Option;
import com.github.dmitmel.argtypes.Positional;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class MainTests extends Assert {
    @Test(expected = ArgumentExistsException.class)
    public void testAdding2IdenticalArgs() {
        ArgumentParser parser = new ArgumentParser("my_cool_app", "some app description", "some app version");
        parser.addArgument(new Flag("-v", "some help", "VERBOSE"));
    }

    @Test(expected = UnexpectedArgumentException.class)
    public void testUsingUnknownArgument() {
        makeTestParser().run("-abc");
    }

    @Test
    public void testHelpInformationGenerating() {
        makeTestParser().run("-h");
    }

    @Test
    public void testParsingRequiredPositionals() {
        Map<String, Object> result = makeTestParser().run("a");
        assertEquals("a", result.get("REQUIRED_VALUE"));
        assertEquals("some value", result.get("OPTIONAL_VALUE"));
    }

    @Test(expected = MissingPositionalsException.class)
    public void testParsingMissingPositionals() {
        makeTestParser().run();
    }

    @Test
    public void testParsingOptionalPositionals() {
        Map<String, Object> result = makeTestParser().run("a", "b");
        assertEquals("a", result.get("REQUIRED_VALUE"));
        assertEquals("b", result.get("OPTIONAL_VALUE"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testParsingMixedArguments() {
        Map<String, Object> result = makeTestParser()
                .run("--some-long-flag", "required", "-V", "optional", "--number", "123", "listItem1", "listItem2",
                        "listItem3");
        assertEquals("123", result.get("SOME_NUMBER"));
        assertEquals(false, result.get("SHOW_HELP"));
        assertEquals(true, result.get("SOME_LONG_FLAG"));
        assertEquals("optional", result.get("OPTIONAL_VALUE"));
        assertEquals(false, result.get("SHOW_VERSION"));
        assertEquals("required", result.get("REQUIRED_VALUE"));
        assertEquals(true, result.get("VERBOSE"));

        List<String> values = (List<String>) result.get("SOME_VALUES");
        assertEquals(3, values.size());
        for (int i = 0; i < 3; i++)
            assertEquals("listItem" + (i + 1), values.get(i));
    }

    @Test
    public void testParserWithOnlyOptionalPositionals() {
        ArgumentParser parser = new ArgumentParser("my_cool_app", "some app description", "some app version");
        parser.addArgument(new Positional("help1", "a", Positional.Usage.OPTIONAL,
                "defaultValue1"));
        parser.addArgument(new Positional("help2", "b", Positional.Usage.OPTIONAL,
                "defaultValue2"));

        Map<String, Object> result1 = parser.run();
        assertEquals("defaultValue1", result1.get("a"));
        assertEquals("defaultValue2", result1.get("b"));

        Map<String, Object> result2 = parser.run("a");
        assertEquals("a", result2.get("a"));
        assertEquals("defaultValue2", result2.get("b"));

        Map<String, Object> result3 = parser.run("a", "b");
        assertEquals("a", result3.get("a"));
        assertEquals("b", result3.get("b"));
    }

    @Test(expected = UnexpectedPositionalsException.class)
    public void testParsingUnexpectedPositionals() {
        new ArgumentParser("my_cool_app", "some app description", "some app version").run("a", "b", "c");
    }

    private ArgumentParser makeTestParser() {
        ArgumentParser parser = new ArgumentParser("my_cool_app", "Test application using JArgParse argument parser.",
                "1.0");

        parser.addArgument(new Positional("This is required positional argument", "REQUIRED_VALUE"));

        parser.addArgument(new Positional("This is optional positional argument with default value", "OPTIONAL_VALUE",
                Positional.Usage.OPTIONAL, "some value"));

        parser.addArgument(new Positional("This is values list args", "SOME_VALUES",
                Positional.Usage.ZERO_OR_MORE));

        parser.addArgument(new Option("-n", "--number", "Input number", "SOME_NUMBER", "1"));

        parser.addArgument(new Flag("-V", "--verbose", "Print what code does", "VERBOSE"));

        parser.addArgument(new Flag(null, "--some-long-flag", "Some long flag", "SOME_LONG_FLAG"));

        return parser;
    }
}
