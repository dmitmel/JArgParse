package com.github.dmitmel.util;

import java.util.Iterator;

public class IterableUtils {
    public static <T> String join(Iterable<T> iterable, String separator) {
        StringBuilder builder = new StringBuilder(0);

        for (Iterator<T> iterator = iterable.iterator(); iterator.hasNext(); ) {
            builder.append(iterator.next());
            if (iterator.hasNext())
                builder.append(separator);
        }

        return builder.toString();
    }
}
