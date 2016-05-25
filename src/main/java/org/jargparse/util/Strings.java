package org.jargparse.util;

import java.util.*;

public class Strings {
    public static String withoutCharacters(String s, List<Character> unnecessary) {
        StringBuilder out = new StringBuilder("");
        for (char c : s.toCharArray())
            if (!unnecessary.contains(c))
                out.append(c);

        return out.toString();
    }

    public static String[] toLinesArray(String s) {
        List<String> lines = new ArrayList<>(0);
        StringBuilder currentLine = new StringBuilder("");

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            currentLine.append(c);

            // Testing Windows' CRLF newline separators
            if ((c == '\n' && getNextChar(s, i) == '\r') || (c == '\r' && getNextChar(s, i) == '\n')) {
                i++;
                lines.add(currentLine.toString() + s.charAt(i));
                currentLine = new StringBuilder("");
            } else if (c == '\n' || c == '\r') {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder("");
            }
        }

        lines.add(currentLine.toString());

        return lines.toArray(new String[0]);
    }

    public static int getNextChar(String s, int currentI) {
        return (currentI + 1 < s.length()) ? s.charAt(currentI + 1) : -1;
    }

    public static String duplicateChar(char c, int times) {
        StringBuilder out = new StringBuilder("");
        for (int i = 0; i < times; i++)
            out.append(c);
        return out.toString();
    }

    /**
     * Returns last character of string, if can't - returns {@code -1}.
     * @param s string
     * @return last character of string, if can't - {@code -1}
     */
    public static int lastChar(String s) {
        if (s != null && s.length() > 0)
            return s.charAt(s.length() - 1);
        else
            return -1;
    }
}
