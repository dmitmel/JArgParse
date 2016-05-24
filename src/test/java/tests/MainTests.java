package tests;

import org.jargparse.ArgumentExistsException;
import org.jargparse.ArgumentParser;
import org.jargparse.MissingPositionalsException;
import org.jargparse.UnexpectedArgumentException;
import org.jargparse.argtypes.Flag;
import org.jargparse.argtypes.Option;
import org.jargparse.argtypes.Positional;
import org.jargparse.util.formatting.StringTokenBuilder;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class MainTests extends Assert {
    @Test(expected = ArgumentExistsException.class)
    public void testAdding2IdenticalArgs() {
        ArgumentParser parser = new ArgumentParser("my_cool_app", "some app description", "some app version");
        parser.addArgument(new Flag("-v", "some help", "verbose"));
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
        assertEquals("a", result.get("positionalValue"));
        assertEquals("some value", result.get("optionalValue"));
    }

    @Test(expected = MissingPositionalsException.class)
    public void testParsingMissingPositionals() {
        makeTestParser().run();
    }

    @Test
    public void testParsingOptionalPositionals() {
        Map<String, Object> result = makeTestParser().run("a", "b");
        assertEquals("a", result.get("positionalValue"));
        assertEquals("b", result.get("optionalValue"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testParsingMixedArguments() {
        Map<String, Object> result = makeTestParser()
                .run("--some-long-flag", "required", "-V", "optional", "--number", "123", "list1", "list2", "list3");
        assertEquals("123", result.get("number"));
        assertEquals(false, result.get("show help"));
        assertEquals(true, result.get("some long flag"));
        assertEquals("optional", result.get("optionalValue"));
        assertEquals(false, result.get("show version"));
        assertEquals("required", result.get("positionalValue"));
        assertEquals(true, result.get("verbose"));

        List<String> values = (List<String>) result.get("valuesList");
        assertEquals(3, values.size());
        for (int i = 0; i < 3; i++)
            assertEquals("list" + (i + 1), values.get(i));
    }

    @Test
    public void testWordAppendingWithStringTokenBuilders() {
        StringTokenBuilder testing = new StringTokenBuilder();
        testing.append("one");
        testing.append("two");
        testing.append("three");
        assertEquals("one two three", testing.joinWithSeparators(" "));
    }

    @Test
    public void testRightTextMarginWithStringTokenBuilder() {
        StringTokenBuilder testing = new StringTokenBuilder();
        testing.append("one");
        testing.append("two");
        testing.append("three");
        testing.append("four");
        testing.append("five");
        assertEquals("one two \nthree four \nfive", testing.joinWithRightMargin(10));
    }

    private ArgumentParser makeTestParser() {
        ArgumentParser parser = new ArgumentParser("my_cool_app", "Test application using JArgParse argument parser.",
                "1.0");

        parser.addArgument(new Positional("This is required positional argument", "REQUIRED_VALUE", "positionalValue"));

        parser.addArgument(new Positional("This is optional positional argument with default value", "OPTIONAL_VALUE",
                "optionalValue", Positional.Usage.OPTIONAL, "some value"));

        parser.addArgument(new Positional("This is values list args", "SOME_VALUES", "valuesList",
                Positional.Usage.ZERO_OR_MORE));

        parser.addArgument(new Option("-n", "--number", "Input number", "SOME_NUMBER", "number", "1"));

        parser.addArgument(new Flag("-V", "--verbose", "Print what code does", "verbose"));

        parser.addArgument(new Flag(null, "--some-long-flag", "Some long flag", "some long flag"));

        return parser;
    }
}
