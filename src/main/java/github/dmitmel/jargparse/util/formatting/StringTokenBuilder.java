package github.dmitmel.jargparse.util.formatting;

import github.dmitmel.jargparse.util.Strings;

import java.util.*;

/**
 * Class stores some string tokens, and then joins them, for example, with right margin.
 */
public class StringTokenBuilder {
    private List<StringToken> tokens = new ArrayList<>(0);

    // Appending operations
    public StringTokenBuilder append(String s) {
        tokens.add(new SimpleStringToken(s));
        return this;
    }
    public StringTokenBuilder append(CharSequence s) { return append(String.valueOf(s)); }
    public StringTokenBuilder append(Object obj) { return append(String.valueOf(obj)); }
    public StringTokenBuilder append(boolean b) { return append(Boolean.toString(b)); }
    public StringTokenBuilder append(char c) { return append(Character.toString(c)); }
    public StringTokenBuilder append(int i) { return append(Integer.toString(i)); }
    public StringTokenBuilder append(long l) { return append(Long.toString(l)); }
    public StringTokenBuilder append(float f) { return append(Float.toString(f)); }
    public StringTokenBuilder append(double d) { return append(Double.toString(d)); }

    public StringTokenBuilder append(StringTokenBuilder builder) {
        for (StringToken newToken : builder.tokens)
            this.tokens.add(newToken);
        return this;
    }

    public <T> StringTokenBuilder append(T[] array) {
        for (T item : array)
            append(item);
        return this;
    }

    public <T> StringTokenBuilder append(List<T> list) {
        for (T item : list)
            append(item);
        return this;
    }

    public StringTokenBuilder appendWithNewlineSpacing(String s, String spacing) {
        tokens.add(new StyledStringToken(s, spacing));
        return this;
    }

    public StringTokenBuilder appendWordsString(String s) {
        return append(s.split(" "));
    }

    // Converting to string operations

    /**
     * Joins string tokens with spaces. But, some characters wouldn't be joined - spaces, tabs, newlines and "\r" chars
     * (only if token consists from only these ones).
     *
     * @param rightMargin right line blocker, if {@code lengthOfLastLine + 1 + lengthOfToken} greater than this value -
     *                    token will be moved to next line.
     * @return formatted string
     */
    public String joinWithRightMargin(int rightMargin) {
        StringBuilder builder = new StringBuilder(0);

        for (ListIterator<StringToken> iterator = tokens.listIterator(); iterator.hasNext(); ) {
            StringToken token = iterator.next();

            String[] lines = Strings.toLinesArray(builder.toString());
            if (lines.length == 0)
                // Probably, there're only newlines
                lines = new String[] {""};
            int trimmedTokenLength =
                    Strings.withoutCharacters(token.getValue().trim(), Arrays.asList('\n', '\r')).length();

            if (needToAddNewlineForToken(token, lines, rightMargin)) {
                builder.append('\n');
                if (token.getType() == StringToken.Type.STYLED)
                    builder.append(((StyledStringToken) token).getNewLineSpacing());
                builder.append(token.getValue());
            } else
                builder.append(token.getValue());

            if (iterator.hasNext()) {
                StringToken next = iterator.next();
                iterator.previous();

                if (Strings.lastChar(next.getValue()) != '\n' && token.getValue().charAt(0) != '\n'
                        && trimmedTokenLength > 0)
                    builder.append(' ');
            }
        }

        return builder.toString();
    }

    private boolean needToAddNewlineForToken(StringToken token, String[] lines, int rightMargin) {
        int trimmedLastLineLength = Strings.withoutCharacters(lines[lines.length - 1].trim(),
                // Characters of newlines aren't require space in console
                Arrays.asList('\n', '\r')).length();
        int trimmedTokenLength =
                Strings.withoutCharacters(token.getValue().trim(), Arrays.asList('\n', '\r')).length();

        return (trimmedLastLineLength + 1 + trimmedTokenLength) > rightMargin;
    }

    public String joinWithSeparators(String separator) {
        StringBuilder builder = new StringBuilder(0);

        for (ListIterator<StringToken> iterator = tokens.listIterator(); iterator.hasNext(); ) {
            StringToken token = iterator.next();
            builder.append(token);
            if (iterator.hasNext())
                builder.append(separator);
        }

        return builder.toString();
    }


    // Overriding methods from java.lang.Object
    @Override
    public String toString() {
        return String.format("StringTokenStream{tokens=%s}", tokens);
    }
    @Override
    public boolean equals(Object o) {
        if (o == this || o == null)
            return true;
        if (this.getClass() != o.getClass())
            return false;

        StringTokenBuilder that = (StringTokenBuilder) o;
        return tokens.equals(that.tokens);

    }
    @Override
    public int hashCode() {
        return tokens.hashCode();
    }
}
