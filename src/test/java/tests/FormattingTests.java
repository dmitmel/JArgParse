package tests;

import com.github.dmitmel.jargparse.util.formatting.StringTokenBuilder;
import org.junit.Assert;
import org.junit.Test;

public class FormattingTests extends Assert {
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
}
